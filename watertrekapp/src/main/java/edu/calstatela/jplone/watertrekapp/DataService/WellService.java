package edu.calstatela.jplone.watertrekapp.DataService;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.Data.Well;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;

/**
 * Created by nes on 3/1/18.
 */

public class WellService {

    //  Get calls  \\

    public static void getWell(NetworkTask.NetworkCallback callback, double latitude, double longitude){

        String url = ("https://watertrek.jpl.nasa.gov/hydrology/rest/well/near/lat/" + latitude + "/lon/" + longitude + "");
        NetworkTask nt = new NetworkTask(callback, Well.TYPE_ID);
        nt.execute(url);
    }

    public static void getWells(NetworkTask.NetworkCallback callback, double latitude, double longitude, double radius) {
        double[] gpsPoints = getPolygon(latitude, longitude, radius);
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/well/within/wkt/POLYGON%28%28"+ //Begin Parenthesis
                +gpsPoints[1] +"%20"+gpsPoints[0] +",%20" +
                +gpsPoints[3] +"%20"+gpsPoints[2] +",%20"+
                +gpsPoints[5] +"%20"+gpsPoints[4] +",%20"+
                +gpsPoints[7] +"%20"+gpsPoints[6] +",%20"+
                +gpsPoints[9] +"%20"+gpsPoints[8] +",%20"+
                +gpsPoints[11] +"%20"+gpsPoints[10] +",%20"+
                +gpsPoints[13] +"%20"+gpsPoints[12] +",%20"+
                +gpsPoints[15] +"%20"+gpsPoints[14] +",%20"+
                +gpsPoints[17] +"%20"+gpsPoints[16] +",%20"+
                +gpsPoints[1] +"%20"+gpsPoints[0] +"%29%29";//Close Parenthesis
        Log.d("String URL", ""+url);

        NetworkTask nt = new NetworkTask(callback, Well.TYPE_ID);
        nt.execute(url);
    }

    public static void getDbgs(NetworkTask.NetworkCallback callback, int masterSiteId){
        List<String> listValues = new ArrayList();
        //Test value
        //int masterSiteId = 60924;
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/well/master_site_id/"+ masterSiteId+"/dbgs/";
        NetworkTask nt = new NetworkTask(callback, Well.DBGS_ID);
        nt.execute(url);

    };

    public static void getMax(NetworkTask.NetworkCallback callback, int masterSiteId){
        //int masterSiteId= 60924;
        String url= ("https://watertrek.jpl.nasa.gov/hydrology/rest/well/master_site_id/"+ masterSiteId+"/dbgs/max");
        NetworkTask nt = new NetworkTask(callback, Well.MAX_ID);
        nt.execute(url);
    }

    public static void getMin(NetworkTask.NetworkCallback callback, int masterSiteId){
        //int masterSiteId= 60924;
        String url= ("https://watertrek.jpl.nasa.gov/hydrology/rest/well/master_site_id/"+ masterSiteId+"/dbgs/min");
        NetworkTask nt = new NetworkTask(callback, Well.MIN_ID);
        nt.execute(url);
    }

    public static void getAvg(NetworkTask.NetworkCallback callback, int masterSiteId){
        //int masterSiteId = 60924;
        String url= ("https://watertrek.jpl.nasa.gov/hydrology/rest/well/master_site_id/"+ masterSiteId+"/dbgs/avg");
        NetworkTask nt = new NetworkTask(callback, Well.AVG_ID);
        nt.execute(url);
    }

    public static void getStdDev(NetworkTask.NetworkCallback callback, int masterSiteId){
        //int masterSiteId = 60924;
        String url = ("https://watertrek.jpl.nasa.gov/hydrology/rest/well/master_site_id/"+masterSiteId+"/dbgs/stddev");
        NetworkTask nt = new NetworkTask(callback, Well.STD_ID);
        nt.execute(url);
    }

    //Parse Functions\\

    public static String parseStdDev(String line){
        String wellStdDev="";
        String[] lines = line.split("\n");
        for(int k = 1; k <lines.length; k++){
            //Log.d("nSplit",lines[k]);
            wellStdDev += lines[k];
        }
        return wellStdDev;
    }

    public static String parseAvg(String line){
        String wellAvg="";
        String[] lines = line.split("\n");
        for(int k = 1; k <lines.length; k++){
            //Log.d("nSplit",lines[k]);
                wellAvg += lines[k];
        }
        return wellAvg;
    }

    public static String parseMax(String line){
        String wellMax ="";
        String[] lines = line.split("\n");
        String joined = TextUtils.join("\t",lines);
        String[] split = joined.split("\t");
        for(int i =2 ; i < split.length; i++){
            wellMax+=split[i]+" ";
        }
        return wellMax;
    }

    public static String parseMin(String line){
        String wellMin ="";
        String[] lines = line.split("\n");
        String joined = TextUtils.join("\t",lines);
        String[] split = joined.split("\t");
        for(int i =2 ; i < split.length; i++){
            wellMin+=split[i]+" ";
        }
        Log.d("parseMin", wellMin);
        return wellMin;
    }

    public static List parseDbgs(String line){
        List<String> dbgsList = new ArrayList();
        String[] rowEntry = line.split("\n");

        for(int i=1; i<rowEntry.length;i++){
            dbgsList.add(rowEntry[i]);
        }

        //Print list
//        for(int i=0; i< dbgsList.size();i++){
//            Log.d("Item", "INDEX "+i+" "+dbgsList.get(i));
//        }
        return dbgsList;
    }

    public static List<Well> parseWells(String res) {
        List<Well> wells = new ArrayList<Well>();
        String[] lines = res.split("\n");
        Log.d("Test PRSE", " "+res);
        // starting from line 1 because line 0 is all field
        for(int i=1; i<lines.length; i++) wells.add(parseWell(lines[i]));
        return wells;
    }

    //Return Well. Used in getWells.
    public static Well parseWell(String line) {
        String[] rowEntry = line.split("\t");
        //Log.d("WELL", rowEntry.toString());
        Well well = new Well(rowEntry);
        Log.d("WELL", ""+ well.getMasterSiteId()+" \n"+well.getCasgemStationId());
        return well;
    }

    public static double[] getPolygon(double lat, double lon,double radius){
        double[] polygonArray= new double[18];
        int latCount=0;
        int lonCount=1;

        //Order of Bearing:  N, NW, W, SW, S, SE, E, NE back to N to complete polygon
        double[] checkedDegrees = {90,135,180,225,270,315,0,45,90};
        //Earth's Radius in KM 6371.
        double dist = radius/6371.0;

        //From StackOverflow
        for(int i = 0; i< checkedDegrees.length;i++) {
            double brng = Math.toRadians(checkedDegrees[i]);
            double lat1 = Math.toRadians(lat);
            double lon1 = Math.toRadians(lon);

            double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
            double a = Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1), Math.cos(dist) - Math.sin(lat1) * Math.sin(lat2));
            //System.out.println("a = " + a);
            double lon2 = lon1 + a;

            lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

//          System.out.println("Latitude = " + Math.toDegrees(lat2) + "\nLongitude = " + Math.toDegrees(lon2));
//          tempLat = Math.toDegrees(lat2);
//          tempLon = Math.toDegrees(lon2);

            //Store every 2 sets as a set of points/ Coordinates.
            polygonArray[latCount]= Math.toDegrees(lat2);
            polygonArray[lonCount]= Math.toDegrees(lon2);

//          System.out.println("LatCount: "+ latCount + "\t LonCount: "+ lonCount);
            latCount=latCount+2;
            lonCount= lonCount+2;
        }
        return polygonArray;
    }
}
