package edu.calstatela.jplone.watertrekapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.arframework.landmark.Landmark;
import edu.calstatela.jplone.arframework.landmark.LandmarkTable;
import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.watertrekapp.Data.Well;
import edu.calstatela.jplone.watertrekapp.DataService.WellService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.R;
import edu.calstatela.jplone.watertrekapp.WatertrekCredentials;
import edu.calstatela.jplone.watertrekapp.billboardview.BillboardView_sorting;


public class MainActivity extends AppCompatActivity implements BillboardView_sorting.TouchCallback{
    private static final String TAG = "waka-MainActivity";
    private static final int CREDENTIALS_ACTIVITY_REQUEST_CODE = 5;

    private RelativeLayout drawerContentsLayout;
    private DrawerLayout mainDrawerLayout;
    private BillboardView_sorting arview;
    private SeekBar radiusSeekBar;

    private boolean tMountain = false;
    private boolean tReservoir = false;
    private boolean tWell = false;
    private boolean tRiver = false;
    private boolean tSoil = false;
    private int radius = 20;

    private ArrayList<Well> wellList = new ArrayList<>();
    private LandmarkTable mountainList = new LandmarkTable();
    int mountainPrefix = 2000000000;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Activity Lifecycle
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WatertrekCredentials credentials = new WatertrekCredentials(this);
        NetworkTask.updateWatertrekCredentials(credentials.getUsername(), credentials.getPassword());

        drawerContentsLayout = (RelativeLayout)findViewById(R.id.whatYouWantInLeftDrawer);
        mainDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        radiusSeekBar = findViewById(R.id.seekBar);
        radiusSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        arview = new BillboardView_sorting(this);
        arview.setTouchCallback(this);
        arview.setDeviceOrientation(Orientation.getOrientationAngle(this));

        FrameLayout mainLayout = (FrameLayout)findViewById(R.id.ar_view_container);
        mainLayout.addView(arview);

        mountainList.loadMountains();
    }

    @Override
    protected void onPause() {
        super.onPause();
        arview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        arview.onResume();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Drawer methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onMenuButtonClicked(View view) {
        mainDrawerLayout.openDrawer(drawerContentsLayout);
    }

    public void toggleMountain(View v) {
        tMountain = !tMountain;

        if(tMountain)
            addMountains();
        else
            removeMountains();
    }

    public void toggleReservoir(View v) {
        tReservoir = !tReservoir;
    }

    public void toggleWell(View v) {
        tWell = !tWell;

        if(tWell)
            addWells();
        else
            removeWells();
    }

    public void toggleRiver(View v) {
        tRiver = !tRiver;
    }

    public void toggleSoil(View v) {
        tSoil = !tSoil;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Credentials Methods
    //
    //////////////////////////////////////////////////////////////////////////////////////////////
    public void launchWatertrekCredentials(View v){
        WatertrekCredentials credentials = new WatertrekCredentials(this);
        CredentialsActivity.launch(this, credentials.getUsername(), credentials.getPassword(), CREDENTIALS_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREDENTIALS_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            String newUsername = data.getStringExtra("username");
            String newPassword = data.getStringExtra("password");
            WatertrekCredentials credentials = new WatertrekCredentials(this);
            credentials.setUsername(newUsername);
            credentials.setPassword(newPassword);
            NetworkTask.updateWatertrekCredentials(newUsername, newPassword);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Mountain Data Methods
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void addMountains(){
        for(int i = 0; i < mountainList.size(); i++){
            Landmark l = mountainList.get(i);
            arview.addBillboard(mountainPrefix+i, R.drawable.mtn_res_ico_clr, l.title, l.description, l.latitude, l.longitude, l.altitude);
        }
    }

    private void removeMountains(){
        for(int i = 0; i < mountainList.size(); i++){
            Landmark l = mountainList.get(i);
            arview.removeBillboard(mountainPrefix+i);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Well Data Methods
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void addWells(){
        float[] loc = arview.getLocation();
        WellService.getWells(wellNetworkCallback, loc[0], loc[1], radius);
    }

    private void removeWells(){
        for(Well well : wellList){
            int id = Integer.parseInt(well.getMasterSiteId());
            arview.removeBillboard(id);
        }
        wellList.clear();
    }

    NetworkTask.NetworkCallback wellNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            List<Well> lWellList = WellService.parseWells(result);
            for(Well well : lWellList){
                wellList.add(well);
                arview.addBillboard(
                        Integer.parseInt(well.getMasterSiteId()),
                        R.drawable.well_bb_icon,
                        "Well #" + well.getMasterSiteId(),
                        "(" + well.getLat() + ", " + well.getLon() + ")",
                        Float.parseFloat(well.getLat()), Float.parseFloat(well.getLon()), 0
                );
            }
        }
    };



    //////////////////////////////////////////////////////////////////////////////////////////////

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            radius = 5 + i * 5;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTouch(int id) {
        Log.d(TAG, "Clicked billboard id: " + id);
//        arview.removeBillboard(id);

        Well well = null;
        for(Well w : wellList){
            int wId = Integer.parseInt(w.getMasterSiteId());
            if(wId == id) {
                well = w;
                break;
            }
        }
        if(well != null) {
            DetailsActivity.launchDetailsActivity(this, "well", well.toString());
            return;
        }


        Landmark landmark = null;
        for(int i = 0; i < mountainList.size(); i++){
            Landmark l = mountainList.get(i);
            if(id == i + mountainPrefix){
                landmark = l;
                break;
            }
        }
        if(landmark != null){
            DetailsActivity.launchDetailsActivity(this, "well", landmark.title + "\n" + landmark.description);
        }

    }
}
