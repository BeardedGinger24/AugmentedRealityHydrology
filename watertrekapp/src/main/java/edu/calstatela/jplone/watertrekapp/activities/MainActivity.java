package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.arframework.landmark.Landmark;
import edu.calstatela.jplone.arframework.landmark.LandmarkTable;
import edu.calstatela.jplone.arframework.ui.SensorARView;
import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.watertrekapp.Data.Reservoir;
import edu.calstatela.jplone.watertrekapp.Data.Well;
import edu.calstatela.jplone.watertrekapp.DataService.ElevationObstructionService;
import edu.calstatela.jplone.watertrekapp.DataService.ReservoirService;
import edu.calstatela.jplone.watertrekapp.DataService.WellService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTaskJSON;
import edu.calstatela.jplone.watertrekapp.R;
import edu.calstatela.jplone.watertrekapp.WatertrekCredentials;
import edu.calstatela.jplone.watertrekapp.billboardview.BillboardView_sorting;



public class MainActivity extends AppCompatActivity implements BillboardView_sorting.TouchCallback, SensorEventListener{
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    private static final int SENSOR_DELAY = 500 * 1000; // 500ms

    private static final String TAG = "waka-MainActivity";
    private static final int CREDENTIALS_ACTIVITY_REQUEST_CODE = 5;

    private RelativeLayout drawerContentsLayout;
    private DrawerLayout mainDrawerLayout;
    private BillboardView_sorting arview;
    private SensorARView rpy;
    private SeekBar radiusSeekBar;

    private boolean tMountain = false;
    private boolean tReservoir = false;
    private boolean tWell = false;
    private boolean tRiver = false;
    private boolean tSoil = false;

    private Switch toggleSoil;
    private Switch toggleRiver;
    private Switch toggleMountain;
    private Switch toggleWell;
    private Switch toggleReservoir;

    private int radius = 20;
    Button obstruct_button;
    Button login_button;
    Button logout_button;

    private ArrayList<Well> wellList = new ArrayList<>();
    private ArrayList<Reservoir> reservoirList = new ArrayList<>(); ///////////////////////////////////////added by Leo***
    private LandmarkTable mountainList = new LandmarkTable();
    int mountainPrefix = 2000000000;

    private boolean isLoggedIn = false;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Activity Lifecycle
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///$$$$$$$$$$$$$$$$$$$$$$$$for the roll pitch ya,
        try {
            mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            Toast.makeText(this, "Problem With Sensors", Toast.LENGTH_LONG).show();
        }

        //$$$$$$$$$$$$$$$$$$$$$$$$

        if(!isLoggedIn){
            WatertrekCredentials credentials = new WatertrekCredentials(this);
            CredentialsActivity.launch(this, credentials.getUsername(), credentials.getPassword(), CREDENTIALS_ACTIVITY_REQUEST_CODE);
        }else {
            WatertrekCredentials credentials = new WatertrekCredentials(this);
            NetworkTask.updateWatertrekCredentials(credentials.getUsername(), credentials.getPassword());
        }

        drawerContentsLayout = (RelativeLayout)findViewById(R.id.whatYouWantInLeftDrawer);
        mainDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        radiusSeekBar = findViewById(R.id.seekBar);
        radiusSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        login_button = (Button)findViewById(R.id.login_button);
        logout_button = (Button)findViewById(R.id.logout_button);
        toggleMountain = (Switch)findViewById(R.id.switch8);
        toggleReservoir = (Switch)findViewById(R.id.switch11);
        toggleWell = (Switch)findViewById(R.id.switch9);
        toggleRiver = (Switch)findViewById(R.id.switch10);
        toggleSoil = (Switch)findViewById(R.id.switch12);
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
    // When Obstruction View button is clicked or called
    public void obstructionClicked(View view){
        String pitch =  String.valueOf(((TextView)findViewById(R.id.bearingL)).getText());
        String roll = String.valueOf( ((TextView)findViewById(R.id.bearingR)).getText());
//        Double currPitch = Double.parseDouble(pitch);
//        Double currRoll = Double.parseDouble(roll);
        // Retrieves curr location
        float[] loc = arview.getLocation();
        //Longitude
        String longy = Float.toString(loc[1]);
        //Lattitude
        String laty = Float.toString(loc[0]);

        Double currlat =  Double.parseDouble(laty);
        Double currlong =  Double.parseDouble(longy);
        ElevationObstructionService.getObstruction(obstructNetworkCallback,currlat,currlong,roll,pitch);
//        Toast.makeText(this,  pitch + " " + roll, Toast.LENGTH_LONG).show();
//        Toast.makeText(this,  currlat, Toast.LENGTH_LONG).show();
//        Toast.makeText(this,  currlong, Toast.LENGTH_LONG).show();

//        Log.d("obstruct",pitch);
//        Log.d("obstruct","...");
//        Log.d("obstruct",roll);
        // call url request
      //  ElevationObstructionService eoj = new ElevationObstructionService();
        // NetworkTask.NetworkCallback callback, double latitude, double longitude , double bearing, double pitch
      // eoj.getObstruction();
    }


    NetworkTaskJSON.NetworkCallback obstructNetworkCallback = new NetworkTaskJSON.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
//            List<Well> lWellList = WellService.parseWells(result);
//            for(Well well : lWellList){
//                wellList.add(well);
//                arview.addBillboard(
//                        Integer.parseInt(well.getMasterSiteId()),
//                        R.drawable.well_bb_icon,
//                        "Well #" + well.getMasterSiteId(),
//                        "(" + well.getLat() + ", " + well.getLon() + ")",
//                        Float.parseFloat(well.getLat()), Float.parseFloat(well.getLon()), 0
//                );
//            }
//            Toast.makeText(getApplicationContext(),"heloo",Toast.LENGTH_LONG).show();
            Log.d("JSON",result);
        }
    };

    public void toggleMountain(View v) {
        if(isLoggedIn) {
            tMountain = !tMountain;
        }else{
            tMountain = false;
        }
        if(tMountain)
            addMountains();
        else
            removeMountains();
    }

    public void toggleReservoir(View v) {
        if(isLoggedIn) {
            tReservoir = !tReservoir;
        }else{
            tReservoir = false;
        }
        if(tReservoir)
            addReservoirs();
        else
            removeReservoirs();
    }

    public void toggleWell(View v) {
        if(isLoggedIn) {
            tWell = !tWell;
        }else{
            tWell = false;
        }
        if(tWell)
            addWells();
        else
            removeWells();
    }

    public void toggleRiver(View v) {
        if(isLoggedIn) {
            tRiver = !tRiver;
        }else{
            tRiver = false;
        }
    }

    public void toggleSoil(View v) {
        if(isLoggedIn) {
            tSoil = !tSoil;
        }else{
            tSoil = false;
        }
    }

    public void logout(View v){
        isLoggedIn = false;
        login_button.setVisibility(v.VISIBLE);
        logout_button.setVisibility(v.GONE);

        toggleReservoir(v);
        toggleMountain(v);
        toggleWell(v);
        toggleSoil(v);
        toggleRiver(v);

        toggleRiver.setChecked(false);
        toggleSoil.setChecked(false);
        toggleWell.setChecked(false);
        toggleMountain.setChecked(false);
        toggleReservoir.setChecked(false);

        toggleSwitches();
    }
    public void login (View v){
        WatertrekCredentials credentials = new WatertrekCredentials(this);
        CredentialsActivity.launch(this, credentials.getUsername(), credentials.getPassword(), CREDENTIALS_ACTIVITY_REQUEST_CODE);
//        login_button.setVisibility(v.GONE);
//        logout_button.setVisibility(v.VISIBLE);
//        isLoggedIn = true;
//        toggleSwitches();

    }

    public void toggleSwitches(){
        toggleReservoir.setClickable(isLoggedIn);
        toggleRiver.setClickable(isLoggedIn);
        toggleWell.setClickable(isLoggedIn);
        toggleMountain.setClickable(isLoggedIn);
        toggleSoil.setClickable(isLoggedIn);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Credentials Methods
    //
    //////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREDENTIALS_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            String newUsername = data.getStringExtra("username");
            String newPassword = data.getStringExtra("password");
            WatertrekCredentials credentials = new WatertrekCredentials(this);
            credentials.setUsername(newUsername);
            credentials.setPassword(newPassword);
            NetworkTask.updateWatertrekCredentials(newUsername, newPassword);

            login_button.setVisibility(View.GONE);
            logout_button.setVisibility(View.VISIBLE);
            isLoggedIn = true;
            toggleSwitches();
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
    //
    //     Reservoir  Data Methods added by Leo *********************
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void addReservoirs(){
        ReservoirService.getAllStorageValues(reservoirNetworkCallback);
    }
    private void removeReservoirs(){
        for(Reservoir reservoir : reservoirList){
            //Removes Reservoir  based on there unique ID or SiteNO
            int id = Integer.parseInt(reservoir.getSiteNo());
            arview.removeBillboard(id);
        }
        reservoirList.clear();
    }

    NetworkTask.NetworkCallback reservoirNetworkCallback = new NetworkTask.NetworkCallback() {

        @Override
        public void onResult(int type, String result) {
            // variable loc gets current location based on gps longitude and lattitude
            float[] loc = arview.getLocation();     // added by  leo
            // Return reservior nearest to range once passed in currently hard coded
            List<Reservoir> rreservoirList = ReservoirService.parseAllReservoirs(result , loc[0] , loc[1]); // change method
            // for every reservoir obj  that is near me add it to reservoirList and add to Billboard in order to display it
            for(Reservoir reservoirr : rreservoirList){

                reservoirList.add(reservoirr);
                arview.addBillboard(
                        Integer.parseInt(reservoirr.getSiteNo()),
                        R.drawable.reservoir_bb_icon,
                        "Reservoir #" + reservoirr.getSiteNo(),
                        "(" + reservoirr.getLat() + ", " + reservoirr.getLon() + ")",
                        Float.parseFloat(reservoirr.getLat()), Float.parseFloat(reservoirr.getLon()), 0
                );


            }
        }
    };

    ////////////////////////////////////////////////////////////////////////////////
    ///
    ///                             SEEKBAR/SLIDER FOR RANGE
    ///
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
        //added  by leo
        Reservoir  rL = null;
        for(Reservoir r : reservoirList){
            int rId = Integer.parseInt(r.getSiteNo());
            if(rId == id) {
                rL = r;
                break;
            }
        }
        if(rL != null) {
            DetailsActivity.launchDetailsActivity(this, "reservoir", rL.toString());
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
            DetailsActivity.launchDetailsActivity(this, "mountain", landmark.title + "\n" + landmark.description);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//            Pitch: (90 , -90)
//            Bearing/yaw: (0 – 360) in degrees.
//            roll is -90 to 90
//            pitch is 180 to -180
//            yaw is 0 to 270 or 360??

//        Log.d("ROLL: " , GZ2); // goes from 0 to 360 evant value 0
//        // this might be the yaw
//        Log.d("PITCH: " , GX2); // goes from 90 to -90 event value 1 divide by two
//        Log.d("YAW " , GY2); //quadrant 1 && 4 are negative and 2&& 3 are positive  X axis is 90  event value 2



        float azimuth_angle = sensorEvent.values[0];
        String Roll = Float.toString(azimuth_angle);
        ///2
        float pitch_angle = sensorEvent.values[1] + 90;
        String Pitch = Float.toString(-pitch_angle);
        float roll_angle = sensorEvent.values[2];
        //Pitch
        ((TextView)findViewById(R.id.bearingL)).setText( Pitch);
//        Log.d("JSON" , )
        //ROll
        ((TextView)findViewById(R.id.bearingR)).setText(Roll);

    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }






    //*********

}




