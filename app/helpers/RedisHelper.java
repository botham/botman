package helpers;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.User;
import play.Logger;

import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

@Singleton
public class RedisHelper {

  private RedisClient redisClient;
  private StatefulRedisConnection<String, String> connection;

  public RedisHelper() {
    Config config = ConfigFactory.load();
    String redisUri = config.getString("redis-uri");
    redisClient = RedisClient.create(redisUri);
    connection = redisClient.connect();
  }

  public CompletionStage<String> addUser(User user) {
    RedisFuture<String> redisFuture = connection.async().hmset(user.getId(), user.asMap());
    redisFuture.thenApplyAsync(ok -> connection.async().set(user.getClient() + "-" + user.getClientId(), user.getId()));
    return redisFuture;
  }

  public CompletionStage<User> getUser(String id) {
    return connection.async().hmget(id, "id", "name", "client", "clientId")
      .thenApplyAsync(list -> {
        Logger.info("Redis.getUser" + list);
        try {
          if(list.get(0) == null) {
            return null;
          } else {
            return new User(list);
          }
        } catch (RuntimeException ex) {
          Logger.error("RuntimeException while creating User in RedisHelper", ex);
          return null;
        }
      });
  }

  public String getUserId(String client, String clientId) {
    return connection.sync().get(client + "-" + clientId);
  }

  public void terminate() {
    connection.close();
    redisClient.shutdown();
  }
}
