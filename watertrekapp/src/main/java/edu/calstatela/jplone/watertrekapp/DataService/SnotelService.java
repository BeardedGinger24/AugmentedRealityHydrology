package edu.calstatela.jplone.watertrekapp.DataService;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.Data.Snotel;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;

/**
 * Created by nes on 3/15/18.
 */

public class SnotelService {

    public static void getAllSnotel(NetworkTask.NetworkCallback callback){
        //404 error. Check after 3/5/18
        String url = "https://watertrek.jpl.nasa.gov/hydrology/rest/snotel/station_id";
        NetworkTask nt = new NetworkTask(callback, Snotel.TYPE_ID);
        nt.execute(url);
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
