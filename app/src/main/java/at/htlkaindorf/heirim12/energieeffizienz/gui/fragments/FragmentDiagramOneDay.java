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
import at.htlkaindorf.heirim12.energieeffizienz.gui.dialogs.DialogRecordsSettingsOneDay;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.diagramFormatter.SpecificValueMarkerView;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.diagramFormatter.XAxisDateFormatter;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.diagramFormatter.YAxisUnitValueFormatter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDiagramOneDay extends Fragment
        implements DialogRecordsSettingsOneDay.OnRecordsSettingsOKListener
{
  //================================================================================================
  // Global declarations
  //================================================================================================
  private View thisFragment;
  private LineChart lineChart1 = null;
  private LineChart lineChart2 = null;
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
    lineDataSet.setDrawCircles(false);
    lineDataSets.add(lineDataSet);
  }

  private void createDiagram(Records records)
  {
    this.records = records;
//    final Calendar firstDate = records.get(0).getDateTime();
//    final Calendar lastDate = records.get(records.getSize() - 1).getDateTime();
//    final long referenceTime = firstDate.getDateTimeInMillis() / 1000;

    long firstDate = (records.get(0).getDateTimeInMillis());
    long lastDate = (records.get(records.getSize() - 1).getDateTimeInMillis());
    final long referenceTime = firstDate / 1000; // all Values formatted in Seconds => the number wont get so hight => better performance with the chart

    // initialize the LineChart and the yAxis which are necessary for creating the dataSets
    lineChart1 = new LineChart(getContext());
    final YAxis yAxisLeft1 = lineChart1.getAxisLeft();
    yAxisLeft1.setEnabled(false);
    String yAxisLeft1Unit = "";
    final YAxis yAxisRight1 = lineChart1.getAxisRight();
    yAxisRight1.setEnabled(false);
    String yAxisRight1Unit = "";

    lineChart2 = new LineChart(getContext());
    final YAxis yAxisLeft2 = lineChart2.getAxisLeft();
    yAxisLeft2.setEnabled(false);
    String yAxisLeft2Unit = "";
    final YAxis yAxisRight2 = lineChart2.getAxisRight();
    yAxisRight2.setEnabled(false);
    String yAxisRight2Unit = "";

    // initialize the dataSets which are necessary for setting the values in the chart
    final List<ILineDataSet> dataSets1 = new ArrayList<>();
    final List<ILineDataSet> dataSets2 = new ArrayList<>();

    final List<Entry> panel1VoltageEntries = new ArrayList<>();
    final List<Entry> panel2VoltageEntries = new ArrayList<>();
    final List<Entry> panel1CurrentEntries = new ArrayList<>();
    final List<Entry> panel2CurrentEntries = new ArrayList<>();
    final List<Entry> panel1PowerEntries = new ArrayList<>();
    final List<Entry> panel2PowerEntries = new ArrayList<>();
    final List<Entry> bothPanelsPowerEntries = new ArrayList<>();

    // save the x,y pair which are requested
    for (Record record : records.getRecords())
    {
//      long timeStep = ((record.getDateTime().getDateTimeInMillis()) / 1000) - referenceTime;
      long timeStep = ((record.getDateTimeInMillis()) / 1000) - referenceTime;
      if (recordsSettings.isPanel1Voltage())
      {
        panel1VoltageEntries.add(new Entry((float) timeStep, (float) record.getPanel1Voltage()));
      }

      if (recordsSettings.isPanel1Current())
      {
        panel1CurrentEntries.add(new Entry((float) timeStep, (float) record.getPanel1Current()));
      }

      if (recordsSettings.isPanel1Power())
      {
        panel1PowerEntries.add(new Entry((float) timeStep, (float) record.getPanel1Power()));
      }

      if (recordsSettings.isPanel2Voltage())
      {
        panel2VoltageEntries.add(new Entry((float) timeStep, (float) record.getPanel2Voltage()));
      }

      if (recordsSettings.isPanel2Current())
      {
        panel2CurrentEntries.add(new Entry((float) timeStep, (float) record.getPanel2Current()));
      }

      if (recordsSettings.isPanel2Power())
      {
        panel2PowerEntries.add(new Entry((float) timeStep, (float) record.getPanel2Power()));
      }

      if (recordsSettings.isBothPower())
      {
        bothPanelsPowerEntries.add(new Entry((float) timeStep, (float) record.getBothPower()));
      }
    }

    // but them in the right dataSet with the right settings
    if (recordsSettings.isPanel1Voltage())
    {
      yAxisLeft1Unit = getString(R.string.fragment_diagram_voltage_unit);
      addLineDataSet(dataSets1, panel1VoltageEntries, R.string.fragment_diagram_voltage_panel1,
              R.color.colorDiagramVoltage1, YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);
    }

    if (recordsSettings.isPanel2Voltage())
    {
      yAxisLeft1Unit = getString(R.string.fragment_diagram_voltage_unit);
      addLineDataSet(dataSets1, panel2VoltageEntries, R.string.fragment_diagram_voltage_panel2,
              R.color.colorDiagramVoltage2, YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);
    }

    if (recordsSettings.isPanel1Current())
    {
      if (recordsSettings.isPanel1Voltage() || recordsSettings.isPanel2Voltage())
      {
        yAxisRight1Unit = getString(R.string.fragment_diagram_current_unit);
        addLineDataSet(dataSets1, panel1CurrentEntries, R.string.fragment_diagram_current_panel1,
                R.color.colorDiagramCurrent1, YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
      } else
      {
        yAxisLeft1Unit = getString(R.string.fragment_diagram_current_unit);
        addLineDataSet(dataSets1, panel1CurrentEntries, R.string.fragment_diagram_current_panel1,
                R.color.colorDiagramCurrent1, YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);

      }
    }

    if (recordsSettings.isPanel2Current())
    {
      if (recordsSettings.isPanel1Voltage() || recordsSettings.isPanel2Voltage())
      {
        yAxisRight1Unit = getString(R.string.fragment_diagram_current_unit);
        addLineDataSet(dataSets1, panel2CurrentEntries, R.string.fragment_diagram_current_panel2,
                R.color.colorDiagramCurrent2, YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);

      } else
      {
        yAxisLeft1Unit = getString(R.string.fragment_diagram_current_unit);
        addLineDataSet(dataSets1, panel2CurrentEntries, R.string.fragment_diagram_current_panel2,
                R.color.colorDiagramCurrent2, YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);
      }
    }

    if (recordsSettings.isPanel1Power())
    {
      if (recordsSettings.isPanel1Voltage() || recordsSettings.isPanel2Voltage())
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          yAxisLeft2Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets2, panel1PowerEntries, R.string.fragment_diagram_power_panel1,
                  R.color.colorDiagramPower1, YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);

        } else
        {
          yAxisRight1Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets1, panel1PowerEntries, R.string.fragment_diagram_power_panel1,
                  R.color.colorDiagramPower1, YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
        }
      } else
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          yAxisRight1Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets1, panel1PowerEntries, R.string.fragment_diagram_power_panel1,
                  R.color.colorDiagramPower1, YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
        } else
        {
          yAxisLeft1Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets1, panel1PowerEntries, R.string.fragment_diagram_power_panel1,
                  R.color.colorDiagramPower1, YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);

        }
      }
    }

    if (recordsSettings.isPanel2Power())
    {
      if (recordsSettings.isPanel1Voltage() || recordsSettings.isPanel2Voltage())
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          yAxisLeft2Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets2, panel2PowerEntries, R.string.fragment_diagram_power_panel2,
                  R.color.colorDiagramPower2, YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);

        } else
        {
          yAxisRight1Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets1, panel2PowerEntries, R.string.fragment_diagram_power_panel2,
                  R.color.colorDiagramPower2, YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);

        }
      } else
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          yAxisRight1Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets1, panel2PowerEntries, R.string.fragment_diagram_power_panel2,
                  R.color.colorDiagramPower2, YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);

        } else
        {
          yAxisLeft1Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets1, panel2PowerEntries, R.string.fragment_diagram_power_panel2,
                  R.color.colorDiagramPower2, YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);

        }
      }
    }

    if (recordsSettings.isBothPower())
    {
      if (recordsSettings.isPanel1Voltage() || recordsSettings.isPanel2Voltage())
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          yAxisLeft2Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets2, bothPanelsPowerEntries,
                  R.string.fragment_diagram_power_both_panels, R.color.colorDiagramPowerBoth,
                  YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);
        } else
        {
          yAxisRight1Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets1, bothPanelsPowerEntries,
                  R.string.fragment_diagram_power_both_panels, R.color.colorDiagramPowerBoth,
                  YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
        }
      } else
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          yAxisRight1Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets1, bothPanelsPowerEntries,
                  R.string.fragment_diagram_power_both_panels, R.color.colorDiagramPowerBoth,
                  YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
        } else
        {
          yAxisLeft1Unit = getString(R.string.fragment_diagram_power_unit);
          addLineDataSet(dataSets1, bothPanelsPowerEntries,
                  R.string.fragment_diagram_power_both_panels, R.color.colorDiagramPowerBoth,
                  YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);
        }
      }
    }

    final LinearLayout layout =
            (LinearLayout) thisFragment.findViewById(R.id.fragment_diagram_mainLinearLayout);
    final LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams
            (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    chartParams.weight = 1;

    // set the data and style the charts
    final XAxis xAxis1 = lineChart1.getXAxis();
    xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
    xAxis1.setValueFormatter(new XAxisDateFormatter(firstDate, lastDate, referenceTime));
    lineChart1.setData(new LineData(dataSets1));
    lineChart1.setLayoutParams(chartParams);
    lineChart1.setMarker(new SpecificValueMarkerView(this.getContext(),
            R.layout.diagram_marker_view, referenceTime, lastDate,
            yAxisLeft1Unit, yAxisRight1Unit));
    lineChart1.getLegend().setWordWrapEnabled(true);
    lineChart1.getDescription().setEnabled(false);
    lineChart1.setAutoScaleMinMaxEnabled(true);
    lineChart1.setKeepPositionOnRotation(true);
    lineChart1.invalidate();
    lineChart1.animateXY(2000, 2000);
    layout.removeAllViews();
    layout.addView(lineChart1);

    if (dataSets2.size() != 0)
    {
      final XAxis xAxis2 = lineChart2.getXAxis();
      xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
      xAxis2.setValueFormatter(new XAxisDateFormatter(firstDate, lastDate, referenceTime));
      lineChart2.setData(new LineData(dataSets2));
      lineChart2.setLayoutParams(chartParams);
      lineChart2.setMarker(new SpecificValueMarkerView(this.getContext(),
              R.layout.diagram_marker_view, referenceTime, lastDate,
              yAxisLeft2Unit, yAxisRight2Unit));
      lineChart2.getLegend().setWordWrapEnabled(true);
      lineChart2.getDescription().setEnabled(false);
      lineChart2.setAutoScaleMinMaxEnabled(true);
      lineChart2.setKeepPositionOnRotation(true);
      lineChart2.invalidate();
      lineChart2.animateXY(2000, 2000);
      layout.addView(lineChart2);
    } else
    {
      lineChart2 = null;
    }
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
      if (lineChart2 != null)
      {
        builder.setTitle(getString(R.string.fragment_diagram_chooser_dialog_save_2diagram));
        final String[] chartsList = {getString(R.string.fragment_diagram_chooser_dialog_diagram1),
                getString(R.string.fragment_diagram_chooser_dialog_diagram2)};
        builder.setSingleChoiceItems(chartsList, 0, null);
      } else
      {
        builder.setTitle(getString(R.string.fragment_diagram_chooser_dialog_save_1diagram));
      }

      builder.setPositiveButton(R.string.dialog_diagram_ok, new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
          if (lineChart2 != null)
          {
            if (((AlertDialog) dialogInterface).getListView().getCheckedItemPosition() == 0)
              saveChart(lineChart1, filename.getText().toString());
            else
              saveChart(lineChart2, filename.getText().toString());
          } else
          {
            saveChart(lineChart1, filename.getText().toString());
          }

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
      final File cachePath = new File(getContext().getCacheDir(), "images");
      cachePath.mkdir();
      final FileOutputStream stream = new FileOutputStream(cachePath + "/image.jpeg");
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
      if (lineChart2 != null)
      {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String[] chartsList = {getString(R.string.fragment_diagram_chooser_dialog_diagram1),
                getString(R.string.fragment_diagram_chooser_dialog_diagram2)};
        builder.setTitle(getString(R.string.fragment_diagram_chooser_dialog_share));
        builder.setSingleChoiceItems(chartsList, 0, null);
        builder.setPositiveButton(R.string.dialog_diagram_ok, new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialogInterface, int i)
          {
            if (((AlertDialog) dialogInterface).getListView().getCheckedItemPosition() == 0)
              shareChart(lineChart1);
            else
              shareChart(lineChart2);
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
      {
        shareChart(lineChart1);
      }
    } else
        showSnackbar(getString(R.string.fragment_diagram_no_chart));
  }


  //================================================================================================
  // Methods for opening the settings dialog and getting the settings
  //================================================================================================
  //This Method is called when the DialogSettings Object is closed with pressed with "ok".
  @Override
  public void onRecordsSettingsOneDayOKListener(RecordsSettings recordsSettings)
  {
    System.out.println("_______________________________________________________________________");
    if (recordsSettings.equals(this.recordsSettings))
      showSnackbar(getString(R.string.fragment_diagram_settings_hasnot_changed));
    else
    {
      this.recordsSettings = recordsSettings;
      showSnackbar(getString(R.string.fragment_diagram_settings_has_changed));

      final LinearLayout layout =
              (LinearLayout) thisFragment.findViewById(R.id.fragment_diagram_mainLinearLayout);
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
    final DialogFragment dialogSettings = new DialogRecordsSettingsOneDay();
    final Bundle recordSettingsBundle = new Bundle();
    if(recordsSettings != null)
    {
      recordSettingsBundle.putBoolean("panel1Voltage", recordsSettings.isPanel1Voltage());
      recordSettingsBundle.putBoolean("panel1Current", recordsSettings.isPanel1Current());
      recordSettingsBundle.putBoolean("panel1Power", recordsSettings.isPanel1Power());
      recordSettingsBundle.putBoolean("panel2Voltage", recordsSettings.isPanel2Voltage());
      recordSettingsBundle.putBoolean("panel2Current", recordsSettings.isPanel2Current());
      recordSettingsBundle.putBoolean("panel2Power", recordsSettings.isPanel2Power());
      recordSettingsBundle.putBoolean("bothPower", recordsSettings.isBothPower());
      recordSettingsBundle.putLong("date", recordsSettings.getStartDate().getTimeInMillis());
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
  public FragmentDiagramOneDay()
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
      thisFragment = inflater.inflate(R.layout.fragment_diagram_one_day, container, false);
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
        ex.printStackTrace();
        result = new Records(ex);
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
      if (records.getException() == null)
        createDiagram(records);
      else
      {
        showSnackbar("Error: "+records.getException().getLocalizedMessage());
        final LinearLayout layout = (LinearLayout)
                thisFragment.findViewById(R.id.fragment_diagram_mainLinearLayout);
        layout.removeAllViews();
        layout.addView(getLayoutInflater(null).
                inflate(R.layout.fragment_diagram_one_day, null, false));
      }
    }
  }
}
