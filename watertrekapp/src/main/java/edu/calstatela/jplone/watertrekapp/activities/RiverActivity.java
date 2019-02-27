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

import edu.calstatela.jplone.watertrekapp.Data.River;
import edu.calstatela.jplone.watertrekapp.Data.SoilMoisture;
import edu.calstatela.jplone.watertrekapp.R;
// class holder not ready at all
public class RiverActivity extends AppCompatActivity {
    MapView map;
    GeoPoint defaultLocation;
    IMapController mapController;
    ItemizedOverlayWithFocus<OverlayItem> mOverlay;
    ArrayList<OverlayItem> markers;
    String  RiveruniqueID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        float lat = Float.parseFloat(getIntent().getStringExtra("lat"));
        float lon = Float.parseFloat(getIntent().getStringExtra("lon"));
        RiveruniqueID = getIntent().getStringExtra("siteNO");
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
        marker.setIcon(this.getDrawable(R.drawable.river_ico_clr_marker));
        marker.setTitle("River: LatLon("+lat+","+lon+")");
        map.getOverlays().add(marker);

        //*********Launch History Activity******************
        Button goHist = (Button) findViewById(R.id.go2hist);


        goHist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiverActivity.this,
                        HistoryActivity.class);
                intent.putExtra("RiverID", RiveruniqueID);
                startActivity(intent); // startActivity allow you to move
            }
        });
    }
    public static void launchDetailsActivity(Activity currentActivity, River e) {
        Log.d("LaunchRiverdetails","going now...");
        Intent intent = new Intent(currentActivity, RiverActivity.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("siteNO",e.getSiteNo());
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
