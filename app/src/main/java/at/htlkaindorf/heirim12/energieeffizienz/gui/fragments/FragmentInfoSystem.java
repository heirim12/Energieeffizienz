package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.htlkaindorf.heirim12.energieeffizienz.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentInfoSystem extends Fragment {


    public FragmentInfoSystem() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(getString(R.string.fragment_info_system_title));
        return inflater.inflate(R.layout.fragment_info_system, container, false);
    }

}
