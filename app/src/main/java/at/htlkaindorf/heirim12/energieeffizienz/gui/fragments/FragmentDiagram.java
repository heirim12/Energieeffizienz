package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.communication.ReceiveRecords;
import at.htlkaindorf.heirim12.energieeffizienz.data.Record;
import at.htlkaindorf.heirim12.energieeffizienz.data.Records;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettings;
import at.htlkaindorf.heirim12.energieeffizienz.gui.dialogs.DialogRecordsSettings;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDiagram extends Fragment
        implements DialogRecordsSettings.OnRecordsSettingsOKListener
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
  // Methods and classes for creating the diagram/s
  //================================================================================================
  private class YAxisUnitValueFormatter implements IAxisValueFormatter
  {
    private final String unit;

    public YAxisUnitValueFormatter(String unit)
    {
      this.unit = unit;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
      return String.format("%.1f%s", value, unit);
    }

    @Override
    public int getDecimalDigits()
    {
      return 0;
    }
  }

  private class XAxisDateFormatter implements IAxisValueFormatter
  {
    private final Calendar firstDate;
    private final Calendar lastDate;
    private final long referenceTime;
    private final SimpleDateFormat dateFormat;

    public XAxisDateFormatter(Calendar firstDate, Calendar lastDate, long referenceTime)
    {
      this.firstDate = firstDate;
      this.lastDate = lastDate;
      this.referenceTime = referenceTime;
      this.dateFormat = getDateFormat();
    }

    private SimpleDateFormat getDateFormat()
    {
      if ((lastDate.get(Calendar.YEAR) - firstDate.get(Calendar.YEAR)) < 1)
      {
        if ((lastDate.get(Calendar.MONTH) - firstDate.get(Calendar.MONTH)) < 1)
        {
          if ((lastDate.get(Calendar.DAY_OF_MONTH) - firstDate.get(Calendar.DAY_OF_MONTH)) < 1)
          {
            return new SimpleDateFormat("H:mm");
          } else
          {
            return new SimpleDateFormat("d.MMM");
          }
        } else
        {
          return new SimpleDateFormat("d.MMM");
        }
      } else
      {
        return new SimpleDateFormat("dd.MM.yy");
      }
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
      return dateFormat.format(new Date((referenceTime + (long) value) * 1000));
    }

    @Override
    public int getDecimalDigits()
    {
      return 0;
    }
  }

  private class SpecificValueMarkerView extends MarkerView implements IMarker
  {
    private final long referenceTime;
    private final long middleTimeStep;
    private final TextView markerViewText;
    private final SimpleDateFormat dateFormat;
    private final String leftUnit;
    private final String rightUnit;
    private Boolean diagramSide; // false = left, true = right
    private MPPointF mOffset;

    public SpecificValueMarkerView(Context context, int layoutResource, long referenceTime,
                                   Calendar lastDate, String leftUnit, String rightUnit)
    {
      super(context, layoutResource);
      markerViewText = (TextView) findViewById(R.id.test_MarkerViewTextView);
      this.referenceTime = referenceTime;
      this.middleTimeStep = ((lastDate.getTimeInMillis() / 1000) - referenceTime) / 2;
      this.dateFormat = new SimpleDateFormat("HH:mm; dd.MM.yyyy");
      this.leftUnit = leftUnit;
      this.rightUnit = rightUnit;
      this.diagramSide = false;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {
      if (e.getX() > middleTimeStep)
        diagramSide = true;
      else
        diagramSide = false;

      String text;
      if (highlight.getAxis() == YAxis.AxisDependency.LEFT)
        text = String.format("%.2f%s %s\n%s", e.getY(), leftUnit,
                getString(R.string.fragment_diagram_marker_view_at),
                dateFormat.format(new Date((referenceTime + (long) e.getX()) * 1000)));
      else
        text = String.format("%.2f%s %s\n%s", e.getY(), rightUnit,
                getString(R.string.fragment_diagram_marker_view_at),
                dateFormat.format(new Date((referenceTime + (long) e.getX()) * 1000)));

      markerViewText.setText(text);
      super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset()
    {

      if (diagramSide)
        mOffset = new MPPointF(-getWidth(), -getHeight());
      else
        mOffset = new MPPointF(0, -getHeight());
      return mOffset;
    }
  }

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
    final Calendar firstDate = records.get(0).getDateTime();
    final Calendar lastDate = records.get(records.getSize() - 1).getDateTime();
    final long referenceTime = firstDate.getTimeInMillis() / 1000;

    // initialize the LineChart and the yAxis which are necessary for creating the dataSets
    lineChart1 = new LineChart(getContext());
    YAxis yAxisLeft1 = lineChart1.getAxisLeft();
    yAxisLeft1.setEnabled(false);
    String yAxisLeft1Unit = "";
    YAxis yAxisRight1 = lineChart1.getAxisRight();
    yAxisRight1.setEnabled(false);
    String yAxisRight1Unit = "";

    lineChart2 = new LineChart(getContext());
    YAxis yAxisLeft2 = lineChart2.getAxisLeft();
    yAxisLeft2.setEnabled(false);
    String yAxisLeft2Unit = "";
    YAxis yAxisRight2 = lineChart2.getAxisRight();
    yAxisRight2.setEnabled(false);
    String yAxisRight2Unit = "";

    // initialize the dataSets which are necessary for setting the values in the chart
    List<ILineDataSet> dataSets1 = new ArrayList<>();
    List<ILineDataSet> dataSets2 = new ArrayList<>();

    final List<Entry> panel1VoltageEntries = new ArrayList<>();
    final List<Entry> panel2VoltageEntries = new ArrayList<>();
    final List<Entry> panel1CurrentEntries = new ArrayList<>();
    final List<Entry> panel2CurrentEntries = new ArrayList<>();
    final List<Entry> panel1PowerEntries = new ArrayList<>();
    final List<Entry> panel2PowerEntries = new ArrayList<>();
    final List<Entry> bothPanelsPowerEntries = new ArrayList<>();
    final List<Entry> panel1EnergyEntries = new ArrayList<>();
    final List<Entry> panel2EnergyEntries = new ArrayList<>();
    final List<Entry> bothPanelsEnergyEntries = new ArrayList<>();

    // save the x,y pair which are requested
    for (Record record : records.getRecords())
    {
      long timeStep = ((record.getDateTime().getTimeInMillis()) / 1000) - referenceTime;

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

      if (recordsSettings.isPanel1Energy())
      {
        panel1EnergyEntries.add(new Entry((float) timeStep, (float) record.getPanel1Energy()));
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

      if (recordsSettings.isPanel2Energy())
      {
        panel2EnergyEntries.add(new Entry((float) timeStep, (float) record.getPanel2Energy()));
      }

      if (recordsSettings.isBothPower())
      {
        bothPanelsPowerEntries.add(new Entry((float) timeStep, (float) record.getBothPower()));
      }

      if (recordsSettings.isBothEnergy())
      {
        bothPanelsEnergyEntries.add(new Entry((float) timeStep, (float) record.getBothEnergy()));
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

    if (recordsSettings.isPanel1Energy())
    {
      if (recordsSettings.isPanel1Voltage() || recordsSettings.isPanel2Voltage())
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisRight2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, panel1EnergyEntries,
                    R.string.fragment_diagram_energy_panel1, R.color.colorDiagramEnergy1,
                    YAxis.AxisDependency.RIGHT, yAxisRight2, yAxisRight2Unit);

          } else
          {
            yAxisLeft2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, panel1EnergyEntries,
                    R.string.fragment_diagram_energy_panel1, R.color.colorDiagramEnergy1,
                    YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);

          }
        } else
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisLeft2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, panel1EnergyEntries,
                    R.string.fragment_diagram_energy_panel1, R.color.colorDiagramEnergy1,
                    YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);
          } else
          {
            yAxisRight1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, panel1EnergyEntries,
                    R.string.fragment_diagram_energy_panel1, R.color.colorDiagramEnergy1,
                    YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);

          }
        }
      } else
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisLeft2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, panel1EnergyEntries,
                    R.string.fragment_diagram_energy_panel1, R.color.colorDiagramEnergy1,
                    YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);

          } else
          {
            yAxisRight1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, panel1EnergyEntries,
                    R.string.fragment_diagram_energy_panel1, R.color.colorDiagramEnergy1,
                    YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
          }
        } else
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisRight1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, panel1EnergyEntries,
                    R.string.fragment_diagram_energy_panel1, R.color.colorDiagramEnergy1,
                    YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
          } else
          {
            yAxisLeft1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, panel1EnergyEntries,
                    R.string.fragment_diagram_energy_panel1, R.color.colorDiagramEnergy1,
                    YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);
          }
        }
      }
    }

    if (recordsSettings.isPanel2Energy())
    {
      if (recordsSettings.isPanel1Voltage() || recordsSettings.isPanel2Voltage())
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisRight2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, panel2EnergyEntries,
                    R.string.fragment_diagram_energy_panel2, R.color.colorDiagramEnergy2,
                    YAxis.AxisDependency.RIGHT, yAxisRight2, yAxisRight2Unit);

          } else
          {
            yAxisLeft2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, panel2EnergyEntries,
                    R.string.fragment_diagram_energy_panel2, R.color.colorDiagramEnergy2,
                    YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);
          }
        } else
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisLeft2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, panel2EnergyEntries,
                    R.string.fragment_diagram_energy_panel2, R.color.colorDiagramEnergy2,
                    YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);
          } else
          {
            yAxisRight1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, panel2EnergyEntries,
                    R.string.fragment_diagram_energy_panel2, R.color.colorDiagramEnergy2,
                    YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
          }
        }
      } else
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisLeft2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, panel2EnergyEntries,
                    R.string.fragment_diagram_energy_panel2, R.color.colorDiagramEnergy2,
                    YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);

          } else
          {
            yAxisRight1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, panel2EnergyEntries,
                    R.string.fragment_diagram_energy_panel2, R.color.colorDiagramEnergy2,
                    YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);

          }
        } else
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisRight1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, panel2EnergyEntries,
                    R.string.fragment_diagram_energy_panel2, R.color.colorDiagramEnergy2,
                    YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
          } else
          {
            yAxisLeft1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, panel2EnergyEntries,
                    R.string.fragment_diagram_energy_panel2, R.color.colorDiagramEnergy2,
                    YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);

          }
        }
      }
    }

    if (recordsSettings.isBothEnergy())
    {
      if (recordsSettings.isPanel1Voltage() || recordsSettings.isPanel2Voltage())
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisRight2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, bothPanelsEnergyEntries,
                    R.string.fragment_diagram_energy_both_panels, R.color.colorDiagramEnergyBoth,
                    YAxis.AxisDependency.RIGHT, yAxisRight2, yAxisRight2Unit);
          } else
          {
            yAxisLeft1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, bothPanelsEnergyEntries,
                    R.string.fragment_diagram_energy_both_panels, R.color.colorDiagramEnergyBoth,
                    YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);
          }
        } else
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisLeft2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, bothPanelsEnergyEntries,
                    R.string.fragment_diagram_energy_both_panels, R.color.colorDiagramEnergyBoth,
                    YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);

          } else
          {
            yAxisRight1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, bothPanelsEnergyEntries,
                    R.string.fragment_diagram_energy_both_panels, R.color.colorDiagramEnergyBoth,
                    YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
          }
        }
      } else
      {
        if (recordsSettings.isPanel1Current() || recordsSettings.isPanel2Current())
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisLeft2Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets2, bothPanelsEnergyEntries,
                    R.string.fragment_diagram_energy_both_panels, R.color.colorDiagramEnergyBoth,
                    YAxis.AxisDependency.LEFT, yAxisLeft2, yAxisLeft2Unit);
          } else
          {
            yAxisRight1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, bothPanelsEnergyEntries,
                    R.string.fragment_diagram_energy_both_panels, R.color.colorDiagramEnergyBoth,
                    YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
          }
        } else
        {
          if (recordsSettings.isPanel1Power() || recordsSettings.isPanel2Power()
                  || recordsSettings.isBothPower())
          {
            yAxisRight1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, bothPanelsEnergyEntries,
                    R.string.fragment_diagram_energy_both_panels, R.color.colorDiagramEnergyBoth,
                    YAxis.AxisDependency.RIGHT, yAxisRight1, yAxisRight1Unit);
          } else
          {
            yAxisLeft1Unit = getString(R.string.fragment_diagram_energy_unit);
            addLineDataSet(dataSets1, bothPanelsEnergyEntries,
                    R.string.fragment_diagram_energy_both_panels, R.color.colorDiagramEnergyBoth,
                    YAxis.AxisDependency.LEFT, yAxisLeft1, yAxisLeft1Unit);
          }
        }
      }
    }


    LinearLayout layout =
            (LinearLayout) thisFragment.findViewById(R.id.fragment_diagram_LinearLayout);
    LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams
            (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    chartParams.weight = 1;

    // set the data and style the charts
    XAxis xAxis1 = lineChart1.getXAxis();
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
      XAxis xAxis2 = lineChart2.getXAxis();
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
        Toast.makeText(getContext(), getString(R.string.fragment_diagram_saved),
                Toast.LENGTH_LONG).show();
      else
        Toast.makeText(getContext(), getString(R.string.fragment_diagram_save_error),
                Toast.LENGTH_LONG).show();
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
      Toast.makeText(getContext(), R.string.fragment_diagram_no_chart, Toast.LENGTH_LONG).show();
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
      Toast.makeText(getContext(), getString(R.string.fragment_diagram_share_error),
              Toast.LENGTH_LONG).show();
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
      Toast.makeText(getContext(), R.string.fragment_diagram_no_chart, Toast.LENGTH_LONG).show();
  }


  //================================================================================================
  // Methods for opening the settings dialog and getting the settings
  //================================================================================================
  //This Method is called when the DialogSettings Object is closed with pressed with "ok".
  @Override
  public void onRecordsSettingsOKListener(RecordsSettings recordsSettings)
  {
    if (recordsSettings.equals(this.recordsSettings))
      Toast.makeText(getContext(),
              getResources().getText(R.string.fragment_diagram_settings_hasnot_changed),
              Toast.LENGTH_LONG).show();
    else
    {
      this.recordsSettings = recordsSettings;

      Toast.makeText(getContext(),
              getResources().getText(R.string.fragment_diagram_settings_has_changed),
              Toast.LENGTH_LONG).show();

      LinearLayout layout =
              (LinearLayout) thisFragment.findViewById(R.id.fragment_diagram_LinearLayout);
      LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams
              (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
      progressBarParams.gravity = Gravity.CENTER_HORIZONTAL;
      layout.removeAllViews();

      ProgressBar progressBar = new ProgressBar(getContext(), null,
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
    final DialogFragment dialogSettings = new DialogRecordsSettings();
    final Bundle recordSettingsBundle = new Bundle();
    if(recordsSettings != null)
    {
      recordSettingsBundle.putBoolean("panel1Voltage", recordsSettings.isPanel1Voltage());
      recordSettingsBundle.putBoolean("panel1Current", recordsSettings.isPanel1Current());
      recordSettingsBundle.putBoolean("panel1Power", recordsSettings.isPanel1Power());
      recordSettingsBundle.putBoolean("panel1Energy", recordsSettings.isPanel1Energy());
      recordSettingsBundle.putBoolean("panel2Voltage", recordsSettings.isPanel2Voltage());
      recordSettingsBundle.putBoolean("panel2Current", recordsSettings.isPanel2Current());
      recordSettingsBundle.putBoolean("panel2Power", recordsSettings.isPanel2Power());
      recordSettingsBundle.putBoolean("panel2Energy", recordsSettings.isPanel2Energy());
      recordSettingsBundle.putBoolean("bothPower", recordsSettings.isBothPower());
      recordSettingsBundle.putBoolean("bothEnergy", recordsSettings.isBothEnergy());
      recordSettingsBundle.putInt("startDay", recordsSettings.getStartDay());
      recordSettingsBundle.putInt("startMonth", recordsSettings.getStartMonth());
      recordSettingsBundle.putInt("startYear", recordsSettings.getStartYear());
      recordSettingsBundle.putInt("endDay", recordsSettings.getEndDay());
      recordSettingsBundle.putInt("endMonth", recordsSettings.getEndMonth());
      recordSettingsBundle.putInt("endYear", recordsSettings.getEndYear());
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
  public FragmentDiagram()
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
    // Inflate the layout for this fragment
    thisFragment = inflater.inflate(R.layout.fragment_diagram, container, false);
    getActivity().setTitle(getString(R.string.fragment_diagram_title));
    setHasOptionsMenu(true);
    if (records != null)
    {
      createDiagram(records);
    }
    return thisFragment;
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
  private class GetDataTask extends AsyncTask<Void, Void, Records>
  {
    @Override
    protected Records doInBackground(Void... voids)
    {
      Records result = null;
      try
      {
        ReceiveRecords receiveRecords = new ReceiveRecords(recordsSettings);
        result = receiveRecords.getRecords();
      } catch (Exception ex)
      {
        Toast.makeText(getActivity(), String.format("Error: %s", ex.getLocalizedMessage()),
                Toast.LENGTH_LONG).show();
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
