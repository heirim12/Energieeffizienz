package at.htlkaindorf.heirim12.energieeffizienz.testClasses.data;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

/**
 * Created by richard on 09.11.2016.
 */

public class HomeValues
{
  private final double
          panel1Watt,
          panel1WattHour,
          panel2Watt,
          panel2WattHour;

  private final ArrayList<BarEntry> watts7Days;
  private final String[] dates;

  public HomeValues(double panel1Watt, double panel1WattHour, double panel2Watt,
                    double panel2WattHour, ArrayList<BarEntry> watts7Days, String[] dates)
  {
    this.panel1Watt = panel1Watt;
    this.panel1WattHour = panel1WattHour;
    this.panel2Watt = panel2Watt;
    this.panel2WattHour = panel2WattHour;
    this.watts7Days = watts7Days;
    this.dates = dates;
  }

  public String[] getDates()
  {
    return dates;
  }

  public ArrayList<BarEntry> getWatts7Days()
  {
    return watts7Days;
  }

  public double getPanel1Watt()
  {
    return panel1Watt;
  }

  public double getPanel1WattHour()
  {
    return panel1WattHour;
  }

  public double getPanel2Watt()
  {
    return panel2Watt;
  }

  public double getPanel2WattHour()
  {
    return panel2WattHour;
  }
}
