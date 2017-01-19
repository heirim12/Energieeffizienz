package at.htlkaindorf.heirim12.energieeffizienz.gui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import at.htlkaindorf.heirim12.energieeffizienz.R;

/**
 * Created by richard on 08.12.2016.
 */

public class DialogDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
  private OnDatePickerOKListener myOKListener;

  // Listener which must be implemented in the Activity/Fragment where the dialog is opened
  // with this Listener the Activity/Fragment gets the data from the dialog
  public interface OnDatePickerOKListener
  {
    public void onDatePickerOkListener(int day, int month, int year);
  }

  // is called when the user presses the OK-button
  // => this methods automatically gets the chosen date
  @Override
  public void onDateSet(DatePicker datePicker, int year, int month, int day)
  {
    this.myOKListener.onDatePickerOkListener(day, (month+1), year);
  }

  // make sure the (Activity)/Fragment implemented the OnDatePickerOKListener
  // make sure that the Activity/Fragment has made a Bundle with the required data
  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);
    try
    {
      myOKListener = (OnDatePickerOKListener) getTargetFragment();
    }
    catch (ClassCastException ex)
    {
      throw new ClassCastException(getTargetFragment().toString() + " must implement OnDatePickerOKListener!");
    }
    catch (NullPointerException ex)
    {
      throw  new NullPointerException(getTargetFragment().toString() + "Date range must be implemented! (with a Bundle");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    // Create a new instance of DatePickerDialog
    DatePickerDialog datePickerDialog =
            new DatePickerDialog(getContext(), this,
                    getArguments().getInt("currentDay"),
                    getArguments().getInt("currentMonth") -1,
                    getArguments().getInt("currentYear"));

    // Set min and max date
    Calendar rangeDate = new GregorianCalendar(
            getArguments().getInt("minYear"),
            getArguments().getInt("minMonth") -1,
            getArguments().getInt("minDay"));

    datePickerDialog.getDatePicker().setMinDate(rangeDate.getTimeInMillis());

    rangeDate = new GregorianCalendar(
            getArguments().getInt("maxYear"),
            getArguments().getInt("maxMonth") -1,
            getArguments().getInt("maxDay"));

    datePickerDialog.getDatePicker().setMaxDate(rangeDate.getTimeInMillis());

    return datePickerDialog;
  }
}
