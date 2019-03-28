package edu.calstatela.jplone.watertrekapp.activities;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import java.util.ArrayList;

import edu.calstatela.jplone.watertrekapp.Data.Reservoir;
import edu.calstatela.jplone.watertrekapp.Data.River;
import edu.calstatela.jplone.watertrekapp.Data.Snotel;
import edu.calstatela.jplone.watertrekapp.Data.SoilMoisture;
import edu.calstatela.jplone.watertrekapp.Data.Well;
import edu.calstatela.jplone.watertrekapp.R;
public class MapActivity extends AppCompatActivity{
    String TAG = "MAP-activity";
    MapView map;
    GeoPoint defaultLocation;
    IMapController mapController;
    ItemizedOverlayWithFocus<OverlayItem> mOverlay;
    ArrayList<OverlayItem> markers;
    //********************
    String  welluniqueID;
    String SoilMoistureuniqueID;
    String  SnoteluniqueID;
    String  RiveruniqueID;
    String ReservoiruniqueID;
    //*******************
    Boolean isWellNull;
    Boolean isRiverNull;
    Boolean isReservoirNull;
    Boolean isSoilNull;
    Boolean isSnotelNull;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        float lat = Float.parseFloat(getIntent().getStringExtra("lat"));
        float lon = Float.parseFloat(getIntent().getStringExtra("lon"));
        //******************************
        welluniqueID = getIntent().getStringExtra("masterSiteId");
        SoilMoistureuniqueID = getIntent().getStringExtra("wbanno");
        SnoteluniqueID = getIntent().getStringExtra("SnotelsiteNO");
        RiveruniqueID = getIntent().getStringExtra("RiversiteNO");
        ReservoiruniqueID = getIntent().getStringExtra("ReservoirsiteNO");
        //************************
            isWellNull = welluniqueID == null;
            isRiverNull = RiveruniqueID == null;
            isReservoirNull = ReservoiruniqueID == null;
            isSoilNull = SoilMoistureuniqueID == null;
            isSnotelNull = SnoteluniqueID == null;

        defaultLocation = new GeoPoint(lat,lon);
        TextView txtData = findViewById(R.id.txt_data);
        txtData.setText(getIntent().getStringExtra("data"));
        map = findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(18);
        mapController.setCenter(defaultLocation);
        Marker marker = new Marker(map);
        marker.setPosition(defaultLocation);
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);


       //*********************
        if (isWellNull == false){
            marker.setIcon(this.getDrawable(R.drawable.well_res_ico_clr_sm));
            marker.setTitle("Well: LatLon("+lat+","+lon+")");
        }
        if(isSoilNull == false){
            marker.setIcon(this.getDrawable(R.drawable.soil_res_ico_clr_sm));
            marker.setTitle("Soil Moisture: LatLon("+lat+","+lon+")");
        }
        if(isSnotelNull == false){
            marker.setIcon(this.getDrawable(R.drawable.snotel_res_ico_sm));
            marker.setTitle("Snotel: LatLon("+lat+","+lon+")");
        }
        if(isRiverNull == false){
            marker.setIcon(this.getDrawable(R.drawable.river_res_ico_clr_sm));
            marker.setTitle("River: LatLon("+lat+","+lon+")");
        }
        if(isReservoirNull == false){
            marker.setIcon(this.getDrawable(R.drawable.reservoir_res_ico_clr_sm));
            marker.setTitle("Reservoir: LatLon("+lat+","+lon+")");
        }
        //*************************************


        map.getOverlays().add(marker);

        Button goHist = (Button) findViewById(R.id.go2hist);


        goHist.setOnClickListener(new View.OnClickListener() {

            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(WellActivity.this,
//                        HistoryActivity.class);
//                intent.putExtra("wellID", welluniqueID);
//                startActivity(intent); // startActivity allow you to move
//            }
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this,
                        HistoryActivity.class);
                //*********************
                if (isWellNull == false){
                    intent.putExtra("wellID", welluniqueID);
                }
                if(isSoilNull == false){
                    intent.putExtra("SoilID", SoilMoistureuniqueID);
                }
                if(isSnotelNull == false){
                    intent.putExtra("SnotelID", SnoteluniqueID);
                }
                if(isRiverNull == false){
                    intent.putExtra("RiverID", RiveruniqueID);
                }
                if(isReservoirNull == false){
                    intent.putExtra("ReservoirID", ReservoiruniqueID);
                }
                //*************************************
                startActivity(intent); // startActivity allow you to move
            }
        });
        Log.d(TAG,"IN ON CREATE");
    }

    public static void launchWellDetailsActivity(Activity currentActivity, Well e) {
        Intent intent = new Intent(currentActivity, MapActivity.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("masterSiteId",e.getMasterSiteId());
        currentActivity.startActivity(intent);
    }

    public static void launchSoilDetailsActivity(Activity currentActivity, SoilMoisture e) {
        Log.d("LaunchSoildetails","going now...");
        Intent intent = new Intent(currentActivity, MapActivity.class);
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
        Intent intent = new Intent(currentActivity, MapActivity.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("SnotelsiteNO",e.getStationId());
        currentActivity.startActivity(intent);
    }

    public static void launchRiverDetailsActivity(Activity currentActivity, River e) {
        Log.d("LaunchRiverdetails","going now...");
        Intent intent = new Intent(currentActivity, MapActivity.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("RiversiteNO",e.getSiteNo());
        currentActivity.startActivity(intent);
    }

    public static void launchReservoirDetailsActivity(Activity currentActivity, Reservoir e) {
        Intent intent = new Intent(currentActivity, MapActivity.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("ReservoirsiteNO",e.getSiteNo());

        currentActivity.startActivity(intent);
    }

    @Override

    protected void onPause() {
        super.onPause();
        map.onPause();
        Log.d(TAG,"IN ON PAUSE");
    }
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
        Log.d(TAG,"IN ON RESUME");
    }
}