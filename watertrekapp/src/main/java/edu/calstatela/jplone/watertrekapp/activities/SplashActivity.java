package edu.calstatela.jplone.watertrekapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

import edu.calstatela.jplone.arframework.ui.SensorARView;
import edu.calstatela.jplone.arframework.util.Permissions;
import edu.calstatela.jplone.arframework.util.Vector3;
import edu.calstatela.jplone.watertrekapp.Data.DatabaseHelper;
import edu.calstatela.jplone.watertrekapp.Data.MeshData;
import edu.calstatela.jplone.watertrekapp.DataService.MeshService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.R;
import edu.calstatela.jplone.watertrekapp.WatertrekCredentials;

public class SplashActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final int CREDENTIALS_ACTIVITY_REQUEST_CODE = 5;

    static String TAG = "splash";
    private TextView tv;
    private TextView loadingText;
    private ImageView iv;
    private ProgressBar pb;
    private Button startBtn;
    private Button meshTest;
    Context context;
    MeshData meshData;
    float[] currentLocation;
    SensorARView sensorARView;
    MeshService.getDEM asyncTask;

    SQLiteDatabase db;
    DatabaseHelper helper;

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_STORAGE = 2;

    Thread t1;
    Thread t2;
    static boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        context = getApplicationContext();
        sensorARView = new SensorARView(context);
        tv = findViewById(R.id.proj_ara);
        loadingText = findViewById(R.id.loadingText);
        iv = findViewById(R.id.ara_ico);
        pb = findViewById(R.id.indeterminateBar);
        startBtn = findViewById(R.id.startBtn);
        //meshTest = findViewById(R.id.meshtest);

        boolean havePermissions = true;
//        if(!Permissions.havePermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION)){
//            Permissions.requestPermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION,REQUEST_LOCATION);
//            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
//            havePermissions = false;
//        }
//        if(!Permissions.havePermission(this, Permissions.PERMISSION_CAMERA)){
//            Permissions.requestPermission(this, Permissions.PERMISSION_CAMERA,REQUEST_CAMERA);
//            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
//            havePermissions = false;
//        }
//        if(!Permissions.havePermission(this, Permissions.PERMISSION_WRITE_EXTERNAL_STORAGE)){
//            Permissions.requestPermission(this, Permissions.PERMISSION_WRITE_EXTERNAL_STORAGE,REQUEST_STORAGE);
//            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
//            havePermissions = false;
//        }

        if(!Permissions.havePermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION) &&
                !Permissions.havePermission(this, Permissions.PERMISSION_CAMERA) &&
                !Permissions.havePermission(this, Permissions.PERMISSION_WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CAMERA);

            havePermissions = false;
        }

        if(!havePermissions)
            return;

        pb.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);
        startBtn.setVisibility(View.VISIBLE);

        Log.d(TAG,"IN ONCREATE");
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorARView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorARView.onResume();
        Log.d(TAG,"IN ON RESUME");
        pb.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);
        startBtn.setVisibility(View.VISIBLE);

        WatertrekCredentials credentials = new WatertrekCredentials(this);
        String user = credentials.getUsername();
        String pass = credentials.getPassword();
        if(!isLoggedIn){
            CredentialsActivity.launch(this,user,pass,5);
        }
        t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                // start the service  to get location update

                Intent i = new Intent(context, MainActivity.class);
                String baseurl = getString(R.string.demurl);

                String[] cred = NetworkTask.getCredentials();
                Log.d(TAG,cred[0]+","+cred[1]);
                asyncTask = new MeshService.getDEM();
                asyncTask.execute(String.valueOf(currentLocation[0]),String.valueOf(currentLocation[1]),String.valueOf(currentLocation[2]),baseurl,cred[0],cred[1]);
                try {
                    meshData = asyncTask.get();
                    meshData.setFilenameTerrain("terrain");
                    meshData.setFilenameTerrainVecs("meshvecs");
                    meshData.setDir(context.getFilesDir()+"");

                    helper = new DatabaseHelper(context);
                    db = helper.getWritableDatabase();
                    helper.addMeshData(db,meshData);


                    //Write meshdata to file
                    genVerts(meshData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                //start the main activity
                startActivity(i);
            }
        });
        t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(context, MeshTest.class);
                String baseurl = getString(R.string.demurl);

                String[] cred = NetworkTask.getCredentials();
                Log.d(TAG,cred[0]+","+cred[1]);
                asyncTask= new MeshService.getDEM();
                asyncTask.execute(String.valueOf(currentLocation[0]),String.valueOf(currentLocation[1]),String.valueOf(currentLocation[2]),baseurl,cred[0],cred[1]);
                try {
                    meshData = asyncTask.get();
                    meshData.setFilenameTerrain("terrain");
                    meshData.setFilenameTerrainVecs("meshvecs");
                    meshData.setDir(context.getFilesDir()+"");

                    helper = new DatabaseHelper(context);
                    db = helper.getWritableDatabase();
                    helper.addMeshData(db,meshData);


                    //Write meshdata to file
                    genVerts(meshData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                //start the main activity
                startActivity(i);
            }
        });
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
            isLoggedIn = true;
        }
    }

    public static void toggleLogin(boolean b){
        isLoggedIn = b;
    }
    public void genVerts(MeshData meshData){
        float[] verts = new float[meshData.Triangles.length*3];
        int index = 0;
        for(int i = 0; i<meshData.Triangles.length-1; i++){
            verts[index] = (float) meshData.Vectors[meshData.Triangles[i]].getX();
            verts[index+1] = (float) meshData.Vectors[meshData.Triangles[i]].getY();
            verts[index+2] = (float) meshData.Vectors[meshData.Triangles[i]].getZ();
            index += 3;
        }

        writeToFile(meshData.Vectors,meshData.getFilenameTerrainVecs());
        writeToFile(verts,meshData.getFilenameTerrain());
    }
    public void writeToFile(float[] input,String filename){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i<input.length; i++){
            sb.append(input[i]);
            if(i<input.length-1){
                sb.append(",");
            }
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(sb.toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public void writeToFile(Vector3[] input, String filename){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i<input.length; i++){
            sb.append(input[i].getVals());
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(sb.toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public void startClicked(View view){
        currentLocation = sensorARView.getLocation();
        startBtn.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        loadingText.setText("Loading Data ...");
        t1.start();
    }
    NetworkTask.NetworkCallback demCallback = new NetworkTask.NetworkCallback() {
        @Override
        public void onResult(int type, String result) {

        }
    };

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        if(requestCode == REQUEST_LOCATION){
//            if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                finish();
//                startActivity(getIntent());
//            }
//        } else if(requestCode == REQUEST_CAMERA){
//            if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                finish();
//                startActivity(getIntent());
//            }
//        } else if(requestCode == REQUEST_STORAGE){
//            if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                finish();
//                startActivity(getIntent());
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case REQUEST_CAMERA:{
                // When request is cancelled, the results array are empty
                if(
                        (grantResults.length >0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        + grantResults[2]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                        ){
                    // Permissions are granted
//                    Toast.makeText(mContext,"Permissions granted.",Toast.LENGTH_SHORT).show();
                }else {
                    // Permissions are denied
//                    Toast.makeText(mContext,"Permissions denied.",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void meshTest(View v){
        currentLocation = sensorARView.getLocation();
        t2.start();
    }
}
