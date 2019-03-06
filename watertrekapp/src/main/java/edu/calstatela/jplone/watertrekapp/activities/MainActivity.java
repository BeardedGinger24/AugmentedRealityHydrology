package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.bvapp.arcmenulibrary.ArcMenu;
import com.bvapp.arcmenulibrary.widget.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.arframework.util.Permissions;
import edu.calstatela.jplone.watertrekapp.Data.DatabaseHelper;
import edu.calstatela.jplone.watertrekapp.Data.MeshInfo;
import edu.calstatela.jplone.watertrekapp.Data.Reservoir;
import edu.calstatela.jplone.watertrekapp.Data.River;
import edu.calstatela.jplone.watertrekapp.Data.Snotel;
import edu.calstatela.jplone.watertrekapp.Data.SoilMoisture;
import edu.calstatela.jplone.watertrekapp.Data.Vector3;
import edu.calstatela.jplone.watertrekapp.Data.Well;
import edu.calstatela.jplone.watertrekapp.DataService.ElevationObstructionService;
import edu.calstatela.jplone.watertrekapp.DataService.ReservoirService;
import edu.calstatela.jplone.watertrekapp.DataService.RiverService;
import edu.calstatela.jplone.watertrekapp.DataService.SnotelService;
import edu.calstatela.jplone.watertrekapp.DataService.SoilMoistureService;
import edu.calstatela.jplone.watertrekapp.DataService.WellService;
import edu.calstatela.jplone.watertrekapp.Helpers.CSVReader;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.LoginService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTaskAuth;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTaskJSON;
import edu.calstatela.jplone.watertrekapp.R;
import edu.calstatela.jplone.watertrekapp.WatertrekCredentials;
import edu.calstatela.jplone.watertrekapp.adapters.Azimuth_RecyclerViewAdapter;
import edu.calstatela.jplone.watertrekapp.adapters.Pitch_RecyclerViewAdapter;
import edu.calstatela.jplone.watertrekapp.billboardview.BillboardView_sorting;

public class MainActivity extends AppCompatActivity implements BillboardView_sorting.TouchCallback, SensorEventListener{
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    private static final int SENSOR_DELAY = 500 * 1000; // 500ms

    private static final String TAG = "waka-MainActivity";
    private static final int CREDENTIALS_ACTIVITY_REQUEST_CODE = 5;


    private DatabaseHelper helper;
    private SQLiteDatabase db;

    private RelativeLayout drawerContentsLayout;
    private DrawerLayout mainDrawerLayout;
    FrameLayout mainLayout;

    private BillboardView_sorting arview;
    private SeekBar radiusSeekBar;

    private boolean tMountain = false;
    private boolean tReservoir = false;
    private boolean tWell = false;
    private boolean tRiver = false;
    private boolean tSoil = false;
    private boolean tSnotel = false;

    private ImageButton ibWell, ibRiver, ibReservoir, ibSoilMoisture, ibMtn, ibSnotel;
    MeshInfo meshInfo;
    private int radius = 20;

    Button login_button;
    Button logout_button;
    Switch cameraToggle;

    private ArrayList<Well> wellList = new ArrayList<>();
    private ArrayList<Reservoir> reservoirList = new ArrayList<>();
    private ArrayList<SoilMoisture> soilList = new ArrayList<>();
    private ArrayList<River> riverList = new ArrayList<>();
    private ArrayList<Snotel> snotelList = new ArrayList<>();

    private boolean isLoggedIn = true;
    private boolean paused = false;

    private ArrayList<String> verticalTicks = new ArrayList<>();
    private ArrayList<String> horizontalTicks = new ArrayList<>();

    //Arc menu items
    ArcMenu arcMenu;
    private static int[] ITEM_DRAWABLES = { R.drawable.mtn_res_ico_clr, R.drawable.reservoir_bb_icon, R.drawable.soil_bb_icon,
            R.drawable.well_bb_icon, R.drawable.river_res_ico_clr, R.drawable.snotel_res_ico, R.drawable.eye24 };
    private String[] str = {"mountain","reservoir","soil","well", "river", "snotel", "eye"};
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

        drawerContentsLayout = (RelativeLayout) findViewById(R.id.whatYouWantInLeftDrawer);
        mainDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        radiusSeekBar = findViewById(R.id.seekBar);
        radiusSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        login_button = (Button)findViewById(R.id.login_button);
        logout_button = (Button)findViewById(R.id.logout_button);

        paused = false;
        if(SplashActivity.isLoggedIn){
            login_button.setVisibility(View.GONE);
            logout_button.setVisibility(View.VISIBLE);
        }else{
            login_button.setVisibility(View.VISIBLE);
            logout_button.setVisibility(View.GONE);
        }

        arview = new BillboardView_sorting(this);
        arview.setTouchCallback(this);
        arview.setDeviceOrientation(Orientation.getOrientationAngle(this));

        cameraToggle = (Switch)findViewById(R.id.cameraToggle);
        if(Permissions.havePermission(this, Permissions.PERMISSION_CAMERA)){
            cameraToggle.setChecked(true);
        }else{
            cameraToggle.setChecked(true);
        }
        arview.changeBGC(cameraToggle.isChecked());

        mainLayout = (FrameLayout)findViewById(R.id.ar_view_container);
        mainLayout.addView(arview);

        arview.setMeshStatus(false);
        meshInfo = getMeshInfo("terrain");

        initSensorRecyclerViews();


        //Floating arc menu
        arcMenu = (ArcMenu) findViewById(R.id.arcMenuX);
        arcMenu.setToolTipTextSize(14);

        arcMenu.setToolTipSide(ArcMenu.TOOLTIP_LEFT);
        arcMenu.setToolTipCorner(2);
        arcMenu.setToolTipPadding(8);
        arcMenu.showTooltip(false);
        arcMenu.setDuration(ArcMenu.ArcMenuDuration.LENGTH_LONG);
        arcMenu.setAnim(500,500, ArcMenu.ANIM_MIDDLE_TO_DOWN, ArcMenu.ANIM_MIDDLE_TO_RIGHT,
                ArcMenu.ANIM_INTERPOLATOR_ANTICIPATE, ArcMenu.ANIM_INTERPOLATOR_ANTICIPATE);
        initArcMenu(arcMenu, str, ITEM_DRAWABLES, ITEM_DRAWABLES.length);
        Log.d(TAG,"IN ON CREATE");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"IN ON PAUSE");
        //arview.onPause();
        mSensorManager.unregisterListener(this);
        paused = true;
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
        mainDrawerLayout.openDrawer(drawerContentsLayout);
    }
    public void toggleCamera(View view){
        if(Permissions.havePermission(this, Permissions.PERMISSION_CAMERA)) {
            arview.changeBGC(cameraToggle.isChecked());
        }
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



    public void toggleReservoir(View v) {
        int i = 1;
        if(isLoggedIn) {
            tReservoir = !tReservoir;
        }else{
            tReservoir = false;
        }
        if(tReservoir) {
            updateArcMenuDrawables(i,tReservoir);
            addReservoirs();
        } else {
            updateArcMenuDrawables(i,tReservoir);
            removeReservoirs();
        }
    }

    public void toggleWell(View v) {
        int i = 3;
        if(isLoggedIn) {
            tWell = !tWell;
        }else{
            tWell = false;
        }
        if(tWell) {
            updateArcMenuDrawables(i,tWell);
            addWells();
        } else {
            updateArcMenuDrawables(i,tWell);
            removeWells();
        }
    }

    public void toggleRiver(View v) {
        int i = 4;
        if(isLoggedIn) {
            tRiver = !tRiver;
        }else{
            tRiver = false;
        }
        if(tRiver) {
            updateArcMenuDrawables(i,tRiver);
            addRiverz();
        } else {
            updateArcMenuDrawables(i,tRiver);
            removeRiverz();
        }
    }

    public void toggleSoil(View v) {
        int i = 2;
        if(isLoggedIn) {
            tSoil = !tSoil;
        }else{
            tSoil = false;
        }
        if(tSoil) {
            updateArcMenuDrawables(i,tSoil);
            addSoilPatches();
        } else {
            updateArcMenuDrawables(i,tSoil);
            removeSoilPatches();
        }
    }


    public void toggleSnotel(View v) {
        int i = 5;
        if(isLoggedIn) {
            tSnotel = !tSnotel;
        }else{
            tSnotel = false;
        }
        if(tSnotel) {
            updateArcMenuDrawables(i,tSnotel);
            addSnotelPillows();

        } else {
            updateArcMenuDrawables(i,tSnotel);
            removeSnotelPillows();

        }

    }


    //MESHDEMO
    public void meshDemo(View view){
        int i = 0;
        if(isLoggedIn){
            tMountain = !tMountain;
        }else{
            tMountain = false;
        }

        if(tMountain) {
            updateArcMenuDrawables(i,tMountain);
            if (arview.meshNull()) {
                arview.addMesh(getMeshInfo("terrain"));
            }
            arview.setMeshStatus(true);
        }else{
            updateArcMenuDrawables(i,tMountain);
            arview.setMeshStatus(false);
        }
    }
    public MeshInfo getMeshInfo(String type){
        File file1 = new File(getFilesDir(),"meshvecs");
        Vector3[] vecs= CSVReader.readVecFile(file1);

        File file2 = new File(getFilesDir(),"terrain");
        float[] verts = CSVReader.readCSV(file2);

        float[] loc = meshdataLoc(file2.getName());

        return new MeshInfo(vecs,type,loc,verts);
    }
    public float[] meshdataLoc(String filename){
        helper = new DatabaseHelper(this);
        db=helper.getReadableDatabase();
        return helper.getMeshData(db,filename);
    }

    public void logout(View v){
        SplashActivity.toggleLogin(false);
        NetworkTask.updateWatertrekCredentials(null, null);
        isLoggedIn = false;
        login_button.setVisibility(v.VISIBLE);
        logout_button.setVisibility(v.GONE);

        toggleReservoir(v);
        meshDemo(v);
        toggleWell(v);
        toggleSoil(v);
        toggleRiver(v);
        toggleSnotel(v);
    }
    public void login (View v){
        SplashActivity.toggleLogin(true);
        isLoggedIn = true;
        WatertrekCredentials credentials = new WatertrekCredentials(this);
        CredentialsActivity.launch(this, credentials.getUsername(), credentials.getPassword(), CREDENTIALS_ACTIVITY_REQUEST_CODE);
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
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Well Data Methods
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void addWells(){
        if(arview.meshNull()){
            arview.addMesh(getMeshInfo("terrain"));
        }
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
            Log.d(TAG,result);
            // Check to see if GET call is empty if so display message to user else continue
            if (lWellList.size() >=1){
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
                        Log.d(TAG,e.toString());
                    }
                }
            }
            else
            {
                Log.d("Well","There are currently no wells within range ");
                Toast.makeText(getApplicationContext(), "There are currently no Wells within range",
                        Toast.LENGTH_LONG).show();
            }

        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //     Reservoir  Data Methods added by Leo *********************
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void addReservoirs(){
        if(arview.meshNull()){
            arview.addMesh(getMeshInfo("terrain"));
        }
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
            List<Reservoir> rreservoirList = ReservoirService.parseAllReservoirs(result , loc[0] , loc[1],radius); // change method
            // for every reservoir obj  that is near me add it to reservoirList and add to Billboard in order to display it
            // Check to see if GET call is empty if so display message to user else continue
            if (rreservoirList.size() >=1){
                for(Reservoir reservoirr : rreservoirList){
                    try {
                        reservoirList.add(reservoirr);
                        arview.addBillboard(
                                Integer.parseInt(reservoirr.getSiteNo()),
                                R.drawable.reservoir_bb_icon,
                                "Reservoir #" + reservoirr.getSiteNo(),
                                "(" + reservoirr.getLat() + ", " + reservoirr.getLon() + ")",
                                Float.parseFloat(reservoirr.getLat()), Float.parseFloat(reservoirr.getLon()), 0
                        );
                    }catch(NumberFormatException e) {

                    }


                }
            }

            else
            {
                Log.d("Reser","There are currently no reservoir  within range ");
                Toast.makeText(getApplicationContext(), "There are currently no Reservoirs within range",
                        Toast.LENGTH_LONG).show();
            }

        }
    };
    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //     SoilMoisture  Data Methods added by Leo *********************
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void addSoilPatches(){
        if(arview.meshNull()){
            arview.addMesh(getMeshInfo("terrain"));
        }
        //retrieves all soil patches
        SoilMoistureService.getSoilMoistures(soilmoistureNetworkCallback);
    }
    private void removeSoilPatches(){
        for(SoilMoisture sm : soilList){
            //Removes Soil Moisture  based on there unique ID or Wbanno
            int id = Integer.parseInt(sm.getWbanno());
            arview.removeBillboard(id);
        }
        soilList.clear();
    }

    NetworkTask.NetworkCallback soilmoistureNetworkCallback = new NetworkTask.NetworkCallback() {

        @Override
        public void onResult(int type, String result) {
            // variable loc gets current location based on gps longitude and lattitude
            float[] loc = arview.getLocation();     // added by  leo
            // Return soil Moisture  nearest to range once passed in currently hard coded
            List<SoilMoisture> soilMoistList = SoilMoistureService.parseAllSoilMoist(result , loc[0] , loc[1],radius); // change method
            // for every soil obj  that is near me add it to soillist  and add to Billboard in order to display it
            // Check to see if GET call is empty if so display message to user else continue
            if (soilMoistList.size() >=1){
                for(SoilMoisture moistySoil : soilMoistList){
                    try {
                        Log.d("soily", moistySoil.getWbanno());

                        soilList.add(moistySoil);
                        arview.addBillboard(
                                Integer.parseInt(moistySoil.getWbanno()),

                                R.drawable.soil_bb_icon,
                                "Soil #" + moistySoil.getWbanno(),
                                "(" + moistySoil.getLat() + ", " + moistySoil.getLon() + ")",
                                Float.parseFloat(moistySoil.getLat()), Float.parseFloat(moistySoil.getLon()), 0
                        );
                    }catch(NumberFormatException e){

                    }

                }
            }
            else
            {
                Log.d("soiledDistance", " There are currently no Soil Moistures within range ");
                Toast.makeText(getApplicationContext(), "There are currently no Soil Moistures within range",
                        Toast.LENGTH_LONG).show();
            }

        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //     Rivers/Rapids  Data Methods
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void addRiverz(){
        if(arview.meshNull()){
            arview.addMesh(getMeshInfo("terrain"));
        }
        // Retrieves curr location
        float[] loc = arview.getLocation();
        //Longitude
        String longy = Float.toString(loc[1]);
        //Lattitude
        String laty = Float.toString(loc[0]);

        Double currlat =  Double.parseDouble(laty);
        Double currlong =  Double.parseDouble(longy);
        //retrieves all Rivers/ Stream Gauges

//        RiverService.getRivers(currlat,currlong,radius);
        RiverService.getRivers(RiverNetworkCallback,currlat,currlong,radius);
    }
    private void removeRiverz(){
        for(River rv : riverList){
            //Removes Soil Moisture  based on there unique ID or Wbanno
            int id = Integer.parseInt(rv.getSiteNo());
            arview.removeBillboard(id);
        }
        riverList.clear();
    }


    NetworkTask.NetworkCallback RiverNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            List<River> lRiverList = RiverService.parseRivers(result);
            // Check to see if GET call is empty if so display message to user else continue
            if (lRiverList.size() >=1){
                for(River riv :lRiverList){
                    try{
                        int id = Integer.parseInt(riv.getSiteNo());
                        riverList.add(riv);
                        arview.addBillboard(id,
                                R.drawable.river_bb_icon,
                                "River # "+ riv.getSiteNo(),
                                "(" + riv.getLat() + "," + riv.getLon() + ")",
                                Float.parseFloat(riv.getLat()), Float.parseFloat(riv.getLon()),0
                        );
                    }catch(NumberFormatException e){

                    }
                }
            }
            else
            {
                Log.d("River","There are currently no Streams/Rivers within range ");
                Toast.makeText(getApplicationContext(), "There are currently no Streams/Rivers within range",
                        Toast.LENGTH_LONG).show();
            }

        }
    };
    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //     Snotel Data Methods
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    public void addSnotelPillows(){
        if(arview.meshNull()){
            arview.addMesh(getMeshInfo("terrain"));
        }
        // Retrieves curr location
        float[] loc = arview.getLocation();
        //Longitude
        String longy = Float.toString(loc[1]);
        //Lattitude
        String laty = Float.toString(loc[0]);

        Double currlat =  Double.parseDouble(laty);
        Double currlong =  Double.parseDouble(longy);
        //retrieves all Rivers/ Stream Gauges

//        RiverService.getRivers(currlat,currlong,radius);
//        pb.setVisibility(View.VISIBLE);
        SnotelService.getAllSnotel(SnotelNetworkCallback);
    }


    public void removeSnotelPillows(){
        for(Snotel snowyy : snotelList){
            //Removes Soil Moisture  based on there unique ID or Wbanno
            int id = Integer.parseInt(snowyy.getStationId());
            arview.removeBillboard(id);
        }
        snotelList.clear();

    }

    NetworkTask.NetworkCallback SnotelNetworkCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {
            float[] loc = arview.getLocation();     // added by  leo
            List<Snotel> lSnotelList = SnotelService.parseAllSnowtels(result, loc[0] , loc[1],radius);
            // Check to see if GET call is empty if so display message to user else continue
            if (lSnotelList.size() >=1){
                for(Snotel snt :lSnotelList){
                    try{
                        int id = Integer.parseInt(snt.getStationId());
                        snotelList.add(snt);
                        arview.addBillboard(id,
                                R.drawable.snotel_res_ico,
                                "Snotel # "+ snt.getStationId(),
                                "(" + snt.getLat() + "," + snt.getLon() + ")",
                                Float.parseFloat(snt.getLat()), Float.parseFloat(snt.getLon()),0
                        );
                    }catch(NumberFormatException e){

                    }
                }
//                pb.setVisibility(View.INVISIBLE);
            }
            else
            {
//                pb.setVisibility(View.INVISIBLE);
                Log.d("snow","There are currently no Snotel Pillows within your  range ");
                Toast.makeText(getApplicationContext(), "There are currently no Snotel Pillows within range",
                        Toast.LENGTH_LONG).show();
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
            Log.d("LaunchWellDetails",w.getMasterSiteId());
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
            Log.d("LaunchResDetails",r.getSiteNo());
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
        //Soil Moisture
        SoilMoisture  sL = null;
        for(SoilMoisture s : soilList){
            Log.d("LaunchSoildetails",s.getWbanno());
            int sId = Integer.parseInt(s.getWbanno());
            if(sId == id) {
                sL = s;
                break;
            }
        }
        if(sL != null) {
            SoilMoistureActivity.launchDetailsActivity(this, sL);
//            Log.d("LaunchSoildetails","going now...");
            return;
        }
        //Snotel
        Snotel  sntel = null;
        for(Snotel s : snotelList){
//            Log.d("LaunchSnoteldetails OT",s.getStationId());
//            Log.d("NOOOOOOOO", "whyyyyyy");
            int snId = Integer.parseInt(s.getStationId());
            if(snId == id) {
                sntel = s;
                break;
            }
        }
        if(sntel != null) {
            Log.d("snow" , sntel.getStationId());
            SnotelActivity.launchDetailsActivity(this, sntel);
//            Log.d("LaunchSoildetails","going now...");
            return;
        }

        //Rapids/ Rivers
        River  rivL = null;
        for(River r : riverList){
            Log.d("LaunchRiverdetails-MAIN",r.getSiteNo());
            int rivId = Integer.parseInt(r.getSiteNo());
            if(rivId == id) {
                rivL = r;
                break;
            }
        }
        if(rivL != null) {
            RiverActivity.launchDetailsActivity(this, rivL);
//            Log.d("LaunchSoildetails","going now...");
            return;
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
        // FIX LATER EFFECTS NATALIES CALL
//        ((TextView)findViewById(R.id.bearingR)).setText(direction + " " + Roll);
        ((TextView)findViewById(R.id.bearingR)).setText(Roll);

    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private void initSensorRecyclerViews(){
        RecyclerView AzimuthView = findViewById(R.id.Azimuth_RecyclerView);
        RecyclerView PitchView = findViewById(R.id.Pitch_RecyclerView);




        Azimuth_RecyclerViewAdapter AzimuthSensorAdapter = new Azimuth_RecyclerViewAdapter(verticalTicks, this);

        //Initialize for Azimuth
        AzimuthView.setAdapter(AzimuthSensorAdapter);
        AzimuthView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));



        Pitch_RecyclerViewAdapter pitchSensorAdapter = new Pitch_RecyclerViewAdapter(horizontalTicks, this);

        //Initialize for Pitch
        PitchView.setAdapter(pitchSensorAdapter);
        PitchView.setLayoutManager(new LinearLayoutManager(this));

    }


    /* Arc Menu Functions */
    private void initArcMenu(final ArcMenu menu, final String[] str, final int[] itemDrawables, int count) {
        for (int i = 0; i < count; i++) {
            FloatingActionButton item = getChildItem(itemDrawables[i]);
            menu.setChildSize(item.getIntrinsicHeight());

            final int position = i;

            menu.addItem(item, str[i], new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch(str[position]){
                        case "mountain":
                            meshDemo(ibMtn);
                            break;
                        case "reservoir":
                            toggleReservoir(ibReservoir);
                            break;
                        case "soil":
                            toggleSoil(ibSoilMoisture);
                            break;
                        case "well":
                            toggleWell(ibWell);
                            break;
                        case "river":
                            toggleRiver(ibRiver);
                            break;
                        case "snotel":
                            toggleSnotel(ibSnotel);
                            break;
                        case "eye":
                            ImageView obstruction = (ImageView)findViewById(R.id.imageView);
                            obstructionClicked(obstruction);
                            break;

                        default:
                    }
                }
            });
        }
    }

    private FloatingActionButton getChildItem(int drawable){
        FloatingActionButton item = new FloatingActionButton(this);
        item.setSize(FloatingActionButton.SIZE_MINI);
        item.setIcon(drawable);
        item.setBackgroundColor(getResources().getColor(R.color.white));
        return item;
    }
    public void updateArcMenuDrawables(final int index, boolean b){
        FloatingActionButton item = getChildItem(ITEM_DRAWABLES[index]);
        if(b){
            item.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }else{
            item.setBackgroundColor(getResources().getColor(R.color.white));
        }
        arcMenu.replaceChildAt(item,str[index],index,new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch(str[index]){
                    case "mountain":
                        meshDemo(ibMtn);
                        break;
                    case "reservoir":
                        toggleReservoir(ibReservoir);
                        break;
                    case "soil":
                        toggleSoil(ibSoilMoisture);
                        break;
                    case "well":
                        toggleWell(ibWell);
                        break;
                    case "river":
                        toggleRiver(ibRiver);
                        break;
                    case "snotel":
                        toggleSnotel(ibSnotel);
                        break;
                    default:
                }
            }
        });
    }
}




