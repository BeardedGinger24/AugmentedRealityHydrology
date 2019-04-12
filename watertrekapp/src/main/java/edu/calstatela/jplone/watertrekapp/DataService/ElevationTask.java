package edu.calstatela.jplone.watertrekapp.DataService;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ElevationTask {
    static String TAG = "ElevationTask";

    public static class MultiPoint extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String address = ("http://34.227.91.44:8080/LOSService/ws/los/earthLOS/getElevations?multiPoints=multipoint"+strings[0]);
            StringBuilder sb = new StringBuilder();
            Log.d(TAG,address);
            try {
                URL url = new URL(address);

                HttpURLConnection urlConnection =
                        (HttpURLConnection) url.openConnection();
                InputStream content = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line = reader.readLine()) != null ){
                    sb.append(line);
                }
                return sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
