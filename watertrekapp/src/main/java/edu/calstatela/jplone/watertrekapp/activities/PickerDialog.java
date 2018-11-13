//package edu.calstatela.jplone.watertrekapp.activities;
//
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.widget.DatePicker;
//import android.widget.TextView;
//
//import java.util.Calendar;
//
//import edu.calstatela.jplone.watertrekapp.R;
//
//public class PickerDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener{
//    Context context;
//    //PROBABLY DELETE THIS WHOLE CLASS
////    private DatePickerDialog.OnDateSetListener mDateSetListener;
////    public PickerDialog() {
////    }
////
////    @SuppressLint("ValidFragment")
////    public PickerDialog(DatePickerDialog.OnDateSetListener callback, Calendar MinDate, Calendar MaxDate, Context context) {
////        mDateSetListener = callback;
////        this.MinDate = MinDate;
////        this.MaxDate = MaxDate;
////        this.context = context;
////    }
////    @SuppressLint("ValidFragment")
////    public PickerDialog(DatePickerDialog.OnDateSetListener callback, Context context) {
////        mDateSetListener = callback;
////        this.context = context;
////    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
////            DateSettings dateSettings = new DateSettings(getActivity());
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        DatePickerDialog dialog;
////            dialog = new DatePickerDialog(getActivity(),dateSettings,year,month,day);
////
////            return dialog;
//
//        return new DatePickerDialog(getActivity(),this,year,month,day);
//    }
//
//
//    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//        int month = monthOfYear +1;
//        String datechosen = (month + "/" + dayOfMonth +"/" + year);
////        Toast.makeText(context,datechosen,Toast.LENGTH_LONG).show();
//        Log.w("DatePicker",datechosen);
////        return new DatePickerDialog(getActivity(), (HistoryActivity)getActivity(), year, month, dayOfMonth);
////        ((TextView) getView().findViewById(R.id.startView)).setText(datechosen);
////            view.getTag("start_date_picker");
////        getActivity().getSupportFragmentManager().ge
////        String mystring = getActivity().getResources().getString(R.string.start_date_picker);
//
////        getActivity().getResources().getString(R.id.startView).getClass();
//        Fragment fragment = getFragmentManager().findFragmentByTag("start_date_picker");
////        String fragy = fragment.toString();
////        Log.w("poop",fragment);
//        if(fragment == null) {
//            ((TextView) getActivity().findViewById(R.id.startView)).setText(datechosen);
//
////            Intent intent = new Intent(getContext(), HistoryActivity.class);
////            intent.putExtra("beginerdate", datechosen);
//            Intent intent = new Intent(getContext(), HistoryActivity.class);
//            intent.putExtra("started", datechosen);
//
////            Bundle bundle = new Bundle();
////            bundle.putString("started", datechosen);
////            intent.putExtras(bundle);
//
//        }
//
//        else if(fragment != null) {
//            ((TextView) getActivity().findViewById(R.id.endView)).setText(datechosen);
//            Intent intent = new Intent(getContext(), HistoryActivity.class);
//            intent.putExtra("ended", datechosen);
//
//        }
//
//
//    }
//
//}
