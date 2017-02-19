package at.htlkaindorf.heirim12.energieeffizienz.data;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

/**
 * Created by richard on 28.01.2017.
 */

public class HomeValues
{
  private final float
          panel1Power,
          panel1Energy,
          panel2Power,
          panel2Energy;

  private final float energy7Days[];
  private final String[] dates;

  public HomeValues(float panel1Power, float panel1Energy,
                    float panel2Power, float panel2Energy,
                    float energy7Days[], String[] dates)
  {
    this.panel1Power = panel1Power;
    this.panel1Energy = panel1Energy;
    this.panel2Power = panel2Power;
    this.panel2Energy = panel2Energy;
    this.energy7Days = energy7Days;
    this.dates = dates;
  }

  public float getPanel1Power()
  {
    return panel1Power;
  }

  public float getPanel1Energy()
  {
    return panel1Energy;
  }

  public float getPanel2Power()
  {
    return panel2Power;
  }

  public float getPanel2Energy()
  {
    return panel2Energy;
  }

  public float[] getEnergy7Days()
  {
    return energy7Days;
  }

  public String[] getDates()
  {
    return dates;
  }
}