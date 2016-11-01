package helpers;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import models.*;
import play.Logger;
import play.db.Database;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class DBHelper {

  private Database db;

  @Inject
  public DBHelper(Database db) {
    this.db = db;
  }

  private <U> DBResult execute(String query, CheckedFunction<U> f) {
    Connection connection = null;
    PreparedStatement statement = null;
    DBResult result = null;
    try {
      connection = db.getConnection();
      statement = connection.prepareStatement(query);
      U u = f.apply(statement);
      result = new DBSuccessResult<>(u);
    } catch (MySQLIntegrityConstraintViolationException ex) {
      int reason = Contract.DUPLICATE_ENTRY;
      result = new DBFailureResult(reason);
    } catch (SQLException ex) {
      Logger.error("Exception caught in DBHelper.execute", ex);
      int reason = Contract.DB_FAILURE;
      result = new DBFailureResult(reason);
    } finally {
      try {
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException ex) {
        Logger.error("Statement or Connection couldn't be closed", ex);
      }
    }
    return result;
  }

  public CompletionStage<DBResult> addUser(User user) {
    CheckedFunction<Integer> queryDB = preparedStatement -> {
      preparedStatement.setString(1, user.getId());
      preparedStatement.setString(2, user.getName());
      preparedStatement.setString(3, user.getClient());
      preparedStatement.setString(4, user.getClientId());
      return preparedStatement.executeUpdate();
    };
    String query = "INSERT INTO User VALUES(?, ?, ?, ?)";
    return CompletableFuture.supplyAsync(() -> execute(query, queryDB));
  }

  public CompletionStage<DBResult> getUser(String userId) {
    CheckedFunction<ResultSet> queryDB = preparedStatement -> {
      preparedStatement.setString(1, userId);
      return preparedStatement.executeQuery();
    };
    String query = "SELECT * FROM User WHERE id = ?";
    return CompletableFuture.supplyAsync(() -> execute(query, queryDB));
  }

  // http://stackoverflow.com/a/18198349/3671697
  @FunctionalInterface
  private interface CheckedFunction<U> {
    U apply(PreparedStatement p) throws SQLException;
  }

}
