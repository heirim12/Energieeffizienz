package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.data.Record;
import at.htlkaindorf.heirim12.energieeffizienz.data.Records;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettings;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettingsOneDay;
import at.htlkaindorf.heirim12.energieeffizienz.gui.dialogs.DialogRecordsSettingsOneDay;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTableOneDay extends Fragment
        implements DialogRecordsSettingsOneDay.OnRecordsSettingsOKListener
{
  //================================================================================================
  // Global declarations
  //================================================================================================
  private View thisFragment;
  private ExecutorService executor = null;
  private RecordsSettings recordsSettings = null;
  private Records records = null;

  //================================================================================================
  // Methods and classes for creating the table
  //================================================================================================
  private class CustomTableLineLayout
  {
    private final RecordsSettings recordsSettings;
    private LinearLayout tableLineLayout;
    private TextView panel1Voltage, panel1Current, panel1Power, panel1Energy;
    private TextView panel2Voltage, panel2Current, panel2Power, panel2Energy;
    private TextView powerBoth, energyBoth;
    private final int color;
    private final int height;


    public CustomTableLineLayout(RecordsSettings recordsSettings, int color, int height)
    {
      this.color = color;
      this.height = height;
      this.recordsSettings = recordsSettings;
      createTableLineLayout();
    }

    private TextView createTextView(String string)
    {
      final TextView textView = new TextView(getContext());
      textView.setLayoutParams(new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      textView.setText(String.format("%s", string));
      int padding = (int)getResources().getDimension(R.dimen.fragment_table_cell);
      textView.setPadding(padding, padding, padding, padding);
      textView.measure(0,0);
      textView.setLayoutParams(new LinearLayout.LayoutParams(
              textView.getMeasuredWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
      textView.setGravity(Gravity.CENTER);
      textView.setGravity(Gravity.CENTER_VERTICAL);
      textView.setGravity(Gravity.CENTER_HORIZONTAL);

      return textView;
    }

    private void createTableLineLayout()
    {
      tableLineLayout = new LinearLayout(getContext());
      tableLineLayout.setOrientation(LinearLayout.HORIZONTAL);
      tableLineLayout.setLayoutParams(
              new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                      height));
      tableLineLayout.setBackgroundColor(ContextCompat.getColor(getContext(), color));

      if (recordsSettings.isPanel1Voltage())
      {
        panel1Voltage = createTextView(getString(R.string.fragment_table_voltage_panel1));
        tableLineLayout.addView(panel1Voltage);
      }

      if (recordsSettings.isPanel1Current())
      {
        panel1Current = createTextView(getString(R.string.fragment_table_current_panel1));
        tableLineLayout.addView(panel1Current);
      }

      if (recordsSettings.isPanel1Power())
      {
        panel1Power = createTextView(getString(R.string.fragment_table_power_panel1));
        tableLineLayout.addView(panel1Power);
      }

      if (recordsSettings.isPanel1Energy())
      {
        panel1Energy = createTextView(getString(R.string.fragment_table_energy_panel1));
        tableLineLayout.addView(panel1Energy);
      }

      if (recordsSettings.isPanel2Voltage())
      {
        panel2Voltage = createTextView(getString(R.string.fragment_table_voltage_panel2));
        tableLineLayout.addView(panel2Voltage);
      }

      if (recordsSettings.isPanel2Current())
      {
        panel2Current = createTextView(getString(R.string.fragment_table_current_panel2));
        tableLineLayout.addView(panel2Current);
      }

      if (recordsSettings.isPanel2Power())
      {
        panel2Power = createTextView(getString(R.string.fragment_table_power_panel2));
        tableLineLayout.addView(panel2Power);
      }

      if (recordsSettings.isPanel2Energy())
      {
        panel2Energy = createTextView(getString(R.string.fragment_table_energy_panel2));
        tableLineLayout.addView(panel2Energy);
      }

      if (recordsSettings.isBothPower())
      {
        powerBoth = createTextView(getString(R.string.fragment_table_power_both_panels));
        tableLineLayout.addView(powerBoth);
      }

      if (recordsSettings.isBothEnergy())
      {
        energyBoth = createTextView(getString(R.string.fragment_table_energy_both_panels));
        tableLineLayout.addView(energyBoth);
      }
    }

    public LinearLayout getTableLineLayout()
    {
      return tableLineLayout;
    }

    public TextView getEnergyBoth()
    {
      return energyBoth;
    }

    public TextView getPanel1Voltage()
    {
      return panel1Voltage;
    }

    public TextView getPanel1Current()
    {
      return panel1Current;
    }

    public TextView getPanel1Power()
    {
      return panel1Power;
    }

    public TextView getPanel1Energy()
    {
      return panel1Energy;
    }

    public TextView getPanel2Voltage()
    {
      return panel2Voltage;
    }

    public TextView getPanel2Current()
    {
      return panel2Current;
    }

    public TextView getPanel2Power()
    {
      return panel2Power;
    }

    public TextView getPanel2Energy()
    {
      return panel2Energy;
    }

    public TextView getPowerBoth()
    {
      return powerBoth;
    }
  }

  private class DataListViewAdapter extends BaseAdapter
  {
    private final Records records;
    private final RecordsSettings recordsSettings;
    private final int lineHeight;
    private final int color1;
    private final int color2;

    public DataListViewAdapter(Records records, RecordsSettings recordsSettings,
                               int color1, int color2, int lineHeight)
    {
      super();
      this.records = records;
      this.recordsSettings = recordsSettings;
      this.lineHeight = lineHeight;
      this.color1 = color1;
      this.color2 = color2;
    }

    @Override
    public int getCount()
    {
      return records.getSize();
    }

    @Override
    public Object getItem(int index)
    {
      return records.get(index);
    }

    @Override
    public long getItemId(int index)
    {
      return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup)
    {
      final Record record = records.get(index);
      final FragmentTable.CustomTableLineLayout viewHolder;

      if (view == null)
      {
        if ((index % 2) == 0)
          viewHolder = new FragmentTable.CustomTableLineLayout(recordsSettings, color1, lineHeight);
        else
          viewHolder = new FragmentTable.CustomTableLineLayout(recordsSettings, color2, lineHeight);
        view = viewHolder.getTableLineLayout();
        view.setTag(viewHolder);
      }
      else
      {
        viewHolder = (FragmentTable.CustomTableLineLayout) view.getTag();
      }

      if (recordsSettings.isPanel1Voltage())
        viewHolder.getPanel1Voltage().setText(String.format("%.2fV",record.getPanel1Voltage()));

      if (recordsSettings.isPanel1Current())
        viewHolder.getPanel1Current().setText(String.format("%.2fA",record.getPanel1Current()));

      if (recordsSettings.isPanel1Power())
        viewHolder.getPanel1Power().setText(String.format("%.2fW",record.getPanel1Power()));

      if (recordsSettings.isPanel1Energy())
        viewHolder.getPanel1Energy().setText(String.format("%.2fWh",record.getPanel1Energy()));

      if (recordsSettings.isPanel2Voltage())
        viewHolder.getPanel2Voltage().setText(String.format("%.2fV",record.getPanel2Voltage()));

      if (recordsSettings.isPanel2Current())
        viewHolder.getPanel2Current().setText(String.format("%.2fA",record.getPanel2Current()));

      if (recordsSettings.isPanel2Power())
        viewHolder.getPanel2Power().setText(String.format("%.2fW",record.getPanel2Power()));

      if (recordsSettings.isPanel2Energy())
        viewHolder.getPanel2Energy().setText(String.format("%.2fWh",record.getPanel2Energy()));

      if (recordsSettings.isBothPower())
        viewHolder.getPowerBoth().setText(String.format("%.2fW",record.getBothPower()));

      if (recordsSettings.isBothEnergy())
        viewHolder.getEnergyBoth().setText(String.format("%.2fWh",record.getBothEnergy()));

      return view;
    }
  }

  private class DateListViewAdapter extends BaseAdapter
  {
    private final Records records;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
    private final int color;
    private final int lineHeight;

    public DateListViewAdapter(Records records, int color, int lineHeight)
    {
      super();
      this.records = records;
      this.color = color;
      this.lineHeight = lineHeight;
    }

    @Override
    public int getCount()
    {
      return records.getSize();
    }

    @Override
    public Object getItem(int index)
    {
      return records.get(index);
    }

    @Override
    public long getItemId(int index)
    {
      return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup)
    {
      final Record record = records.get(index);
      final TextView textViewHolder;

      if (view == null)
      {
        textViewHolder = new TextView(getContext());
        textViewHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                lineHeight));
        int padding = (int)getResources().getDimension(R.dimen.fragment_table_cell);
        textViewHolder.setPadding(padding, padding, padding, padding);
        textViewHolder.setBackgroundColor(getResources().getColor(color));
        textViewHolder.setGravity(Gravity.CENTER);
        textViewHolder.setGravity(Gravity.CENTER_VERTICAL);
        textViewHolder.setGravity(Gravity.CENTER_HORIZONTAL);
        view = textViewHolder;
        view.setTag(textViewHolder);
      }
      else
      {
        textViewHolder = (TextView) view.getTag();
      }

      textViewHolder.setText(dateFormat.format(new Date(record.getDateTime().getTimeInMillis())));
      return view;
    }
  }

  private void createTable(Records records)
  {
    //Main Layout with is defined in the xml-file
    final LinearLayout mainLayout =
            (LinearLayout) thisFragment.findViewById(R.id.fragment_table_mainLinearLayout);

    //TextView for the the Date/Time title
    final TextView dateTextView = new TextView(getContext());
    dateTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
    int padding = (int)getResources().getDimension(R.dimen.fragment_table_cell);
    dateTextView.setPadding(padding, padding, padding, padding);
    dateTextView.setBackgroundColor(getResources().getColor(R.color.colorTableWhite));
    dateTextView.setText(new SimpleDateFormat("dd.MM.yy HH:mm").format(
            new Date(records.get(1).getDateTime().getTimeInMillis())));
    dateTextView.measure(0,0);
    final int lineHeight = dateTextView.getMeasuredHeight();

//  Layout and children views for the left side of the Table (Date/Time)
    final LinearLayout dateLayout = new LinearLayout(getContext());
    dateLayout.setLayoutParams(new LinearLayout.LayoutParams(dateTextView.getMeasuredWidth(),
            ViewGroup.LayoutParams.MATCH_PARENT));
    dateLayout.setOrientation(LinearLayout.VERTICAL);

    //setting the text for the TextView (Date/Time title)
    dateTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
    dateTextView.setText(getString(R.string.fragment_table_dateTime));

    //ListView for the Date/Time
    final ListView dateListView = new ListView(getContext());
    dateListView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
    dateListView.setAdapter(new FragmentTable.DateListViewAdapter(records, R.color.colorTableWhite, lineHeight));

    dateLayout.addView(dateTextView);
    dateLayout.addView(dateListView);


    //Layout and children views for the right side of the table (Data)
    final HorizontalScrollView dataHorizontalScrollView = new HorizontalScrollView(getContext());
    dataHorizontalScrollView.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

    final LinearLayout dataLayout = new LinearLayout(getContext());
    dataLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
    dataLayout.setOrientation(LinearLayout.VERTICAL);

    //Headline for the table
    final LinearLayout header = new FragmentTable.CustomTableLineLayout(
            recordsSettings,R.color.colorTableHeader, lineHeight).getTableLineLayout();

    //ListView for the Data
    final ListView dataListView = new ListView(getContext());
    dataListView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
    dataListView.setAdapter(new FragmentTable.DataListViewAdapter(records, recordsSettings,
            R.color.colorTableRow1, R.color.colorTableRow2, lineHeight));
    dataListView.setBackgroundColor(getResources().getColor(R.color.colorTableWhite));

    dataLayout.addView(header);
    dataLayout.addView(dataListView);
    dataHorizontalScrollView.addView(dataLayout);


    mainLayout.removeAllViews();
    mainLayout.addView(dateLayout);
    mainLayout.addView(dataHorizontalScrollView);

    //OnScrollListener: If one ListView Scrolls the other Scrolls the same;
    // For the user it looks like it is one.
    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener()
    {
      @Override
      public void onScrollStateChanged(AbsListView absListView, int i)
      {

      }

      @Override
      public void onScroll(AbsListView absListView, int i, int i1, int i2)
      {
        View v = absListView.getChildAt(0);
        if (v != null)
        {
          if (absListView == dateListView)
          {
            dataListView.setSelectionFromTop(i, v.getTop());

          } else if (absListView == dataListView)
          {
            dateListView.setSelectionFromTop(i, v.getTop());
          }
        }
      }
    };

    dataListView.setOnScrollListener(onScrollListener);
    dateListView.setOnScrollListener(onScrollListener);
  }

  public FragmentTableOneDay()
  {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_fragment_table_one_day, container, false);
  }

  @Override
  public void onRecordsSettingsOneDayOKListener(RecordsSettingsOneDay recordsSettingsOneDay)
  {

  }
}
