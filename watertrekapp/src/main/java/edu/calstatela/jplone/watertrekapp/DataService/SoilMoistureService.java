package edu.calstatela.jplone.watertrekapp.DataService;



import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.Data.SoilMoisture;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;

/**
 * Created by nes on 3/6/18.
 */

public class SoilMoistureService {

    //Get
    public static void getSoilMoistures(NetworkTask.NetworkCallback callback){
        //404 error. Sponsor changed url? Check after 3/5/18
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/soilmoisture/wbanno";
        NetworkTask nt = new NetworkTask(callback, SoilMoisture.TYPE_ID);
        nt.execute(url);
    }

    //Depth is from 5 cm to 10 cm.
    public static void getSoilMoistureInfo(NetworkTask.NetworkCallback callback, int id , int depth){
        //404 error. Sponsor changed url? Check after 3/5/18
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/soilmoisture/wbanno/"+id +"/at/"+depth+"cm";
        NetworkTask nt = new NetworkTask(callback, SoilMoisture.ADDTL_ID);
        nt.execute(url);
    }

    //Parse \\

    public static List parseAdditionalInfo(String line){
        List<String> addInfoList = new ArrayList();
        String[] lines = line.split("\n");
        for(int i=1; i<lines.length; i++){
            addInfoList.add(lines[i]);
            //Log.d("ADD INFO:", " "+lines[i]);
        }
        return addInfoList;
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

    //Add created soil objects to list and return.
    public static List parseAllSoilMoist(String line, float longitu, float lat, int  radius ){
        Log.d("soiledInit","pasingsoil has been called");
        Log.d("soiledInit",line);
        double mycurrLat = (double) lat;
        double mycurrlong = (double) longitu;
        // , float longitu, float lat
//        double currlong =  (double) longitu;
//        double currlat = (double) lat;
        List<SoilMoisture> soilMoistureList = new ArrayList<SoilMoisture>();
        // splits retrieved data by Enter key every Reservoir object starts in a new Line
        String[] lines = line.split("\n");

        for(int i=1; i<lines.length; i++) {
            soilMoistureList.add(parseSoilMoisture(lines[i]));
            Log.d("soiledAP",lines[i]);
            Log.d("soiledAP","  ");
        }
        //reservoir Near filters all the data  to  distance  near km chosen
        List<SoilMoisture> soilNear = new ArrayList<SoilMoisture>();
        for(SoilMoisture smList : soilMoistureList){
//            Log.d("soiled",smList.toString());
            double laty =  Double.parseDouble(smList.getLat());
            double longy =  Double.parseDouble(smList.getLon());
            // mycurrlong is latitude retrieved using phone while laty is latitude retrieved from get call
            // if less than or equal to range (100) add reserNear to List and return it back
            if (getDistanceFromLatLonInKm (mycurrlong, mycurrLat , laty, longy) <= radius)
            {
                soilNear.add(smList);
                Log.d("soiledDistance",smList.getWbanno() + " soil within range ");

            }
            else
            {
                Log.d("soiledDistance",smList.getWbanno() + " NOT  within range ");
            }

        }
        return soilNear;

    }




    //Create SOIL object. Read in one line and pass to constructor.
    // parses every line of SOIL obj by Tab to seperate values and returns it back to parseSOILMOISTURES
    public static SoilMoisture parseSoilMoisture(String line) {
        String[] rowEntry = line.split("\t");
        SoilMoisture sm = new SoilMoisture(rowEntry);

        return sm;
    }
// seperates individual Values
    public static List<SoilMoisture> parseSoilMoistures(String line){
        List<SoilMoisture> soilList = new ArrayList<>();
        String[] lines = line.split("\n");
        for(int i=1; i<lines.length; i++) soilList.add(parseSoilMoisture(lines[i]));
        return soilList;

}





    }

