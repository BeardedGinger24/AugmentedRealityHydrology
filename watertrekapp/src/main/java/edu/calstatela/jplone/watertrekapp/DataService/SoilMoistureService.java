package edu.calstatela.jplone.watertrekapp.DataService;



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

    public static SoilMoisture parseSoilMoisture(String line) {
        String[] rowEntry = line.split("\t");
        SoilMoisture sm = new SoilMoisture(rowEntry);
        return sm;
    }

    public static List<SoilMoisture> parseSoilMoistures(String line){
        List<SoilMoisture> soilList = new ArrayList<>();
        String[] lines = line.split("\n");
        for(int i=1; i<lines.length; i++) soilList.add(parseSoilMoisture(lines[i]));
        return soilList;

}

    }

