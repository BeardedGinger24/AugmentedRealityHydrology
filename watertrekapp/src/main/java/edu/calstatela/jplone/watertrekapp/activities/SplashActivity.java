package edu.calstatela.jplone.watertrekapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import edu.calstatela.jplone.arframework.util.Permissions;
import edu.calstatela.jplone.watertrekapp.R;

public class SplashActivity extends AppCompatActivity {
private TextView tv;
private ImageView iv;

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
            Permissions.requestPermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION);
            havePermissions = false;
        }
        if(!Permissions.havePermission(this, Permissions.PERMISSION_CAMERA)){
            Permissions.requestPermission(this, Permissions.PERMISSION_CAMERA);
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
                    sleep(5000);
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
}
