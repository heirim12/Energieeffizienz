package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.data.Record;
import at.htlkaindorf.heirim12.energieeffizienz.data.Records;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettings;
import at.htlkaindorf.heirim12.energieeffizienz.database.PhotovoltaicDatabase;
import at.htlkaindorf.heirim12.energieeffizienz.gui.dialogs.DialogRecordsEnergySettings;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.diagramFormatter.SpecificValueMarkerView;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.diagramFormatter.XAxisDateFormatter;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.diagramFormatter.YAxisUnitValueFormatter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDiagramEnergy extends Fragment
        implements DialogRecordsEnergySettings.OnRecordsSettingsOKListener
{
  //================================================================================================
  // Global declarations
  //================================================================================================
  private View thisFragment;
  private LineChart lineChart1 = null;
  private ExecutorService executor = null;
  private RecordsSettings recordsSettings = null;
  private Records records;

  //================================================================================================
  // Helping Methods
  //================================================================================================
  private void showSnackbar(String text)
  {
    Snackbar.make(getActivity().findViewById(android.R.id.content),
            text, Snackbar.LENGTH_LONG).show();
  }

  //================================================================================================
  // Methods and classes for creating the diagram/s
  //================================================================================================
  private void addLineDataSet(List<ILineDataSet> lineDataSets, List<Entry> entries,
                              int entriesName, int entriesColor,
                              YAxis.AxisDependency axisDependency, YAxis yAxis,
                              String entriesUnit)
  {
    LineDataSet lineDataSet = new LineDataSet(entries, getString(entriesName));
    lineDataSet.setColor(ContextCompat.getColor(getContext(), entriesColor));
    lineDataSet.setAxisDependency(axisDependency);
    lineDataSet.setHighLightColor(ContextCompat.getColor(getContext(),
            R.color.colorDiagramHighlighting));
    lineDataSet.setDrawValues(false);
    lineDataSet.setLineWidth(lineDataSet.getLineWidth()); //makes the line thicker => don´t know why
    yAxis.setEnabled(true);
    yAxis.setValueFormatter(new YAxisUnitValueFormatter(entriesUnit));
    if (entries.size() < 4)
      lineDataSet.setDrawCircles(true);
    else
      lineDataSet.setDrawCircles(false);
    lineDataSets.add(lineDataSet);
  }

  private void createDiagram(Records records)
  {
    this.records = records;
//    final Calendar firstDate = records.get(0).getDateTime();
//    final Calendar lastDate = records.get(records.getSize() - 1).getDateTime();
//    final long referenceTime = firstDate.getDateTimeInMillis() / 1000;

    final long firstDateInMillis = (records.get(0).getDateTimeInMillis());
    final long lastDateInMillis = (records.get(records.getSize() - 1).getDateTimeInMillis());
    final long referenceTimeInSeconds = firstDateInMillis / 1000; //all Values formatted in Seconds => the number wont get so hight => better performance with the chart

    // initialize the LineChart and the yAxis which are necessary for creating the dataSets
    lineChart1 = new LineChart(getContext());
    final YAxis yAxisLeft1 = lineChart1.getAxisLeft();
    yAxisLeft1.setEnabled(true);
    final YAxis yAxisRight = lineChart1.getAxisRight();
    yAxisRight.setEnabled(false);
    String yAxisLeft1Unit = getString(R.string.fragment_diagram_energy_unit);

    final List<ILineDataSet> dataSets1 = new ArrayList<>();

    final List<Entry> panel1EnergyEntries = new ArrayList<>();
    final List<Entry> panel2EnergyEntries = new ArrayList<>();
    final List<Entry> bothPanelsEnergyEntries = new ArrayList<>();

    // save the x,y pair which are requested
    for (Record record : records.getRecords())
    {
//      long timeStep = ((record.getDateTime().getDateTimeInMillis()) / 1000) - referenceTime;
      long timeStep = ((record.getDateTimeInMillis()) / 1000) - referenceTimeInSeconds; //all Values formatted in Seconds => the number wont get so hight => better performance with the chart
      if (recordsSettings.isPanel1Energy())
      {
        panel1EnergyEntries.add(new Entry((float) timeStep, (float) record.getPanel1Energy()));
      }

      if (recordsSettings.isPanel2Energy())
      {
        panel2EnergyEntries.add(new Entry((float) timeStep, (float) record.getPanel2Energy()));
      }

      if (recordsSettings.isBothEnergy())
      {
        bothPanelsEnergyEntries.add(new Entry((float) timeStep, (float) record.getBothEnergy()));
      }
    }

    if (recordsSettings.isPanel1Energy())
    {
      addLineDataSet(dataSets1, panel1EnergyEntries, R.string.fragment_diagram_energy_panel1,
              R.color.colorDiagramEnergy1, YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);
    }

    if (recordsSettings.isPanel2Energy())
    {
      addLineDataSet(dataSets1, panel2EnergyEntries, R.string.fragment_diagram_energy_panel2,
              R.color.colorDiagramEnergy2, YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);
    }

    if (recordsSettings.isBothEnergy())
    {
      addLineDataSet(dataSets1, bothPanelsEnergyEntries,
              R.string.fragment_diagram_energy_both_panels, R.color.colorDiagramEnergyBoth,
              YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);
    }

    final LinearLayout layout =
            (LinearLayout) thisFragment.findViewById(R.id.fragment_diagram_energy_mainLinearLayout);
    final LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams
            (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    lineChart1.setLayoutParams(chartParams);

    //set Data
    lineChart1.setData(new LineData(dataSets1));

    //style the charts
    final XAxis xAxis1 = lineChart1.getXAxis();
    xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
    xAxis1.setValueFormatter(new XAxisDateFormatter(firstDateInMillis,
            lastDateInMillis, referenceTimeInSeconds));

    lineChart1.setMarker(new SpecificValueMarkerView(this.getContext(),
            R.layout.diagram_marker_view, referenceTimeInSeconds, lastDateInMillis,
            yAxisLeft1Unit, ""));
    lineChart1.getLegend().setWordWrapEnabled(true);
    lineChart1.getDescription().setEnabled(false);
    lineChart1.setAutoScaleMinMaxEnabled(true);
    lineChart1.setKeepPositionOnRotation(true);
    lineChart1.invalidate();
    lineChart1.animateXY(2000, 2000);
    layout.removeAllViews();
    layout.addView(lineChart1);
  }


  //================================================================================================
  // Methods for saving the diagram
  //================================================================================================
  private void saveChart(LineChart chart, String filename)
  {
    if (chart.saveToGallery(filename, getString(R.string.app_name), "",
            Bitmap.CompressFormat.JPEG, 100))
      showSnackbar(getString(R.string.fragment_diagram_saved));
    else
      showSnackbar(getString(R.string.fragment_diagram_save_error));
  }

  private void saveChartSettings()
  {
    if (lineChart1 != null)
    {
      final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
      final EditText filename = new EditText(getContext());
      filename.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT));

      builder.setView(filename);
      builder.setTitle(getString(R.string.fragment_diagram_chooser_dialog_save_1diagram));

      builder.setPositiveButton(R.string.dialog_diagram_ok, new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
          saveChart(lineChart1, filename.getText().toString());
        }
      });
      builder.setNegativeButton(R.string.dialog_diagram_cancel, new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
        }
      }); //ignore
      AlertDialog chooserDialog = builder.create();
      chooserDialog.show();
    } else
      showSnackbar(getString(R.string.fragment_diagram_no_chart));
  }


  //================================================================================================
  // Methods for sharing the diagram
  //================================================================================================
  private void saveBitmap(LineChart chart)
  {
    try
    {
      File cachePath = new File(getContext().getCacheDir(), "images");
      cachePath.mkdir();
      FileOutputStream stream = new FileOutputStream(cachePath + "/image.jpeg");
      chart.getChartBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
      stream.close();
    } catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }

  private void shareChart(LineChart chart)
  {
    saveBitmap(chart);
    File imagePath = new File(getContext().getCacheDir(), "images");
    File newFile = new File(imagePath, "image.jpeg");
    Uri contentUri = FileProvider.getUriForFile(getContext(),
            "at.htlkaindorf.heirim12.energieeffizienz.fileprovider", newFile);

    if (contentUri != null)
    {
      final Intent shareIntent = new Intent();
      shareIntent.setAction(Intent.ACTION_SEND);
      shareIntent.setDataAndType(contentUri, getContext().getContentResolver().getType(contentUri));
      shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
      startActivity(Intent.createChooser(shareIntent, "Test"));
    } else
    {
      showSnackbar(getString(R.string.fragment_diagram_share_error));
    }
  }

  private void chooseChartToShare()
  {
    if (lineChart1 != null)
    {
      shareChart(lineChart1);
    } else
      showSnackbar(getString(R.string.fragment_diagram_no_chart));
  }

  //================================================================================================
  // Methods for opening the settings dialog and getting the settings
  //================================================================================================
  //This Method is called when the DialogSettings Object is closed with pressed with "ok".
  @Override
  public void onRecordsSettingsOKListener(RecordsSettings recordsSettings)
  {
    System.out.println("_______________________________________________________________________");
    if (recordsSettings.equals(this.recordsSettings))
      showSnackbar(getString(R.string.fragment_diagram_settings_hasnot_changed));
    else
    {
      this.recordsSettings = recordsSettings;

      showSnackbar(getString(R.string.fragment_diagram_settings_has_changed));

      final LinearLayout layout =
              (LinearLayout) thisFragment.findViewById(R.id.fragment_diagram_energy_mainLinearLayout);
      final LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams
              (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
      progressBarParams.gravity = Gravity.CENTER_HORIZONTAL;
      layout.removeAllViews();

      final ProgressBar progressBar = new ProgressBar(getContext(), null,
              android.R.attr.progressBarStyleLarge);
      progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccentGreen),
              PorterDuff.Mode.SRC_IN);
      progressBar.setLayoutParams(progressBarParams);
      layout.addView(progressBar);

      executor = Executors.newSingleThreadExecutor();
      executor.execute(new Runnable()
      {
        @Override
        public void run()
        {
          new GetDataTask().execute();
        }
      });
    }
  }

  private void openSettings()
  {
    final DialogFragment dialogSettings = new DialogRecordsEnergySettings();
    final Bundle recordSettingsBundle = new Bundle();
    if (recordsSettings != null)
    {
      recordSettingsBundle.putBoolean("panel1Energy", recordsSettings.isPanel1Energy());
      recordSettingsBundle.putBoolean("panel2Energy", recordsSettings.isPanel2Energy());
      recordSettingsBundle.putBoolean("bothEnergy", recordsSettings.isBothEnergy());
      recordSettingsBundle.putLong("startDate", recordsSettings.getStartDate().getTimeInMillis());
      recordSettingsBundle.putLong("endDate", recordsSettings.getEndDate().getTimeInMillis());
      dialogSettings.setArguments(recordSettingsBundle);
    }
    dialogSettings.setTargetFragment(this, 0);
    dialogSettings.show(getFragmentManager(), "dialogRecordsSettings");
  }


  //================================================================================================
  // Button for the Settings (solved with a optionmenu)
  //================================================================================================
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
    inflater.inflate(R.menu.fragment_diagram_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.diagram_settings_icon:
        openSettings();
        return true;

      case R.id.diagram_share_icon:
        chooseChartToShare();
        return true;

      case R.id.diagram_save_icon:
        saveChartSettings();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }


  //================================================================================================
  // Constructor
  //================================================================================================
  public FragmentDiagramEnergy()
  {
    // Required empty public constructor
  }


  //================================================================================================
  // Lifecycle
  //================================================================================================
  public void onSaveInstanceState(Bundle savedInstanceState)
  {
    super.onSaveInstanceState(savedInstanceState);
  }

  // this method is only called once for this fragment because of the setRetainInstance Method
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    // onDestroy will not be called => data will be saved
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    if (records == null)
    {
      thisFragment = inflater.inflate(R.layout.fragment_fragment_diagram_energy, container, false);
      getActivity().setTitle(getString(R.string.fragment_diagram_title));
      setHasOptionsMenu(true);
    }

    return thisFragment;
  }

  @Override
  public void onStop()
  {
    //TODO:
    if (executor != null)
      executor.shutdown();
    super.onStop();
  }


  //================================================================================================
  // Multithreading
  //================================================================================================
  private class GetDataTask extends AsyncTask<Void, Void, Records>
  {
    @Override
    protected Records doInBackground(Void... voids)
    {
      Records result = null;
      try
      {
        final PhotovoltaicDatabase photovoltaicDatabase = PhotovoltaicDatabase.getInstance();
        result = photovoltaicDatabase.getHistory(recordsSettings);
      } catch (Exception ex)
      {
        System.out.println("___________________________________________________________________");
        ex.printStackTrace();
        System.out.println(ex.getLocalizedMessage());
        System.out.println("___________________________________________________________________");
      }
      return result;
    }

    @Override
    protected void onPostExecute(Records records)
    {
      if (executor.isShutdown())
        return;
      if (records == null)
        return;
      super.onPostExecute(records);
      createDiagram(records);
    }
  }
}
