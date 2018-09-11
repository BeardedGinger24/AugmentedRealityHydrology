package edu.calstatela.jplone.watertrekapp.DataService;


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
    public static void getAllStorageValues(NetworkTask.NetworkCallback callback, String site_no){
        String url ="https://watertrek.jpl.nasa.gov/hydrology/rest/reservoir/site_no/"+ site_no+"/storage";
        //Log.d("URL", ""+url);
        NetworkTask nt = new NetworkTask(callback, Reservoir.ADDTL_ID);
        //Log.d("getSTRG", "In REST CALL");
        nt.execute(url);
    }

    //Add created Reservoir objects to list and return.
    public static List parseAllReservoirs(String line){
        List<Reservoir> reservoirList = new ArrayList();

        String[] lines = line.split("\n");
        for(int i=1; i<lines.length; i++) reservoirList.add(parseReservoir(lines[i]));
        return reservoirList;
    }

    //Create Reservoir object. Read in one line and pass to constructor.
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
