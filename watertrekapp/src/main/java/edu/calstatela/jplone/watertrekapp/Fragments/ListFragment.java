package edu.calstatela.jplone.watertrekapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.calstatela.jplone.watertrekapp.R;

public class ListFragment extends Fragment {
    //********************
    String  welluniqueID;
    String SoilMoistureuniqueID;
    String  SnoteluniqueID;
    String  RiveruniqueID;
    String ReservoiruniqueID;
    //*******************
    Boolean isWellNull;
    Boolean isRiverNull;
    Boolean isReservoirNull;
    Boolean isSoilNull;
    Boolean isSnotelNull;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.list_view_fragment, container, false);
    }
}