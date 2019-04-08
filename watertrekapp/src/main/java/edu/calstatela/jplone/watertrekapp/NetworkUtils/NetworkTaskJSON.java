package edu.calstatela.jplone.watertrekapp.NetworkUtils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class NetworkTaskJSON extends AsyncTask<String,Void,String> {
    private NetworkCallback callbackJS;
    private int data_type;
    public NetworkTaskJSON(NetworkCallback callbackJS,int data_type )
    {
        this.callbackJS = callbackJS;
        this.data_type = data_type;
    }

    public String readJSONFeed(String address){
        URL url = null;
        try{
            url = new URL(address);
        }catch (MalformedURLException e){
            e.printStackTrace();
        };
        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection urlConnection = null;
        Log.d("NTJ",url+"");
        try{
            urlConnection=(HttpURLConnection) url.openConnection();
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            InputStream content = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }
        return stringBuilder.toString();
    }

    @Override
    protected  String doInBackground(String... params){

        return readJSONFeed(params[0]);
    }

    protected void onPostExecute(String result){
        Log.d("NTJ",result);
        try{
            JSONObject object = new JSONObject(result);
            String doesitObstruct = object.toString();
            callbackJS.onResult(this.data_type, doesitObstruct);
        } catch (JSONException e) {
            e.printStackTrace();
            super.onPostExecute(result);
        }

    }


    public interface NetworkCallback {
        String onResult(int type, String result);
    }
}
