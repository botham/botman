package models;

import java.util.Set;

public class Message {

    private String sender, text, senderClient, recipientClient;
    private Set<String> recipients;

    public Message(String sender, String senderClient, Set<String> recipients, String text) {
        this.sender = sender;
        this.senderClient = senderClient;
        this.recipients = recipients;
        this.text = text;
    }

    //empty constructor for Jackson
    public Message() {}

    public String getSender() {
        return sender;
    }

    public Set<String> getRecipients() {
        return recipients;
    }

    public String getText() {
        return text;
    }

    public String getSenderClient() {
        return senderClient;
    }

    public String getRecipientClient() {
        return recipientClient;
    }

}
