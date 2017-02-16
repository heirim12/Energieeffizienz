package at.htlkaindorf.heirim12.energieeffizienz.gui.dialogs;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettings;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DialogRecordsSettingsOneDay.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DialogRecordsSettingsOneDay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DialogRecordsSettingsOneDay extends DialogFragment
        implements DialogDatePicker.OnDatePickerOKListener
{
  //================================================================================================
  // Global declarations
  //================================================================================================
  private View viewDialog;
  private OnRecordsSettingsOKListener myOKListener;

  private int
          day = 0, month = 0, year = 0;


  //================================================================================================
// Listener
//================================================================================================
  public interface OnRecordsSettingsOKListener
  {
    public void onRecordsSettingsOneDayOKListener(RecordsSettings recordsSettings);
  }

  private class DialogOKClickListener implements DialogInterface.OnClickListener
  {
    private final CheckBox bothPower,
            panel1Voltage, panel1Current, panel1Power,
            panel2Voltage, panel2Current, panel2Power;

    public DialogOKClickListener(CheckBox bothPower,
                                 CheckBox panel1Voltage, CheckBox panel1Current,
                                 CheckBox panel1Power,
                                 CheckBox panel2Voltage, CheckBox panel2Current,
                                 CheckBox panel2Power)
    {
      this.bothPower = bothPower;
      this.panel1Voltage = panel1Voltage;
      this.panel1Current = panel1Current;
      this.panel1Power = panel1Power;
      this.panel2Voltage = panel2Voltage;
      this.panel2Current = panel2Current;
      this.panel2Power = panel2Power;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i)
    {
      // month -1 because GregorianCalender starts month with 0
      Calendar date = new GregorianCalendar(year, month, day);

      final RecordsSettings recordsSettings =
              new RecordsSettings(date, date,
                      bothPower.isChecked(), false,
                      panel1Voltage.isChecked(), panel1Current.isChecked(),
                      panel1Power.isChecked(), false,
                      panel2Voltage.isChecked(), panel2Current.isChecked(),
                      panel2Power.isChecked(), false);
      myOKListener.onRecordsSettingsOneDayOKListener
              (recordsSettings);
    }
  }

  private class DialogCheckboxClickListener implements View.OnClickListener
  {
    private final CheckBox bothPower,
            panel1All, panel1Voltage, panel1Current, panel1Power,
            panel2All, panel2Voltage, panel2Current, panel2Power;

    public DialogCheckboxClickListener(CheckBox bothPower,
                                       CheckBox panel1All, CheckBox panel1Voltage, CheckBox panel1Current,
                                       CheckBox panel1Power,
                                       CheckBox panel2All, CheckBox panel2Voltage, CheckBox panel2Current,
                                       CheckBox panel2Power)
    {
      this.bothPower = bothPower;
      this.panel1All = panel1All;
      this.panel1Voltage = panel1Voltage;
      this.panel1Current = panel1Current;
      this.panel1Power = panel1Power;
      this.panel2All = panel2All;
      this.panel2Voltage = panel2Voltage;
      this.panel2Current = panel2Current;
      this.panel2Power = panel2Power;
    }

    private void allClicked(CheckBox all, CheckBox boxes[])
    {
      if (all.isChecked())
      {
        for (int i = 0; i < boxes.length; i++)
        {
          boxes[i].setChecked(true);
        }
      } else if (all.isChecked() == false)
      {
        for (int i = 0; i < boxes.length; i++)
        {
          boxes[i].setChecked(false);
        }
      }
    }

    private void normalClicked(CheckBox selected, CheckBox all, CheckBox boxes[])
    {
      if (selected.isChecked())
      {
        int trueCount = 0;
        for (int i = 0; i < boxes.length; i++)
        {
          if (boxes[i].isChecked())
          {
            trueCount++;
            if (trueCount == boxes.length)
            {
              all.setChecked(true);
              allClicked(all, boxes);
              return;
            }
          }
        }
      } else if (selected.isChecked() == false)
      {
        all.setChecked(false);
      }
    }

    @Override
    public void onClick(View view)
    {
      switch (view.getId())
      {
        case R.id.dialog_records_one_day_settings_all1:
        {
          allClicked(panel1All,
                  new CheckBox[]{panel1Voltage, panel1Current, panel1Power});
          break;
        }

        case R.id.dialog_records_one_day_settings_all2:
        {
          allClicked(panel2All,
                  new CheckBox[]{panel2Voltage, panel2Current, panel2Power});
          break;
        }

        case R.id.dialog_records_one_day_settings_voltage1:
        {
          normalClicked(panel1Voltage, panel1All,
                  new CheckBox[]{panel1Voltage, panel1Current, panel1Power});
          break;
        }

        case R.id.dialog_records_one_day_settings_current1:
        {
          normalClicked(panel1Current, panel1All,
                  new CheckBox[]{panel1Voltage, panel1Current, panel1Power});
          break;
        }
        case R.id.dialog_records_one_day_settings_power1:
        {
          normalClicked(panel1Power, panel1All,
                  new CheckBox[]{panel1Voltage, panel1Current, panel1Power});
          break;
        }

        case R.id.dialog_records_one_day_settings_voltage2:
        {
          normalClicked(panel2Voltage, panel2All,
                  new CheckBox[]{panel2Voltage, panel2Current, panel2Power});
          break;
        }

        case R.id.dialog_records_one_day_settings_current2:
        {
          normalClicked(panel2Current, panel2All,
                  new CheckBox[]{panel2Voltage, panel2Current, panel2Power});
          break;
        }
        case R.id.dialog_records_one_day_settings_power2:
        {
          normalClicked(panel2Power, panel2All,
                  new CheckBox[]{panel2Voltage, panel2Current, panel2Power});
          break;
        }
      }
    }
  }

  private class DialogImageButtonClickListener implements View.OnClickListener
  {
    private final int minDay, minMonth, minYear, maxDay, maxMonth, maxYear;
    private final Fragment callingFragment;

    private DialogImageButtonClickListener(int minDay, int minMonth, int minYear,
                                           int maxDay, int maxMonth, int maxYear,
                                           Fragment callingFragment)
    {
      this.minDay = minDay;
      this.minMonth = minMonth;
      this.minYear = minYear;
      this.maxDay = maxDay;
      this.maxMonth = maxMonth;
      this.maxYear = maxYear;
      this.callingFragment = callingFragment;
    }

    @Override
    public void onClick(View view)
    {
      Bundle dateData = new Bundle();
      if (view.getId() == R.id.dialog_record_one_day_date_button)
      {
        // Give the DatePicker the range for the possible dates
        dateData.putInt("minDay", minDay);
        dateData.putInt("minMonth", minMonth);
        dateData.putInt("minYear", minYear);
        dateData.putInt("maxDay", maxDay);
        dateData.putInt("maxMonth", maxMonth);
        dateData.putInt("maxYear", maxYear);
        dateData.putInt("currentDay", day);
        dateData.putInt("currentMonth", month);
        dateData.putInt("currentYear", year);
      }

      // Make and show the Dialog
      android.support.v4.app.DialogFragment dialogDatePicker = new DialogDatePicker();
      dialogDatePicker.setTargetFragment(callingFragment, 0);
      dialogDatePicker.setArguments(dateData);
      try
      {
        dialogDatePicker.show(getFragmentManager(), "dialogDatePicker");
      } catch (Exception ex)
      {
        ex.printStackTrace();
        Toast.makeText(getActivity().getBaseContext(),
                ex.getLocalizedMessage(),
                Toast.LENGTH_LONG).show();
      }
    }
  }

  // Listener from the DatePicker DialogFragment
  @Override
  public void onDatePickerOkListener(int day, int month, int year)
  {
      this.day = day;
      this.month = month;
      this.year = year;
      TextView endDate = (TextView) viewDialog.findViewById(R.id.dialog_record_one_day_date);
      endDate.setText(String.format("%02d.%02d.%04d", day, month, year));
  }


  //================================================================================================
  // Lifecycle
  //================================================================================================
  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);
    try
    {
      myOKListener = (OnRecordsSettingsOKListener) getTargetFragment();
    } catch (ClassCastException ex)
    {
      throw new ClassCastException(getTargetFragment().toString() +
              " must implement OnRecordSettingsOKListener!");
    }
  }

  //Save old checkboxes states -> when DialogFragment gets killed
  //from the system or when the users changes the orientation of the device
  @Override
  public void onSaveInstanceState(Bundle savedInstanceState)
  {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putInt("day", day);
    savedInstanceState.putInt("month", month);
    savedInstanceState.putInt("year", year);
  }

  public void restoreData(Bundle savedInstanceState, Bundle recordSettingsBundle,
                          TextView date, CheckBox bothPower,
                          CheckBox panel1All, CheckBox panel1Voltage, CheckBox panel1Current,
                          CheckBox panel1Power,
                          CheckBox panel2All, CheckBox panel2Voltage, CheckBox panel2Current,
                          CheckBox panel2Power)
  {
    if (savedInstanceState != null)
    {
      day = savedInstanceState.getInt("day");
      month = savedInstanceState.getInt("month");
      year = savedInstanceState.getInt("year");

      date.setText(String.format("%02d.%02d.%04d", day, month, year));
      //checkboxes state will be saved and restored automatically
    } else if (recordSettingsBundle != null)
    {
      bothPower.setChecked(recordSettingsBundle.getBoolean("bothPower", false));
      panel1Voltage.setChecked(recordSettingsBundle.getBoolean("panel1Voltage", false));
      panel1Current.setChecked(recordSettingsBundle.getBoolean("panel1Current", false));
      panel1Power.setChecked(recordSettingsBundle.getBoolean("panel1Power", false));
      panel2Voltage.setChecked(recordSettingsBundle.getBoolean("panel2Voltage", false));
      panel2Current.setChecked(recordSettingsBundle.getBoolean("panel2Current", false));
      panel2Power.setChecked(recordSettingsBundle.getBoolean("panel2Power", false));
      final Calendar calendarDate = new GregorianCalendar();
      calendarDate.setTimeInMillis(recordSettingsBundle.getLong("date"));

      if (panel1Voltage.isChecked() && panel1Current.isChecked() && panel1Power.isChecked())
        panel1All.setChecked(true);

      if (panel2Voltage.isChecked() && panel2Current.isChecked() && panel2Power.isChecked())
        panel2All.setChecked(true);

      date.setText(String.format("%02d.%02d.%04d",
              calendarDate.get(Calendar.DAY_OF_MONTH),
              calendarDate.get(Calendar.MONTH),
              calendarDate.get(Calendar.YEAR)));
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    viewDialog = getActivity().getLayoutInflater().inflate(
            R.layout.fragment_dialog_records_one_day_settings, null);

    final Calendar currentDate = Calendar.getInstance();
    final int
            minDay = 1,
            minMonth = 1,
            minYear = 2016,
            maxDay = currentDate.get(Calendar.DAY_OF_MONTH),
            maxMonth = currentDate.get(Calendar.MONTH) + 1,
            maxYear = currentDate.get(Calendar.YEAR);

    day = maxDay;
    month = maxMonth;
    year = maxYear;

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setView(viewDialog);

    TextView date = (TextView) viewDialog.findViewById(R.id.dialog_record_one_day_date);
    date.setText(String.format("%02d.%02d.%04d", day, month, year));


    ImageButton dateButton =
            (ImageButton) viewDialog.findViewById(R.id.dialog_record_one_day_date_button);

    dateButton.setOnClickListener(new DialogImageButtonClickListener(minDay,
            minMonth, minYear, maxDay, maxMonth, maxYear, this));

    CheckBox
            bothPower = (CheckBox) viewDialog.findViewById(R.id.dialog_records_one_day_powerBoth),
            panel1All = (CheckBox) viewDialog.findViewById(R.id.dialog_records_one_day_settings_all1),
            panel1Voltage = (CheckBox) viewDialog.findViewById(R.id.dialog_records_one_day_settings_voltage1),
            panel1Current = (CheckBox) viewDialog.findViewById(R.id.dialog_records_one_day_settings_current1),
            panel1Power = (CheckBox) viewDialog.findViewById(R.id.dialog_records_one_day_settings_power1),
            panel2All = (CheckBox) viewDialog.findViewById(R.id.dialog_records_one_day_settings_all2),
            panel2Voltage = (CheckBox) viewDialog.findViewById(R.id.dialog_records_one_day_settings_voltage2),
            panel2Current = (CheckBox) viewDialog.findViewById(R.id.dialog_records_one_day_settings_current2),
            panel2Power = (CheckBox) viewDialog.findViewById(R.id.dialog_records_one_day_settings_power2);

    final DialogCheckboxClickListener checkboxListener =
            new DialogCheckboxClickListener(bothPower,
                    panel1All, panel1Voltage, panel1Current, panel1Power,
                    panel2All, panel2Voltage, panel2Current, panel2Power);

    bothPower.setOnClickListener(checkboxListener);
    panel1All.setOnClickListener(checkboxListener);
    panel1Voltage.setOnClickListener(checkboxListener);
    panel1Current.setOnClickListener(checkboxListener);
    panel1Power.setOnClickListener(checkboxListener);
    panel2All.setOnClickListener(checkboxListener);
    panel2Voltage.setOnClickListener(checkboxListener);
    panel2Current.setOnClickListener(checkboxListener);
    panel2Power.setOnClickListener(checkboxListener);

    final DialogOKClickListener oKListener = new DialogOKClickListener(bothPower,
            panel1Voltage, panel1Current, panel1Power,
            panel2Voltage, panel2Current, panel2Power);

    builder.setPositiveButton(getResources().getText(R.string.dialog_diagram_ok), oKListener);
    builder.setNegativeButton(getResources().getText(R.string.dialog_diagram_cancel), null);

    restoreData(savedInstanceState, this.getArguments(),
            date, bothPower,
            panel1All, panel1Voltage, panel1Current, panel1Power,
            panel2All, panel2Voltage, panel2Current, panel2Power);
    return builder.create();
  }

}

