package edu.calstatela.jplone.watertrekapp.DataService;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.Data.Reservoir;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;

/**
 * Created by nes on 3/5/18.
 */

public class ReservoirService {

    //Invoke Reservoir REST call. Gets all Reservoir from resources.
    public static void getAllReservoir(NetworkTask.NetworkCallback callback){
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/reservoir/site_no";
        NetworkTask nt = new NetworkTask(callback, Reservoir.TYPE_ID);
        nt.execute(url);
    }

    //Invoke
    public static void getAllStorageValues(NetworkTask.NetworkCallback callback){
// example of source no 09096100
        // use our longitude and latitude 3rd and 4th column  to filter site NO  and description 1st and 2nd column
        // String url ="https://watertrek.jpl.nasa.gov/hydrology/rest/reservoir/site_no/"+ site_no+"/storage";
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/reservoir/site_no";        //Log.d("URL", ""+url);
       // NetworkTask nt = new NetworkTask(callback, Reservoir.ADDTL_ID);
        NetworkTask nt = new NetworkTask(callback, Reservoir.TYPE_ID);

        //Log.d("getSTRG", "In REST CALL");
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
    public static List parseAllReservoirs(String line, float longitu, float lat , int radius){
        double mycurrLat = (double) lat;
        double mycurrlong = (double) longitu;
        // , float longitu, float lat
//        double currlong =  (double) longitu;
//        double currlat = (double) lat;
        List<Reservoir> reservoirList = new ArrayList<Reservoir>();
        // splits retrieved data by Enter key every Reservoir object starts in a new Line
        String[] lines = line.split("\n");

        for(int i=1; i<lines.length; i++) {
            reservoirList.add(parseReservoir(lines[i]));
            Log.d("Reser", "is adding ");
        }
        //reservoir Near filters all the data  to  distance  near km chosen
        List<Reservoir> reservoirNear = new ArrayList<Reservoir>();
        for(Reservoir rnm : reservoirList){
            double laty =  Double.parseDouble(rnm.getLat());
            double longy =  Double.parseDouble(rnm.getLon());
            // mycurrlong is latitude retrieved using phone while laty is latitude retrieved from get call
            // if less than or equal to range (100) add reserNear to List and return it back
            if (getDistanceFromLatLonInKm (mycurrlong, mycurrLat , laty, longy) <= radius)
            {
                reservoirNear.add(rnm);
                Log.d("Reser",rnm.getSiteNo() + " rerservior within range ");
            }
            else {
                Log.d("Reser",rnm.getSiteNo() + " rerservior NOT  within range ");
            }

        }
        return reservoirNear;

    }

    //Create Reservoir object. Read in one line and pass to constructor.
    // parses every line of reservoir obj by Tab to seperate values and returns it back to parseAllReservoir
    public static Reservoir parseReservoir(String line){
        String[] rowEntry = line.split("\t");
        Reservoir reservoir = new Reservoir(rowEntry);
        return reservoir;
    }

    //
    public static List parseAllStorageValues(String line){
        List<String> storageList = new ArrayList();
        String[] lines = line.split("\n");
        for(int i=1; i < lines.length ; i++){
            storageList.add(lines[i]);
            //Log.d("STRG", ""+storageList.get(i));

        }
        return storageList;
    }
}
