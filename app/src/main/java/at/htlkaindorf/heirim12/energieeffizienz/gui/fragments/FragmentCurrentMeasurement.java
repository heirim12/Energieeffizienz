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

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.data.CurrentValues;
import at.htlkaindorf.heirim12.energieeffizienz.database.PhotovoltaicDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCurrentMeasurement extends Fragment
{
  //================================================================================================
  // Global declarations
  //================================================================================================
  private final static int[] refreshMs = { 200, 1000, 3000, 5000, 10000 };

  private View thisFragment;
  private int refreshCycle = 1;
  private static Boolean inBackground = new Boolean(false);
  private ScheduledExecutorService scheduledExecutorService = null;
  private Future future = null;

  //================================================================================================
  // Methods for setting the gui
  //================================================================================================
  private void setText(int id, String text)
  {
    final TextView textView = (TextView) thisFragment.findViewById(id);
    textView.setText(text);
  }

  private void fillTextViews(CurrentValues currentValues)
  {
    if(currentValues != null)
    {
      final int textViewId[] = {
              R.id.cur_meas_textViewVolt1,
              R.id.cur_meas_textViewAmpere1,
              R.id.cur_meas_textViewWatt1,
              R.id.cur_meas_textViewAzimuth1,
              R.id.cur_meas_textViewElevation1,

              R.id.cur_meas_textViewVolt2,
              R.id.cur_meas_textViewAmpere2,
              R.id.cur_meas_textViewWatt2,
              R.id.cur_meas_textViewAzimuth2,
              R.id.cur_meas_textViewOrientationValue2,

              R.id.cur_meas_textViewVoltBattery};

      final String textViewText[] = {
              String.format("%.2f%s",currentValues.getPanel1Voltage(),"V"),
              String.format("%.2f%s",currentValues.getPanel1Current(), "A"),
              String.format("%.2f%s",currentValues.getPanel1Power(), "W"),
              String.format("%d%s",currentValues.getPanel1Azimuth(), "°"),
              String.format("%d%s",currentValues.getPanel1Elevation(), "°" ),

              String.format("%.2f%s",currentValues.getPanel2Voltage(),"V"),
              String.format("%.2f%s",currentValues.getPanel2Current(), "A"),
              String.format("%.2f%s",currentValues.getPanel2Power(), "W"),
              String.format("%d%s",currentValues.getPanel2Azimuth(), "°"),
              String.format("%d%s",currentValues.getPanel2Elevation(), "°" ),

              String.format("%.2f%s",currentValues.getAccuVoltage(), "V")};

      for(int i = 0 ; i < textViewId.length ; i++)
      {
        setText(textViewId[i], textViewText[i]);
      }
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
    thisFragment = inflater.inflate(R.layout.fragment_current_measurement, container, false);
    getActivity().setTitle(getString(R.string.fragment_current_measurement_title));

    final Spinner refreshCycleSpinner =
            (Spinner) thisFragment.findViewById(R.id.cur_meas_spinnerRefreshCycle);
   // ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
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
        future = scheduledExecutorService.scheduleWithFixedDelay(
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
    return thisFragment;
  }

  @Override
  public void onStart()
  {
    super.onStart();
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    future = scheduledExecutorService.scheduleWithFixedDelay(
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
    if (scheduledExecutorService !=null)
      scheduledExecutorService.shutdown();
    super.onStop();
  }


  //================================================================================================
  // Multithreading
  //================================================================================================
  private class UpdateInfoTask extends AsyncTask<Void, Void, CurrentValues>
  {
    @Override
    protected CurrentValues doInBackground(Void... voids)
    {
      CurrentValues result = null;
      synchronized (UpdateInfoTask.class)
      {
        if (inBackground)
          return null;
        inBackground = true;
      }
      try
      {
        final PhotovoltaicDatabase photovoltaicDatabase = PhotovoltaicDatabase.getInstance();
        result = photovoltaicDatabase.getCurrentValues();
      }
      catch (SQLException ex)
      {
        ex.printStackTrace();
    //    Toast.makeText(getContext(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
      }
      catch (Exception ex)
      {
        System.out.println(ex.getLocalizedMessage());
       // Toast.makeText(getContext(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
    protected void onPostExecute(CurrentValues currentValues)
    {
      if (currentValues == null)
        return;
      super.onPostExecute(currentValues);
      fillTextViews(currentValues);
    }
  }
}