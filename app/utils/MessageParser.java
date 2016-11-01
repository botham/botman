package utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

  /**
   * extracts recipients except sender if `text` contains
   */
  public static Set<String> extractRecipients(String text, String sender) {
    Pattern pattern = Pattern.compile("(?<=\\+)(?!" + sender + ")\\w+");
    Matcher matcher = pattern.matcher(text);
    Set<String> recipients = new HashSet<>();
    while (matcher.find()) {
      recipients.add(matcher.group(0));
    }
    return recipients;
  }

  public static String extractText(String text) {
    // "(?<!\\+|\\w)(\\w+)" in order to select all non-userID words
    // http://stackoverflow.com/a/18066013/3671697
    return text.replaceAll("^\\s+|\\s+$|\\s+(?=\\s)|\\s?\\+|(?<=\\+)(\\w+)", "");
  }

  public static String extractUserId(String text) {
    Pattern pattern = Pattern.compile("(?<=\\+signup\\s)\\w+");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.group(0);
    }
    return null;
  }
}
