package edu.calstatela.jplone.watertrekapp;

import android.content.Context;
import android.content.SharedPreferences;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class WatertrekCredentials {

    private static final String USERNAME_KEY = "watertrekUsername";
    private static final String PASSWORD_KEY = "watertrekPassword";

    private SharedPreferences sharedPreferences;

    public WatertrekCredentials(Context context){
        sharedPreferences = getDefaultSharedPreferences(context);
    }

    public void setUsername(String username){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();
    }

    public String getUsername(){
        return sharedPreferences.getString(USERNAME_KEY, "");
    }

    public void setPassword(String password){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PASSWORD_KEY, password);
        editor.apply();
    }

    public String getPassword(){
        return sharedPreferences.getString(PASSWORD_KEY, "");
    }
}
