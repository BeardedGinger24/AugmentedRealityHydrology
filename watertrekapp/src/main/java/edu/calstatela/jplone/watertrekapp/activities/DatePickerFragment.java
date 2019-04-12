package edu.calstatela.jplone.watertrekapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.calstatela.jplone.watertrekapp.R;

public class DatePickerFragment extends DialogFragment  {
    DatePickerDialog.OnDateSetListener ondateSet;
    private int year, month, day;

    public DatePickerFragment() {}

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }
    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT, ondateSet, year, month, day);
//            return  new DatePickerDialog(getActivity(),ondateSet,year,month,day);
//        return new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,(DatePickerDialog.OnDateSetListener) getActivity(),year,month,day);
    }







}


//**********************

//public class DatePickerFragment extends DialogFragment {
//    OnDateSetListener ondateSet;
//    private int year, month, day;
//
//    public DatePickerFragment() {}
//
//    public void setCallBack(OnDateSetListener ondate) {
//        ondateSet = ondate;
//    }
//
//    @SuppressLint("NewApi")
//    @Override
//    public void setArguments(Bundle args) {
//        super.setArguments(args);
//        year = args.getInt("year");
//        month = args.getInt("month");
//        day = args.getInt("day");
//    }
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
//    }
//}