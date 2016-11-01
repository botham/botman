package bots;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Configuration {

    private static final Config config = ConfigFactory.load();
    static final String telegramBotToken = System.getenv("TELEGRAM_BOT_TOKEN");
    static final String facebookBotToken = System.getenv("FACEBOOK_BOT_TOKEN");
    public static final String facebookBotVerification = System.getenv("FACEBOOK_BOT_VERIFICATION");
    static final String telegramBotName = config.getString("telegram.bot-name");

}
