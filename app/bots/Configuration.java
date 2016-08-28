package bots;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Map;

public class Configuration {

    private static final Config config = ConfigFactory.load();
    static final String telegramBotToken = System.getenv("TELEGRAM-BOT-TOKEN");
    static final String facebookBotToken = System.getenv("FACEBOOK-BOT-TOKEN");
    public static final String facebookBotVerification = System.getenv("FACEBOOK-BOT-VERIFICATION");
    static final String telegramBotName = config.getString("telegram.bot-name");

}
