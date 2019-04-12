package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;


import edu.calstatela.jplone.watertrekapp.Data.SoilMoisture;
import edu.calstatela.jplone.watertrekapp.R;

public class SoilMoistureActivity extends AppCompatActivity {
    MapView map;
    GeoPoint defaultLocation;
    IMapController mapController;
    ItemizedOverlayWithFocus<OverlayItem> mOverlay;
    ArrayList<OverlayItem> markers;
    String SoilMoistureuniqueID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        float lat = Float.parseFloat(getIntent().getStringExtra("lat"));
        float lon = Float.parseFloat(getIntent().getStringExtra("lon"));
        SoilMoistureuniqueID = getIntent().getStringExtra("wbanno");
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
        marker.setIcon(this.getDrawable(R.drawable.soil_res_ico_clr_sm));
        marker.setTitle("Soil Moisture: LatLon("+lat+","+lon+")");
        map.getOverlays().add(marker);

        //*********Launch History Activity******************
//        Button goHist = (Button) findViewById(R.id.go2hist);
//
//
//        goHist.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SoilMoistureActivity.this,
//                        HistoryActivity.class);
//                intent.putExtra("SoilID", SoilMoistureuniqueID);
//                startActivity(intent); // startActivity allow you to move
//            }
//        });

    }
    public static void launchDetailsActivity(Activity currentActivity, SoilMoisture e) {
        Log.d("LaunchSoildetails","going now...");
        Intent intent = new Intent(currentActivity, SoilMoistureActivity.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("wbanno",e.getWbanno());
        currentActivity.startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }
}
