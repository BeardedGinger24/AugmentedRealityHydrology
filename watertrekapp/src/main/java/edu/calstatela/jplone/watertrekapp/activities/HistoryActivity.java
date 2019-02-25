package edu.calstatela.jplone.watertrekapp.activities;

import android.app.DatePickerDialog;
import android.content.Context;
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
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.DataService.RiverService;
import edu.calstatela.jplone.watertrekapp.DataService.WellService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.R;


//FragmentActivity
public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Context context;

    TextView starttext;
    TextView endtext ;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    public String firstDate;
    public String lastDate;
    // xValue ArrayList stores the values of the x-axis in the graph.
    public ArrayList<String> xValue = new ArrayList<>();
    // yValue ArrayList stores the values of the y-axis in the graph.
    public ArrayList<String> yValue = new ArrayList<>();
    // dateList ArrayList is used to create a List of Dates from xValue;
    public ArrayList<Date> dateList = new ArrayList<>();


    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    private TextView mDisplaydate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    //DELETE TESTERLIST
    // Various arrayList for various POI
    private ArrayList<String> dbgsUList = new ArrayList<>();
    private ArrayList<String> dischargeList = new ArrayList<>();
    // Wells Dbgs
    // Rivers Discharge
    // Buttons for start Data and End Date
    public int startBclick,endBclick;
    //Unique ID for that specific chosen POI
    String WELLID;
    String RiverID;
    String ReservoirID;
    String SoilMoistureID;
    Boolean isWellNull;
    Boolean isRiverNull;
    Boolean isReservoirNull;
    Boolean isSoilNull;


//need to change dialog from calender to scroller

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;

        WELLID = getIntent().getStringExtra("wellID");
        RiverID = getIntent().getStringExtra("RiverID");
        ReservoirID = getIntent().getStringExtra("ReservoirID");
        SoilMoistureID = getIntent().getStringExtra("SoilID");
//        Log.d("isnull?", "WELL: " + WELLID);
//        Log.d("isnull?", "River: " + RiverID);
//        Log.d("isnull?", "Reservoir: " + ReservoirID);
//        Log.d("isnull?", "Soil: " + SoilMoistureID);
         isWellNull = WELLID == null;
         isRiverNull = RiverID == null;
         isReservoirNull = ReservoirID == null;
         isSoilNull = SoilMoistureID == null;
        // Check to see which POI data where looking at

        // Button with text as Show History and onClick set to displayHistoryList
        Button searchButt = findViewById(R.id.Search);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // used to format dates
       simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


        // Generate Dates.
        // Calender.getInstance() sets the calender to current date and time.
        calendar = Calendar.getInstance();
        // calender.getTime() returns a Date object representing the calenders.
        Date d1 = calendar.getTime();
        // calender.add() takes in two arugments which consist of a Calender value. Calender.DATE for the day of the month. The second argument being an integer have that will add a value to that specified date/time.
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d4 = calendar.getTime();
//        calendar.set(1991, 10, 31);
//        Date d5 = calendar.getTime();

        // Create a string array of sample dates.
        String[] sampleDates = {"1991-10-31", "2013-03-20", "2013-04-20"};

        // Used to test parsing of strings to dates with simpleDateFormat.parse method.
//        Date d5 = new Date();
//        try {
//            d5 = simpleDateFormat.parse(sampleDates[0]);
//
//            Log.i("Date example", d5.toString());
//
//        } catch (ParseException e) {
//        }


        Log.i("Calendar", calendar.getTime().toString());

        graph = (GraphView) findViewById(R.id.graph);


//        DataPoint[] dp = new DataPoint[]{ new DataPoint(d1, 5), new DataPoint(d2, 10), new DataPoint(d3, 15), new DataPoint(d4, 5), new DataPoint(d5,15)};
        DataPoint[] dp = new DataPoint[]{ new DataPoint(d1, 5), new DataPoint(d2, 10), new DataPoint(d3, 15), new DataPoint(d4, 5)};


//         populate series with DataPoints
        series = new LineGraphSeries<>(dp
        );

        series.setDrawDataPoints(true);

        // sets angle for label on x axis.
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(110);

        Log.i("Date-1", String.valueOf(d1));

        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
//        graph.getViewport().setMinX(d1.getTime());
//        graph.getViewport().setMaxX(d3.getTime());
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d4.getTime());


        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(20);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(false); // enables vertical zooming and scrolling

        // add a new series to the graph
        graph.addSeries(series);

        // set title
        // Make Case Statement to set Graph Title  depending on Selected POI
        graph.setTitle("DBGS (ft) vs. Time (YYYY-MM-DD)");

        // custom label formatter to show feet "ft" and date; DefaultLabelFormatter()
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show date for x values
                    return simpleDateFormat.format(new Date((long)value));
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        // as we use dates as labels, the human rounding to nice readable numbers
        graph.getGridLabelRenderer().setHumanRounding(false);
        // count of the horizontal labels, that will be shown at one time
        // the number of x-axis values shown
        graph.getGridLabelRenderer().setNumHorizontalLabels(dp.length);
        // Fixes issue where more than three digits would be cutoff on the y axis.
        graph.getGridLabelRenderer().setPadding(40);

        // ?



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
    public Boolean dateVerifier(){
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
        if ((yearStartDate == yearEndDate) && (monthStartdate == monthEnddate) && (dayStartDate == dayEndDate) ){
            Log.d("checkDate","Dates cannot be the same");
            Toast.makeText(getApplicationContext(), " Start Date cannot be the same as EndDate", Toast.LENGTH_LONG).show();
            return false;
        }

        else if (yearStartDate > yearEndDate){
            Log.d("checkDate","yearstartdate is Wrong cannot search backwards!!!");
            Toast.makeText(getApplicationContext(), "End Date cannot Preceed Start Date", Toast.LENGTH_LONG).show();
            return false;
        }
        //correct year input
        else if  (yearStartDate <= yearEndDate){
//            Toast.makeText(getApplicationContext(), "Searching ...", Toast.LENGTH_LONG).show();
            Log.d("checkDate","yearstartdate is correct");

            //wrong month input
            if (monthStartdate > monthEnddate){
                Log.d("checkDate","monthstartdate is Wrong cannot search backwards!!!");
                Toast.makeText(getApplicationContext(), "End Date cannot Preceed Start Date", Toast.LENGTH_LONG).show();
                return false;

            }
            // correct month input
            else if (monthStartdate <= monthEnddate)
            {
                Log.d("checkDate","monthstartdate is correct");
                //wong day input
                if (dayStartDate > dayEndDate){
                    Log.d("checkDate","daystartdate is Wrong cannot search backwards!!!");
                    Toast.makeText(getApplicationContext(), "End Date cannot Preceed Start Date", Toast.LENGTH_LONG).show();
                    return false;

                }
                //rihgt day input
                else if (dayStartDate <= dayEndDate){
                    Log.d("checkDate","daystartdate is correct");
                    Toast.makeText(getApplicationContext(), "Searching ...", Toast.LENGTH_LONG).show();
                    return true;
                }

            }
        }
        // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$DATE VERIFICATION$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        return true;
    }

    private void addWells(String WELLID){
//        //$$$$$$$$$$$$$$$$$$ clear list so things dont pile up twice
//        dbgsUList.clear();
//        dateVerifier();
        if (dateVerifier() != false){
            WellService.getDBGSunits(wellNetworkCallback, firstDate, lastDate,WELLID);
        }
//        RiverService.getDischarge(riverNetworkCallback,firstDate, lastDate,RiverID);
    }

    private void addRivers(String RiverID){
//        dateVerifier();
        if (dateVerifier() != false){
            RiverService.getDischarge(riverNetworkCallback,firstDate, lastDate,RiverID);
        }
    }

    //Method to find max value of an arraylist of strings
    private double findMaxDouble(ArrayList<String> arrayList) {
        double maxValue = 0;
        try {
             maxValue = Double.parseDouble(arrayList.get(0));
        } catch (Exception e) {
            Log.i("Out of bounds", String.valueOf(maxValue));
        }
        for (int i = 1; i < arrayList.size(); i++) {
            double currentValue = Double.parseDouble(arrayList.get(i));
            if (maxValue < currentValue) {
                maxValue = currentValue;
            }
        }
        return maxValue;
    }
    //Method to find min value of an arraylist of strings
    private double findMinDouble(ArrayList<String> arrayList) {
        double minValue = 0;
        try {
            minValue = Double.parseDouble(arrayList.get(0));
        } catch (Exception e) {
            Log.i("Out of bounds", String.valueOf(minValue));
        }
        for (int i = 1; i < arrayList.size(); i++) {
            double currentValue = Double.parseDouble(arrayList.get(i));
            if (minValue > currentValue) {
                minValue = currentValue;
            }
        }
        return minValue;
    }

    //Method to order dates.
//    private ArrayList<String> orderXValues(ArrayList<String> xList) {
//
//
//        return xList;
//    }

    // METHOD that populates recyclerView
    //*****************************WELL RecylerView Starts ******************************************
    NetworkTask.NetworkCallback wellNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            List<String> dbgsunitList = WellService.parseDBGSunits(result);
            if (dbgsunitList.size() < 1){
                Toast.makeText(getApplicationContext(), " No informationhas been recorded thus far", Toast.LENGTH_LONG).show();
                return;
            }
            else {
                // clears old list so it doesnt double stack / repeat Data twice
                dbgsUList.clear();
                yValue.clear();
                xValue.clear();
                for (int i = 0; i < dbgsunitList.size(); i++) {
                    String dbu = dbgsunitList.get(i);
                    dbgsUList.add(dbu);
                    if (i > 0) {
                        String[] date = dbu.split("T");
                        // values are seperated by tabs not spaces.
                        String[] value = dbu.split("\t");
                        xValue.add(date[0]);
                        yValue.add(value[1]);
                        Log.i("x-value", xValue.get(i - 1));
                        Log.i("y-value", yValue.get(i - 1));
                    }
                }

                Date earliestDate = new Date();
                Date latestDate = new Date();
                for (int i = 0; i < xValue.size(); i++) {
                    Date date = new Date();
                    if (i == 0) {
                        try {
                            earliestDate = simpleDateFormat.parse(xValue.get(0));


                        } catch (ParseException e) {
                        }
                    }
                    if (i == xValue.size() -1) {
                        try {
                            latestDate = simpleDateFormat.parse(xValue.get(xValue.size() - 1));

                        } catch (ParseException e) {
                        }
                    }
                    try {
                        date = simpleDateFormat.parse(xValue.get(i));
                        dateList.add(date);

                    } catch (ParseException e) {
                    }
                }

                DataPoint[] dataPoints = new DataPoint[xValue.size()];
                DataPoint dataPoint;
                for (int i = 0; i < dateList.size(); i++) {
                    try {
                        if (yValue.get(i) != null) {
                            dataPoint = new DataPoint(dateList.get(i), Double.parseDouble(yValue.get(i)));
                            dataPoints[i] = dataPoint;
                        }
                    } catch (Exception e) {
                        Log.i("Dunno", "dunno");
                    }
                }

                //Set Min and Max for x-axis values
                graph.getViewport().setMinX(earliestDate.getTime());
                graph.getViewport().setMaxX(latestDate.getTime());

                Log.i("Earliest Date", earliestDate.toString());
                Log.i("Earliest Date", latestDate.toString());

                double maxY = findMaxDouble(yValue);
                Log.i("MaxY", String.valueOf(maxY));
                double minY = findMinDouble(yValue);
                Log.i("MinY", String.valueOf(minY));
                //Set Min and Max for y-axis values
                graph.getViewport().setMinY(minY);
                graph.getViewport().setMaxY(maxY);



//                for (int i = 0; i < xValue.size(); i++) {
//                    Log.i("Datapoint", String.valueOf(dataPoints[i]));
//                }
                for (int i = 0; i < dataPoints.length; i++) {
                    Log.i("Datapoint", String.valueOf(dataPoints[i]));
                }
//                graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(context));

                graph.getViewport().setXAxisBoundsManual(true);
                graph.getGridLabelRenderer().setHumanRounding(false);
                graph.getGridLabelRenderer().setNumHorizontalLabels(dataPoints.length);



                series.resetData(dataPoints);

            }

            }

    };

    //*****************************WELL RecylerView Ends ******************************************

    //*****************************RIVER/STREAMGAUGES RecylerView Starts ******************************************
    NetworkTask.NetworkCallback riverNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            List<String> dischargeList = RiverService.parseDischarges(result);
            if (dischargeList.size() < 1){
                Toast.makeText(getApplicationContext(), " No informationhas been recorded thus far", Toast.LENGTH_LONG).show();
                return;
            }
            else
                // clears old list so it doesnt double stack / repeat Data twice
                dischargeList.clear();
            for(String dsl : dischargeList){
                dischargeList.add(dsl);

            }
        }
    };

    //*****************************RIVER/STREAMGAUGES RecylerView Ends ******************************************

    // for river discharge  parse using xml

//    "https://watertrek.jpl.nasa.gov/hydrology/rest/streamgauge/site_no/09331850/discharge/from/1981-01-01T00:00:00/through/1990-01-01T00:00:00"
   // String riverurl = "https://watertrek.jpl.nasa.gov/hydrology/rest/streamgauge/site_no/09331850/discharge/from/1981-01-01T00%3A00%3A00/through/1990-01-01T00%3A00%3A00";

    // [ass firstDate && LastDate into a method that will retrieve data and make recylrerview
    // need to make a method to make sure that firstDate Or lastDate are NOT NULL
    // Need to fix issue on Double clicking in order to get the date to display
    // displayHistoryList is linked to button where id="Search"
    public void displayHistoryList(View v)
    {
//        Log.d("wwwid" , WELLID);
        if(isWellNull == false){
            addWells(WELLID);
            ListView lv = findViewById(R.id.historyList);
//        testerarraylist
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryActivity.this,android.R.layout.simple_list_item_1,dbgsUList);
            lv.setAdapter(adapter);
            Log.i("xValue", "hello");
        }
        if (isRiverNull == false){
            addRivers(RiverID);
            ListView lv = findViewById(R.id.historyList);
//        testerarraylist
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryActivity.this,android.R.layout.simple_list_item_1,dischargeList);
            lv.setAdapter(adapter);
        }

//        Log.d("starz" , firstDate);
//        Log.d("starz" , lastDate);


//        final StringBuilder sb = new StringBuilder(starttext.getText().length());
//        sb.append(starttext.getText());
//        String x = sb.toString();
//        Log.d("tv",x); //example 11/1/2018

    }
}
