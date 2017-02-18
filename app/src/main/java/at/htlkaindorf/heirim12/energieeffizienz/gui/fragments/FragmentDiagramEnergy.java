package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

import java.util.concurrent.ExecutorService;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.data.Records;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettings;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDiagramEnergy extends Fragment
{
  //================================================================================================
  // Global declarations
  //================================================================================================
  private View thisFragment;
  private LineChart lineChart1 = null;
  private ExecutorService executor = null;
  private RecordsSettings recordsSettings = null;
  private Records records;


  public FragmentDiagramEnergy()
  {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState)
  {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_fragment_diagram_energy, container, false);
  }

}
