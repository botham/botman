package bots;

import models.UserMessages;
import models.Message;
import utils.MessageParser;

import java.util.Set;
import java.util.function.Function;

public interface Bot {
    String getName();

    void sendMessage(String recipientId, String message);

    default void checkAndSend(String senderId, String text, Function<Message, Void> send) {
        Set<String> recipients = MessageParser.extractRecipients(text);
        if(!recipients.isEmpty()) {
            Message message = new Message(senderId, recipients, text);
            send.apply(message);
        }
        else {
            sendMessage(senderId, UserMessages.USER_ID_NOT_SPECIFIED);
        }
    }
}
