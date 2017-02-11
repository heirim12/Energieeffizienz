package at.htlkaindorf.heirim12.energieeffizienz.data;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

/**
 * Created by richard on 28.01.2017.
 */

public class HomeValues
{
  private final double
          panel1Power,
          panel1Energy,
          panel2Power,
          panel2Energy;

  private final double energy7Days[];
  private final String[] dates;

  public HomeValues(double panel1Power, double panel1Energy,
                    double panel2Power, double panel2Energy,
                    double energy7Days[], String[] dates)
  {
    this.panel1Power = panel1Power;
    this.panel1Energy = panel1Energy;
    this.panel2Power = panel2Power;
    this.panel2Energy = panel2Energy;
    this.energy7Days = energy7Days;
    this.dates = dates;
  }

  public double getPanel1Power()
  {
    return panel1Power;
  }

  public double getPanel1Energy()
  {
    return panel1Energy;
  }

  public double getPanel2Power()
  {
    return panel2Power;
  }

  public double getPanel2Energy()
  {
    return panel2Energy;
  }

  public double[] getEnergy7Days()
  {
    return energy7Days;
  }

  public String[] getDates()
  {
    return dates;
  }
}