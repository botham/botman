package utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

  public static Set<String> extractRecipients(String text) {
    Pattern pattern = Pattern.compile("(?<=\\+)\\w+");
    Matcher matcher = pattern.matcher(text);
    Set<String> recipients = new HashSet<>();
    while(matcher.find()) {
      recipients.add(matcher.group(0));
    }
    return recipients;
  }

  public static String extractUserId(String text) {
    Pattern pattern = Pattern.compile("(?<=\\+signup\\s)\\w+");
    Matcher matcher = pattern.matcher(text);
    if(matcher.find()) {
      return matcher.group(0);
    }
    return null;
  }
}
