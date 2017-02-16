package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments;


import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.communication.ReceiveRecords;
import at.htlkaindorf.heirim12.energieeffizienz.data.Record;
import at.htlkaindorf.heirim12.energieeffizienz.data.Records;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettings;
import at.htlkaindorf.heirim12.energieeffizienz.database.PhotovoltaicDatabase;
import at.htlkaindorf.heirim12.energieeffizienz.gui.dialogs.DialogRecordsSettings;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTable extends Fragment
        implements DialogRecordsSettings.OnRecordsSettingsOKListener
{
  //================================================================================================
  // Global declarations
  //================================================================================================
  private View thisFragment;
  private ExecutorService executor = null;
  private RecordsSettings recordsSettings = null;
  private Records records;


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
      final CustomTableLineLayout viewHolder;

      if (view == null)
      {
        if ((index % 2) == 0)
          viewHolder = new CustomTableLineLayout(recordsSettings, color1, lineHeight);
        else
          viewHolder = new CustomTableLineLayout(recordsSettings, color2, lineHeight);
        view = viewHolder.getTableLineLayout();
        view.setTag(viewHolder);
      }
      else
      {
        viewHolder = (CustomTableLineLayout) view.getTag();
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

  private int calculateTextWigthInPx(TextView textView)
  {
    Rect bounds = new Rect();
    Paint textPaint = textView.getPaint();
    String text = textView.getText().toString();
    textPaint.getTextBounds(text,0,text.length(),bounds);
    int height = bounds.height();
    return bounds.width();
  }

  private int calculateTextHeightInPx(TextView textView)
  {
    Rect bounds = new Rect();
    Paint textPaint = textView.getPaint();
    String text = textView.getText().toString();
    textPaint.getTextBounds(text,0,text.length(),bounds);
    return bounds.height();
  }

  private void createTable(Records records)
  {
    final LinearLayout mainLayout =
            (LinearLayout) thisFragment.findViewById(R.id.fragment_table_mainLinearLayout);
//    final int mainPadding = (int)getResources().getDimension(R.dimen.fragment_table_layout_margin);
//    mainLayout.setPadding(mainPadding, mainPadding, mainPadding, mainPadding);

    final TextView dateTextView = new TextView(getContext());
    dateTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
    int padding = (int)getResources().getDimension(R.dimen.fragment_table_cell);
    dateTextView.setPadding(padding, padding, padding, padding);
    dateTextView.setBackgroundColor(getResources().getColor(R.color.colorTableDateColumn));
    dateTextView.setText(new SimpleDateFormat("dd.MM.yy HH:mm").format(
            new Date(records.get(1).getDateTime().getTimeInMillis())));
    dateTextView.measure(0,0);
    final int lineHeight = dateTextView.getMeasuredHeight();

//    Layout and children views for the left side of the Table (Date/Time)
    final LinearLayout dateLayout = new LinearLayout(getContext());
    dateLayout.setLayoutParams(new LinearLayout.LayoutParams(dateTextView.getMeasuredWidth(),
            ViewGroup.LayoutParams.MATCH_PARENT));
    dateLayout.setOrientation(LinearLayout.VERTICAL);

    dateTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
    dateTextView.setText(getString(R.string.fragment_table_dateTime));


    final ListView dateListView = new ListView(getContext());
    dateListView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
    dateListView.setAdapter(new DateListViewAdapter(records, R.color.colorTableDateColumn, lineHeight));

    dateLayout.addView(dateTextView);
    dateLayout.addView(dateListView);


//    //Layout and children views for the rigth side of the table (Data)
    final HorizontalScrollView dataHorizontalScrollView = new HorizontalScrollView(getContext());
    dataHorizontalScrollView.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

    final LinearLayout dataLayout = new LinearLayout(getContext());
    dataLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
    dataLayout.setOrientation(LinearLayout.VERTICAL);

    final LinearLayout header = new CustomTableLineLayout(recordsSettings,R.color.colorTableHeader, lineHeight).getTableLineLayout();

    final ListView dataListView = new ListView(getContext());
    dataListView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
    dataListView.setAdapter(new DataListViewAdapter(records, recordsSettings,
            R.color.colorTableRow1, R.color.colorTableRow2, lineHeight));
    dataListView.setBackgroundColor(getResources().getColor(R.color.colorTableDateColumn));

    dataLayout.addView(header);
    dataLayout.addView(dataListView);
    dataHorizontalScrollView.addView(dataLayout);


    mainLayout.removeAllViews();
    mainLayout.addView(dateLayout);
    mainLayout.addView(dataHorizontalScrollView);

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

  //================================================================================================
  // Methods for saving the diagram
  //================================================================================================
  private void saveTable(String filename)
  {
    //TODO
  }

  private void saveTableSettings()
  {
    //TODO
    Toast.makeText(getContext(), "Not implemented now!", Toast.LENGTH_SHORT).show();
  }


  //================================================================================================
  // Methods for sharing the diagram
  //================================================================================================
  private void shareTable()
  {
    //TODO:
    Toast.makeText(getContext(), "Not implemented now!", Toast.LENGTH_SHORT).show();
  }


  //================================================================================================
  // Methods for opening the settings dialog and getting the settings
  //================================================================================================
  //This Methode is called when the DialogSettings Object is closed with pressed with "ok".
  public void onRecordsSettingsOKListener(RecordsSettings recordsSettings)
  {
    if (recordsSettings.equals(this.recordsSettings))
      Toast.makeText(getContext(),
              getResources().getText(R.string.fragment_table_settings_hasnot_changed),
              Toast.LENGTH_LONG).show();
    else
    {
      this.recordsSettings = recordsSettings;

      Toast.makeText(getContext(),
              getResources().getText(R.string.fragment_table_settings_has_changed),
              Toast.LENGTH_LONG).show();

      LinearLayout mainLayout =
              (LinearLayout) thisFragment.findViewById(R.id.fragment_table_mainLinearLayout);
      LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams
              (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
      progressBarParams.gravity = Gravity.CENTER_VERTICAL;
      mainLayout.removeAllViews();

      ProgressBar progressBar = new ProgressBar(getContext(), null,
              android.R.attr.progressBarStyleLarge);
      progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccentGreen),
              PorterDuff.Mode.SRC_IN);
      progressBar.setLayoutParams(progressBarParams);
      mainLayout.addView(progressBar);

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
    inflater.inflate(R.menu.fragment_table_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.table_settings_icon:
        openSettings();
        return true;

      case R.id.table_share_icon:
        shareTable();
        return true;

      case R.id.table_save_icon:
        saveTableSettings();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  //================================================================================================
  // Constructor
  //================================================================================================
  public FragmentTable()
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
    thisFragment = inflater.inflate(R.layout.fragment_table, container, false);
    getActivity().setTitle(getString(R.string.fragment_table_title));
    setHasOptionsMenu(true);
    if (records != null)
    {
      createTable(records);
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
//        ReceiveRecords receiveRecords = new ReceiveRecords(recordsSettings);
//        result = receiveRecords.getRecords();
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
      createTable(records);
    }
  }
}
