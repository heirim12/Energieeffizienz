package at.htlkaindorf.heirim12.energieeffizienz.database.testDatabaseComunication;

import java.util.GregorianCalendar;

import at.htlkaindorf.heirim12.energieeffizienz.database.PhotovoltaicDatabase;

/**
 * Created by richard on 24.01.2017.
 */

public class RaspberryComTester
{

  private double panel1Volt = 0;
  private double panel1Ampere = 0;
  private double panel1Watt = 0;
  private int panel1Azimuth = 0;
  private int panel1Elevation = 0;
  private double panel2Volt = 0;
  private double panel2Ampere  = 0;
  private double panel2Watt = 0;
  private int panel2Azimuth = 30;
  private int panel2Elevation = 30;
  private double voltageAccu = 0;

  private void calculate()
  {
    panel1Volt = (double) Math.round( (Math.random()*3 + 30) * 100) / 100.0;
    panel1Ampere = (double) Math.round( (Math.random()*3) * 100) / 100.0;
    panel1Watt = (panel1Volt * panel1Ampere);
    panel1Azimuth = (int) (Math.random()*90);
    panel1Elevation = (int) (Math.random()*90);

    panel2Volt = (double) Math.round( (Math.random()*3 + 30) * 100) / 100.0;
    panel2Ampere  = (double) Math.round( (Math.random()*3) * 100) / 100.0;
    panel2Watt = (panel2Volt * panel2Ampere);
    panel2Azimuth = (int) (Math.random()*90);
    panel2Elevation = (int) (Math.random()*90);

    voltageAccu = (double) Math.round( (Math.random()*3 + 10) * 100) / 100.0;
  }

  public double getPanel1Volt()
  {
    return panel1Volt;
  }

  public double getPanel1Ampere()
  {
    return panel1Ampere;
  }

  public double getPanel1Watt()
  {
    return panel1Watt;
  }

  public int getPanel1Azimuth()
  {
    return panel1Azimuth;
  }

  public int getPanel1Elevation()
  {
    return panel1Elevation;
  }

  public double getPanel2Volt()
  {
    return panel2Volt;
  }

  public double getPanel2Ampere()
  {
    return panel2Ampere;
  }

  public double getPanel2Watt()
  {
    return panel2Watt;
  }

  public int getPanel2Azimuth()
  {
    return panel2Azimuth;
  }

  public int getPanel2Elevation()
  {
    return panel2Elevation;
  }

  public double getVoltageAccu()
  {
    return voltageAccu;
  }

  public static void main(String[] args)
  {
    while (true)
    {
      try
      {
        final RaspberryComTester tester = new RaspberryComTester();
        final PhotovoltaicDatabase photovoltaicDatabase = PhotovoltaicDatabase.getInstance();
        tester.calculate();
        String dataSet = String.format("%f, %f, %d, %d, %f, %f, %f",
                tester.getPanel1Volt(), tester.getPanel1Ampere(),
                tester.getPanel1Azimuth(), tester.getPanel1Elevation(),
                tester.getPanel2Volt(), tester.getPanel2Ampere(),
                tester.getVoltageAccu());
        System.out.println(dataSet);
        photovoltaicDatabase.refreshCurrentDataSet(
                new GregorianCalendar(2016,0,24),
                tester.getPanel1Volt(), tester.getPanel1Ampere(),
                tester.getPanel1Azimuth(), tester.getPanel1Elevation(),
                tester.getPanel2Volt(), tester.getPanel2Ampere(),
                tester.getVoltageAccu());
        Thread.sleep(500);
      }
      catch (Exception ex)
      {
        System.out.println(ex.getLocalizedMessage());
      }
    }
  }
}
