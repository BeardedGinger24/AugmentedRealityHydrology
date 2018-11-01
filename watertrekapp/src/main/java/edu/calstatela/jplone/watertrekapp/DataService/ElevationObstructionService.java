package edu.calstatela.jplone.watertrekapp.DataService;


import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;

public class ElevationObstructionService {
//    Find obstruction given location, bearing, and pitch:





    public static void getObstruction(NetworkTask.NetworkCallback callback, double latitude, double longitude , double bearing, double pitch){
//        http://54.237.80.7:8080/LOSService/ws/los/earthLOS/firstViewObs?origin=[-118.20177021,34.12922031]&bearing=29&pitch=5

        //Double Check int Value
        String url = ("http://54.237.80.7:8080/LOSService/ws/los/earthLOS/firstViewObs?origin=["+longitude+","+latitude+"]&bearing="+bearing+"&pitch="+pitch);
        NetworkTask nt = new NetworkTask(callback,0);
        nt.execute(url);
    }


//    Given start and end point find the pitch angle of highest elevation point in path.

    public static void getPitchAngleAtHighestElevationPoint(NetworkTask.NetworkCallback callback, double startlatitude, double startlongitude , double endlatitude, double endlongitude){
        //http://54.237.80.7:8080/LOSService/ws/los/earthLOS/calculateAngleOfHighest?path=[[-118.20177021,34.12922031],[-118.20775683,34.24009466]]
        //Double Check int Value
        String url = ("http://54.237.80.7:8080/LOSService/ws/los/earthLOS/calculateAngleOfHighest?path=[["+startlongitude+","+startlatitude+",["+endlongitude+","+endlatitude+"]]");
        NetworkTask nt = new NetworkTask(callback,0);
        nt.execute(url);
    }


//    Given start and end point, find pitch angle to the end point


    public static void getPitchAngleToEndPoint(NetworkTask.NetworkCallback callback, double startlatitude, double startlongitude , double endlatitude, double endlongitude){
//        http://54.237.80.7:8080/LOSService/ws/los/earthLOS/calculateAngle?path=[[-118.20177021,34.12922031],[-118.20775683,34.24009466]]
        //Double Check int Value

        String url = ("http://54.237.80.7:8080/LOSService/ws/los/earthLOS/calculateAngle?path=[["+startlongitude+","+startlatitude+",["+endlongitude+","+endlatitude+"]]");
        NetworkTask nt = new NetworkTask(callback,0);
        nt.execute(url);
    }




//    Given multiple points in WKT format, return points with elevation values

    public static void getPointsElevationValues(NetworkTask.NetworkCallback callback, double startlatitude, double startlongitude , double endlatitude, double endlongitude){
//        http://54.237.80.7:8080/LOSService/ws/los/earthLOS/getElevations?multiPoints=multipoint((-118.16,34.07),(-118.2,34.1),(-118.17,34.22))
        //FIX VALUES
        String url = ("http://54.237.80.7:8080/LOSService/ws/los/earthLOS/getElevations?multiPoints=multipoint((-118.16,34.07),(-118.2,34.1),(-118.17,34.22))");
        NetworkTask nt = new NetworkTask(callback,0);
        nt.execute(url);
    }



}
