package edu.calstatela.jplone.watertrekapp.DataService;

import android.util.Log;

import edu.calstatela.jplone.watertrekapp.Data.DEM;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;

public class DEMService {

    public static void getDEM(NetworkTask.NetworkCallback callback, double latitude, double longitude,double radius){

        double[] gpsPoints = getPolygon(latitude, longitude, radius);
        //Set url path for future DEM get request here
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/dem/within/wkt/POLYGON%28%28"+ //Begin Parenthesis
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

        NetworkTask nt = new NetworkTask(callback, DEM.TYPE_ID);
        nt.execute(url);
    }

    public static float[] demToMesh(DEM dem){
        //set size of array according to dem size
        float[] mesh = new float[1];
        //add mesh creation code here***
        return mesh;
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
