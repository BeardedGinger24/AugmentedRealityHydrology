package edu.calstatela.jplone.watertrekapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

import edu.calstatela.jplone.arframework.ui.SensorARView;
import edu.calstatela.jplone.arframework.util.Permissions;
import edu.calstatela.jplone.arframework.util.Vector3;
import edu.calstatela.jplone.watertrekapp.Data.DatabaseHelper;
import edu.calstatela.jplone.watertrekapp.Data.MeshData;
import edu.calstatela.jplone.watertrekapp.DataService.MeshService;
import edu.calstatela.jplone.watertrekapp.DataService.TextureService;
import edu.calstatela.jplone.watertrekapp.Helpers.OBJMaker;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.R;
import edu.calstatela.jplone.watertrekapp.WatertrekCredentials;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class SplashActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final int CREDENTIALS_ACTIVITY_REQUEST_CODE = 5;

    static String TAG = "splash";
    private TextView tv;
    private TextView loadingText;
    private ImageView iv;
    private ProgressBar pb;
    private Button startBtn;
    Context context;
    float[] currentLocation;
    SensorARView sensorARView;

    MeshService.getDEM meshAsyncTask;
    TextureService.getTexture terrainTextureAsyncTask;
    TextureService.getTexture riverTextureAsyncTask;

    private static final int REQUEST_CAMERA = 0;

    Thread t1;
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

        boolean havePermissions = true;

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
                Intent i = new Intent(context, MainActivity.class);
                String demurl = getString(R.string.demurl);
                String textureUrl = getString(R.string.textureUrl);
                String riverUrl = getString(R.string.riverTextUrl);
                String[] cred = NetworkTask.getCredentials();
                meshAsyncTask = new MeshService.getDEM();
                meshAsyncTask.execute(String.valueOf(currentLocation[0]),String.valueOf(currentLocation[1]),demurl,cred[0],cred[1]);

                terrainTextureAsyncTask = new TextureService.getTexture();
                terrainTextureAsyncTask.execute(String.valueOf(currentLocation[0]),String.valueOf(currentLocation[1]),textureUrl,cred[0],cred[1]);
                riverTextureAsyncTask = new TextureService.getTexture();
                riverTextureAsyncTask.execute(String.valueOf(currentLocation[0]),String.valueOf(currentLocation[1]),riverUrl,cred[0],cred[1]);
                try {
                    String objData = meshAsyncTask.get();
                    Bitmap bmp1 = terrainTextureAsyncTask.get();
                    Bitmap bmp2 = riverTextureAsyncTask.get();
                    OBJMaker objMaker = new OBJMaker();

                    File file1 = new File(getFilesDir(),"mesh.obj");
                    objMaker.loadData(file1,objData);

                    File file2 = new File(getFilesDir(),"texture.bmp");
                    objMaker.loadData(file2,bmp1);

                    File file3 = new File(getFilesDir(),"river.bmp");
                    objMaker.loadData(file3,bmp2);

                    bmp1.recycle();
                    bmp2.recycle();
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

    public void startClicked(View view){
        currentLocation = sensorARView.getLocation();
        startBtn.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        loadingText.setText("Loading Data ...");
        t1.start();
    }


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
                }else {

                }
                return;
            }
        }
    }
}
