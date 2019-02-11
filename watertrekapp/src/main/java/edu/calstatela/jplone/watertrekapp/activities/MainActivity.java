package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.calstatela.jplone.arframework.landmark.Landmark;
import edu.calstatela.jplone.arframework.landmark.LandmarkTable;
import edu.calstatela.jplone.arframework.ui.SensorARView;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.watertrekapp.Data.Reservoir;
import edu.calstatela.jplone.watertrekapp.Data.Well;
import edu.calstatela.jplone.watertrekapp.DataService.ElevationObstructionService;
import edu.calstatela.jplone.watertrekapp.DataService.ReservoirService;
import edu.calstatela.jplone.watertrekapp.DataService.WellService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.LoginService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTaskJSON;
import edu.calstatela.jplone.watertrekapp.R;
import edu.calstatela.jplone.watertrekapp.WatertrekCredentials;
import edu.calstatela.jplone.watertrekapp.billboardview.BillboardView_sorting;
import it.beppi.balloonpopuplibrary.BalloonPopup;

import com.bvapp.arcmenulibrary.ArcMenu;
import com.bvapp.arcmenulibrary.widget.FloatingActionButton;



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

    private ImageButton ibWell, ibRiver, ibReservoir, ibSoilMoisture, ibMtn;

    private int radius = 20;
    Button mesh_demo;
    Button obstruct_button;
    Button login_button;
    Button logout_button;

    private ArrayList<Well> wellList = new ArrayList<>();
    private ArrayList<Reservoir> reservoirList = new ArrayList<>(); ///////////////////////////////////////added by Leo***
    private LandmarkTable mountainList = new LandmarkTable();
    int mountainPrefix = 2000000000;

    NetworkTask task;
    private boolean isLoggedIn = false;

    //Arc menu items
    private static final int[] ITEM_DRAWABLES = { R.drawable.mtn_res_ico_clr, R.drawable.reservoir_bb_icon, R.drawable.soil_bb_icon,
            R.drawable.well_bb_icon, R.drawable.river_res_ico_clr, R.drawable.eye24 };
    private String[] str = {"mountain","reservoir","soil","well", "river", "eye"};
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

        // Check to see if the user is already logged in
        WatertrekCredentials credentialsTest = new WatertrekCredentials(this);
        String userName = credentialsTest.getUsername();
        String passWord = credentialsTest.getPassword();
        if(!userName.isEmpty() && !passWord.isEmpty()){
            Log.d("USERNAME", "username: " + userName);
            Log.d("PASSWORD", "password: " + passWord);

            isLoggedIn = true;
        }

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

        //mesh demo btn
        mesh_demo = findViewById(R.id.mesh_demo);

        ibWell = (ImageButton) findViewById(R.id.imageButton_Well);
        ibRiver = (ImageButton) findViewById(R.id.imageButton_River);
        ibReservoir = (ImageButton) findViewById(R.id.imageButton_Reservoir);
        ibSoilMoisture = (ImageButton) findViewById(R.id.imageButton_Soil_Moisture);
        ibMtn = (ImageButton) findViewById(R.id.imageButton_Mountain);

        ibWell.setBackgroundTintMode(null);
        ibMtn.setBackgroundTintMode(null);
        ibSoilMoisture.setBackgroundTintMode(null);
        ibReservoir.setBackgroundTintMode(null);
        ibRiver.setBackgroundTintMode(null);

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

        //Floating arc menu
        ArcMenu arcMenu = (ArcMenu) findViewById(R.id.arcMenuX);
        arcMenu.setToolTipTextSize(14);

        arcMenu.setToolTipSide(ArcMenu.TOOLTIP_LEFT);
        arcMenu.setToolTipTextColor(Color.WHITE);
        arcMenu.setToolTipBackColor(Color.parseColor("#88000000"));
        arcMenu.setToolTipCorner(2);
        arcMenu.setToolTipPadding(8);
        arcMenu.setColorNormal(getResources().getColor(R.color.white));
        arcMenu.showTooltip(false);
        arcMenu.setDuration(ArcMenu.ArcMenuDuration.LENGTH_LONG);
        arcMenu.setAnim(500,500, ArcMenu.ANIM_MIDDLE_TO_DOWN, ArcMenu.ANIM_MIDDLE_TO_RIGHT,
                ArcMenu.ANIM_INTERPOLATOR_ANTICIPATE, ArcMenu.ANIM_INTERPOLATOR_ANTICIPATE);
        initArcMenu(arcMenu, str, ITEM_DRAWABLES, ITEM_DRAWABLES.length);



    }

    @Override
    protected void onPause() {
        super.onPause();
        arview.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        arview.onResume();
        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Drawer methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onMenuButtonClicked(View view) {
//        mainDrawerLayout.openDrawer(drawerContentsLayout);




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

        //popup view for elevation
        BalloonPopup bp = BalloonPopup.Builder(getApplicationContext(), view)
                .text("Pitch: " + pitch + " Roll: " + roll)
                .shape(BalloonPopup.BalloonShape.rounded_square)
                .timeToLive(4000)
                .positionOffset(-30,-80)
                .show();

    }

    public String parseNatCall(String JSONString) {
        String elevation="Elevation: ";

        // Parse for elevation
        try {
            JSONObject results = new JSONObject(JSONString);
            String temp = results.getString("obstruction_point");
            results = new JSONObject(temp);
            elevation += results.getString("elevation");
        } catch (JSONException e)  {
            e.printStackTrace();
        }

        // Handle error string or append dimension.
        if (elevation.equals("Elevation: ")) {
            elevation = "Obstruction view = False";
        } else {
            elevation += " meters";
        }

        return elevation;
    }

    NetworkTaskJSON.NetworkCallback obstructNetworkCallback = new NetworkTaskJSON.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            Log.d("JSON",result);
            Toast.makeText(getApplicationContext(), parseNatCall(result),Toast.LENGTH_LONG).show();
        }
    };

    public void toggleMountain(View v) {
        if(isLoggedIn) {
            tMountain = !tMountain;
        }else{
            tMountain = false;
        }

        if(tMountain){
            addMountains();
            ibMtn.setBackgroundTintMode(PorterDuff.Mode.SRC);
            ibMtn.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorAccent));
            toggleMountain.setChecked(true);
        } else{
            removeMountains();
            ibMtn.setBackgroundTintMode(null);
            toggleMountain.setChecked(false);
        }

    }

    public void toggleReservoir(View v) {
        if(isLoggedIn) {
            tReservoir = !tReservoir;
        }else{
            tReservoir = false;
        }
        if(tReservoir) {
            addReservoirs();
            ibReservoir.setBackgroundTintMode(PorterDuff.Mode.SRC);
            ibReservoir.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorAccent));
            toggleReservoir.setChecked(true);
        } else {
            removeReservoirs();
            ibReservoir.setBackgroundTintMode(null);
            toggleReservoir.setChecked(false);
        }
    }

    public void toggleWell(View v) {
        if(isLoggedIn) {
            tWell = !tWell;
        }else{
            tWell = false;
        }
        if(tWell) {
            addWells();
            ibWell.setBackgroundTintMode(PorterDuff.Mode.SRC);
            ibWell.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorAccent));
            toggleWell.setChecked(true);
        } else {
            removeWells();
            ibWell.setBackgroundTintMode(null);
            toggleWell.setChecked(false);
        }
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

    //MESHDEMO
    public void meshDemo(View view){
        GeoMath.setReference(arview.getLocation());
        Intent intent = new Intent(this, DisplayMeshActivity.class);
        startActivity(intent);
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
    NetworkTask.NetworkCallback logincallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREDENTIALS_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            String newUsername = data.getStringExtra("username");
            String newPassword = data.getStringExtra("password");
                WatertrekCredentials credentials = new WatertrekCredentials(this);
                credentials.setUsername(newUsername);
                credentials.setPassword(newPassword);
                NetworkTask.updateWatertrekCredentials(newUsername, newPassword);

                LoginService.checkLoginStatus(logincallback);
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

            for(Well well :lWellList){
                try{
                    int id = Integer.parseInt(well.getMasterSiteId());
                    wellList.add(well);
                    arview.addBillboard(id,
                                         R.drawable.well_bb_icon,
                                        "Well # "+ well.getMasterSiteId(),
                                            "(" + well.getLat() + "," + well.getLon() + ")",
                                        Float.parseFloat(well.getLat()), Float.parseFloat(well.getLon()),0
                    );
                }catch(NumberFormatException e){

                }
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
            WellActivity.launchDetailsActivity(this, well);
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
            ReservoirActivity.launchDetailsActivity(this, rL);
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
            MountainActivity.launchDetailsActivity(this, landmark);
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
        String Roll = Integer.toString((int) azimuth_angle);
        ///2
        float pitch_angle = sensorEvent.values[1] + 90;
        String Pitch = Integer.toString((int) -pitch_angle);
        float roll_angle = sensorEvent.values[2];

        // Cardinal Directions to be displayed with Roll
        int rollInt = Integer.parseInt(Roll);
        String direction = "";
        if(rollInt > 360 - (22) || rollInt < (22)){
            direction = "N";
        } else if (rollInt > 90 - (22) && rollInt < 90 + (22)) {
            direction = "E";
        } else if (rollInt > 180 - (22) && rollInt < 180 + (22)) {
            direction = "S";
        } else if (rollInt > 270 - (22) && rollInt < 270 + (22)) {
            direction = "W";
        } else if (rollInt > 45 - (23) && rollInt < 45 + (23)) {
            direction = "NE";
        } else if (rollInt > 135 - (23) && rollInt < 135 + (23)) {
            direction = "SE";
        } else if (rollInt > 225 - (23) && rollInt < 225 + (23)) {
            direction = "SW";
        } else if (rollInt > 315 - (23) && rollInt < 315 + (23)) {
            direction = "NW";
        }


        //Pitch
        ((TextView)findViewById(R.id.bearingL)).setText(Pitch);
//        Log.d("JSON" , )
        //ROll
        ((TextView)findViewById(R.id.bearingR)).setText(direction + " " + Roll);

    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    /* Arc Menu Functions */
    private void initArcMenu(final ArcMenu menu, final String[] str, int[] itemDrawables, int count) {
        for (int i = 0; i < count; i++) {
            FloatingActionButton item = getChildItem(itemDrawables[i]);
            menu.setChildSize(item.getIntrinsicHeight());

            final int position = i;

            menu.addItem(item, str[i], new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch(str[position]){
                        case "mountain":
                            toggleMountain = (Switch)findViewById(R.id.switch12);
                            toggleMountain(toggleMountain);
                            break;
                        case "reservoir":
                            toggleReservoir = (Switch)findViewById(R.id.switch12);
                            toggleReservoir(toggleReservoir);
                            break;
                        case "soil":
                            toggleSoil = (Switch)findViewById(R.id.switch12);
                            toggleSoil(toggleSoil);
                            break;
                        case "well":
                            toggleWell = (Switch)findViewById(R.id.switch9);
                            toggleWell(toggleWell);
                            break;
                        case "river":
                            toggleRiver = (Switch)findViewById(R.id.switch10);
                            toggleRiver(toggleRiver);
                            break;
                        case "eye":
                            ImageView popupThing = (ImageView)findViewById(R.id.imageView);
                            obstructionClicked(popupThing);
                            break;

                        default:
                    }
                }
            });
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(MainActivity.this, "Github",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private FloatingActionButton getChildItem(int drawable){
        FloatingActionButton item = new FloatingActionButton(this);
        item.setSize(FloatingActionButton.SIZE_MINI);
        item.setIcon(drawable);
        item.setBackgroundColor(getResources().getColor(R.color.white));
        return item;
    }






    //*********

}




