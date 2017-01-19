package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
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

  private class TableListViewAdapter extends BaseAdapter
  {
    private final Records records;
    private final RecordsSettings recordsSettings;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");

    public TableListViewAdapter(Records records, RecordsSettings recordsSettings)
    {
      //super();
      this.records = records;
      this.recordsSettings = recordsSettings;
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
        viewHolder = new CustomTableLineLayout(recordsSettings);
        view = viewHolder.getTableLineLayout();
        view.setTag(viewHolder);
      }
      else
      {
        viewHolder = (CustomTableLineLayout) view.getTag();
      }

      viewHolder.getDateTime().setText(
              dateFormat.format(new Date(record.getDateTime().getTimeInMillis())));

      if (recordsSettings.isPanel1Voltage())
        viewHolder.getPanel1Voltage().setText(String.format("%.2f",record.getPanel1Voltage()));

      if (recordsSettings.isPanel1Current())
        viewHolder.getPanel1Current().setText(String.format("%.2f",record.getPanel1Current()));

      if (recordsSettings.isPanel1Power())
        viewHolder.getPanel1Power().setText(String.format("%.2f",record.getPanel1Power()));

      if (recordsSettings.isPanel1Energy())
        viewHolder.getPanel1Energy().setText(String.format("%.2f",record.getPanel1Energy()));

      if (recordsSettings.isPanel2Voltage())
        viewHolder.getPanel2Voltage().setText(String.format("%.2f",record.getPanel2Voltage()));

      if (recordsSettings.isPanel2Current())
        viewHolder.getPanel2Current().setText(String.format("%.2f",record.getPanel2Current()));

      if (recordsSettings.isPanel2Power())
        viewHolder.getPanel2Power().setText(String.format("%.2f",record.getPanel2Power()));

      if (recordsSettings.isPanel2Energy())
        viewHolder.getPanel2Energy().setText(String.format("%.2f",record.getPanel2Energy()));

      if (recordsSettings.isBothPower())
        viewHolder.getPowerBoth().setText(String.format("%.2f",record.getBothPower()));

      if (recordsSettings.isBothEnergy())
        viewHolder.getEnergyBoth().setText(String.format("%.2f",record.getBothEnergy()));

      return view;
    }
  }


  private class CustomTableLineLayout
  {
    private final RecordsSettings recordsSettings;
    private LinearLayout tableLineLayout;
    private TextView dateTime;
    private TextView panel1Voltage, panel1Current, panel1Power, panel1Energy;
    private TextView panel2Voltage, panel2Current, panel2Power, panel2Energy;
    private TextView powerBoth, energyBoth;


    public CustomTableLineLayout(RecordsSettings recordsSettings)
    {
      this.recordsSettings = recordsSettings;
      createTableLineLayout();
    }

    private TextView createTextView(float weight, String string)
    {
      final TextView textView = new TextView(getContext());
      textView.setLayoutParams(new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, weight));
      textView.setGravity(Gravity.CENTER);
      textView.setText(String.format("%s", string));
      //textView.setBackgroundColor(ContextCompat.getColor(getContext(), backgroundColor));
      //textView.setPadding(border, border, border, border);
      //textView.setTextColor();
      return textView;
    }

    private void createTableLineLayout()
    {
      tableLineLayout = new LinearLayout(getContext());
      tableLineLayout.setOrientation(LinearLayout.HORIZONTAL);
      tableLineLayout.setLayoutParams(
              new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                      LinearLayout.LayoutParams.WRAP_CONTENT));
      tableLineLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorTableCell));

      dateTime = createTextView(1, getString(R.string.fragment_table_dateTime));
      tableLineLayout.addView(dateTime);

      if (recordsSettings.isPanel1Voltage())
      {
        panel1Voltage = createTextView(1, getString(R.string.fragment_table_voltage_panel1));
        tableLineLayout.addView(panel1Voltage);
      }

      if (recordsSettings.isPanel1Current())
      {
        panel1Current = createTextView(1, getString(R.string.fragment_table_current_panel1));
        tableLineLayout.addView(panel1Current);
      }

      if (recordsSettings.isPanel1Power())
      {
        panel1Power = createTextView(1, getString(R.string.fragment_table_power_panel1));
        tableLineLayout.addView(panel1Power);
      }

      if (recordsSettings.isPanel1Energy())
      {
        panel1Energy = createTextView(1, getString(R.string.fragment_table_energy_panel1));
        tableLineLayout.addView(panel1Energy);
      }

      if (recordsSettings.isPanel2Voltage())
      {
        panel2Voltage = createTextView(1, getString(R.string.fragment_table_voltage_panel2));
        tableLineLayout.addView(panel2Voltage);
      }

      if (recordsSettings.isPanel2Current())
      {
        panel2Current = createTextView(1, getString(R.string.fragment_table_current_panel2));
        tableLineLayout.addView(panel2Current);
      }

      if (recordsSettings.isPanel2Power())
      {
        panel2Power = createTextView(1, getString(R.string.fragment_table_power_panel2));
        tableLineLayout.addView(panel2Power);
      }

      if (recordsSettings.isPanel2Energy())
      {
        panel2Energy = createTextView(1, getString(R.string.fragment_table_energy_panel2));
        tableLineLayout.addView(panel2Energy);
      }

      if (recordsSettings.isBothPower())
      {
        powerBoth = createTextView(1, getString(R.string.fragment_table_power_both_panels));
        tableLineLayout.addView(powerBoth);
      }

      if (recordsSettings.isBothEnergy())
      {
        energyBoth = createTextView(1, getString(R.string.fragment_table_energy_both_panels));
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

    public TextView getDateTime()
    {
      return dateTime;
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

  private void createTable(Records records)
  {
    final LinearLayout mainLayout =
            (LinearLayout) thisFragment.findViewById(R.id.fragment_table_mainLayout);
    final CustomTableLineLayout customTableLineLayout = new CustomTableLineLayout(recordsSettings);
    mainLayout.removeAllViews();

    final ListView listView = new ListView(getContext());
    listView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));

    TableListViewAdapter adapter =
            new TableListViewAdapter(records, recordsSettings);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
      {

      }
    });

    mainLayout.addView(customTableLineLayout.getTableLineLayout());
    mainLayout.addView(listView);
  }

  //================================================================================================
  // Methods for opening the settings dialog and getting the settings
  //================================================================================================
  //This Methode is called when the DialogSettings Object is closed with pressed with "ok".
  @Override
  public void onRecordsSettingsOKListener(RecordsSettings recordsSettings)
  {
    if (recordsSettings.equals(this.recordsSettings))
      Toast.makeText(getContext(),
              getResources().getText(R.string.fragment_table_settings_hasnot_changed),
              Toast.LENGTH_LONG).show();
    else
    {
      Toast.makeText(getContext(),
              getResources().getText(R.string.fragment_table_settings_has_changed),
              Toast.LENGTH_LONG).show();
      this.recordsSettings = recordsSettings;
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
    DialogFragment dialogSettings = new DialogRecordsSettings();
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
    if (item.getItemId() == R.id.table_settings_icon)
    {
      openSettings();
      return true;
    } else
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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    // Inflate the layout for this fragment
    thisFragment = inflater.inflate(R.layout.fragment_table, container, false);
    getActivity().setTitle(getString(R.string.fragment_table_title));
    setHasOptionsMenu(true);

//
//    final int textViewID = View.generateViewId();
//    System.out.println(textViewID);
//                                                                                                          ///TEST 1243
//    LinearLayout linearLayout = (LinearLayout) thisFragment.findViewById(R.id.fragment_table_mainLayout);
//    TextView textView = new TextView(getContext());
//    textView.setId(textViewID);
//    System.out.println(textView.getId());
//    textView.setText("Test1");
//    linearLayout.addView(textView);
//    Button button = new Button(getContext());
//
//    linearLayout.addView(button);
//    button.setOnClickListener(new View.OnClickListener()
//    {
//      @Override
//      public void onClick(View view)
//      {
//        TextView textView4 = (TextView) thisFragment.findViewById(textViewID);
//        textView4.setText("JUHUU");
//      }
//    });


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
      createTable(records);
    }
  }
}
