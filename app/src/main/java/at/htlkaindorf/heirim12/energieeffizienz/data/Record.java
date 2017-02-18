package at.htlkaindorf.heirim12.energieeffizienz.data;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by richard on 29.12.2016.
 */

public class Record
{
  private final long timeInMillis;
  //private final Calendar dateTime;
  private final double
                        bothPower, bothEnergy,
                        panel1Voltage, panel1Current, panel1Power, panel1Energy,
                        panel2Voltage, panel2Current, panel2Power, panel2Energy;


  public Record(Calendar dateTime, double bothPower, double bothEnergy,
                double panel1Voltage, double panel1Current, double panel1Power, double panel1Energy,
                double panel2Voltage, double panel2Current, double panel2Power, double panel2Energy)
  {
//    this.dateTime = dateTime;
    this.timeInMillis = dateTime.getTimeInMillis();
    this.bothPower = bothPower;
    this.bothEnergy = bothEnergy;
    this.panel1Voltage = panel1Voltage;
    this.panel1Current = panel1Current;
    this.panel1Power = panel1Power;
    this.panel1Energy = panel1Energy;
    this.panel2Voltage = panel2Voltage;
    this.panel2Current = panel2Current;
    this.panel2Power = panel2Power;
    this.panel2Energy = panel2Energy;
  }



//  public Calendar getDateTime()
//  {
//    return dateTime;
//  }

  public long getTimeInMillis()
  {
    return timeInMillis;
  }

  public double getBothPower()
  {
    return bothPower;
  }

  public double getBothEnergy()
  {
    return bothEnergy;
  }

  public double getPanel1Voltage()
  {
    return panel1Voltage;
  }

  public double getPanel1Current()
  {
    return panel1Current;
  }

  public double getPanel1Power()
  {
    return panel1Power;
  }

  public double getPanel1Energy()
  {
    return panel1Energy;
  }

  public double getPanel2Voltage()
  {
    return panel2Voltage;
  }

  public double getPanel2Current()
  {
    return panel2Current;
  }

  public double getPanel2Power()
  {
    return panel2Power;
  }

  public double getPanel2Energy()
  {
    return panel2Energy;
  }
}
