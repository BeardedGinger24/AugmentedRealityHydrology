package edu.calstatela.jplone.watertrekapp.activities;

import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import java.io.File;

import edu.calstatela.jplone.arframework.ui.ARView;
import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.watertrekapp.Data.DatabaseHelper;
import edu.calstatela.jplone.watertrekapp.Data.MeshInfo;
import edu.calstatela.jplone.watertrekapp.Data.Vector3;
import edu.calstatela.jplone.watertrekapp.Helpers.CSVReader;
import edu.calstatela.jplone.watertrekapp.R;
import edu.calstatela.jplone.watertrekapp.billboardview.ARViewTest;
import edu.calstatela.jplone.watertrekapp.billboardview.BillboardView_sorting;

public class MeshTest extends AppCompatActivity implements SensorEventListener{
    String TAG = "mesh-test";
    ARViewTest arview;
    MeshInfo meshInfo;

    ImageButton mtnBtn;
    NumberPicker nx;
    NumberPicker nz;
    NumberPicker ny;

    FrameLayout fl;

    private DatabaseHelper helper;
    private SQLiteDatabase db;

    boolean tMountain;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meshtest);

        mtnBtn = findViewById(R.id.mtnBtn);
//        nx = findViewById(R.id.numX);
//        nz = findViewById(R.id.numZ);
//        ny = findViewById(R.id.numY);

        tMountain = false;

        arview = new ARViewTest(this);
        arview.setDeviceOrientation(Orientation.getOrientationAngle(this));
        arview.setMeshStatus(false);

        fl = findViewById(R.id.ar_view_container);
        fl.addView(arview);

        meshInfo = getMeshInfo("terrain");
    }

    @Override
    protected void onResume() {
        super.onResume();
        arview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        arview.onPause();
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

    public void showMesh(View v){
        tMountain = !tMountain;
        Log.d(TAG,"Mesh toggled");
        if(tMountain) {
            if (arview.meshNull()) {
                arview.addMesh(getMeshInfo("terrain"));
                Log.d(TAG,"Mesh Added");
            }
            arview.setMeshStatus(true);
        }else{
            arview.setMeshStatus(false);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
