package at.htlkaindorf.heirim12.energieeffizienz.data;

import java.util.Calendar;

/**
 * Created by richard on 23.01.2017.
 */

public class Records2
{
  private final Calendar dateTime;
  private double
          bothPower, bothEnergy,
          panel1Voltage, panel1Current, panel1Power, panel1Energy,
          panel2Voltage, panel2Current, panel2Power, panel2Energy,
          accuVoltage;

  public Records2(Calendar dateTime)
  {
    this.dateTime = dateTime;
  }

  public Calendar getDateTime()
  {
    return dateTime;
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

  public void setBothPower(double bothPower)
  {
    this.bothPower = bothPower;
  }

  public void setBothEnergy(double bothEnergy)
  {
    this.bothEnergy = bothEnergy;
  }

  public void setPanel1Voltage(double panel1Voltage)
  {
    this.panel1Voltage = panel1Voltage;
  }

  public void setPanel1Current(double panel1Current)
  {
    this.panel1Current = panel1Current;
  }

  public void setPanel1Power(double panel1Power)
  {
    this.panel1Power = panel1Power;
  }

  public void setPanel1Energy(double panel1Energy)
  {
    this.panel1Energy = panel1Energy;
  }

  public void setPanel2Voltage(double panel2Voltage)
  {
    this.panel2Voltage = panel2Voltage;
  }

  public void setPanel2Current(double panel2Current)
  {
    this.panel2Current = panel2Current;
  }

  public void setPanel2Power(double panel2Power)
  {
    this.panel2Power = panel2Power;
  }

  public void setPanel2Energy(double panel2Energy)
  {
    this.panel2Energy = panel2Energy;
  }
}
