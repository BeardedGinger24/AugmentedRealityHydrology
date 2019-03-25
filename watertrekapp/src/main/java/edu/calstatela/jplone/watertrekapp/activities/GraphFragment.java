package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.calstatela.jplone.watertrekapp.R;
public class GraphFragment extends Fragment {
@Override
public View onCreateView(LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState)
    {
    //---Inflate the layout for this fragment---
    return inflater.inflate(
            R.layout.graph_view_fragment, container, false);
    }






}
