package bots;

import com.fasterxml.jackson.databind.JsonNode;
import models.Contract;
import models.User;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import utils.MessageParser;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@Singleton
public class FacebookBot implements Bot {

  private WSClient ws;
  private BotFather botFather;

  @Inject
  public FacebookBot(WSClient ws, BotFather botFather) {
    this.ws = ws;
    this.botFather = botFather;
    botFather.registerBot(Contract.FACEBOOK, this);
  }


  public boolean receive(JsonNode json) {
    if (json != null) {
      JsonNode messaging = json.findPath("entry").get(0).findPath("messaging");

      for (JsonNode node : messaging) {
        JsonNode jsonMessage = node.findPath("message");
        String senderId = node.findPath("sender").findPath("id").asText();
        String text = jsonMessage.findPath("text").asText();
        Logger.debug("SenderID: " + senderId + " text: " + node.toString());

        if (!text.isEmpty() && !senderId.isEmpty()) {
          if(text.startsWith("+signup")) {
            String userId = MessageParser.extractUserId(text);
            CompletionStage<String> completionStageName = getUserNameFromFacebook(senderId);
            completionStageName.thenApplyAsync(name -> {
              if(name != null && userId != null && !userId.isEmpty()) {
                botFather.registerUser(new User(userId, name, Contract.FACEBOOK, senderId)).thenApplyAsync(dbMessage -> {
                  sendMessage(senderId, dbMessage);
                  return null;
                });
              }
              return name;
            });
          } else {
            checkAndSend(senderId, Contract.FACEBOOK, text, botFather.sendMessage);
          }
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public String getName() {
    return Contract.FACEBOOK;
  }

  @Override
  public void sendMessage(String recipient, String message) {
    Map<String, JsonNode> map = new HashMap<>();
    map.put("recipient", Json.newObject().put("id", recipient));
    map.put("message", Json.newObject().put("text", message));
    JsonNode json = Json.newObject().setAll(map);
    WSRequest request = ws.url("https://graph.facebook.com/v2.6/me/messages");

    WSRequest complexRequest = request.setHeader("Content-Type", "application/json")
      .setQueryParameter("access_token", Configuration.facebookBotToken)
      .setContentType("application/json");
    complexRequest.post(json);
  }

  private CompletionStage<String> getUserNameFromFacebook(String senderId) {
    String url = "https://graph.facebook.com/v2.6/" + senderId +
            "?fields=first_name,last_name&access_token=" + Configuration.facebookBotToken;
    WSRequest request = ws.url(url);


    return request.get().thenApplyAsync(WSResponse::asJson)
              .thenApplyAsync(name -> name.path("first_name").asText() + " " + name.path("last_name").asText());
  }
}