package edu.calstatela.jplone.watertrekapp.DataService;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.Data.Snotel;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;



public class SnotelService {

    public static void getAllSnotel(NetworkTask.NetworkCallback callback){
        // xml format
        // Retrieve a list of station ids with the latitude and longitude
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id";
        NetworkTask nt = new NetworkTask(callback, Snotel.TYPE_ID);
        nt.execute(url);
    }
    public static void getAllAggregatedSnotelValues()
    {
//        Calculates the aggregated value for the given station id over the entire data record.
        // JSON FORMAT
        // 1054 is an example of station ID
        // after /swe/ avg  retrieves average
        // after /swe/ min  retrieves maximum
        // after /swe/ max  retrieves maximum
        // after /swe/ stddev   retrieves standard deviation
        // units = us or units =metric   sets to us or metric system
        String url =  "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id/1054/swe/avg?format=json&units=us";


    }
    public static void getAggregatedSnotelFromStart(){

        //    Calculates the aggregated value for the given station id from the start date through the end of the data record.
        // JSON FORMAT
        // 1054 is an example of station ID
        // after /swe/ avg  retrieves average
        // after /swe/ min  retrieves maximum
        // after /swe/ max  retrieves maximum
        // after /swe/ stddev   retrieves standard deviation
        // units = us or units =metric   sets to us or metric system
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id/1054/swe/avg/from/1900-01-28T00%3A00%3A00?format=json&units=metric";


    }
    public static  void getAggregatedSnotelStartThruFinish(){
//      Calculates the aggregated value for the given station id from the start date through the end date.
        // JSON FORMAT
        // 1054 is an example of station ID
        // after /swe/ avg  retrieves average
        // after /swe/ min  retrieves maximum
        // after /swe/ max  retrieves maximum
        // after /swe/ stddev   retrieves standard deviation
        // units = us or units =metric   sets to us or metric system
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id/1054/swe/max/from/1900-12-12T00%3A00%3A00/through/2000-12-12T00%3A00%3A00?format=json&units=us";
    }
    public static void getSnotelTimeSeries(){
        // Retreives the time series values for the given station id over the entire data record.
        // JSON FORMAT
        // units = us or units =metric   sets to us or metric system

        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id/1000/swe?format=json&units=us";

    }
    public static void getSnotelTimeSeriesStartThruFinish(){
        // Retreives the time series values for the given station id from the start date through the end date.
        // JSON FORMAT
        // units = us or units =metric   sets to us or metric system
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id/1000/swe/from/2016-01-28T00%3A00%3A00/through/2018-01-28T00%3A00%3A00?format=json&units=us";

//        NetworkTaskJSONAuthentication ntjsauth = new NetworkTaskJSONAuthentication();


//        ReadJSONFeedTask  rjsft =  new  ReadJSONFeedTask().execute(url);
    }

    public static void getAdditionalInfo(NetworkTask.NetworkCallback callback, int stationId){
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id/"+stationId+"/swe";
        NetworkTask nt = new NetworkTask(callback, Snotel.ADDTL_ID);
        nt.execute(url);

    }

    //Parse functions \\

    //Not implemented. Fix -> Fixed 4/2/18
    public static List parseAdditionalInfo(String line){
        List<String> addInfoList = new ArrayList();
        String[] lines = line.split("\n");
        for(int i=1; i<lines.length; i++){
            addInfoList.add(lines[i]);
            Log.d("ADD INFO:", " "+lines[i]);
        }
        return addInfoList;
    }

    public static Snotel parseSnotel(String line){
        String[] rowEntry = line.split("\t");
        Snotel snotel = new Snotel(rowEntry);
        return snotel;
    }

    public static List parseAllSnotel(String line){
        List<Snotel> snotelList = new ArrayList();
        String[] lines = line.split("\n");
        for(int i=1; i<lines.length; i++) snotelList.add(parseSnotel(lines[i]));
        return snotelList;
    }
}
