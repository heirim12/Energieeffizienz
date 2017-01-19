package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments;



import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.testClasses.data.CurrentMeasurementValues;
import at.htlkaindorf.heirim12.energieeffizienz.testClasses.testCalc.CalculateCurrentMeasurementValues;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCurrentMeasurement extends Fragment
{
  //================================================================================================
  // Global declarations
  //================================================================================================
  private final static int[] refreshMs = { 200, 1000, 3000, 5000, 10000 };

  private View rootView;
  private CurrentMeasurementValues currentMeasurementValues;
  private Timer timer;
  private int refreshCycle = 1;

  private static Boolean inBackground = new Boolean(false);

  private ScheduledExecutorService exe = null;
  private Future future = null;


  //================================================================================================
  // Methods for setting the gui
  //================================================================================================
  private void setText(int id, String text)
  {
    final TextView textView = (TextView) rootView.findViewById(id);
    textView.setText(text);
  }

  private void fillTextViews(CurrentMeasurementValues currentMeasurementValues)
  {
    final int textViewId[] = {
            R.id.cur_meas_textViewVolt1,
            R.id.cur_meas_textViewAmpere1,
            R.id.cur_meas_textViewWatt1,
            R.id.cur_meas_textViewAngleValue1,
            R.id.cur_meas_textViewOrientationValue1,

            R.id.cur_meas_textViewVolt2,
            R.id.cur_meas_textViewAmpere2,
            R.id.cur_meas_textViewWatt2,

            R.id.cur_meas_textViewVoltEngines,
            R.id.cur_meas_textViewAmpereEngines,
            R.id.cur_meas_textViewWattEngienes,

            R.id.cur_meas_textViewVoltBattery};

    final String textViewText[] = {
            String.format("%s%s",currentMeasurementValues.getPanel1Volt(),"V"),
            currentMeasurementValues.getPanel1Ampere() + "A",
            String.format(currentMeasurementValues.getPanel1Watt() + "W"),
            String.format(currentMeasurementValues.getPanel1Angle() + "°"),
            String.format(currentMeasurementValues.getPanel1Orientation()),

            String.format(currentMeasurementValues.getPanel2Volt() + "V"),
            String.format(currentMeasurementValues.getPanel2Ampere() + "A"),
            String.format(currentMeasurementValues.getPanel2Watt() + "W"),

            String.format(currentMeasurementValues.getEnginesVolt() + "V"),
            String.format(currentMeasurementValues.getEnginesAmpere() + "A"),
            String.format(currentMeasurementValues.getEnginesWatt() + "W"),

            String.format(currentMeasurementValues.getBatteryVolt() + "V")};

    for(int i = 0 ; i < textViewId.length ; i++)
    {
      setText(textViewId[i], textViewText[i]);
    }
  }


  //================================================================================================
  // Constructor
  //================================================================================================
  public FragmentCurrentMeasurement()
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
    getActivity().setTitle(getString(R.string.fragment_current_measurement_title));

    rootView = inflater.inflate(R.layout.fragment_current_measurement, container, false);
//        int refreshCycle = 1;




    final Spinner refreshCycleSpinner =
            (Spinner) rootView.findViewById(R.id.cur_meas_spinnerRefreshCycle);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
            R.array.refresh_array, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    refreshCycleSpinner.setAdapter(adapter);

    refreshCycleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
    {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
      {
        refreshCycle = (position<refreshMs.length) ? refreshMs[position] : 400;
        if (future!=null)
          future.cancel(false);
        future = exe.scheduleWithFixedDelay(
                new Runnable()
                {
                  @Override
                  public void run()
                  {
                    new UpdateInfoTask().execute();
                  }
                }, 100, refreshCycle, TimeUnit.MILLISECONDS
        );
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView)
      {

      }
    });

    //return inflater.inflate(R.layout.fragment_current_measurement, container, false);
    return rootView;
  }

  @Override
  public void onStart()
  {
    super.onStart();
    exe = Executors.newSingleThreadScheduledExecutor();
    future = exe.scheduleWithFixedDelay(
            new Runnable()
            {
              @Override
              public void run()
              {
                new UpdateInfoTask().execute();
              }
            }, refreshMs[0], refreshMs[0], TimeUnit.MILLISECONDS
    );
  }

  @Override
  public void onStop()
  {
    if (exe!=null)
      exe.shutdown();
    super.onStop();
  }


  //================================================================================================
  // Multithreading
  //================================================================================================
  private class UpdateInfoTask extends AsyncTask<Void, Void, CurrentMeasurementValues>
  {
    @Override
    protected CurrentMeasurementValues doInBackground(Void... voids)
    {
      final CurrentMeasurementValues result;
      synchronized (UpdateInfoTask.class)
      {
        if (inBackground)
          return null;
        inBackground = true;
      }
      try
      {
        CalculateCurrentMeasurementValues calculateCurrentMeasurementValues =
                new CalculateCurrentMeasurementValues();
        result = calculateCurrentMeasurementValues.getCurrentMeasurementValues();
      }
      finally
      {
        synchronized (UpdateInfoTask.class)
        {
          inBackground = false;
        }
      }

      return result;
    }

    @Override
    protected void onPostExecute(CurrentMeasurementValues currentMeasurementValues)
    {
      if (currentMeasurementValues == null)
        return;
      super.onPostExecute(currentMeasurementValues);
      fillTextViews(currentMeasurementValues);
    }
  }
}