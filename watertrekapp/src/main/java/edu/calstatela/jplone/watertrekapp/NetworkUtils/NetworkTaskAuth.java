package edu.calstatela.jplone.watertrekapp.NetworkUtils;

import android.os.AsyncTask;

import org.json.JSONException;
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

public class NetworkTaskAuth extends AsyncTask<String,Void,String> {
    private NetworkTaskAuth.NetworkCallback callbackJS;
    private int data_type;
    public NetworkTaskAuth(NetworkTaskAuth.NetworkCallback callbackJS, int data_type )
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
//    private ConnectivityManager.NetworkCallback callback;

//    public NetworkTaskJSON(ConnectivityManager.NetworkCallback callback, int data_type) {
//        this.callback = callback;
//        this.data_type = data_type;
//    }

    @Override
    protected  String doInBackground(String... params){
//        try{
////            String myurl = params[0];
//            URL jurl = new URL(params[0]);
//
////            URL jurl = new URL(params[0]);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(jurl.openConnection().getInputStream(), "UTF-8"));
//        String jsonString = reader.readLine();
//            Log.d("JSON", jsonString);
//
//        }catch (MalformedURLException e){
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return readJSONFeed (params[0]);
    }

    protected void onPostExecute(String result){
        try{

            JSONObject object = new JSONObject(result);
//            Log.d("JSON", object.toString());
            String doesitObstruct = object.toString();
            callbackJS.onResult(this.data_type, doesitObstruct);
//            Toast.makeText( context,object.toString(),Toast.LENGTH_LONG).show();
//            JSONArray Jarray  = object.getJSONArray("obstructed_view");
//            or (int i = 0; i < Jarray.length(); i++)
//            {
//                JSONObject Jasonobject = Jarray.getJSONObject(i);
//            }

            // JSONArray jsonArray = new JSONArray(result);
            //  Log.i("JSON","NUmber of surveys fed" + jsonArray.length());

            // print out the content of the json feed
//            for (int i =0; i<=jsonArray.length();i++)
//            {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                Log.i("JSON","NUmber of surveys fed" + jsonObject.getString("obstructed_view"));
////                Toast.makeText(getBaseContext(),jsonObject.getString("obstructed_view"), Toast.LENGTH_LONG).show();
//            }
//                        for (int i = 0; i < Jarray.length(); i++)
//            {
//                JSONObject Jasonobject = Jarray.getJSONObject(i);
//                                Log.d("JSON","Number of surveys fed" + Jasonobject);
//
//            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    public interface NetworkCallback {
        void onResult(int type, String result);
    }
}
