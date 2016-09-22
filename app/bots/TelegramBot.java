package bots;

import models.Contract;
import models.User;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import play.Logger;
import utils.MessageParser;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TelegramBot extends TelegramLongPollingBot implements Bot {

    private BotFather botFather;

    @Inject
    public TelegramBot(BotFather botFather) {
        this.botFather = botFather;
        botFather.registerBot(Contract.TELEGRAM, this);
    }


    @Override
    public void onUpdateReceived(Update update) {
        try {
            if(update.hasMessage()) {
                Message telegramMessage = update.getMessage();
                String text = telegramMessage.getText();
                String senderId = telegramMessage.getFrom().getId().toString();
                String senderFirstName = telegramMessage.getFrom().getFirstName();
                String senderLastName = telegramMessage.getFrom().getLastName();
                String name = senderFirstName + " " + senderLastName;

                if(text.startsWith("+signup")) {
                    String userId = MessageParser.extractUserId(text);
                    if(userId != null && !userId.isEmpty() && senderFirstName != null && senderLastName != null) {
                        botFather.registerUser(new User(userId, name, Contract.TELEGRAM, senderId)).thenApplyAsync(dbMessage -> {
                            sendMessage(senderId, dbMessage);
                            return null;
                        });
                    }
                } else {
                    checkAndSend(senderId, Contract.TELEGRAM, text, botFather.sendMessage);
                }
            }
        } catch (Exception ex) {
            Logger.error("Exception in onUpdateReceived()", ex);
        }
    }

    @Override
    public String getBotUsername() {
        return Configuration.telegramBotName;
    }

    @Override
    public String getBotToken() {
        return Configuration.telegramBotToken;
    }

    @Override
    public String getName() {
        return Contract.TELEGRAM;
    }

    @Override
    public void sendMessage(String recipientId, String message) {
        SendMessage newMessage = new SendMessage();
        newMessage.enableMarkdown(true);
        newMessage.setChatId(recipientId);
        Logger.info("ChatId: " + recipientId);
        newMessage.setText(message);
        try {
            sendMessage(newMessage);
        } catch (TelegramApiException t) {
            Logger.error("TelegramApiException", t.getApiResponse());
        }
    }
}
