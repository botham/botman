import bots.BotFather;
import bots.FacebookBot;
import bots.TelegramBot;
import com.google.inject.AbstractModule;
import helpers.RedisHelper;
import services.BotRegisterer;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 * <p>
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

  @Override
  public void configure() {

    bind(BotRegisterer.class).asEagerSingleton();

    bind(BotFather.class).asEagerSingleton();

    bind(TelegramBot.class).asEagerSingleton();

    bind(FacebookBot.class).asEagerSingleton();

    bind(RedisHelper.class).asEagerSingleton();
  }
}
