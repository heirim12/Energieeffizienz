package at.htlkaindorf.heirim12.energieeffizienz.data;

import java.util.Calendar;

/**
 * Created by richard on 16.02.2017.
 */

public class EnergyRecord
{
  private final double panel1Energy, panel2Energy, bothEnergy;
  private final Calendar date;

  public EnergyRecord(double panel1Energy, double panel2Energy, double bothEnergy, Calendar date)
  {
    this.panel1Energy = panel1Energy;
    this.panel2Energy = panel2Energy;
    this.bothEnergy = bothEnergy;
    this.date = date;
  }

  public Calendar getDate()
  {
    return date;
  }

  public double getPanel1Energy()
  {
    return panel1Energy;
  }

  public double getPanel2Energy()
  {
    return panel2Energy;
  }

  public double getBothEnergy()
  {
    return bothEnergy;
  }
}
