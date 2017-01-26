package at.htlkaindorf.heirim12.energieeffizienz.database;

import java.sql.*;
import java.util.*;


/**
 * Created by richard on 21.01.2017.
 */

public class Database implements AutoCloseable
{
  private final String url, user, password;
  private Connection connection = null;

  public Database(String url, String user, String password)
  {
    this.url = url;
    this.user = user;
    this.password = password;
  }

  public void open()
          throws SQLException
  {
//    try
//    {
//      Class.forName("org.postgresql.Driver");
//    }
//    catch (Exception ex)
//    {
//      ex.printStackTrace();
//    }

    if (connection == null || connection.isClosed())
      connection = DriverManager.getConnection(url, user, password);
  }

  @Override
  public void close()
          throws SQLException
  {
    if (connection != null && !connection.isClosed())
      connection.close();
    connection = null;
  }

  public int executeUpdate(String sql)
    throws SQLException
  {
    try (Statement statement = connection.createStatement())
    {
      return statement.executeUpdate(sql);
    }
  }

  public Statement createStatement()
    throws SQLException
  {
    return connection.createStatement();
  }

  public static void main(String[] args)
  {
    try(Database database = new Database("jdbc:postgresql://127.0.0.1/pv", "smartphone", "htl"))
    {
      database.open();
      System.out.println("Connection has been succesfully open");

      Statement statement = database.createStatement();
      System.out.println("Statemant has been created");

      try (ResultSet resultSet = statement.executeQuery("SELECT * FROM pv_current"))
      {
        while (resultSet.next())
        {
          System.out.println(resultSet.getString("voltage1"));
        }
      }

    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
