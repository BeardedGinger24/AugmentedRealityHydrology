package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Permission;
import java.util.concurrent.ExecutionException;

import edu.calstatela.jplone.arframework.sensor.ARGps;
import edu.calstatela.jplone.arframework.ui.SensorARView;
import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.arframework.util.Permissions;
import edu.calstatela.jplone.watertrekapp.Data.DatabaseHelper;
import edu.calstatela.jplone.watertrekapp.Data.MeshData;
import edu.calstatela.jplone.watertrekapp.DataService.MeshService;
import edu.calstatela.jplone.watertrekapp.R;
import edu.calstatela.jplone.watertrekapp.billboardview.BillboardView_sorting;

public class SplashActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    String TAG = "splash";
    private TextView tv;
    private TextView loadingText;
    private ImageView iv;
    private ProgressBar pb;

    float[] currentLocation = {34.0f,-117.93f,69.54f};
    MeshService.getDEM asyncTask;

    SQLiteDatabase db;
    DatabaseHelper helper;

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        tv = findViewById(R.id.proj_ara);
        loadingText = findViewById(R.id.loadingText);
        iv = findViewById(R.id.ara_ico);
        pb = findViewById(R.id.indeterminateBar);

        boolean havePermissions = true;
        if(!Permissions.havePermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION)){
            //Permissions.requestPermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            havePermissions = false;
        }
        if(!Permissions.havePermission(this, Permissions.PERMISSION_CAMERA)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            havePermissions = false;
        }
        if(!Permissions.havePermission(this, Permissions.PERMISSION_WRITE_EXTERNAL_STORAGE)){
            //Permissions.requestPermission(this, Permissions.PERMISSION_CAMERA);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            havePermissions = false;
        }
        if(!havePermissions)
            return;


//        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.mytransition);
//        tv.startAnimation(myanim);
//        iv.startAnimation(myanim);
//        pb.startAnimation(myanim);
//        loadingText.setAnimation(myanim);
        final Intent i = new Intent(this, MainActivity.class);
        
        //Grab DEM
        loadingText.setText("Loading DEM ...");
        String baseurl = getString(R.string.demurl);

        asyncTask= new MeshService.getDEM();
        asyncTask.execute(String.valueOf(currentLocation[0]),String.valueOf(currentLocation[1]),String.valueOf(currentLocation[2]),baseurl);
        MeshData meshData = null;
        try {
            meshData = asyncTask.get();
            meshData.setFilename("terrain");
            meshData.setDir(this.getFilesDir()+"");

            loadingText.setText("Updating DB ...");
            helper = new DatabaseHelper(this);
            db = helper.getWritableDatabase();
            helper.addMeshData(db,meshData);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Write meshdata to file
        loadingText.setText("Writing data to file ...");
        genVerts(meshData);

        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void genVerts(MeshData meshData){
        float[] verts = new float[meshData.Triangles.length*3];
        int index = 0;
        for(int i = 0; i<meshData.Triangles.length-1; i++){
            verts[index] = (float) meshData.Vertices[meshData.Triangles[i]].getX();
            verts[index+1] = (float) meshData.Vertices[meshData.Triangles[i]].getY();
            verts[index+2] = (float) meshData.Vertices[meshData.Triangles[i]].getZ();
            index += 3;
        }

        writeToFile(verts,"terrain");
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_LOCATION){
            if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                finish();
                startActivity(getIntent());
            }
        } else if(requestCode == REQUEST_CAMERA){
            if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                finish();
                startActivity(getIntent());
            }
        } else if(requestCode == REQUEST_STORAGE){
            if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                finish();
                startActivity(getIntent());
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
