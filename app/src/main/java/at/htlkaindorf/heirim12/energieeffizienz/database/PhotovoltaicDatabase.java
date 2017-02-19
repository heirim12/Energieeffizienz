package at.htlkaindorf.heirim12.energieeffizienz.database;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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
  private static PhotovoltaicDatabase theInstance = null; //only one instance of the object

  private PhotovoltaicDatabase(String ip, String port, String user, String password)
          throws ClassNotFoundException
  {
    super(String.format("jdbc:postgresql://%s:%s/pv", ip, port), user, password);
    Class.forName("org.postgresql.Driver");
  }

  public static synchronized PhotovoltaicDatabase getInstance()
          throws ClassNotFoundException
  {
    if (theInstance == null)
      theInstance =
              new PhotovoltaicDatabase("127.0.0.1", "5432", "raspberry", "htl");
    return theInstance;
  }

  //After you use changeSttings you have to get a new Instance with getInstance
  public static synchronized void changeSettings(String ip, String port, String user, String password)
          throws ClassNotFoundException
  {
    theInstance = new PhotovoltaicDatabase(ip, port, user, password);
  }

  // method for saving a history-dataset into the pv_history table
  // method has been tested and works
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

  // method for refreshing the only dataset in the pv_current table
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

      // updates the pv_current table entry(s), if there is no entry one will be inserted
      if (executeUpdate(sql) == 0)
      {
        sql = String.format(Locale.ENGLISH, "INSERT INTO pv_current " +
                        "(epoch_ms, voltage1, current1, azimuth1, elevation1," +
                        "voltage2, current2, voltage_accu)" +
                        " VALUES (%d, %f, %f, %d, %d, %f, %f, %f)",
                dateTime.getTimeInMillis(), voltage1, current1, azimuth1, elevation1,
                voltage2, current2, voltageAccu);
        executeUpdate(sql);
      }
    } finally
    {
      close();
    }
  }

  // method for getting the values for the startpage of the app
  // gets all date of the last 7 days from the pv_history table
  // calculates the total energy of every day for the chart
  // calculates the average power of each panel and the received energy from today
  // method has not been tested yet
  public HomeValues getHomeValues()
          throws SQLException
  {
    try
    {
      open();

      final Calendar today = Calendar.getInstance(); // save the current date and time
      final Calendar sixDaysBefore = new GregorianCalendar(
              today.get(Calendar.YEAR),
              today.get(Calendar.MONTH),
              today.get(Calendar.DAY_OF_MONTH) - 6); // date sis days before

      try (
              // gets all entries in the pv_history table of the last six days
              final Statement statement = createStatement();
              final ResultSet resultSet =
                      statement.executeQuery(String.format("SELECT epoch_ms," +
                                      " voltage1, current1, voltage2, current2 " +
                                      "FROM pv_history WHERE epoch_ms >= %d AND epoch_ms <= %d",
                              sixDaysBefore.getTimeInMillis(), today.getTimeInMillis()));
      )
      {
        float energy7Days[] = new float[7];
        String[] dates = new String[7];
        float averagePower1 = 0;
        float averagePower2 = 0;
        float energy1 = 0;
        float energy2 = 0;
        int count = 0;

        boolean hasNext = resultSet.next();
        Calendar currentResultSetDateTime = new GregorianCalendar();
        currentResultSetDateTime.setTimeInMillis(resultSet.getLong("epoch_ms"));
        int day = 0;

        while (hasNext)
        {
          final float  power1 = (float) (resultSet.getDouble("voltage1") *
                  (float) resultSet.getDouble("current1"));
          final float  power2 = (float) (resultSet.getDouble("voltage2") *
                  (float) resultSet.getDouble("current2"));


          if (resultSet.next())
          {
            Calendar nextResultSetDateTime = new GregorianCalendar();
            nextResultSetDateTime.setTimeInMillis(resultSet.getLong("epoch_ms"));
            final long timeStep = nextResultSetDateTime.getTimeInMillis()
                    - currentResultSetDateTime.getTimeInMillis();

            // check if the next dataset has been measured on the same day
            if (currentResultSetDateTime.get(Calendar.DAY_OF_MONTH)
                    == nextResultSetDateTime.get(Calendar.DAY_OF_MONTH))
            {

              // adds the power values if they has been measured on the last day
              if (currentResultSetDateTime.get(Calendar.DAY_OF_MONTH)
                      == today.get(Calendar.DAY_OF_MONTH))
              {
                averagePower1 += power1;
                averagePower2 += power2;
                count++;
              }

              // calculates the energy from the current time to the next
              energy1 += (power1 * (double) timeStep);
              energy2 += (power2 * (double) timeStep);

              currentResultSetDateTime = nextResultSetDateTime;
            } else
            {
              // the last energy value of the day will not be considered
              // => doesn´t matter because it will be in the night
              energy7Days[day] = ((energy1 + energy2) / 3600000); // divide because we want Wh as unit
              day++;
              energy1 = energy2 = 0;
              currentResultSetDateTime = nextResultSetDateTime;
            }

          } else
          {
            averagePower1 /= count;
            averagePower2 /= count;
            energy7Days[day] = ((energy1 + energy2) / 3600000); // divide because we want Wh as unit
            hasNext = false;
          }
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MMM");
        for (int i = 0; i < 5; i++)
        {
          sixDaysBefore.add(Calendar.DATE, (i));

          dates[i] = (dateFormat.format(sixDaysBefore.getTime()));
        }
        dates[5] = "Yesterday";
        dates[6] = "Today";

        return new HomeValues
                (averagePower1, energy1 / 3600000,
                        averagePower2, energy2 / 3600000,
                        energy7Days, dates);
      }
    } finally
    {
      close();
    }
  }

  // method gets the data from the pv_current table
  // method works
  public CurrentValues getCurrentValues()
          throws SQLException
  {
    try
    {
      open();
      try (
              final Statement statement = createStatement();
              final ResultSet resultSet =
                      statement.executeQuery("SELECT * FROM pv_current");
      )
      {
        CurrentValues currentValues = null;
        if (resultSet.next())
        {
          currentValues = new CurrentValues(
                  (float) resultSet.getDouble("voltage1"), (float) resultSet.getDouble("current1"),
                  (float) resultSet.getDouble("voltage1") * (float) resultSet.getDouble("current1"),
                  resultSet.getInt("azimuth1"), resultSet.getInt("elevation1"),
                  (float) resultSet.getDouble("voltage2"), (float) resultSet.getDouble("current2"),
                  (float) resultSet.getDouble("voltage2") * (float) resultSet.getDouble("current2"),
                  (float) resultSet.getDouble("voltage_accu"));
        }
        return currentValues;
      }
    } finally
    {
      close();
    }
  }

  // method gets the relevant data from the pv_history table and calculates the requested values
  // method has not been tested yet
  public Records getHistory(RecordsSettings recordsSettings)
          throws SQLException
  {
    try
    {
      open();
      final Statement statement = createStatement();
      final Calendar endDate = new GregorianCalendar();
      endDate.setTimeInMillis(recordsSettings.getEndDate().getTimeInMillis()); // we gets a date with the time 00:00
      endDate.add(Calendar.HOUR, 23); // add time to get 23:59
      endDate.add(Calendar.MINUTE, 59);
      String sql = ("SELECT epoch_ms,");
      // builds the query dynamic for the requested values
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

      sql = sql.substring(0, sql.length() - 1); // removes the last comma
      sql = String.format("%s FROM pv_history WHERE epoch_ms >= %d AND epoch_ms <= %d", sql,
              recordsSettings.getStartDate().getTimeInMillis(),
              endDate.getTimeInMillis());

      System.out.println(sql);


      final ResultSet resultSet = statement.executeQuery(sql);
      final Records records = new Records();

      boolean hasNext = resultSet.next();
      Calendar currentResultSetDateTime = new GregorianCalendar();
      currentResultSetDateTime.setTimeInMillis(resultSet.getLong("epoch_ms"));
      float panel1Energy = 0, panel2Energy = 0, bothEnergy = 0;

      while (hasNext)
      {
        float panel1Voltage = Float.NaN,
                panel1Current = Float.NaN,
                panel1Power = Float.NaN,
                panel2Voltage = Float.NaN,
                panel2Current = Float.NaN,
                panel2Power = Float.NaN,
                bothPower = Float.NaN;

        //saves the requested data
        if (recordsSettings.isPanel1Voltage()
                || recordsSettings.isPanel1Energy() || recordsSettings.isBothEnergy())
          panel1Voltage = (float) resultSet.getDouble("voltage1");

        if (recordsSettings.isPanel1Current()
                || recordsSettings.isPanel1Energy() || recordsSettings.isBothEnergy())
          panel1Current = (float) resultSet.getDouble("current1");

        if (recordsSettings.isPanel1Power())
          panel1Power = (float) resultSet.getDouble("voltage1") * (float) resultSet.getDouble("current1");

        if (recordsSettings.isPanel2Voltage()
                || recordsSettings.isPanel2Energy() || recordsSettings.isBothEnergy())
          panel2Voltage = (float) resultSet.getDouble("voltage2");

        if (recordsSettings.isPanel2Current()
                || recordsSettings.isPanel2Energy() || recordsSettings.isBothEnergy())
          panel2Current = (float) resultSet.getDouble("current2");

        if (recordsSettings.isPanel2Power())
          panel2Power = (float) resultSet.getDouble("voltage2") * (float) resultSet.getDouble("current2");

        if (recordsSettings.isBothPower())
          bothPower = (float) resultSet.getDouble("voltage1") *
                  (float) resultSet.getDouble("current1")
                  + (float) resultSet.getDouble("voltage2") *
                  (float) resultSet.getDouble("current2");

        if (resultSet.next())
        {
          Calendar nextResultSetDateTime = new GregorianCalendar();
          nextResultSetDateTime.setTimeInMillis(resultSet.getLong("epoch_ms"));

          if (currentResultSetDateTime.get(Calendar.DAY_OF_MONTH)
                  == nextResultSetDateTime.get(Calendar.DAY_OF_MONTH))
          {
            final int timeStep = (int) ((nextResultSetDateTime.getTimeInMillis()
                    - currentResultSetDateTime.getTimeInMillis()));

            if (recordsSettings.isPanel1Energy())
              panel1Energy += ((panel1Voltage * panel1Current) * (double) timeStep);

            if (recordsSettings.isPanel2Energy())
              panel2Energy += ((panel2Voltage * panel2Current) * (double) timeStep);

            if (recordsSettings.isBothEnergy())
              bothEnergy += (((panel1Voltage * panel1Current) + (panel2Voltage * panel2Current))
                      * (double) timeStep);


            if (recordsSettings.isPanel1Voltage() ||
                    recordsSettings.isPanel1Current() ||
                    recordsSettings.isPanel1Power() ||
                    recordsSettings.isPanel2Voltage() ||
                    recordsSettings.isPanel2Current() ||
                    recordsSettings.isPanel2Power() ||
                    recordsSettings.isBothPower())
            records.add(new Record(currentResultSetDateTime.getTimeInMillis(),
                    bothPower, Float.NaN,
                    panel1Voltage, panel1Current, panel1Power, Float.NaN,
                    panel2Voltage, panel2Current, panel2Power, Float.NaN));

            currentResultSetDateTime = nextResultSetDateTime;
          } else
          {
            // the last energy value of the day will not be considered
            // => doesn´t matter because it will be in the night
              records.add(new Record(currentResultSetDateTime.getTimeInMillis(),
                      bothPower, bothEnergy / 3600000,
                      panel1Voltage, panel1Current, panel1Power, panel1Energy / 3600000,
                      panel2Voltage, panel2Current, panel2Power, panel2Energy / 3600000));
            // divide the eneryvalues because we want Wh as unit

            bothEnergy = panel1Energy = panel2Energy = 0;
            currentResultSetDateTime = nextResultSetDateTime;
          }
        } else
        {
          records.add(new Record(currentResultSetDateTime.getTimeInMillis(),
                  bothPower, bothEnergy / 3600000,
                  panel1Voltage, panel1Current, panel1Power, panel1Energy / 3600000,
                  panel2Voltage, panel2Current, panel2Power, panel2Energy / 3600000));
          // divide the energyvalues because we want Wh as unit
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
      HomeValues homeValues = db.getHomeValues();
      System.out.println(homeValues.getPanel1Power());
      System.out.println(homeValues.getPanel1Energy());
      System.out.println(homeValues.getPanel2Power());
      System.out.println(homeValues.getPanel2Energy());

      for (int i = 0; i < 7; i++)
        System.out.println(homeValues.getEnergy7Days()[i]);

      //refreshCurrentDataSet Test:
//      db.refreshCurrentDataSet(new GregorianCalendar(2016, 1, 1), 50, 50, 50, 50, 50, 50, 50);
////      db.refreshCurrentDataSet(new GregorianCalendar(2016, 1, 1), 8, 8, 8, 8, 8, 8, 8);
//      final CurrentValues currentValues = db.getCurrentValues();
//      if (currentValues != null)
//      {
//        System.out.println(currentValues.getPanel1Voltage());
//        System.out.println(currentValues.getPanel1Current());
//        System.out.println(currentValues.getPanel1Power());
//        System.out.println(currentValues.getPanel1Azimuth());
//        System.out.println(currentValues.getPanel1Elevation());
//        System.out.println(currentValues.getPanel2Voltage());
//        System.out.println(currentValues.getPanel2Current());
//        System.out.println(currentValues.getPanel2Power());
//        System.out.println(currentValues.getPanel2Azimuth());
//        System.out.println(currentValues.getPanel2Elevation());
//        System.out.println(currentValues.getAccuVoltage());
//      }
//
//
//////      getHistory Test:
//      final Records records = db.getHistory(new RecordsSettings(
//              new GregorianCalendar(2016, 0, 1), new GregorianCalendar(2016, 1, 1),
//              true, true, true, true, true, true, true, true, true, true));
//
//      if (records != null)
//      {
//        for (Record record : records.getRecords())
//        {
//
//          if (!Double.isNaN(record.getBothEnergy()))
//          {
//            System.out.println(record.getDateTime().getDateTimeInMillis());
//            System.out.println(record.getPanel1Voltage());
//            System.out.println(record.getPanel1Current());
//            System.out.println(record.getPanel1Power());
//            System.out.println(record.getPanel1Energy());
//            System.out.println(record.getPanel2Voltage());
//            System.out.println(record.getPanel2Current());
//            System.out.println(record.getPanel2Power());
//            System.out.println(record.getPanel2Energy());
//            System.out.println(record.getBothPower());
//            System.out.println(record.getBothEnergy());
//          }
//        }
//      }

    } catch (SQLException ex)
    {
      ex.printStackTrace();
    } catch (ClassNotFoundException ex)
    {
      ex.printStackTrace();
    }
  }
}
