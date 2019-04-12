package edu.calstatela.jplone.watertrekapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import edu.calstatela.jplone.watertrekapp.R;

public class GraphFragment extends Fragment {

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if(bundle != null){
            ArrayList<String> dbgs = bundle.getStringArrayList("dbgs");
            String one = dbgs.get(0);
            String two = dbgs.get(1);
            Toast.makeText(getContext(),"TESTING!",Toast.LENGTH_SHORT).show();
            Log.d("graphfrag", one);
            Log.d("graphfrag", two);
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.graph_view_fragment, container, false);
    }
}