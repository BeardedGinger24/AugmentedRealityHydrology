package edu.calstatela.jplone.watertrekapp.NetworkUtils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import edu.calstatela.jplone.watertrekapp.WatertrekCredentials;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by bill on 2/27/18.
 */

public class NetworkTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "waka-NetworkTask";
    private int data_type;
    private NetworkCallback callback;
    private static String watertrekUsername = "";
    private static String watertrekPassword = "";



    public NetworkTask(NetworkCallback callback, int data_type) {
        this.callback = callback;
        this.data_type = data_type;
    }

    @Override
    protected String doInBackground(String... params) {
        if(params.length == 0) // expecting a URL String
            return null;
        try {
            URL url = new URL(params[0]);
            HttpsURLConnection urlConnection =
                    (HttpsURLConnection) url.openConnection();

            Authenticator.setDefault(new Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(watertrekUsername, watertrekPassword.toCharArray());
                }
            });

            urlConnection.connect();

            String response = urlConnection.getResponseMessage();

            if(response.equals("OK")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String result = "";
                String line;
                int count = 0;

                //Removed second condition of '&& count <=100'
                while((line = br.readLine()) != null ) {
                    result += line + "\n";
                    count++;
                }
                br.close();
                return result;

            }

            return null;
        }
        catch(Exception e) {
            Log.d(TAG, "Exception!");
            Log.d(TAG,  e.toString() + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        callback.onResult(this.data_type, result);
    }

    public interface NetworkCallback {
        void onResult(int type, String result);
    }


    public static void updateWatertrekCredentials(String username, String password){
        watertrekUsername = username;
        watertrekPassword = password;
    }

}