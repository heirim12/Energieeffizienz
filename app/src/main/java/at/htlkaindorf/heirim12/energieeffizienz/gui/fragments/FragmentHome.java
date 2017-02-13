package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.data.HomeValues;
import at.htlkaindorf.heirim12.energieeffizienz.database.PhotovoltaicDatabase;



/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment
{
  //================================================================================================
  // Global declarations
  //================================================================================================
  private View rootView;
  private ExecutorService executor = null;
  private BarChart barChart = null;


  //================================================================================================
  // Methods for setting the gui
  //================================================================================================
  private void setText(int id, String text)
  {
    final TextView textView = (TextView) rootView.findViewById(id);
    textView.setText(text);
  }

  private void fillTextViews(HomeValues homeValues)
  {
    final int textViewId[] = {
            R.id.home_textViewWatt1,
            R.id.home_textViewWattHour1,
            R.id.home_textViewWatt2,
            R.id.home_textViewWattHour2};

    final String textViewText[] = {
            String.format("%.2fW",homeValues.getPanel1Power()),
            String.format("%.2fWh",homeValues.getPanel1Energy()),
            String.format("%.2fW",homeValues.getPanel2Power()),
            String.format("%.2fWh",homeValues.getPanel1Energy())};

    for (int i = 0; i < textViewId.length; i++)
    {
      setText(textViewId[i], textViewText[i]);
    }
  }

  private class MyCustomFormatter implements IAxisValueFormatter
  {

    private String[] xAxisStrings;

    public MyCustomFormatter(String[] xAxisStrings)
    {
      this.xAxisStrings = xAxisStrings;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
      return xAxisStrings[(int) value];
    }

    @Override
    public int getDecimalDigits()
    {
      return 0;
    }
  }

  private void makeBarChart(HomeValues homeValues, BarChart barChart)
  {
    ArrayList<BarEntry> energy7Days = new ArrayList<>();
    for(int i = 0; i < homeValues.getEnergy7Days().length; i++)
    {
      energy7Days.add(new BarEntry((float) i, (float) (homeValues.getEnergy7Days())[i]));
    }
    BarDataSet barDataSet = new BarDataSet(energy7Days, "energy of both panels");
    barDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorHomeChart));
    BarData theData = new BarData(barDataSet);
    barChart.setData(theData);

    barChart.setTouchEnabled(false);
    barChart.setDescription(null);
    barChart.getLegend().setEnabled(false);
    barChart.setExtraBottomOffset(20);

    YAxis yAxis = barChart.getAxisLeft();
    yAxis.setAxisMinimum(0f);
    yAxis.setTextSize(10f);
    yAxis.setTextColor(Color.BLACK);
    yAxis.setDrawAxisLine(true);
    yAxis.setDrawGridLines(true); // grid lines
    barChart.getAxisRight().setEnabled(false); // no right axis

    XAxis xAxis = barChart.getXAxis();
    xAxis.setTextSize(10f);
    xAxis.setTextColor(Color.BLACK);
    xAxis.setDrawAxisLine(true);
    xAxis.setDrawGridLines(true);
    // set a custom value formatter
    xAxis.setValueFormatter(new MyCustomFormatter(homeValues.getDates()));
    xAxis.setLabelRotationAngle(25);
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

    barChart.setFitBars(true); // make the x-axis fit exactly all bars
    barChart.animateXY(2000,2000); // animate vertical 3000 milliseconds
    //barChart.invalidate(); // refresh
  }


  //================================================================================================
  // Constructor
  //================================================================================================
  public FragmentHome()
  {
    // Required empty public constructor
  }

  //================================================================================================
  // Lifecycle
  //================================================================================================
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    // Inflate the layout for this fragment
    rootView = inflater.inflate(R.layout.fragment_home, container, false);
    getActivity().setTitle(getString(R.string.fragment_home_title));
    barChart = (BarChart) rootView.findViewById(R.id.home_barChart);

    return rootView;
  }

  @Override
  public void onStart()
  {
    super.onStart();
    executor = Executors.newSingleThreadExecutor();
    executor.execute(
            new Runnable()
            {
              @Override
              public void run()
              {
                new GetInfoTask().execute();
              }
            });
  }

  @Override
  public void onStop()
  {
    if (executor != null)
      executor.shutdown();
    super.onStop();
  }


  //================================================================================================
  // Multithreading
  //================================================================================================
  private class GetInfoTask extends AsyncTask<Void, Void, HomeValues>
  {
    @Override
    protected HomeValues doInBackground(Void... voids)
    {
      HomeValues result = null;
      try
      {
//        CalculateHomeValues calculateHomeValues =
//                new CalculateHomeValues();
//        result = calculateHomeValues.getHomeValues();
        final PhotovoltaicDatabase photovoltaicDatabase = PhotovoltaicDatabase.getInstance();
        result = photovoltaicDatabase.getHomeValues();
      }
      catch (Exception ex)
      {
//        Toast.makeText(getActivity(), String.format("Error: %s", ex.getLocalizedMessage()),
//                Toast.LENGTH_LONG).show();
        ex.printStackTrace();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(ex.getLocalizedMessage());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      }
      finally
      {
//        Toast.makeText(getActivity(), String.format("Error: %s", "Test!!"),
//                Toast.LENGTH_LONG).show();
      }
      return result;
    }

    @Override
    protected void onPostExecute(HomeValues homeValues)
    {
      if (executor.isShutdown())
        return;
      if (homeValues == null)
        return;
      super.onPostExecute(homeValues);
      fillTextViews(homeValues);
      makeBarChart(homeValues,barChart);
    }
  }
}
