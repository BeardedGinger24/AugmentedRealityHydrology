package edu.calstatela.jplone.watertrekapp.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.DataService.WellService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.R;


//FragmentActivity
public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    TextView starttext;
    TextView endtext ;
    public String firstDate;
    public String lastDate;

    private TextView mDisplaydate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    //DELETE TESTERLIST

    private ArrayList<String> dbgsUList = new ArrayList<>();
    public int startBclick,endBclick;


//need to change dialog from calender to scroller

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        GraphView graph = (GraphView) findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        graph.addSeries(series);

        Button searchButt = findViewById(R.id.Search);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);



    Button startButton = (Button)findViewById(R.id.sdate);
    startButton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view)
        {

            startBclick = 1;
            endBclick =2;
            DialogFragment dp = new DatePickerFragment();
            dp.show(getSupportFragmentManager(),"start_date_chosen");
        }
    });

        Button endButton = (Button)findViewById(R.id.edate);
        endButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                endBclick = 1;
                startBclick = 2;


                DialogFragment dialogpicker = new DatePickerFragment();
                dialogpicker.show(getSupportFragmentManager(),"end_date_chosen");
            }
        });

    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        // Example of date format ?? /2017-05-06 yr/month/day
        Calendar calendar = new GregorianCalendar(year,month,dayOfMonth);
//        Calendar calendar = GregorianCalendar.getInstance();
//        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        month = month+1;
        String smonth = Integer.toString(month);
        String sdayOfMonth =  Integer.toString(dayOfMonth);

        String fixeddayOfMonth = "0" + dayOfMonth;
        String fixedmonth = "0" + month;
        // both month and day need to append a zero
        // if else statements just for now  Need to find a way to Verify End Date is not Before Start Date ????
        // This can be done by concatenating dates and converting to int and then checking which int is bigger Remember to Implement?????
        if ((smonth.length() == 1) && (sdayOfMonth.length() == 1))
        {
            String datechosen = (year+ "-" + fixedmonth +"-" + fixeddayOfMonth);
            if(startBclick == 1)
            {
                starttext =(TextView) findViewById(R.id.startView);
                starttext.setText(datechosen);
                firstDate = datechosen;
            }
            if(endBclick == 1)
            {
                endtext = (TextView) findViewById(R.id.endView);
                endtext.setText(datechosen);
                lastDate = datechosen;
            }
        }
        else if  ((smonth.length() == 1) && (sdayOfMonth.length() != 1))
        {

            String datechosen = (year+ "-" + fixedmonth +"-" + dayOfMonth);
            if(startBclick == 1)
            {
                starttext =(TextView) findViewById(R.id.startView);
                starttext.setText(datechosen);
                firstDate = datechosen;
            }
            if(endBclick == 1)
            {
                endtext = (TextView) findViewById(R.id.endView);
                endtext.setText(datechosen);
                lastDate = datechosen;
            }
        }
        else if  ((smonth.length() != 1) && (sdayOfMonth.length() == 1))
        {
            String datechosen = (year+ "-" + month +"-" + fixeddayOfMonth);
            if(startBclick == 1)
            {
                starttext =(TextView) findViewById(R.id.startView);
                starttext.setText(datechosen);
                firstDate = datechosen;
            }
            if(endBclick == 1)
            {
                endtext = (TextView) findViewById(R.id.endView);
                endtext.setText(datechosen);
                lastDate = datechosen;
            }
        }
        else {

            String datechosen = (year + "-" + month + "-" + dayOfMonth);
            if(startBclick == 1)
            {
                starttext =(TextView) findViewById(R.id.startView);
                starttext.setText(datechosen);
                firstDate = datechosen;
            }
            if(endBclick == 1)
            {
                endtext = (TextView) findViewById(R.id.endView);
                endtext.setText(datechosen);
                lastDate = datechosen;
            }

        }



    }


    // retrieve data from url  NEED to Fix to pass mastersiteID of the well

    private void addWells(){

        WellService.getDBGSunits(wellNetworkCallback, firstDate, lastDate);

    }

    NetworkTask.NetworkCallback wellNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            List<String> dbgsunitList = WellService.parseDBGSunits(result);
            for(String dbu : dbgsunitList){
                dbgsUList.add(dbu);

            }
        }
    };






    // [ass firstDate && LastDate into a method that will retrieve data and make recylrerview
    // need to make a method to make sure that firstDate Or lastDate are NOT NULL
    // Need to fix issue on Double clicking in order to get the date to display
    public void displayHistoryList(View v)
    {
        addWells();
//        Log.d("starz" , firstDate);
//        Log.d("starz" , lastDate);

        ListView lv = findViewById(R.id.historyList);
//        testerarraylist
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryActivity.this,android.R.layout.simple_list_item_1,dbgsUList);
        lv.setAdapter(adapter);
//        final StringBuilder sb = new StringBuilder(starttext.getText().length());
//        sb.append(starttext.getText());
//        String x = sb.toString();
//        Log.d("tv",x); //example 11/1/2018

    }







}
