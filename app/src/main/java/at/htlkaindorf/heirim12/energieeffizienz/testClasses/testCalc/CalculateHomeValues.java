package at.htlkaindorf.heirim12.energieeffizienz.testClasses.testCalc;

import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import at.htlkaindorf.heirim12.energieeffizienz.testClasses.data.HomeValues;

/**
 * Created by richard on 09.11.2016.
 */

public class CalculateHomeValues
{
  private HomeValues homeValues;

  public CalculateHomeValues()
          throws Exception
  {
    calculate();
  }

  private void calculate()
          throws Exception
  {
    double
            panel1Watt = 0,
            panel2Watt = 0,
            panel1WattHour = 0,
            panel2WattHour = 0;

    ArrayList<BarEntry> watts7Days = new ArrayList<>();
    String[] dates = new String[7];

    try
    {
      panel1Watt = (double) Math.round( (Math.random()*100 + 100) * 100) / 100.0;
      panel2Watt = (double) Math.round( (Math.random()*100 + 100) * 100) / 100.0;
      panel1WattHour = (double) Math.round( (Math.random()* 400) * 100) / 100.0;
      panel2WattHour = (double) Math.round( (Math.random()* 400) * 100) / 100.0;

      for(int i = 0 ; i < 6 ; i++)
      {
        final double wattHour = (double) Math.round( (Math.random()* 800) * 100) / 100.0;
        watts7Days.add(new BarEntry((float) i , (float) wattHour));
      }
      watts7Days.add(new BarEntry(6f , (float) (panel1WattHour+panel2WattHour)));

      dates[6] = "Today";
      dates[5] = "Yesterday";

      for(int i = 0; i < 5 ; i++)
      {
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.DATE, (-6 + i));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MMM");
        dates[i] = (dateFormat.format(calender.getTime()));
      }

      for(int i = 0; i < 7 ; i++)
      {
        System.out.println(dates[i]);
      }
    }
    catch (Exception ex)
    {
      throw new Exception("Something went wrong, no Values for the Home page!");
    }
    finally
    {
      homeValues = new HomeValues(panel1Watt, panel1WattHour, panel2Watt, panel2WattHour,
              watts7Days, dates);
    }
  }

  public HomeValues getHomeValues()
  {
    return homeValues;
  }

  public static void main(String[] args) {
    //TODO

  }
}
