package edu.calstatela.jplone.watertrekapp.Helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.calstatela.jplone.watertrekapp.Data.Vector3;

public class CSVReader {
    static String TAG = "waka-CSV";
    public static float[] readCSV(File file){
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line=reader.readLine()) !=null){
                sb.append(line);
            }
            reader.close();

            Log.d(TAG,sb.toString());
            String[] stringresult = sb.toString().split(",");
            float[] result = new float[stringresult.length];

            for(int i = 0; i<stringresult.length; i++){
                float temp = Float.parseFloat(stringresult[i]);
                result[i] = temp;
            }

            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Vector3[] readVecFile(File file){

        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line=reader.readLine()) !=null){
                sb.append(line);
            }
            reader.close();

            Log.d(TAG,sb.toString());
            String[] stringresult = sb.toString().split(";");
            Vector3[] result = new Vector3[stringresult.length];

            for(int i = 0; i<stringresult.length; i++){
                Vector3 tempVec = new Vector3(0,0,0);
                String[] tempVals = stringresult[i].split(",");
                tempVec.setX(Double.parseDouble(tempVals[0]));
                tempVec.setY(Double.parseDouble(tempVals[1]));
                tempVec.setZ(Double.parseDouble(tempVals[2]));

                result[i] = tempVec;
            }

            Log.d(TAG,"Reached Result in CSV READER");
            return result;

        } catch (FileNotFoundException e) {
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
