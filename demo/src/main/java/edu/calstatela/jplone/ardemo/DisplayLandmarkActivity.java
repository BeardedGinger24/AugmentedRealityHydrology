package edu.calstatela.jplone.ardemo;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.calstatela.jplone.arframework.ARData.ARLandmark;
import edu.calstatela.jplone.arframework.ARData.ARLandmarkTable;
import edu.calstatela.jplone.arframework.ARFragment;
import edu.calstatela.jplone.arframework.ARGL.Billboard.ARGLSizedBillboard;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLRenderJob;

public class DisplayLandmarkActivity extends AppCompatActivity {
    ARFragment arFragment;
    ARLandmarkTable arLandmarkTable;
    String type = "csula";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ar);

        type = getIntent().getStringExtra("type");

        // hide the action bar (gets fullscreen)
        getSupportActionBar().hide();

        // setting up fragments
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        arFragment = ARFragment.newGPSInstance();
        ft.add(R.id.ar_view_container, arFragment);

        ft.commit();

        // setting up data table
        arLandmarkTable = new ARLandmarkTable();
        if(type.equals("cities"))
            arLandmarkTable.loadCities();
        else
            arLandmarkTable.loadCalstateLA();

        // build the compass and add them to AR fragment to be displayed
        buildLandmarks();
    }

    void buildLandmarks() {
        int ara_icon = edu.calstatela.jplone.arframework.R.drawable.ara_icon;

        for(int i=0; i<arLandmarkTable.size(); i++) {
            ARLandmark current = arLandmarkTable.get(i);
            final int index = i;

            arFragment.addJob(ARGLRenderJob.makeBillboard(5, ara_icon, current, new ARGLSizedBillboard.Listener() {
                @Override
                public void interact(ARGLSizedBillboard billboard) {
                    Intent intent = new Intent(DisplayLandmarkActivity.this, DisplayDataActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("billboard_id", index);
                    startActivity(intent);
                }
            }));
        }
    }
}
