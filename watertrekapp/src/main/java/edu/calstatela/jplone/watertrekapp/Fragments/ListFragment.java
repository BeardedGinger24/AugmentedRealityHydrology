package edu.calstatela.jplone.watertrekapp.Fragments;

import android.app.AlertDialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.Data.ReservoirStorageData;
import edu.calstatela.jplone.watertrekapp.Data.RiverStorageData;
import edu.calstatela.jplone.watertrekapp.DataService.ReservoirService;
import edu.calstatela.jplone.watertrekapp.DataService.RiverService;
import edu.calstatela.jplone.watertrekapp.DataService.SnotelService;
import edu.calstatela.jplone.watertrekapp.DataService.SoilMoistureService;
import edu.calstatela.jplone.watertrekapp.DataService.WellService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTaskAuth;
import edu.calstatela.jplone.watertrekapp.R;
import edu.calstatela.jplone.watertrekapp.activities.DatePickerFragment;


public class ListFragment extends Fragment   {
    Context context;
    TextView starttext;
    TextView endtext;
    View lView;
    public String firstDate;
    public String lastDate;
    private ProgressBar pb;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;


//    private TextView mDisplaydate;
//    private DatePickerDialog.OnDateSetListener mDateSetListener;
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


    //********************
    String WELLID;
    String RiverID;
    String ReservoirID;
    String SoilMoistureID;
    String SnotelID;
    //*******************
    Boolean isWellNull;
    Boolean isRiverNull;
    Boolean isReservoirNull;
    Boolean isSoilNull;
    Boolean isSnotelNull;

    public ListFragment() {
        // Required empty public constructor
    }


//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        return  new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT,this,year,month,day);
////        return new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,(DatePickerDialog.OnDateSetListener) getActivity(),year,month,day);
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //******************************
        WELLID = getActivity().getIntent().getStringExtra("masterSiteId");
        SoilMoistureID = getActivity().getIntent().getStringExtra("wbanno");
        SnotelID = getActivity().getIntent().getStringExtra("SnotelsiteNO");
        RiverID = getActivity().getIntent().getStringExtra("RiversiteNO");
        ReservoirID = getActivity().getIntent().getStringExtra("ReservoirsiteNO");
        //************************
        Log.d("ListView","WEllID: " +  WELLID);
        Log.d("ListView","SoilID: " +  SoilMoistureID);
        Log.d("ListView","snotelID: " +  SnotelID);
        Log.d("ListView","RiverID: " + RiverID);
        Log.d("ListView","ReservoirID: " +  ReservoirID);
        isWellNull = WELLID == null;
        isRiverNull = RiverID == null;
        isReservoirNull = ReservoirID == null;
        isSoilNull = SoilMoistureID == null;
        isSnotelNull = SnotelID == null;

        lView = inflater.inflate(R.layout.list_view_fragment, container, false);
        pb = lView.findViewById(R.id.historyLoad);
        pb.setVisibility(View.INVISIBLE);

        Button startButton = (Button) lView.findViewById(R.id.sdate);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBclick = 1;
                endBclick = 2;
                showDatePicker();


            }
        });

        Button endButton = (Button) lView.findViewById(R.id.edate);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                endBclick = 1;
                startBclick = 2;
                showDatePicker();
            }
        });
        Button searchButt = lView.findViewById(R.id.Search);
        searchButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayHistoryList(lView);
            }
        });

        // Inflate the layout for this fragment
        return lView;


    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

public void onDateSet(DatePicker v, int year, int month, int dayOfMonth) {
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
            starttext = (TextView) lView.findViewById(R.id.startView);
            starttext.setText(datechosen);
            firstDate = datechosen;

        }
        if (endBclick == 1) {

            endtext = (TextView) lView.findViewById(R.id.endView);
            endtext.setText(datechosen);
            lastDate = datechosen;
        }
    } else if ((smonth.length() == 1) && (sdayOfMonth.length() != 1)) {

        String datechosen = (year + "-" + fixedmonth + "-" + dayOfMonth);
        if (startBclick == 1) {


            starttext = (TextView) lView.findViewById(R.id.startView);
            starttext.setText(datechosen);
            firstDate = datechosen;
        }
        if (endBclick == 1) {
            endtext = (TextView) lView.findViewById(R.id.endView);
            endtext.setText(datechosen);
            lastDate = datechosen;
        }
    } else if ((smonth.length() != 1) && (sdayOfMonth.length() == 1)) {
        String datechosen = (year + "-" + month + "-" + fixeddayOfMonth);
        if (startBclick == 1) {

            starttext = (TextView) lView.findViewById(R.id.startView);
            starttext.setText(datechosen);
            firstDate = datechosen;
        }
        if (endBclick == 1) {

            endtext = (TextView) lView.findViewById(R.id.endView);
            endtext.setText(datechosen);
            lastDate = datechosen;
        }
    } else {

        String datechosen = (year + "-" + month + "-" + dayOfMonth);
        if (startBclick == 1) {

            starttext = (TextView) lView.findViewById(R.id.startView);
            starttext.setText(datechosen);
            firstDate = datechosen;
        }
        if (endBclick == 1) {
            endtext = (TextView) lView.findViewById(R.id.endView);
            endtext.setText(datechosen);
            lastDate = datechosen;
        }

    }


}
    };


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
            Toast.makeText(getActivity(), " Searching...", Toast.LENGTH_SHORT).show();
            return true;

        } else if (startDate > EndDate) {
            //Start Date Cannot Preceed EndDate
            Toast.makeText(getActivity(), "Start Date Cannot Preceed EndDate", Toast.LENGTH_SHORT).show();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return false;


        } else {
            //Date Cannot be the Same
            Toast.makeText(getActivity(), "Start Date Cannot EQUAL  EndDate", Toast.LENGTH_SHORT).show();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return false;

        }
    }

    private void addWells(String WELLID) {
        if (dateVerifier() != false) {
            pb.setVisibility(View.VISIBLE);
            WellService.getDBGSunits(wellNetworkCallback, firstDate, lastDate, WELLID);
            pb.setVisibility(View.VISIBLE);

        }
    }


    private void addReservoirs(String ReservoirID) {
        if (dateVerifier() != false) {
            pb.setVisibility(View.VISIBLE);
            Log.d("reserves", "Calling ALL RESERVOIRS");
//            ReservoirService.getStorage(reservoirNetworkCallback, firstDate, lastDate, ReservoirID);
            ReservoirService.getStorageJSON(reservoirNetworkCallbackJSON, firstDate, lastDate, ReservoirID);
            pb.setVisibility(View.VISIBLE);
        }
    }


    private void addRivers(String RiverID) {
        if (dateVerifier() != false) {
            pb.setVisibility(View.VISIBLE);
            Log.d("discharge", "Calling rivernetworkcallback");
//            RiverService.getDischarge(riverNetworkCallback, firstDate, lastDate, RiverID);
            RiverService.getDischargeJSON(riverNetworkCallbackJSON, firstDate, lastDate, RiverID);
            pb.setVisibility(View.VISIBLE);
        }
    }

    private void addSoils(String soilMoistureID) {
        if (dateVerifier() != false) {
            pb.setVisibility(View.VISIBLE);
            Log.d("soily", "Calling SOILnetworkcallback");
            SoilMoistureService.getSoilDepthThruTime(soilNetworkCallback, firstDate, lastDate, SoilMoistureID);
            pb.setVisibility(View.VISIBLE);
        }
    }


    private void addSnotel(String snotID) {
        if (dateVerifier() != false) {
            pb.setVisibility(View.VISIBLE);
            Log.d("snow", "Calling snotelnetworkcallback");
            SnotelService.getSnotelTimeSeriesStartThruFinish(snowtelNetworkCallback, firstDate, lastDate, snotID);
            pb.setVisibility(View.VISIBLE);
        }
    }



    // METHOD that populates recyclerView
    //*****************************WELL RecylerView Starts ******************************************
    NetworkTask.NetworkCallback wellNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            List<String> dbgsunitList = WellService.parseDBGSunits(result);
            if (dbgsunitList.size() < 1) {
                pb.setVisibility(View.INVISIBLE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), " No informationhas been recorded thus far", Toast.LENGTH_SHORT).show();
                return;
            } else {
// clears old list so it doesnt double stack / repeat Data twice

                dbgsUList.clear();
                for (int i = 0; i < dbgsunitList.size(); i++) {
                    String dbu = dbgsunitList.get(i);
                    dbgsUList.add(dbu);
                    if (i > 0) {
                        String[] date = dbu.split("T");
                        // values are seperated by tabs not spaces.
                        String[] value = dbu.split("\t");
                    }
                }


                pb.setVisibility(View.INVISIBLE);
                ListView lv = lView.findViewById(R.id.historyList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dbgsUList);
                lv.setAdapter(adapter);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                pb.setVisibility(View.INVISIBLE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), " No informationhas been recorded thus far", Toast.LENGTH_SHORT).show();
                return;
            } else {
                // clears old list so it doesnt double stack / repeat Data twice


                resStorageList.clear();

                for (int i = 0; i < resList.size(); i++) {
                    String dbu = resList.get(i);
                    resStorageList.add(dbu);
                    if (i > 0) {
                        String[] date = dbu.split("T");
                        // values are seperated by tabs not spaces.
                        String[] value = dbu.split("\t");
                    }
                }


                pb.setVisibility(View.INVISIBLE);
                ListView lv = lView.findViewById(R.id.historyList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, resStorageList);
                lv.setAdapter(adapter);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }


    };

    //**********************Reservoir Recycler View ENDS*****************************************
    //*****************RESERVOIR  RECYCLER VIEW JSON STARTS *************************************************

    NetworkTaskAuth.NetworkCallback reservoirNetworkCallbackJSON = new NetworkTaskAuth.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            List <ReservoirStorageData> resResults = ReservoirService.parseStoragesJSON(result);
            if (resResults.size() < 1) {
                Log.d("Reservoir-Storage", "No information has been recorded thus far");
                pb.setVisibility(View.INVISIBLE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), " No information has been recorded thus far", Toast.LENGTH_SHORT).show();

                return;
            } else {

                resStorageList.clear();

                for (int i = 0; i < resResults.size(); i++) {
                    String tempDT = resResults.get(i).getDateTime();
                    String tempStorage = resResults.get(i).getStorage();
                    String tempUnit = resResults.get(i).getUnits();
                    String tempLast = tempDT +" " + tempStorage + " " + tempUnit;
                    resStorageList.add(tempLast);

                }

                pb.setVisibility(View.INVISIBLE);
                ListView lv = lView.findViewById(R.id.historyList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, resStorageList);
                lv.setAdapter(adapter);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    };

    //*****************RESRVOIR RECYCLER VIEW JSON ENDS **********************************************


    //*****************************RIVER/STREAMGAUGES RecylerView Starts ******************************************
    NetworkTask.NetworkCallback riverNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            Log.d("discharge", "before parsingdischarges");
            List<String> disList = RiverService.parseDischarges(result);
            Log.d("discharge", "after parsingdischarges");
            if (disList.size() < 1) {
                Log.d("discharge", "No information has been recorded thus far");
                pb.setVisibility(View.INVISIBLE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), " No information has been recorded thus far", Toast.LENGTH_LONG).show();

                return;
            } else {

                dischargeList.clear();
                for (int i = 0; i < disList.size(); i++) {
                    String dsl = disList.get(i);
                    dischargeList.add(dsl);
                    if (i > 0) {
                        String[] date = dsl.split("T");
                        // values are seperated by tabs not spaces.
                        String[] value = dsl.split("\t");

                    }
                }


                pb.setVisibility(View.INVISIBLE);
                ListView lv = lView.findViewById(R.id.historyList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dischargeList);
                lv.setAdapter(adapter);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    };

    //*****************************RIVER/STREAMGAUGES RecylerView Ends ******************************************
    //*****************RIVER/STREAMGAUGES  RECYCLER VIEW JSON STARTS *************************************************

    NetworkTaskAuth.NetworkCallback riverNetworkCallbackJSON = new NetworkTaskAuth.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            Log.d("discharge", "before parsingdischarges");
            List <RiverStorageData> resResults = RiverService.parseDischargesJSON(result);
//            List<String> disList = RiverService.parseDischargesJSON(result);
            Log.d("discharge", "after parsingdischarges");
            if (resResults.size() < 1) {
                Log.d("discharge", "No information has been recorded thus far");
                pb.setVisibility(View.INVISIBLE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), " No information has been recorded thus far", Toast.LENGTH_LONG).show();

                return;
            } else {

                dischargeList.clear();

                for (int i = 0; i < resResults.size(); i++) {
                    String tempDT = resResults.get(i).getDateTime();
                    String tempStorage = resResults.get(i).getStorage();
                    String tempUnit = resResults.get(i).getUnits();
                    String tempLast = tempDT +" " + tempStorage + " " + tempUnit;
//                    String dsl = disList.get(i);
                    dischargeList.add(tempLast);

                }

                pb.setVisibility(View.INVISIBLE);
                ListView lv = lView.findViewById(R.id.historyList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dischargeList);
                lv.setAdapter(adapter);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    };


    //*****************RIVER/STREAMGAUGES  RECYCLER VIEW JSON ENDS *************************************************
    //*********************Soil Moisture RecyclerView Starts**********************

    NetworkTask.NetworkCallback soilNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            Log.d("soil", "before parsingdischarges");
            List<String> soilyList = SoilMoistureService.parseindySoil(result);
            Log.d("soil", "after parsingdischarges");
            if (soilyList.size() < 1) {
                Log.d("soil", "No information has been recorded thus far");
                pb.setVisibility(View.INVISIBLE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), " No information has been recorded thus far", Toast.LENGTH_LONG).show();
                return;
            } else {


                // clears old list so it doesnt double stack / repeat Data twice
                soilDepthList.clear();

                for (int i = 0; i < soilyList.size(); i++) {
                    String swe = soilyList.get(i);
                    soilDepthList.add(swe);
                    if (i > 0) {
                        String[] date = swe.split("T");
                        // values are seperated by tabs not spaces.
                        String[] value = swe.split("\t");

                    }
                }

                pb.setVisibility(View.INVISIBLE);
                ListView lv = lView.findViewById(R.id.historyList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, soilDepthList);
                lv.setAdapter(adapter);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    };


    //*********************Soil Moisture RecyclerView ENDS**********************


    //*********************SNOWTEL RecyclerView Starts**********************
    NetworkTask.NetworkCallback snowtelNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {

            List<String> snowyList = SnotelService.parseTimes(result);

            if (snowyList.size() < 1) {
                Log.d("snow", "No information has been recorded thus far");
                pb.setVisibility(View.INVISIBLE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), " No information has been recorded thus far", Toast.LENGTH_LONG).show();
                return;
            } else {

                // clears old list so it doesnt double stack / repeat Data twice
                sweSnotelList.clear();

                for (int i = 0; i < snowyList.size(); i++) {
                    String snot = snowyList.get(i);
                    sweSnotelList.add(snot);
                    if (i > 0) {
                        String[] date = snot.split("T");
                        // values are seperated by tabs not spaces.
                        String[] value = snot.split("\t");
                    }
                    // Check if all y values are null before populating graph
                }

                pb.setVisibility(View.INVISIBLE);
                ListView lv = lView.findViewById(R.id.historyList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sweSnotelList);
                lv.setAdapter(adapter);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    };


    //*********************SNOWTELRecyclerView ENDS**********************


    // for river discharge  parse using xml

//    "https://watertrek.jpl.nasa.gov/hydrology/rest/streamgauge/site_no/09331850/discharge/from/1981-01-01T00:00:00/through/1990-01-01T00:00:00"
    // String riverurl = "https://watertrek.jpl.nasa.gov/hydrology/rest/streamgauge/site_no/09331850/discharge/from/1981-01-01T00%3A00%3A00/through/1990-01-01T00%3A00%3A00";


    // [ass firstDate && LastDate into a method that will retrieve data and make recylrerview
    // need to make a method to make sure that firstDate Or lastDate are NOT NULL
    // Need to fix issue on Double clicking in order to get the date to display



    public void displayHistoryList(View v) {

        if (isWellNull == false) {
            Log.d("wells", "ADDED WELLS ");
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            addWells(WELLID);
        }
        if (isReservoirNull == false) {
            Log.d("reserves", "ADDED RESERVOIRS ");
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            addReservoirs(ReservoirID);


        }
        if (isRiverNull == false) {
            Log.d("discharge", "ADDED RIVERS ");
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            addRivers(RiverID);

        }
        if (isSoilNull == false) {
            Log.d("soily", "ADDED SOIL MOISTURE ");
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            addSoils(SoilMoistureID);

        }
        if (isSnotelNull == false) {
            Log.d("snow", "ADDED Snotels ");
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            addSnotel(SnotelID);

        }

    }
}

