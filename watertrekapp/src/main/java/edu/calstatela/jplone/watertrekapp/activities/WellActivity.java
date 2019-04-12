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
import edu.calstatela.jplone.watertrekapp.Data.Well;
import edu.calstatela.jplone.watertrekapp.R;
public class WellActivity extends AppCompatActivity{
    String TAG = "well-activity";
    MapView map;
    GeoPoint defaultLocation;
    IMapController mapController;
    ItemizedOverlayWithFocus<OverlayItem> mOverlay;
    ArrayList<OverlayItem> markers;
    String  welluniqueID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        float lat = Float.parseFloat(getIntent().getStringExtra("lat"));
        float lon = Float.parseFloat(getIntent().getStringExtra("lon"));
        welluniqueID = getIntent().getStringExtra("masterSiteId");
        Log.d("wwwid",welluniqueID);
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
        marker.setIcon(this.getDrawable(R.drawable.well_res_ico_clr_sm));
        marker.setTitle("Well: LatLon("+lat+","+lon+")");
        map.getOverlays().add(marker);

//        Button goHist = (Button) findViewById(R.id.go2hist);


//        goHist.setOnClickListener(new View.OnClickListener() {
//
//            @Override
////            public void onClick(View v) {
////                Intent intent = new Intent(WellActivity.this,
////                        HistoryActivity.class);
////                intent.putExtra("wellID", welluniqueID);
////                startActivity(intent); // startActivity allow you to move
////            }
//            public void onClick(View v) {
//                Intent intent = new Intent(WellActivity.this,
//                        HistoryActivity.class);
//                intent.putExtra("wellID", welluniqueID);
//                startActivity(intent); // startActivity allow you to move
//            }
//        });
        Log.d(TAG,"IN ON CREATE");
    }
    public static void launchDetailsActivity(Activity currentActivity, Well e) {
        Intent intent = new Intent(currentActivity, WellActivity.class);
        intent.putExtra("data", e.toString());
        intent.putExtra("lat",e.getLat());
        intent.putExtra("lon",e.getLon());
        intent.putExtra("masterSiteId",e.getMasterSiteId());
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