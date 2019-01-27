package edu.calstatela.jplone.watertrekapp.Helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CSVReader {

    public static float[] readCSV(File file){
        String TAG = "Mesh";
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
}
