package services;

import bots.TelegramBot;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.BotSession;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BotRegisterer {

  @Inject
  public BotRegisterer(TelegramBot telegramBot, ApplicationLifecycle applicationLifecycle) {
    //register Telegram Bot
    try {
      TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
      BotSession botSession = telegramBotsApi.registerBot(telegramBot);
      applicationLifecycle.addStopHook(() -> {
        botSession.close();
        return null;
      });

    } catch (TelegramApiException ex) {
      Logger.error("Telegram Bot couldn't be registered", ex);
    }
  }
}
