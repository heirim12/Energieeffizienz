package at.htlkaindorf.heirim12.energieeffizienz.database;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import at.htlkaindorf.heirim12.energieeffizienz.data.CurrentValues;
import at.htlkaindorf.heirim12.energieeffizienz.data.HomeValues;
import at.htlkaindorf.heirim12.energieeffizienz.data.Record;
import at.htlkaindorf.heirim12.energieeffizienz.data.Records;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettings;

/**
 * Created by richard on 22.01.2017.
 */

public class PhotovoltaicDatabase extends Database
{
  private static PhotovoltaicDatabase theInstance = null;

  private PhotovoltaicDatabase(String url, String user, String password)
          throws ClassNotFoundException
  {
    super(url, user, password);
    //String url, String user, String password)
    Class.forName("org.postgresql.Driver");
  }

  public static PhotovoltaicDatabase getInstance()
          throws ClassNotFoundException
  {
    if (theInstance == null)
      theInstance =
              new PhotovoltaicDatabase("jdbc:postgresql://localhost:5432/pv", "raspberry", "htl");
    return theInstance;
  }

  public void changeSettings(String url, String user, String password)
          throws ClassNotFoundException
  {
    theInstance = new PhotovoltaicDatabase(url, user, password);
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

  public HomeValues getHomeValues()
          throws SQLException
  {
    try
    {
      open();

      final Calendar today = Calendar.getInstance();
      final Calendar sixDaysBefore = new GregorianCalendar(
              today.get(Calendar.YEAR),
              today.get(Calendar.DAY_OF_MONTH),
              today.get(Calendar.DAY_OF_MONTH) - 6);

      try (
              final Statement statement = createStatement();
              final ResultSet resultSet =
                      statement.executeQuery(String.format("SELECT epoch_ms," +
                                      " voltage1, current1, voltage2, current2 " +
                                      "FROM pv_history WHERE epoch_ms >= %d AND epoch_ms <= %d",
                              sixDaysBefore.getTimeInMillis(), today.getTimeInMillis()));
      )
      {
        final ArrayList<Double> energy7Days = new ArrayList<>();
        double averagePower1 = 0;
        double averagePower2 = 0;
        double energy1 = 0;
        double energy2 = 0;
        int count = 0;

        boolean hasNext = resultSet.next();
        Calendar currentResultSetDateTime = new GregorianCalendar();
        currentResultSetDateTime.setTimeInMillis(resultSet.getLong("epoch_ms"));

        while (hasNext)
        {
          final double power1 = (resultSet.getDouble("voltage1") * resultSet.getDouble("current1"));
          final double power2 = (resultSet.getDouble("voltage2") * resultSet.getDouble("current2"));

          if (resultSet.next())
          {
            Calendar nextResultSetDateTime = new GregorianCalendar();
            nextResultSetDateTime.setTimeInMillis(resultSet.getLong("epoch_ms"));
            final long timeStep = nextResultSetDateTime.getTimeInMillis()
                    - currentResultSetDateTime.getTimeInMillis();

            if (currentResultSetDateTime.get(Calendar.DAY_OF_MONTH)
                    == nextResultSetDateTime.get(Calendar.DAY_OF_MONTH))
            {
              if (currentResultSetDateTime.get(Calendar.DAY_OF_MONTH)
                      == today.get(Calendar.DAY_OF_MONTH))
              {
                averagePower1 += power1;
                averagePower2 += power2;
                count++;
              }

              energy1 += (power1 * (double) timeStep);
              energy2 += (power2 * (double) timeStep);

              currentResultSetDateTime = nextResultSetDateTime;
            } else
            {
              energy7Days.add(energy1 + energy2);
              energy1 = 0;
              energy2 = 0;

              energy1 += (power1 * (double) timeStep);
              energy2 += (power2 * (double) timeStep);
              currentResultSetDateTime = nextResultSetDateTime;
            }

          } else
          {
            averagePower1 /= count;
            averagePower2 /= count;
            energy7Days.add(energy1 + energy2);
            hasNext = false;
          }
        }

        return new HomeValues(averagePower1, energy1, averagePower2, energy2, energy7Days);
      }
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

      if (recordsSettings.isPanel1Voltage()
              || recordsSettings.isPanel1Power() || recordsSettings.isPanel1Energy()
              || recordsSettings.isBothPower() || recordsSettings.isBothEnergy())
        sql = String.format("%s voltage1,", sql);

      if (recordsSettings.isPanel1Current()
              || recordsSettings.isPanel1Power() || recordsSettings.isPanel1Energy()
              || recordsSettings.isBothPower() || recordsSettings.isBothEnergy())
        sql = String.format("%s current1,", sql);

      if (recordsSettings.isPanel2Voltage()
              || recordsSettings.isPanel2Power() || recordsSettings.isPanel2Energy()
              || recordsSettings.isBothPower() || recordsSettings.isBothEnergy())
        sql = String.format("%s voltage2,", sql);

      if (recordsSettings.isPanel2Current()
              || recordsSettings.isPanel2Power() || recordsSettings.isPanel2Energy()
              || recordsSettings.isBothPower() || recordsSettings.isBothEnergy())
        sql = String.format("%s current2,", sql);

      sql = String.format("%s FROM pv_history WHERE epoch_ms >= %d AND epoch_ms <= %d", sql,
              recordsSettings.getStartDate().getTimeInMillis(),
              recordsSettings.getEndDate().getTimeInMillis());

      System.out.println(sql);

      final ResultSet resultSet = statement.executeQuery(sql);
      final Records records = new Records();

      boolean hasNext = resultSet.next();
      Calendar currentResultSetDateTime = new GregorianCalendar();
      currentResultSetDateTime.setTimeInMillis(resultSet.getLong("epoch_ms"));
      double panel1Energy = 0, panel2Energy = 0, bothEnergy = 0;

      while (hasNext)
      {
        double panel1Voltage = Double.NaN,
                panel1Current = Double.NaN,
                panel1Power = Double.NaN,
                panel2Voltage = Double.NaN,
                panel2Current = Double.NaN,
                panel2Power = Double.NaN,
                bothPower = Double.NaN;


        if (recordsSettings.isPanel1Voltage())
          panel1Voltage = resultSet.getDouble("voltage1");

        if (recordsSettings.isPanel1Current())
          panel1Current = resultSet.getDouble("current1");

        if (recordsSettings.isPanel1Power())
          panel1Power = resultSet.getDouble("voltage1") * resultSet.getDouble("current1");

        if (recordsSettings.isPanel2Voltage())
          panel1Voltage = resultSet.getDouble("voltage2");

        if (recordsSettings.isPanel2Current())
          panel1Current = resultSet.getDouble("current2");

        if (recordsSettings.isPanel2Power())
          panel1Power = resultSet.getDouble("voltage2") * resultSet.getDouble("current2");

        if (recordsSettings.isBothPower())
          bothPower = resultSet.getDouble("voltage1") * resultSet.getDouble("current1")
                  + resultSet.getDouble("voltage2") * resultSet.getDouble("current2");

        if (resultSet.next())
        {
          Calendar nextResultSetDateTime = new GregorianCalendar();
          nextResultSetDateTime.setTimeInMillis(resultSet.getLong("epoch_ms"));

          if (currentResultSetDateTime.get(Calendar.DAY_OF_MONTH)
                  == nextResultSetDateTime.get(Calendar.DAY_OF_MONTH))
          {
            final int timeStep = (int) (nextResultSetDateTime.getTimeInMillis()
                    - currentResultSetDateTime.getTimeInMillis());

            if (recordsSettings.isPanel1Energy())
              panel1Energy += ((resultSet.getDouble("voltage1") * resultSet.getDouble("current1"))
                      * (double) timeStep);

            if (recordsSettings.isPanel2Energy())
              panel2Energy += ((resultSet.getDouble("voltage2") * resultSet.getDouble("current2"))
                      * (double) timeStep);

            if (recordsSettings.isBothEnergy())
              bothEnergy += (((resultSet.getDouble("voltage1") * resultSet.getDouble("current1"))
                      + (resultSet.getDouble("voltage2") * resultSet.getDouble("current2")))
                      * (double) timeStep);

            records.add(new Record(currentResultSetDateTime,
                    bothPower, Double.NaN,
                    panel1Voltage, panel1Current, panel1Power, Double.NaN,
                    panel2Voltage, panel2Current, panel2Power, Double.NaN));

            currentResultSetDateTime = nextResultSetDateTime;
          } else
          {
            records.add(new Record(currentResultSetDateTime,
                    bothPower, bothEnergy,
                    panel1Voltage, panel1Current, panel1Power, panel1Energy,
                    panel2Voltage, panel2Current, panel2Power, panel2Energy));

            bothEnergy = panel1Energy = panel2Energy = 0;
          }
        } else
        {
          hasNext = false;
        }
      }
      return records;
    } finally
    {
      close();
    }
  }

  public static void main(String[] args)
  {

    try
    {
      PhotovoltaicDatabase db = getInstance();

      //refreshCurrentDataSet Test:
      db.refreshCurrentDataSet(new GregorianCalendar(2016, 1, 1), 50, 50, 50, 50, 50, 50, 50);
//      db.refreshCurrentDataSet(new GregorianCalendar(2016, 1, 1), 8, 8, 8, 8, 8, 8, 8);
      final CurrentValues currentValues = db.getCurrentValues();
      if (currentValues != null)
      {
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
      }


      //getHistory Test:
      final Records records = db.getHistory(new RecordsSettings(
              new GregorianCalendar(2016, 0, 1), new GregorianCalendar(2017, 0, 1),
              true, true, true, true, true, true, true, true, true, true));

      if (records != null)
      {
        for (Record record : records.getRecords())
        {
          System.out.println(record.getDateTime().getTimeInMillis());
          System.out.println(record.getPanel1Voltage());
          System.out.println(record.getPanel1Current());
          System.out.println(record.getPanel1Power());
          System.out.println(record.getPanel1Energy());
          System.out.println(record.getPanel2Voltage());
          System.out.println(record.getPanel2Current());
          System.out.println(record.getPanel2Power());
          System.out.println(record.getPanel2Energy());
          System.out.println(record.getBothPower());
          System.out.println(record.getBothEnergy());
          System.out.println("____________________");
        }
      }

    } catch (SQLException ex)
    {
      ex.printStackTrace();
    } catch (ClassNotFoundException ex)
    {
      ex.printStackTrace();
    }
  }
}
