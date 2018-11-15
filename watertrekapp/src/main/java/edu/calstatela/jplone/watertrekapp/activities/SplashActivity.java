package edu.calstatela.jplone.watertrekapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.Permission;

import edu.calstatela.jplone.arframework.util.Permissions;
import edu.calstatela.jplone.watertrekapp.R;

public class SplashActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
private TextView tv;
private ImageView iv;

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();

        setContentView(R.layout.activity_splash);
        tv = (TextView) findViewById(R.id.proj_ara);
        iv = (ImageView) findViewById(R.id.ara_ico);


        boolean havePermissions = true;
        if(!Permissions.havePermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION)){
            //Permissions.requestPermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            havePermissions = false;
        }
        if(!Permissions.havePermission(this, Permissions.PERMISSION_CAMERA)){
            //Permissions.requestPermission(this, Permissions.PERMISSION_CAMERA);
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


        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        tv.startAnimation(myanim);
        iv.startAnimation(myanim);
        final Intent i = new Intent(this, MainActivity.class);
        Thread timer = new Thread(){
            public void run (){
                try {
                    sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally{
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();

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
