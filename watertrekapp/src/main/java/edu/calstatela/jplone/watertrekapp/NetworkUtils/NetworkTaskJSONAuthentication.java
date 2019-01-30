package edu.calstatela.jplone.watertrekapp.NetworkUtils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

public class NetworkTaskJSONAuthentication  {


    public String readJSONFeed(String address) {
        URL url = null;
        try {
            url = new URL(address);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ;
        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication("water", "water4us!".toCharArray());
                }
            });

            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream content = new BufferedInputStream(
                    urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return stringBuilder.toString();
    }
    protected String doInBackground(String... urls) {
        return readJSONFeed(urls[0]);

        //**************************

    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            Log.d("JSON", "ReadingJSONFEED CURRENTLY...");
            return readJSONFeed(urls[0]);
        }

        // depends on JSONOBJECT
        protected void onPostExecute(String result) {
            try {
                JSONObject riverJsonOBJ = new JSONObject(result);
                Log.d("JSON", "Nigga We MAde IT!");

//                JSONArray jsonArray = new JSONArray(result);
                Log.d("JSON", "This is the rivers comID " +
                        riverJsonOBJ.getString("comid"));
                Log.d("JSON",riverJsonOBJ.getString("flowstat") );

                //---print out the content of the json feed---
                JSONArray jsonArrayFlowstat = new JSONArray(riverJsonOBJ.getString("flowstat"));
                for(int x = 0 ;  x <jsonArrayFlowstat.length(); x++){
                    JSONObject jsonObject = jsonArrayFlowstat.getJSONObject(x);
                    Log.d("JSON", "DATE_: " + jsonObject.getString("date"));
                    Log.d("JSON", "RATE: " + jsonObject.getString("rate"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // launches json search to retrieve data
//    new ReadJSONFeedTask().execute(url);

}
