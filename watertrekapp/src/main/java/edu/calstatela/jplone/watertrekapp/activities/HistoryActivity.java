package edu.calstatela.jplone.watertrekapp.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    String WELLID;

//need to change dialog from calender to scroller

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WELLID = getIntent().getStringExtra("wellID");
        Button searchButt = findViewById(R.id.Search);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // used to format dates
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M-dd-yyyy");

        // generate Dates
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();

        GraphView graph = (GraphView) findViewById(R.id.graph);

        // populate series with DataPoints
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(d1, 100),
                new DataPoint(d2, 150),
                new DataPoint(d3, 230)
        });


        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d3.getTime());

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(100);
        graph.getViewport().setMaxY(200);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling

        // add a new series to the graph
        graph.addSeries(series);

        // set title
        graph.setTitle("DBGS vs. Time");

        // custom label formatter to show feet "ft" and date
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show date for x values
                    return simpleDateFormat.format(new Date((long)value));
                } else {
                    // show feet for y values
                    return super.formatLabel(value, isValueX) + " ft";
                }
            }
        });

        // as we use dates as labels, the human rounding to nice readable numbers
        graph.getGridLabelRenderer().setHumanRounding(false);
        // count of the horizontal labels, that will be shown at one time
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

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

    private void addWells(String WELLID){
        // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$DATE VERIFICATION$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        Log.d("checkDate" , firstDate);
        Log.d("checkDate" , lastDate);
        String[] sparts = firstDate.split("-");
        Log.d("checkDate",sparts[0]);
        Log.d("checkDate",sparts[1]);
        Log.d("checkDate",sparts[2]);
        // Example of Date format: 2017-05-06 yr/month/day
        int yearStartDate = Integer.parseInt(sparts[0]);
        int monthStartdate = Integer.parseInt(sparts[1]);
        int dayStartDate = Integer.parseInt(sparts[2]);
        String[] eparts = lastDate.split("-");
        Log.d("checkDate",eparts[0]);
        Log.d("checkDate",eparts[1]);
        Log.d("checkDate",eparts[2]);
        int yearEndDate = Integer.parseInt(eparts[0]);
        int monthEnddate = Integer.parseInt(eparts[1]);
        int dayEndDate = Integer.parseInt(eparts[2]);
        // wrong year input
        if (yearStartDate > yearEndDate){
            Log.d("checkDate","yearstartdate is Wrong cannot search backwards!!!");
            Toast.makeText(getApplicationContext(), "End Date cannot Preceed Start Date", Toast.LENGTH_LONG).show();
            return;
        }
        //correct year input
        else if  (yearStartDate <= yearEndDate){
//            Toast.makeText(getApplicationContext(), "Searching ...", Toast.LENGTH_LONG).show();
            Log.d("checkDate","yearstartdate is correct");
            //wrong month input
            if (monthStartdate > monthEnddate){
                Log.d("checkDate","monthstartdate is Wrong cannot search backwards!!!");
                Toast.makeText(getApplicationContext(), "End Date cannot Preceed Start Date", Toast.LENGTH_LONG).show();
                return;

            }
            // correct month input
            else if (monthStartdate <= monthEnddate)
            {
                Log.d("checkDate","monthstartdate is correct");
                //wong day input
                if (dayStartDate > dayEndDate){
                    Log.d("checkDate","daystartdate is Wrong cannot search backwards!!!");
                    Toast.makeText(getApplicationContext(), "End Date cannot Preceed Start Date", Toast.LENGTH_LONG).show();
                    return;

                }
                //rihgt day input
                else if (dayStartDate < dayEndDate){
                    Log.d("checkDate","daystartdate is correct");
                    Toast.makeText(getApplicationContext(), "Searching ...", Toast.LENGTH_LONG).show();
                }
                else if(dayStartDate == dayEndDate){
                    Log.d("checkDate","Dates cannot be the same");
                    Toast.makeText(getApplicationContext(), " Start Date cannot be the same as EndDate", Toast.LENGTH_LONG).show();
                    return;

                }
            }
        }
        // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$DATE VERIFICATION$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        WellService.getDBGSunits(wellNetworkCallback, firstDate, lastDate,WELLID);

    }
    // METHOD that populates recyclerView
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
        Log.d("wwwid" , WELLID);

        addWells(WELLID);

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
