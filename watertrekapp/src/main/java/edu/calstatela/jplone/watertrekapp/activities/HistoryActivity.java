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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.DataService.ReservoirService;
import edu.calstatela.jplone.watertrekapp.DataService.RiverService;
import edu.calstatela.jplone.watertrekapp.DataService.SnotelService;
import edu.calstatela.jplone.watertrekapp.DataService.SoilMoistureService;
import edu.calstatela.jplone.watertrekapp.DataService.WellService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.R;


//FragmentActivity
public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Context context;
    TextView starttext;
    TextView endtext;

    GraphView graph;
    LineGraphSeries<DataPoint> series;

    public String firstDate;
    public String lastDate;
    private ProgressBar pb;

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
    //    private ProgressBar pb;
    //DELETE TESTERLIST
    // Various arrayList for various POI
    //WELLS , RESERVOIR , RIVERS , SOIL , SNOTEL
    private ArrayList<String> dbgsUList = new ArrayList<>();
    private ArrayList<String> resStorageList = new ArrayList<>();
    private ArrayList<String> dischargeList = new ArrayList<>();
    private ArrayList<String> soilDepthList = new ArrayList<>();
    private ArrayList<String> sweSnotelList = new ArrayList<>();

    // Wells Dbgs
    // Rivers Discharge
    // Buttons for start Data and End Date
    public int startBclick, endBclick;
    //Unique ID for that specific chosen POI
    String WELLID;
    String RiverID;
    String ReservoirID;
    String SoilMoistureID;
    String SnotelID;
    // checks to see if id was passed thru
    Boolean isWellNull;
    Boolean isRiverNull;
    Boolean isReservoirNull;
    Boolean isSoilNull;
    Boolean isSnotelNull;


//need to change dialog from calender to scroller

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        context = this;
        WELLID = getIntent().getStringExtra("wellID");
        RiverID = getIntent().getStringExtra("RiverID");
        ReservoirID = getIntent().getStringExtra("ReservoirID");
        SoilMoistureID = getIntent().getStringExtra("SoilID");
        SnotelID = getIntent().getStringExtra("SnotelID");


        isWellNull = WELLID == null;
        isRiverNull = RiverID == null;
        isReservoirNull = ReservoirID == null;
        isSoilNull = SoilMoistureID == null;
        isSnotelNull = SnotelID == null;
        // Check to see which POI data where looking at

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

        // Create a string array of sample dates.
        String[] sampleDates = {"1991-10-31", "2013-03-20", "2013-04-20"};

        Button searchButt = findViewById(R.id.Search);

        pb = findViewById(R.id.historyLoad);
        pb.setVisibility(View.INVISIBLE);

        graph = (GraphView) findViewById(R.id.graph);

        // enable scaling and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        // Fixes issue where more than three digits would be cutoff on the y axis.
        graph.getGridLabelRenderer().setPadding(40);
        // Sets angle for label on x axis.
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(110);

        DataPoint[] dp = new DataPoint[]{new DataPoint(d1, 5), new DataPoint(d2, 10), new DataPoint(d3, 15), new DataPoint(d4, 5)};
        series = new LineGraphSeries<>(dp);
        // Sets markers on the line graph.
        series.setDrawDataPoints(true);
        // add a new series to the graph

        // set title
        // Make Case Statement to set Graph Title  depending on Selected POI
        graph.setTitle("DBGS (ft) vs. Time (YYYY-MM-DD)");
        graph.addSeries(series);


        Button startButton = (Button) findViewById(R.id.sdate);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startBclick = 1;
                endBclick = 2;
                DialogFragment dp = new DatePickerFragment();
                dp.show(getSupportFragmentManager(), "start_date_chosen");
            }
        });

        Button endButton = (Button) findViewById(R.id.edate);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endBclick = 1;
                startBclick = 2;


                DialogFragment dialogpicker = new DatePickerFragment();
                dialogpicker.show(getSupportFragmentManager(), "end_date_chosen");
            }
        });
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        // Example of date format ?? /2017-05-06 yr/month/day
        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
//        Calendar calendar = GregorianCalendar.getInstance();
//        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        month = month + 1;
        String smonth = Integer.toString(month);
        String sdayOfMonth = Integer.toString(dayOfMonth);

        String fixeddayOfMonth = "0" + dayOfMonth;
        String fixedmonth = "0" + month;
        // both month and day need to append a zero
        // if else statements just for now  Need to find a way to Verify End Date is not Before Start Date ????
        // This can be done by concatenating dates and converting to int and then checking which int is bigger Remember to Implement?????
        if ((smonth.length() == 1) && (sdayOfMonth.length() == 1)) {
            String datechosen = (year + "-" + fixedmonth + "-" + fixeddayOfMonth);
            if (startBclick == 1) {
                starttext = (TextView) findViewById(R.id.startView);
                starttext.setText(datechosen);
                firstDate = datechosen;
            }
            if (endBclick == 1) {
                endtext = (TextView) findViewById(R.id.endView);
                endtext.setText(datechosen);
                lastDate = datechosen;
            }
        } else if ((smonth.length() == 1) && (sdayOfMonth.length() != 1)) {

            String datechosen = (year + "-" + fixedmonth + "-" + dayOfMonth);
            if (startBclick == 1) {
                starttext = (TextView) findViewById(R.id.startView);
                starttext.setText(datechosen);
                firstDate = datechosen;
            }
            if (endBclick == 1) {
                endtext = (TextView) findViewById(R.id.endView);
                endtext.setText(datechosen);
                lastDate = datechosen;
            }
        } else if ((smonth.length() != 1) && (sdayOfMonth.length() == 1)) {
            String datechosen = (year + "-" + month + "-" + fixeddayOfMonth);
            if (startBclick == 1) {
                starttext = (TextView) findViewById(R.id.startView);
                starttext.setText(datechosen);
                firstDate = datechosen;
            }
            if (endBclick == 1) {
                endtext = (TextView) findViewById(R.id.endView);
                endtext.setText(datechosen);
                lastDate = datechosen;
            }
        } else {

            String datechosen = (year + "-" + month + "-" + dayOfMonth);
            if (startBclick == 1) {
                starttext = (TextView) findViewById(R.id.startView);
                starttext.setText(datechosen);
                firstDate = datechosen;
            }
            if (endBclick == 1) {
                endtext = (TextView) findViewById(R.id.endView);
                endtext.setText(datechosen);
                lastDate = datechosen;
            }

        }


    }


    public Boolean dateVerifier() {
        // Example of Date format: 2017-05-06 yr/month/day
        String[] sparts = firstDate.split("-");
        String yStartDate = sparts[0];
        String mStartDate = sparts[1];
        String dStartDate = sparts[2];
        String parsedStartDate = yStartDate + mStartDate + dStartDate;
        int startDate = Integer.parseInt(parsedStartDate);

        String[] eparts = lastDate.split("-");
        String yEndDate = eparts[0];
        String mEndDate = eparts[1];
        String dEndDate = eparts[2];
        String parsedEndDate = yEndDate + mEndDate + dEndDate;
        int EndDate = Integer.parseInt(parsedEndDate);

        if (startDate < EndDate) {
            //correct output
            Toast.makeText(getApplicationContext(), " Searching...", Toast.LENGTH_LONG).show();
            return true;

        } else if (startDate > EndDate) {
            //Start Date Cannot Preceed EndDate
            Toast.makeText(getApplicationContext(), "Start Date Cannot Preceed EndDate", Toast.LENGTH_LONG).show();
            return false;


        } else {
            //Date Cannot be the Same
            Toast.makeText(getApplicationContext(), "Start Date Cannot EQUAL  EndDate", Toast.LENGTH_LONG).show();
            return false;

        }
    }

    private void addWells(String WELLID) {
        if (dateVerifier() != false){
            pb.setVisibility(View.VISIBLE);
            WellService.getDBGSunits(wellNetworkCallback, firstDate, lastDate,WELLID);
            pb.setVisibility(View.INVISIBLE);

        }
    }


    private void addReservoirs (String ReservoirID)
    {
        if(dateVerifier() !=false){
            pb.setVisibility(View.VISIBLE);
            Log.d("reserves", "Calling ALL RESERVOIRS");
            ReservoirService.getStorage(reservoirNetworkCallback, firstDate, lastDate, ReservoirID);
            pb.setVisibility(View.VISIBLE);
        }
    }


    private void addRivers(String RiverID){
        if (dateVerifier() != false){
            pb.setVisibility(View.VISIBLE);
            Log.d("discharge", "Calling rivernetworkcallback");
            RiverService.getDischarge(riverNetworkCallback, firstDate, lastDate, RiverID);
            pb.setVisibility(View.VISIBLE);
        }
    }

    private void addSoils(String soilMoistureID){
        if (dateVerifier() != false){
            pb.setVisibility(View.VISIBLE);
            Log.d("soily", "Calling SOILnetworkcallback");
            SoilMoistureService.getSoilDepthThruTime(soilNetworkCallback, firstDate, lastDate, SoilMoistureID);
            pb.setVisibility(View.VISIBLE);
        }
    }


    private void addSnotel(String snotID){
        if (dateVerifier() != false){
            pb.setVisibility(View.VISIBLE);
            Log.d("snow", "Calling snotelnetworkcallback");
            SnotelService.getSnotelTimeSeriesStartThruFinish(snowtelNetworkCallback, firstDate, lastDate, snotID);
            pb.setVisibility(View.VISIBLE);
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

            double currentValue = 0;
            try {
                currentValue = Double.parseDouble(arrayList.get(i));
                if (maxValue < currentValue) {
                    maxValue = currentValue;
                }

            } catch (Exception e) {
                Log.i("Null value detected.", "");
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
            double currentValue = 0;
            try {
                currentValue = Double.parseDouble(arrayList.get(i));
                if (minValue > currentValue) {
                    minValue = currentValue;
                }

            } catch (Exception e) {
                Log.i("Null value detected.", "");
            }
        }
        return minValue;
    }

    private DataPoint[] bubbleSortDates(DataPoint[] dataPoints) {
        int n = dataPoints.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                double x1 = 0;
                double x2 = 0;
                try {
                     x1 = dataPoints[j].getX();
                     x2 = dataPoints[j + 1].getX();
                } catch (Exception e) {
                    Log.i("Not able to getX" , "");
                }

                if (x1 > x2) {
                    // swap arr[j+1] and arr[i]
                    DataPoint temp = dataPoints[j];
                    dataPoints[j] = dataPoints[j + 1];
                    dataPoints[j + 1] = temp;
                }
            }
        }
        return dataPoints;
    }


    //Method to order dates in ascending order.
    private DataPoint[] ascendingDates(DataPoint[] dataPoints) {
        DataPoint[] dp = new DataPoint[dataPoints.length];
        Arrays.sort(dataPoints);

        return dp;
    }

    // METHOD that populates recyclerView
    //*****************************WELL RecylerView Starts ******************************************
    NetworkTask.NetworkCallback wellNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            List<String> dbgsunitList = WellService.parseDBGSunits(result);
            if (dbgsunitList.size() < 1) {
                Toast.makeText(getApplicationContext(), " No informationhas been recorded thus far", Toast.LENGTH_LONG).show();
                return;
            } else {
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
                    try {
                        date = simpleDateFormat.parse(simpleDateFormat.format(simpleDateFormat.parse(xValue.get(i))));
                        Log.d("Date Format",date+"");
                        dateList.add(date);

                    } catch (ParseException e) {
                    }
                }

                DataPoint[] dataPoints = new DataPoint[xValue.size()];
                DataPoint dataPoint = null;
                String isNull = "null";
                for (int i = 0; i < dateList.size(); i++) {
                    try {
                        if (yValue.get(i) != null && dateList.get(i) != null) {
                            dataPoint = new DataPoint(dateList.get(i), Double.parseDouble(yValue.get(i)));
                            dataPoints[i] = dataPoint;
                        }
                    } catch (Exception e) {
                        dataPoints[i] = dataPoint;
                    }
                }


                for (int i = 0; i < dataPoints.length; i++) {
                    try {
                        Log.i("Datapoint", String.valueOf(dataPoints[i].getX())+","+String.valueOf(dataPoints[i].getY()));
                    } catch (Exception e) {
                        Log.i("nullPointException", "");
                    }
                }

                dataPoints = bubbleSortDates(dataPoints);
                for(int i = 0; i<dataPoints.length;i++){
                    Log.i("dp",simpleDateFormat.format(new Date((long)dataPoints[i].getX()))+","+dataPoints[i].getY());
                }
                Log.i("dp","Min/Max X:"+dataPoints[0].getX()+","+dataPoints[dataPoints.length-1].getX());
                graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(context));
                graph.getGridLabelRenderer().setNumHorizontalLabels(10);
                graph.getGridLabelRenderer().setNumVerticalLabels(10);
                //Set Min and Max for x-axis values
                graph.getViewport().setMinX(dataPoints[0].getX());
                graph.getViewport().setMaxX(dataPoints[dataPoints.length-1].getX());
                graph.getViewport().setXAxisBoundsManual(true);

                //Set Min and Max for y-axis values
                double maxY = findMaxDouble(yValue);
                double minY = findMinDouble(yValue);
                Log.i("dp","Min/Max Y:"+minY+","+maxY);
                graph.getViewport().setMinY(minY);
                graph.getViewport().setMaxY(maxY);
                graph.getViewport().setYAxisBoundsManual(true);

                graph.getGridLabelRenderer().setHumanRounding(false);
                graph.getGridLabelRenderer().setLabelsSpace(10);
                graph.getGridLabelRenderer().setPadding(100);
                series.resetData(dataPoints);

            }

        }

    };

    //*****************************WELL RecylerView Ends ******************************************
    //**********************Reservoir Recycler View Starts*****************************************
    NetworkTask.NetworkCallback reservoirNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            List<String> resList = ReservoirService.parseIndyStorage(result);
            if (resList.size() < 1) {
//                pb.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), " No informationhas been recorded thus far", Toast.LENGTH_LONG).show();
                return;
            } else
                // clears old list so it doesnt double stack / repeat Data twice
//                pb.setVisibility(View.INVISIBLE);
                resStorageList.clear();

            for (String dbu : resList) {
                resStorageList.add(dbu);

            }
            pb.setVisibility(View.INVISIBLE);
        }
    };

    //**********************Reservoir Recycler View ENDS*****************************************

    //*****************************RIVER/STREAMGAUGES RecylerView Starts ******************************************
    NetworkTask.NetworkCallback riverNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            Log.d("discharge", "before parsingdischarges");
            List<String> disList = RiverService.parseDischarges(result);
            Log.d("discharge", "after parsingdischarges");
            if (disList.size() < 1) {
//                pb.setVisibility(View.INVISIBLE);
                Log.d("discharge", "No information has been recorded thus far");
                pb.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), " No information has been recorded thus far", Toast.LENGTH_LONG).show();

                return;
            } else {
                // clears old list so it doesnt double stack / repeat Data twice
//                pb.setVisibility(View.INVISIBLE);
                dischargeList.clear();
                for (String dsl : disList) {
//                    Log.d("discharge", dsl);
                    dischargeList.add(dsl);

                }
            }
            pb.setVisibility(View.INVISIBLE);
        }
    };

    //*****************************RIVER/STREAMGAUGES RecylerView Ends ******************************************

    //*********************Soil Moisture RecyclerView Starts**********************

    NetworkTask.NetworkCallback soilNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            Log.d("soil", "before parsingdischarges");
            List<String> soilyList = SoilMoistureService.parseindySoil(result);
            Log.d("soil", "after parsingdischarges");
            if (soilyList.size() < 1) {
//                pb.setVisibility(View.INVISIBLE);
                Log.d("soil", "No information has been recorded thus far");
                pb.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), " No information has been recorded thus far", Toast.LENGTH_LONG).show();
                return;
            } else {
                // clears old list so it doesnt double stack / repeat Data twice
//                pb.setVisibility(View.INVISIBLE);
                soilDepthList.clear();
                for (String swe : soilyList) {
//                    Log.d("discharge", dsl);
                    soilDepthList.add(swe);

                }
            }
            pb.setVisibility(View.INVISIBLE);
        }
    };


    //*********************Soil Moisture RecyclerView ENDS**********************


    //*********************SNOWTEL RecyclerView Starts**********************
    NetworkTask.NetworkCallback snowtelNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {

            List<String> snowyList = SnotelService.parseTimes(result);

            if (snowyList.size() < 1) {
//                pb.setVisibility(View.INVISIBLE);
                Log.d("snow", "No information has been recorded thus far");
                pb.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), " No information has been recorded thus far", Toast.LENGTH_LONG).show();
                return;
            } else {
                // clears old list so it doesnt double stack / repeat Data twice
//                pb.setVisibility(View.INVISIBLE);
                sweSnotelList.clear();
                for (String snot : snowyList) {
                    sweSnotelList.add(snot);

                }
            }
            pb.setVisibility(View.INVISIBLE);
        }
    };


    //*********************SNOWTELRecyclerView ENDS**********************


    // for river discharge  parse using xml

//    "https://watertrek.jpl.nasa.gov/hydrology/rest/streamgauge/site_no/09331850/discharge/from/1981-01-01T00:00:00/through/1990-01-01T00:00:00"
    // String riverurl = "https://watertrek.jpl.nasa.gov/hydrology/rest/streamgauge/site_no/09331850/discharge/from/1981-01-01T00%3A00%3A00/through/1990-01-01T00%3A00%3A00";


    // [ass firstDate && LastDate into a method that will retrieve data and make recylrerview
    // need to make a method to make sure that firstDate Or lastDate are NOT NULL
    // Need to fix issue on Double clicking in order to get the date to display

    public void displayHistoryList(View v)
    {
//        pb.setVisibility(View.VISIBLE);
//        Log.d("wwwid" , WELLID);
        if (isWellNull == false) {
            ListView lv = findViewById(R.id.historyList);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, dbgsUList);
            lv.setAdapter(adapter);
            addWells(WELLID);

        }
        if (isReservoirNull == false) {
            Log.d("reserves", "ADDED RESERVOIRS ");
            ListView lv = findViewById(R.id.historyList);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, resStorageList);
            lv.setAdapter(adapter);
            addReservoirs(ReservoirID);

        }
        if (isRiverNull == false) {
            Log.d("discharge", "ADDED RIVERS ");
            ListView lv = findViewById(R.id.historyList);
            addRivers(RiverID);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, dischargeList);
            lv.setAdapter(adapter);
        }
        if (isSoilNull == false) {
            Log.d("soily", "ADDED SOIL MOISTURE ");
            ListView lv = findViewById(R.id.historyList);
            addSoils(SoilMoistureID);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, soilDepthList);
            lv.setAdapter(adapter);
        }
        if (isSnotelNull == false) {
            Log.d("snow", "ADDED Snotels ");
            ListView lv = findViewById(R.id.historyList);
            addSnotel(SnotelID);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, sweSnotelList);
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
