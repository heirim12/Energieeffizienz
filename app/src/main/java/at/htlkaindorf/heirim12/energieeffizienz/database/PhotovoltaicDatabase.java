package at.htlkaindorf.heirim12.energieeffizienz.database;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import at.htlkaindorf.heirim12.energieeffizienz.data.CurrentValues;
import at.htlkaindorf.heirim12.energieeffizienz.data.Records;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettings;

/**
 * Created by richard on 22.01.2017.
 */

public class PhotovoltaicDatabase extends Database
{
  private static PhotovoltaicDatabase theInstance = null;

  public PhotovoltaicDatabase()
          throws ClassNotFoundException
  {
    super("jdbc:postgresql://localhost:5432/pv", "raspberry", "htl");
    //String url, String user, String password)
    Class.forName("org.postgresql.Driver");
  }

  public static PhotovoltaicDatabase getInstance()
          throws ClassNotFoundException
  {
    if (theInstance == null)
      theInstance = new PhotovoltaicDatabase();
    return theInstance;
  }

  public void writeHistoryDataSet(Calendar dateTime, double voltage1, double current1,
                                  int azimuth1, int elevation1,
                                  double voltage2, double current2,
                                  double voltageAccu)
          throws SQLException
  {
    try
    {
      open();
      String sql = String.format(Locale.ENGLISH, "INSERT INTO pv_history " +
                      "(epoch_ms, voltage1, current1, azimuth1, elevation1," +
                      "voltage2, current2, voltage_accu)" +
                      " VALUES (%d, %f, %f, %d, %d, %f, %f, %f)",
              dateTime.getTimeInMillis(), voltage1, current1, azimuth1, elevation1,
              voltage2, current2, voltageAccu);
      executeUpdate(sql);
    } finally
    {
      close();
    }
  }

  public void refreshCurrentDataSet(Calendar dateTime, double voltage1, double current1,
                                    int azimuth1, int elevation1,
                                    double voltage2, double current2,
                                    double voltageAccu)
          throws SQLException
  {
    try
    {
      open();
      String sql = String.format(Locale.ENGLISH, "UPDATE pv_current " +
                      "SET epoch_ms = %d, voltage1 = %f, current1 = %f," +
                      "azimuth1 = %d, elevation1 = %d," +
                      "voltage2 = %f, current2 = %f, voltage_accu = %f",
              dateTime.getTimeInMillis(), voltage1, current1, azimuth1, elevation1,
              voltage2, current2, voltageAccu);

      //IF statement throws Exception ???

//      String sql = String.format(Locale.ENGLISH, "IF EXISTS " +
//                      "(SELECT * FROM pv_current WHERE id_current = 1)" +
//                      "THEN " +
//                      "UPDATE pv_current " +
//                      "SET epoch_ms = %d, voltage1 = %f, current1 = %f," +
//                      "azimuth1 = %d, elevation1 = %d," +
//                      "voltage2 = %f, current2 = %f, voltage_accu = %f ; " +
//                      "ELSE " +
//                      "INSERT INTO pv_current " +
//                      "(epoch_ms, voltage1, current1, azimuth1, elevation1," +
//                      "voltage2, current2, voltage_accu)" +
//                      " VALUES (%d, %f, %f, %d, %d, %f, %f, %f) ; " +
//                      "END IF",
//              dateTime.getTimeInMillis(), voltage1, current1, azimuth1, elevation1,
//              voltage2, current2, voltageAccu,
//              dateTime.getTimeInMillis(), voltage1, current1, azimuth1, elevation1,
//              voltage2, current2, voltageAccu);

      System.out.println(sql);
      executeUpdate(sql);
    } finally
    {
      close();
    }
  }

  public CurrentValues getCurrentValues()
          throws SQLException
  {
    try
    {
      open();
      try (
              final Statement statement = createStatement();
              final ResultSet resultSet =
                      statement.executeQuery("SELECT * FROM pv_current WHERE id_current = 1");
      )
      {
        CurrentValues currentValues = null;
        while (resultSet.next())
        {
          currentValues = new CurrentValues(
                  resultSet.getDouble("voltage1"), resultSet.getDouble("current1"),
                  resultSet.getDouble("voltage1") * resultSet.getDouble("current1"),
                  resultSet.getInt("azimuth1"), resultSet.getInt("elevation1"),
                  resultSet.getDouble("voltage2"), resultSet.getDouble("current2"),
                  resultSet.getDouble("voltage2") * resultSet.getDouble("current2"),
                  resultSet.getDouble("voltage_accu"));
        }
        return currentValues;
      }
    } finally
    {
      close();
    }
  }

  public Records getHistory(RecordsSettings recordsSettings)
          throws SQLException
  {
    try
    {
      final Statement statement = createStatement();
      String sql = ("SELECT epoch_ms,");
      if (recordsSettings.isPanel1Voltage())
        sql = String.format("%s voltage1,", sql);

      if (recordsSettings.isPanel1Current())
        sql = String.format("%s current1,", sql);

      if (recordsSettings.isPanel2Voltage())
        sql = String.format("%s voltage2,", sql);

      if (recordsSettings.isPanel2Current())
        sql = String.format("%s current2,", sql);

      sql = String.format("%s FROM pv_history WHERE epoch_ms >= %d AND epoch_ms <= %d", sql, recordsSettings.ge)
      System.out.println(sql);
    }
    finally
    {
      close();
    }
    return new Records();
  }

  public static void main(String[] args)
  {

    try
    {
      PhotovoltaicDatabase db = getInstance();
      db.refreshCurrentDataSet(new GregorianCalendar(2016, 1, 1), 50, 50, 50, 50, 50, 50, 50);
//      db.refreshCurrentDataSet(new GregorianCalendar(2016, 1, 1), 8, 8, 8, 8, 8, 8, 8);
      final CurrentValues currentValues = db.getCurrentValues();
      System.out.println(currentValues.getPanel1Voltage());
      System.out.println(currentValues.getPanel1Current());
      System.out.println(currentValues.getPanel1Power());
      System.out.println(currentValues.getPanel1Azimuth());
      System.out.println(currentValues.getPanel1Elevation());
      System.out.println(currentValues.getPanel2Voltage());
      System.out.println(currentValues.getPanel2Current());
      System.out.println(currentValues.getPanel2Power());
      System.out.println(currentValues.getPanel2Azimuth());
      System.out.println(currentValues.getPanel2Elevation());
      System.out.println(currentValues.getAccuVoltage());
    } catch (SQLException ex)
    {
      ex.printStackTrace();
    }
    catch (ClassNotFoundException ex)
    {
      ex.printStackTrace();
    }
  }
}
