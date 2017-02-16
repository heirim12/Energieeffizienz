package at.htlkaindorf.heirim12.energieeffizienz.gui.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 */
public class DialogRecordsEnergySettings
//        extends DialogFragment implements DialogDatePicker.OnDatePickerOKListener
{
//  //================================================================================================
//  // Global declarations
//  //================================================================================================
//  private View viewDialog;
//  private OnRecordsSettingsOKListener myOKListener;
//  private boolean datePickerStatus;
//
//  // false -> startDateButton was pressed last; true -> endDateButton was pressed last
//  private int
//          startDay = 0, startMonth = 0, startYear = 0,
//          endDay = 0, endMonth = 0, endYear = 0;
//
//  //================================================================================================
//  // Listener
//  //================================================================================================
//  public interface OnRecordsSettingsOKListener
//  {
//    public void onRecordsSettingsOKListener(RecordsSettings recordsSettings);
//  }
//
//  private class DialogOKClickListener implements DialogInterface.OnClickListener
//  {
//    private final CheckBox bothEnergy, panel1Energy, panel2Energy;
//
//    public DialogOKClickListener(CheckBox bothEnergy, CheckBox panel1Energy, CheckBox panel2Energy)
//    {
//      this.bothEnergy = bothEnergy;
//      this.panel1Energy = panel1Energy;
//      this.panel2Energy = panel2Energy;
//    }
//
//    @Override
//    public void onClick(DialogInterface dialogInterface, int i)
//    {
//      // month -1 because GregorianCalender starts month with 0
//      Calendar startDatum = new GregorianCalendar(startYear, startMonth - 1, startDay);
//      Calendar endDatum = new GregorianCalendar(endYear, endMonth - 1, endDay);
//
//      final RecordsSettings recordsSettings =
//              new RecordsSettings(startDatum, endDatum,
//                      false, bothEnergy.isChecked(),
//                      false, false, false, panel1Energy.isChecked(),
//                      false, false, false, panel2Energy.isChecked());
//      myOKListener.onRecordsSettingsOKListener
//              (recordsSettings);
//
//    }
//  }
//
//  private class DialogImageButtonClickListener implements View.OnClickListener
//  {
//    private final int minDay, minMonth, minYear, maxDay, maxMonth, maxYear;
//    private final Fragment callingFragment;
//
//    private DialogImageButtonClickListener(int minDay, int minMonth, int minYear,
//                                           int maxDay, int maxMonth, int maxYear,
//                                           Fragment callingFragment)
//    {
//      this.minDay = minDay;
//      this.minMonth = minMonth;
//      this.minYear = minYear;
//      this.maxDay = maxDay;
//      this.maxMonth = maxMonth;
//      this.maxYear = maxYear;
//      this.callingFragment = callingFragment;
//    }
//
//    @Override
//    public void onClick(View view)
//    {
//      Bundle dateData = new Bundle();
//      if (view.getId() == R.id.dialog_diagram_settings_buttonStartDate)
//      {
//        // Give the DatePicker the range for the possible dates
//        dateData.putInt("minDay", minDay);
//        dateData.putInt("minMonth", minMonth);
//        dateData.putInt("minYear", minYear);
//        dateData.putInt("maxDay", endDay);
//        dateData.putInt("maxMonth", endMonth);
//        dateData.putInt("maxYear", endYear);
//        dateData.putInt("currentDay", startDay);
//        dateData.putInt("currentMonth", startMonth);
//        dateData.putInt("currentYear", startYear);
//        datePickerStatus = false;
//      } else if (view.getId() == R.id.dialog_diagram_settings_buttonEndDate)
//      {
//        dateData.putInt("minDay", startDay);
//        dateData.putInt("minMonth", startMonth);
//        dateData.putInt("minYear", startYear);
//        dateData.putInt("maxDay", maxDay);
//        dateData.putInt("maxMonth", maxMonth);
//        dateData.putInt("maxYear", maxYear);
//        dateData.putInt("currentDay", endDay);
//        dateData.putInt("currentMonth", endMonth);
//        dateData.putInt("currentYear", endYear);
//        datePickerStatus = true;
//      }
//
//      // Make and show the Dialog
//      DialogFragment dialogDatePicker = new DialogDatePicker();
//      dialogDatePicker.setTargetFragment(callingFragment, 0);
//      dialogDatePicker.setArguments(dateData);
//      try
//      {
//        dialogDatePicker.show(getFragmentManager(), "dialogDatePicker");
//      } catch (Exception ex)
//      {
//        ex.printStackTrace();
//        Toast.makeText(getActivity().getBaseContext(),
//                ex.getLocalizedMessage(),
//                Toast.LENGTH_LONG).show();
//      }
//    }
//  }
//
//  // Listener from the DatePicker DialogFragment
//  @Override
//  public void onDatePickerOkListener(int day, int month, int year)
//  {
//    if (datePickerStatus)
//    {
//      endDay = day;
//      endMonth = month;
//      endYear = year;
//      TextView endDate = (TextView)
//              viewDialog.findViewById(R.id.dialog_settings_energy_textViewEndDate);
//      endDate.setText(String.format("%02d.%02d.%04d", endDay, endMonth, endYear));
//    } else if (datePickerStatus == false)
//    {
//      startDay = day;
//      startMonth = month;
//      startYear = year;
//      TextView startDate = (TextView)
//              viewDialog.findViewById(R.id.dialog_settings_energy_textViewStartDate);
//      startDate.setText(String.format("%02d.%02d.%04d", startDay, startMonth, startYear));
//    }
//  }
//
//
//  public DialogRecordsEnergySettings()
//  {
//    // Required empty public constructor
//  }
//
//  //================================================================================================
//  // Lifecycle
//  //================================================================================================
//  @Override
//  public void onAttach(Context context)
//  {
//    super.onAttach(context);
//    try
//    {
//      myOKListener = (OnRecordsSettingsOKListener) getTargetFragment();
//    } catch (ClassCastException ex)
//    {
//      throw new ClassCastException(getTargetFragment().toString() +
//              " must implement OnRecordSettingsOKListener!");
//    }
//  }
//
//  //Save old checkboxes states -> when DialogFragment gets killed
//  //from the system or when the users changes the orientation of the device
//  @Override
//  public void onSaveInstanceState(Bundle savedInstanceState)
//  {
//    super.onSaveInstanceState(savedInstanceState);
//    savedInstanceState.putBoolean("datePickerStatus", datePickerStatus);
//    savedInstanceState.putInt("startDay", startDay);
//    savedInstanceState.putInt("startMonth", startMonth);
//    savedInstanceState.putInt("startYear", startYear);
//    savedInstanceState.putInt("endDay", endDay);
//    savedInstanceState.putInt("endMonth", endMonth);
//    savedInstanceState.putInt("endYear", endYear);
//  }
//
//  public void restoreData(Bundle savedInstanceState, Bundle recordSettingsBundle,
//                          TextView startDate, TextView endDate,
//                          CheckBox bothAll, CheckBox bothPower, CheckBox bothEnergy,
//                          CheckBox panel1All, CheckBox panel1Voltage, CheckBox panel1Current,
//                          CheckBox panel1Power, CheckBox panel1Energy,
//                          CheckBox panel2All, CheckBox panel2Voltage, CheckBox panel2Current,
//                          CheckBox panel2Power, CheckBox panel2Energy)
//  {
//    if (savedInstanceState != null)
//    {
//      datePickerStatus = savedInstanceState.getBoolean("datePickerStatus");
//      startDay = savedInstanceState.getInt("startDay");
//      startMonth = savedInstanceState.getInt("startMonth");
//      startYear = savedInstanceState.getInt("startYear");
//      endDay = savedInstanceState.getInt("endDay");
//      endMonth = savedInstanceState.getInt("endMonth");
//      endYear = savedInstanceState.getInt("endYear");
//
//      startDate.setText(String.format("%02d.%02d.%04d", startDay, startMonth, startYear));
//      endDate.setText(String.format("%02d.%02d.%04d", endDay, endMonth, endYear));
//      //checkboxes state will be saved and restored automatically
//    } else if (recordSettingsBundle != null)
//    {
//      bothPower.setChecked(recordSettingsBundle.getBoolean("bothPower", false));
//      bothEnergy.setChecked(recordSettingsBundle.getBoolean("bothEnergy", false));
//      panel1Voltage.setChecked(recordSettingsBundle.getBoolean("panel1Voltage", false));
//      panel1Current.setChecked(recordSettingsBundle.getBoolean("panel1Current", false));
//      panel1Power.setChecked(recordSettingsBundle.getBoolean("panel1Power", false));
//      panel1Energy.setChecked(recordSettingsBundle.getBoolean("panel1Energy", false));
//      panel2Voltage.setChecked(recordSettingsBundle.getBoolean("panel2Voltage", false));
//      panel2Current.setChecked(recordSettingsBundle.getBoolean("panel2Current", false));
//      panel2Power.setChecked(recordSettingsBundle.getBoolean("panel2Power", false));
//      panel2Energy.setChecked(recordSettingsBundle.getBoolean("panel2Energy", false));
//
//      if (bothPower.isChecked() && bothEnergy.isChecked())
//        bothAll.setChecked(true);
//
//      if (panel1Voltage.isChecked() && panel1Current.isChecked() && panel1Power.isChecked()
//              && panel1Energy.isChecked())
//        panel1All.setChecked(true);
//
//      if (panel2Voltage.isChecked() && panel2Current.isChecked() && panel2Power.isChecked()
//              && panel2Energy.isChecked())
//        panel2All.setChecked(true);
//
//      startDate.setText(String.format("%02d.%02d.%04d", startDay, startMonth, startYear));
//      endDate.setText(String.format("%02d.%02d.%04d", endDay, endMonth, endYear));
//    }
//  }
//
//  @Override
//  public Dialog onCreateDialog(Bundle savedInstanceState)
//  {
//    viewDialog = getActivity().getLayoutInflater().inflate(R.layout.dialog_records_settings, null);
//
//    final Calendar currentDate = Calendar.getInstance();
//    final int
//            minDay = 1,
//            minMonth = 1,
//            minYear = 2016,
//            maxDay = currentDate.get(Calendar.DAY_OF_MONTH),
//            maxMonth = currentDate.get(Calendar.MONTH) + 1,
//            maxYear = currentDate.get(Calendar.YEAR);
//
//    startDay = endDay = maxDay;
//    startMonth = endMonth = maxMonth;
//    startYear = endYear = maxYear;
//
//    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//    builder.setView(viewDialog);
//
//    TextView startDate = (TextView) viewDialog.findViewById(R.id.dialog_diagram_textViewStartDate);
//    startDate.setText(String.format("%02d.%02d.%04d", startDay, startMonth, startYear));
//    TextView endDate = (TextView) viewDialog.findViewById(R.id.dialog_diagram_textViewEndDate);
//    endDate.setText(String.format("%02d.%02d.%04d", endDay, endMonth, endYear));
//
//    ImageButton startDateButton =
//            (ImageButton) viewDialog.findViewById(R.id.dialog_diagram_settings_buttonStartDate);
//    ImageButton endDateButton =
//            (ImageButton) viewDialog.findViewById(R.id.dialog_diagram_settings_buttonEndDate);
//
//    DialogRecordsSettings.DialogImageButtonClickListener imageButtonListener = new DialogRecordsSettings.DialogImageButtonClickListener(minDay,
//            minMonth, minYear, maxDay, maxMonth, maxYear, this);
//    startDateButton.setOnClickListener(imageButtonListener);
//    endDateButton.setOnClickListener(imageButtonListener);
//
//    CheckBox
//            bothAll = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_allBoth),
//            bothPower = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_powerBoth),
//            bothEnergy = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_energyBoth),
//            panel1All = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_all1),
//            panel1Voltage = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_voltage1),
//            panel1Current = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_current1),
//            panel1Power = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_power1),
//            panel1Energy = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_energy1),
//            panel2All = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_all2),
//            panel2Voltage = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_voltage2),
//            panel2Current = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_current2),
//            panel2Power = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_power2),
//            panel2Energy = (CheckBox) viewDialog.findViewById(R.id.dialog_diagram_settings_energy2);
//
//    final DialogRecordsSettings.DialogCheckboxClickListener checkboxListener =
//            new DialogRecordsSettings.DialogCheckboxClickListener(bothAll, bothPower, bothEnergy,
//                    panel1All, panel1Voltage, panel1Current, panel1Power, panel1Energy,
//                    panel2All, panel2Voltage, panel2Current, panel2Power, panel2Energy);
//
//    bothAll.setOnClickListener(checkboxListener);
//    bothPower.setOnClickListener(checkboxListener);
//    bothEnergy.setOnClickListener(checkboxListener);
//    panel1All.setOnClickListener(checkboxListener);
//    panel1Voltage.setOnClickListener(checkboxListener);
//    panel1Current.setOnClickListener(checkboxListener);
//    panel1Power.setOnClickListener(checkboxListener);
//    panel1Energy.setOnClickListener(checkboxListener);
//    panel2All.setOnClickListener(checkboxListener);
//    panel2Voltage.setOnClickListener(checkboxListener);
//    panel2Current.setOnClickListener(checkboxListener);
//    panel2Power.setOnClickListener(checkboxListener);
//    panel2Energy.setOnClickListener(checkboxListener);
//
//    final DialogRecordsSettings.DialogOKClickListener oKListener = new DialogRecordsSettings.DialogOKClickListener(bothPower, bothEnergy,
//            panel1Voltage, panel1Current, panel1Power, panel1Energy,
//            panel2Voltage, panel2Current, panel2Power, panel2Energy);
//
//    builder.setPositiveButton(getResources().getText(R.string.dialog_diagram_ok), oKListener);
//    builder.setNegativeButton(getResources().getText(R.string.dialog_diagram_cancel), null);
//
//    restoreData(savedInstanceState, this.getArguments(),
//            startDate, endDate,
//            bothAll, bothPower, bothEnergy,
//            panel1All, panel1Voltage, panel1Current, panel1Power, panel1Energy,
//            panel2All, panel2Voltage, panel2Current, panel2Power, panel2Energy);
//    return builder.create();
//  }
//
//
//
//  @Override
//  public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                           Bundle savedInstanceState)
//  {
//    // Inflate the layout for this fragment
//    return inflater.inflate(R.layout.fragment_dialog_records_energy_settings, container, false);
//  }

}
