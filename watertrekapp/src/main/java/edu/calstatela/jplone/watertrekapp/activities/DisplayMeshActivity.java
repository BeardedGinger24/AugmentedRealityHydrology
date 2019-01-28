package edu.calstatela.jplone.watertrekapp.activities;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.concurrent.ExecutionException;
import edu.calstatela.jplone.arframework.graphics3d.camera.Camera3D;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.drawable.ColorHolder;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Model;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.entity.ScaleObject;
import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
import edu.calstatela.jplone.arframework.ui.SensorARActivity;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.watertrekapp.Data.DatabaseHelper;
import edu.calstatela.jplone.watertrekapp.Data.MeshData;
import edu.calstatela.jplone.watertrekapp.Data.Vector3;
import edu.calstatela.jplone.watertrekapp.Helpers.CSVReader;
import edu.calstatela.jplone.watertrekapp.R;
import edu.calstatela.jplone.watertrekapp.billboardview.BillboardView_sorting;

import static android.icu.lang.UProperty.MATH;

public class DisplayMeshActivity extends SensorARActivity{
    String TAG = "waka-Mesh";
    private Camera3D camera;
    private Entity entity1,entity2,e;
    private Scene scene;
    Vector3[] vecs;
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    public void GLInit() {
        super.GLInit();
        Billboard.init();
        scene = new Scene();
        entity1 = null;
        entity2 = null;
        camera = new Camera3D();

        setupScene();
    }
    private void setupScene(){
        File file1 = new File(getFilesDir(),"meshvecs");
        vecs= CSVReader.readVecFile(file1);

        File file2 = new File(getFilesDir(),"terrain");
        float[] verts = CSVReader.readCSV(file2);
        float[] loc = meshdataLoc(file2.getName());

        Log.d(TAG,"meshdata loc: "+loc[0]+","+loc[1]+","+loc[2]);

        Model mesh = new Model();
        mesh.loadVertices(verts);
        mesh.setDrawingModeTriangles();
        ColorHolder purple = new ColorHolder(mesh, new float[]{1, 0, 1, 0.01f});
        entity1 = scene.addDrawable(purple);
        entity1.setLatLonAlt(loc);
        float[] temp = entity1.getPosition();
        Log.d(TAG,"XYZ :"+temp[0]+","+temp[1]+","+temp[2]);
        //entity1.setPosition(0f, -0.1f, 0f);
        //entity1.yaw(0);

        Model wireFrame = new Model();
        wireFrame.loadVertices(verts);
        wireFrame.setDrawingModeLines();
        ColorHolder black = new ColorHolder(wireFrame, new float[]{0,0,0,1f});
        entity2 = scene.addDrawable(black);
        entity2.setLatLonAlt(loc);
        //entity2.setPosition(0f,-0.1f,0f);
        //entity2.yaw(0);

        Billboard bb = new BillboardMaker().make(this, R.drawable.well_bb_icon);
        ScaleObject sbb = new ScaleObject(bb, 0.002f, 0.001f, 0.001f);
        e = new Entity();
        e.setDrawable(sbb);
        float[] bbLoc = getbbLoc(new float[]{loc[0]-0.002f,loc[1]-0.009f},loc);
        e.setPosition(bbLoc[0],bbLoc[1]+0.04f,bbLoc[2]);

    }
    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);
        camera.setPerspective(60, (float)width / height, 0.001f, 100000f);
        camera.setViewport(0, 0, width, height);
    }
    @Override
    public void GLDraw() {
        super.GLDraw();
        camera.clear();
        /* Do camera stuff */
//            if(currentOrientation != null && currentLocation != null) {
        if(getOrientation() != null) {
            camera.setOrientationQuaternion(getOrientation(), Orientation.getOrientationAngle(this));
//                camera.setLatLonAlt(currentLocation);
//                Log.d(TAG, "setting camera orientation");
        }
        /* Draw */
        scene.draw(camera.getProjectionMatrix(), camera.getViewMatrix());
        e.draw(camera.getProjectionMatrix(),camera.getViewMatrix(),e.getModelMatrix());
    }

    public float[] meshdataLoc(String filename){
        helper = new DatabaseHelper(this);
        db=helper.getReadableDatabase();
        return helper.getMeshData(db,filename);
    }
    public float[] getbbLoc(float[] bbloc,float[] meshloc){
        float bbx = bbloc[1];
        float bby = bbloc[0];

        float mx = meshloc[1];
        float my = meshloc[0];

        double x = (Math.abs(mx-0.20)-Math.abs(bbx))/0.002;
        double y = (Math.abs(my+0.20)-Math.abs(bby))/0.002;
        int index = (int) (x+(y*200));

        Vector3 vec = vecs[index];
        float[] result = {(float) vec.getZ(), (float) vec.getX(), (float) vec.getY()};
        return result;
    }
}