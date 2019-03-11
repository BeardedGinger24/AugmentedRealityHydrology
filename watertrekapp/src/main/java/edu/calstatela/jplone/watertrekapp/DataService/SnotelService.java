package edu.calstatela.jplone.watertrekapp.DataService;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.Data.Snotel;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTaskJSONAuthentication;


public class SnotelService {
static String TAG = "snotel-service";
    public static void getAllSnotel(NetworkTask.NetworkCallback callback ){
        // xml format
        // Retrieve a list of station ids with the latitude and longitude
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id";
        Log.d(TAG,url);
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
    public static void getSnotelTimeSeriesStartThruFinish(NetworkTask.NetworkCallback callback, String startDate , String endDate, String uniqueID){
        // Retreives the time series values for the given station id from the start date through the end date.
        // JSON FORMAT
        // units = us or units =metric   sets to us or metric system
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id/"+uniqueID+"/swe/from/"+startDate+"T00:00:00/through/"+endDate+"T00:00:00?format=text&units=metric";
        Log.d(TAG,url);
        NetworkTask nt = new NetworkTask(callback, Snotel.ADDTL_ID);
        nt.execute(url);
//        NetworkTaskJSONAuthentication ntjsauth = new NetworkTaskJSONAuthentication();


//        ReadJSONFeedTask  rjsft =  new  ReadJSONFeedTask().execute(url);
    }

    public static void getAdditionalInfo(NetworkTask.NetworkCallback callback, int stationId){
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id/"+stationId+"/swe";
        Log.d(TAG,url);
        NetworkTask nt = new NetworkTask(callback, Snotel.ADDTL_ID);
        nt.execute(url);

    }

    //////////////////////////HAVERSINE FORMULA//////////////added by leo from stackoverflow XD *************
    public static double getDistanceFromLatLonInKm(double userLat,double userLon, double dataLat, double dataLon) {
        int  R = 6371; // Radius of the earth in km
        double  dLat = deg2rad(dataLat - userLat);  // deg2rad below
        double  dLon = deg2rad(dataLon - userLon);
        double  a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(userLat)) * Math.cos(deg2rad(dataLat)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    public static double  deg2rad(double deg) {
        return deg * (Math.PI/180);
    }
//////////////////////////////////////////////////////////////////////

    //Add created Reservoir objects to list and return.
    //hardcoded value to show reserviors of closest 100  km Reservior
    //  becasue none within 25km where i live just pass range as a parameter to fix
    public static List parseAllSnowtels(String line, float longitu, float lat , int radius){
        double mycurrLat = (double) lat;
        double mycurrlong = (double) longitu;
        // , float longitu, float lat
//        double currlong =  (double) longitu;
//        double currlat = (double) lat;
        List<Snotel> snotelList = new ArrayList<Snotel>();
        // splits retrieved data by Enter key every Reservoir object starts in a new Line
        String[] lines = line.split("\n");

        for(int i=1; i<lines.length; i++) {
            snotelList.add(parseSnows(lines[i]));
            Log.d("snow", "is adding ...");
        }
        //reservoir Near filters all the data  to  distance  near km chosen
        List<Snotel> SnotelNear = new ArrayList<Snotel>();
        for(Snotel sntl : snotelList){
            double laty =  Double.parseDouble(sntl.getLat());
            double longy =  Double.parseDouble(sntl.getLon());
            // mycurrlong is latitude retrieved using phone while laty is latitude retrieved from get call
            // if less than or equal to range (100) add reserNear to List and return it back
            if (getDistanceFromLatLonInKm (mycurrlong, mycurrLat , laty, longy) <= radius *40)
            {
                SnotelNear.add(sntl);
                Log.d("snow",sntl.getStationId() + " snotel within range ");
            }
            else {
                Log.d("snow",sntl.getStationId() + " snowtel NOT  within range ");
            }

        }
        return SnotelNear;

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
    public static List<Snotel> parseSnowtelCall(String res) {
        List<Snotel> snows = new ArrayList<Snotel>();
        String[] lines = res.split("\n");
        // starting from line 1 because line 0 is all field
        for(int i=1; i<lines.length; i++) snows.add(parseSnows(lines[i]));
        return snows;
    }
    public static Snotel parseSnows(String line) {
        String[] rowEntry = line.split("\t");
        Snotel snw = new Snotel(rowEntry);
        return snw;
    }

    public static List parseTimes(String line){
        Log.d("snow" , line);
        List<String> unitTimeList = new ArrayList();
        String[] rowEntry = line.split("\n");
        //**************possibly delete this **********
        if (rowEntry.length == 1){
            return unitTimeList ;
        }

        //****************
        if (rowEntry[1].equals("null")){
            return unitTimeList ;
        }
        if (rowEntry[1] == null){
            return unitTimeList ;
        }

        for(int i=0; i<rowEntry.length;i++){
            unitTimeList.add(rowEntry[i]);
        }

        Log.d("snow", "END");
        return unitTimeList;

    }

//    public static Snotel parseSnotel(String line){
//        String[] rowEntry = line.split("\t");
//        Snotel snotel = new Snotel(rowEntry);
//        return snotel;
//    }

//    public static List parseAllSnotel(String line){
//        List<Snotel> snotelList = new ArrayList();
//        String[] lines = line.split("\n");
//        for(int i=1; i<lines.length; i++) snotelList.add(parseSnotel(lines[i]));
//        return snotelList;
//    }
}
