package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import edu.calstatela.jplone.watertrekapp.Data.Reservoir;
import edu.calstatela.jplone.watertrekapp.Data.River;
import edu.calstatela.jplone.watertrekapp.Data.Snotel;
import edu.calstatela.jplone.watertrekapp.Data.SoilMoisture;
import edu.calstatela.jplone.watertrekapp.Data.Well;
import edu.calstatela.jplone.watertrekapp.Fragments.BlankFragment;
import edu.calstatela.jplone.watertrekapp.Fragments.GraphFragment;
import edu.calstatela.jplone.watertrekapp.Fragments.TestFragment;
import edu.calstatela.jplone.watertrekapp.R;

public class Navigation extends AppCompatActivity {

    // Reference to Bottom Navigation
    // https://medium.com/@oluwabukunmi.aluko/bottom-navigation-view-with-fragments-a074bfd08711

    final Fragment fragment1 = new MapActivity();
    final Fragment fragment2 = new GraphFragment();
    final Fragment fragment3 = new edu.calstatela.jplone.watertrekapp.Fragments.ListFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map_id:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;
                case R.id.navigation_graph_id:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;
                case R.id.navigation_list_id:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();

    }

    public static void launchWellDetailsActivity(Activity currentActivity, Well e) {
        Intent intent = new Intent(currentActivity, Navigation.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("masterSiteId",e.getMasterSiteId());
        currentActivity.startActivity(intent);
    }

    public static void launchSoilDetailsActivity(Activity currentActivity, SoilMoisture e) {
        Log.d("LaunchSoildetails","going now...");
        Intent intent = new Intent(currentActivity, Navigation.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("wbanno",e.getWbanno());
        currentActivity.startActivity(intent);
    }


    public static void launchSnotelDetailsActivity(Activity currentActivity, Snotel e) {
        Log.d("snow","Launching Snotel Detailes Activty going now...");
        Log.d("snow",e.getStationId());
        Log.d("snow",e.getLat());
        Log.d("snow",e.getLon());
        Log.d("snow",e.toString());
        Intent intent = new Intent(currentActivity, Navigation.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("SnotelsiteNO",e.getStationId());
        currentActivity.startActivity(intent);
    }

    public static void launchRiverDetailsActivity(Activity currentActivity, River e) {
        Log.d("LaunchRiverdetails","going now...");
        Intent intent = new Intent(currentActivity, Navigation.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("RiversiteNO",e.getSiteNo());
        currentActivity.startActivity(intent);
    }

    public static void launchReservoirDetailsActivity(Activity currentActivity, Reservoir e) {
        Intent intent = new Intent(currentActivity, Navigation.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("ReservoirsiteNO",e.getSiteNo());
        currentActivity.startActivity(intent);
    }

}
