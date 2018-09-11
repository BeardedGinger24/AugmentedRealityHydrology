package edu.calstatela.jplone.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.arframework.util.Permissions;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "waka-MenuActivity";

    LinearLayout layout;

    private void addActivityButton(String label, final Class<?> theClass){
        Button button = new Button(this);
        layout.addView(button);
        button.setText(label);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, theClass);
                startActivity(intent);
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        addActivityButton("Compass", BillboardCompassActivity.class);
        addActivityButton("Landmarks", BillboardLandmarksActivity.class);
        addActivityButton("ShapeDraw", ShapeDrawActivity.class);
        addActivityButton("CircleScene", CircleSceneActivity.class);
        addActivityButton("Mountains", MountainDrawActivity.class);


        if(!Permissions.havePermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION)){
            Permissions.requestPermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION);
        }
        if(!Permissions.havePermission(this, Permissions.PERMISSION_CAMERA)){
            Permissions.requestPermission(this, Permissions.PERMISSION_CAMERA);
        }
    }

}
