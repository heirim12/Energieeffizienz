package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.data.Records;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettings;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTableEnergy extends Fragment
{
//  //================================================================================================
//  // Global declarations
//  //================================================================================================
//  private View thisFragment;
//  private ExecutorService executor = null;
//  private RecordsSettings recordsSettings = null;
//  private Records records = null;
//
//  public FragmentTableEnergy()
//  {
//    // Required empty public constructor
//  }
//
//  private void createTable(Records records)
//  {
//  }
//
//  //================================================================================================
//  // Methods for saving the diagram
//  //================================================================================================
//  private void saveTable(String filename)
//  {
//    //TODO
//  }
//
//  private void saveTableSettings()
//  {
//    //TODO
//    Toast.makeText(getContext(), "Not implemented now!", Toast.LENGTH_SHORT).show();
//  }
//
//  //================================================================================================
//  // Methods for sharing the diagram
//  //================================================================================================
//  private void shareTable()
//  {
//    //TODO:
//    Toast.makeText(getContext(), "Not implemented now!", Toast.LENGTH_SHORT).show();
//  }
//
//  //================================================================================================
//  // Methods for opening the settings dialog and getting the settings
//  //================================================================================================
//  //This Methode is called when the DialogSettings Object is closed with pressed with "ok".
//  public void onRecordsSettingsOKListener(RecordsSettings recordsSettings)
//  {
//
//  }
//
//  private void openSettings()
//  {
//    final DialogFragment dialogSettings = new DialogRecordsSettings();
//    final Bundle recordSettingsBundle = new Bundle();
//    if(recordsSettings != null)
//    {
//      recordSettingsBundle.putBoolean("panel1Voltage", recordsSettings.isPanel1Voltage());
//      recordSettingsBundle.putBoolean("panel1Current", recordsSettings.isPanel1Current());
//      recordSettingsBundle.putBoolean("panel1Power", recordsSettings.isPanel1Power());
//      recordSettingsBundle.putBoolean("panel1Energy", recordsSettings.isPanel1Energy());
//      recordSettingsBundle.putBoolean("panel2Voltage", recordsSettings.isPanel2Voltage());
//      recordSettingsBundle.putBoolean("panel2Current", recordsSettings.isPanel2Current());
//      recordSettingsBundle.putBoolean("panel2Power", recordsSettings.isPanel2Power());
//      recordSettingsBundle.putBoolean("panel2Energy", recordsSettings.isPanel2Energy());
//      recordSettingsBundle.putBoolean("bothPower", recordsSettings.isBothPower());
//      recordSettingsBundle.putBoolean("bothEnergy", recordsSettings.isBothEnergy());
//      recordSettingsBundle.putLong("startDate", recordsSettings.getStartDate().getTimeInMillis());
//      recordSettingsBundle.putLong("endDate", recordsSettings.getEndDate().getTimeInMillis());
//      dialogSettings.setArguments(recordSettingsBundle);
//    }
//    dialogSettings.setTargetFragment(this, 0);
//    dialogSettings.show(getFragmentManager(), "dialogRecordsSettings");
//  }
//
//
//  @Override
//  public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                           Bundle savedInstanceState)
//  {
//    // Inflate the layout for this fragment
//    return inflater.inflate(R.layout.fragment_fragment_table_energy, container, false);
//  }

}
