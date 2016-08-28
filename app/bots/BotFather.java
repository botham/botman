package bots;

import helpers.DBHelper;
import helpers.RedisHelper;
import models.*;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Singleton
public class BotFather {

  private RedisHelper redisHelper;
  private DBHelper dbHelper;
  private Map<String, Bot> botRegistry = new HashMap<>();

  @Inject
  public BotFather(RedisHelper redisHelper, DBHelper dbHelper) {
    this.redisHelper = redisHelper;
    this.dbHelper = dbHelper;
  }

  Function<Message, Void> sendMessage =
    message -> {
      Logger.debug("BotFather message: " + message.getText());
      String text = message.getText();
      Set<String> recipients = message.getRecipients();
      if (!recipients.isEmpty()) {
        for (String userId : recipients) {
          CompletionStage<String> senderId = redisHelper.getUserId(message.getSenderClient(), message.getSender());
          CompletionStage<User> recipient = redisHelper.getUser(userId);
          recipient.thenCombineAsync(senderId, (recipientUser, sId) -> {
            if(recipientUser != null && sId != null) {
              send(recipientUser, sId, text);
            }
            else {
              Logger.warn("Either recipientUser or sId or both are null");
            }
            return null;
          });
        }
      }
      return null;
    };


  private boolean send(User recipient, String senderId, String text) {
    String client = recipient.getClient();
    String clientId = recipient.getClientId();
    text = "[@" + senderId + "]: " + text;
    Logger.debug("sent text: " + text);
    try {
      Bot recipientBot = botRegistry.get(client);
      recipientBot.sendMessage(clientId, text);
      return true;
    } catch (NullPointerException ex) {
      Logger.error("Recipient Bot couldn't be found", ex);
      return false;
    }
  }

  void registerBot(String botName, Bot bot) {
    botRegistry.put(botName, bot);
  }

  CompletionStage<String> registerUser(User user) {
    CompletionStage<User> existsInRedis = redisHelper.getUser(user.getId());
    return existsInRedis.thenComposeAsync(redisUser -> {
      if(redisUser != null) {
        return CompletableFuture.completedFuture(UserMessages.USER_ALREADY_EXISTS);
      } else {
        CompletionStage<DBResult> dbFuture = dbHelper.addUser(user);
        return dbFuture.thenComposeAsync(dbResult -> {
          if(dbResult.isSuccess()) {
            redisHelper.addUser(user);
            return CompletableFuture.completedFuture(UserMessages.USER_REGISTERED);
          } else {
            DBFailureResult dbFailureResult = (DBFailureResult) dbResult;
            int reason = dbFailureResult.getReason();
            if(reason == Contract.DUPLICATE_ENTRY) {
              return CompletableFuture.completedFuture(UserMessages.USER_ALREADY_EXISTS);
            } else {
              return CompletableFuture.completedFuture(UserMessages.USER_NOT_REGISTERED);
            }
          }
        });
      }
    });
  }

}