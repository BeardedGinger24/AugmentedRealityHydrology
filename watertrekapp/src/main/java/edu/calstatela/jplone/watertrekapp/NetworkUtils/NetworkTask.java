package edu.calstatela.jplone.watertrekapp.NetworkUtils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
        if(params.length == 0) { // expecting a URL String
            Log.d(TAG,"In if");
            return null;
        }else{
            Log.d(TAG,"In if");
        }
        try {
            URL url = new URL(params[0]);
            Log.d(TAG,"In Try");
            HttpsURLConnection urlConnection =
                    (HttpsURLConnection) url.openConnection();
            Log.d(TAG,"After openConnect()");
            Authenticator.setDefault(new Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(watertrekUsername, watertrekPassword.toCharArray());
                }
            });
            Log.d(TAG,"After authenticator");
            urlConnection.connect();
            Log.d(TAG,"After connect()");
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
                Log.d(TAG,"RESPONSE WAS: "+response);
                //return "OK";
                return result;
            }else{
                Log.d(TAG,"RESPONSE WAS: "+response);
                Log.d(TAG,"VALID CREDENTIALS");
                //handle valid credentials here??
                return "Valid";
            }
        }
        catch(Exception e) {
            Log.d(TAG, "Exception!");
            Log.d(TAG,  e.toString() + ": " + e.getMessage());
            Log.d(TAG,"INVALID CREDENTIALS");
            //handle invalid credentials here??
            return "Invalid";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,"In postExe" + result);
        callback.onResult(this.data_type, result);
//        return result;
    }

    public interface NetworkCallback {
        void onResult(int type, String result);
    }


    public static void updateWatertrekCredentials(String username, String password){
        Log.d(TAG,"In updateCredentials");
        watertrekUsername = username;
        watertrekPassword = password;
    }

    public static String[] getCredentials(){
        return new String[]{watertrekUsername,watertrekPassword};
    }
}