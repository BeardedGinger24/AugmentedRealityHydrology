package edu.calstatela.jplone.watertrekapp.DataService;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.Data.River;
import edu.calstatela.jplone.watertrekapp.Data.SoilMoisture;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTaskAuth;

public class RiverService {


    //    String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/river/containing/-119.2%2078.3/flowstat";
    public static void getAllRiverIDS(NetworkTask.NetworkCallback callback){
        //gets all  Rivers JSON FORMAT
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/river/comid?format=json";
//        NetworkTask nt = new NetworkTask(callback, SoilMoisture.TYPE_ID);
//        nt.execute(url);
    }
    public static void getDischarge(NetworkTask.NetworkCallback callback, String startDate , String endDate, String masterSiteId){
//        int masterSiteId = 91133; example ID
        String masterId = masterSiteId;
//        yr/month/day
        // returns  history of depth below ground surface  DBGS

        String url = ("https://watertrek.jpl.nasa.gov/hydrology/rest/streamgauge/site_no/"+masterSiteId+"/discharge/from/"+startDate+"T00:00:00/through/"+endDate+"T00:00:00");
        Log.d("riverurl" , url);
        NetworkTask nt = new NetworkTask(callback, River.DISCHARGE_UNITS);
        nt.execute(url);
    }
    //RIVER CALL NOT STREAMGAUGE
    public static void avgFlux(NetworkTask.NetworkCallback callback, String startDate , String endDate, String comid){
        String comID = comid;
        String url =  " https://watertrek.jpl.nasa.gov/hydrology/rest/river/comid/1176550/avg/2016-01-28T00:00:00/2019-01-28T00:00:00";
        NetworkTask nt = new NetworkTask(callback,River.AVGFLUX);
        nt.execute(url);
    }

    public static void getRivers(NetworkTask.NetworkCallback callback, double latitude, double longitude, double radius) {
        double[] gpsPoints = getPolygon(latitude, longitude, radius);
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/streamgauge/within/wkt/POLYGON%28%28"+ //Begin Parenthesis
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


        NetworkTask nt = new NetworkTask(callback, River.TYPE_ID);
        nt.execute(url);
    }
    // returns river ID that contains provided Stream gauge ID
    public static void StreamtoRiverID(NetworkTaskAuth.NetworkCallback callback ,  String StreamID ){
        // example stream id to test 09424050
        // correlating riverid 21437781
        StreamID = "09424050";
        //gets all  River ID in JSON  FORMAT
        String url  = "https://watertrek.jpl.nasa.gov/hydrology/rest/river/containing/streamgauge/site_no/" + StreamID+ "?format=json";
        NetworkTaskAuth nt = new NetworkTaskAuth(callback, River.TYPE_ID);
        nt.execute(url);
    }

    //retrieves various river information such as  WKT
    public static void SpecificRiverInfo(NetworkTaskAuth.NetworkCallback callback ,  String riverID ){
        // example stream id to test 09424050
        // correlating riverid 21437781
        //gets all  River ID in JSON  FORMAT
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/river/comid/" +riverID+"?format=json";
//        String url  = "https://watertrek.jpl.nasa.gov/hydrology/rest/river/containing/streamgauge/site_no/" + StreamID+ "?format=json";
//        NetworkTask nt = new NetworkTask(callback, SoilMoisture.TYPE_ID);
//        nt.execute(url);
        NetworkTaskAuth nt = new NetworkTaskAuth(callback, River.TYPE_ID);
        nt.execute(url);
    }






    //Depth is from 5 cm to 10 cm.
    public static void getIndyRiver(NetworkTask.NetworkCallback callback, int id ){
        // example com id  1176550 with format JSON
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/river/comid/1176550?format=json";

        NetworkTask nt = new NetworkTask(callback, SoilMoisture.ADDTL_ID);
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




    //*******************************************GET POLYGON CODE*****************************************

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


    //*******************************************GET POLYGON CODE ENDS HERE*****************************************
    public static List<River> parseRivers(String res) {
        List<River> rivers = new ArrayList<River>();
        String[] lines = res.split("\n");
        // starting from line 1 because line 0 is all field
        for(int i=1; i<lines.length; i++) rivers.add(parseRiver(lines[i]));
        return rivers;
    }

    //Return single River.
    public static River parseRiver(String line) {
        String[] rowEntry = line.split("\t");
        //Log.d("WELL", rowEntry.toString());
        River riv = new River(rowEntry);
        return riv;
    }

    public static List parseDischarges(String line){
        Log.d("discharge" , line);
        List<String> unitdisList = new ArrayList();
        String[] rowEntry = line.split("\n");
        //**************possibly delete this **********
        if (rowEntry.length == 1){
            return unitdisList ;
        }

        //****************
        if (rowEntry[1].equals("null")){
            return unitdisList ;
        }
        if (rowEntry[1] == null){
            return unitdisList ;
        }

        for(int i=0; i<rowEntry.length;i++){
            unitdisList.add(rowEntry[i]);
        }

        Log.d("discharge", "END");
        return unitdisList;

    }



}
