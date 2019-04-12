package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListFragment;
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

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    TextView starttext;
    TextView endtext;
    public String firstDate;
    public String lastDate;
    public int startBclick, endBclick;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
            return  new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT,this,year,month,day);
//        return new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,(DatePickerDialog.OnDateSetListener) getActivity(),year,month,day);
    }



public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
// calls on date set need to find a way to access textview from ListFragment
//    ((ListFragment)getActivity()).yourPublicMethod();
//    FragmentManager fm = getFragmentManager();
//    ListFragment fragm = (ListFragment) fm.findFragmentById(R.);
//    fragm.onDateSet();


}






}
