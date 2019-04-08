package edu.calstatela.jplone.watertrekapp.DataService;


import android.util.Log;

import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTaskJSON;

public class ElevationObstructionService {
//    Find obstruction given location, bearing, and pitch:

    static String TAG = "obs-service";



    public static void getObstruction(NetworkTaskJSON.NetworkCallback callback, double latitude, double longitude , String bearing, String pitch){
//        http://54.237.80.7:8080/LOSService/ws/los/earthLOS/firstViewObs?origin=[-118.20177021,34.12922031]&bearing=29&pitch=5

        //Double Check int Value
        String url = ("http://34.227.91.44:8080/LOSService/ws/los/earthLOS/firstViewObs?origin=["+longitude+","+latitude+"]&bearing="+bearing+"&pitch="+pitch+"&height=0.01");
        Log.d(TAG, url);
//        URL theurl = new URL(SERVER_URL);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(theurl));
//        String jsonString = reader.readLine();
//        JSONObject jsonObject = new JSONObject(jsonString);
//        // jsonObject.getString("name")
//         new NetworkTaskJSON().execute(url);
        NetworkTaskJSON nt = new NetworkTaskJSON(callback,0);
        nt.execute(url);
    }


//    Given start and end point find the pitch angle of highest elevation point in path.

    public static void getPitchAngleAtHighestElevationPoint( double startlatitude, double startlongitude , double endlatitude, double endlongitude){
        //http://54.237.80.7:8080/LOSService/ws/los/earthLOS/calculateAngleOfHighest?path=[[-118.20177021,34.12922031],[-118.20775683,34.24009466]]
        //Double Check int Value
        String url = ("http://34.227.91.44:8080/LOSService/ws/los/earthLOS/calculateAngleOfHighest?path=[["+startlongitude+","+startlatitude+",["+endlongitude+","+endlatitude+"]]");
//        NetworkTask nt = new NetworkTask(callback,0);
//        new NetworkTaskJSON().execute(url);
//        nt.execute(url);
    }


//    Given start and end point, find pitch angle to the end point


    public static void getPitchAngleToEndPoint( double startlatitude, double startlongitude , double endlatitude, double endlongitude){
//        http://54.237.80.7:8080/LOSService/ws/los/earthLOS/calculateAngle?path=[[-118.20177021,34.12922031],[-118.20775683,34.24009466]]
        //Double Check int Value

        String url = ("http://34.227.91.44:8080/LOSService/ws/los/earthLOS/calculateAngle?path=[["+startlongitude+","+startlatitude+"],["+endlongitude+","+endlatitude+"]]");
//        new NetworkTaskJSON().execute(url);
//        NetworkTask nt = new NetworkTask(callback,0);
//        nt.execute(url);
    }




//    Given multiple points in WKT format, return points with elevation values

    public static String getPointElevation(double startlatitude, double startlongitude ){

        String url = ("http://34.227.91.44:8080/LOSService/ws/los/earthLOS/getElevations?multiPoints=multipoint(("+startlongitude+","+startlatitude+"))");
//        NetworkTaskJSON nt = new NetworkTaskJSON(callback,0);
//        nt.execute(url);
    return url;
    }



}
