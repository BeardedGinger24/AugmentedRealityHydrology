package edu.calstatela.jplone.demo;

import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import edu.calstatela.jplone.arframework.graphics3d.camera.Camera3D;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.entity.ScaleObject;
import edu.calstatela.jplone.arframework.graphics3d.matrix.Projection;
import edu.calstatela.jplone.arframework.graphics3d.scene.TouchScene;
import edu.calstatela.jplone.arframework.landmark.Landmark;
import edu.calstatela.jplone.arframework.landmark.LandmarkTable;
import edu.calstatela.jplone.arframework.ui.SensorARActivity;
import edu.calstatela.jplone.arframework.util.GeoMath;


public class BillboardLandmarksActivity extends SensorARActivity {
    static final String TAG = "waka_BBLandmarks";


    TouchScene mScene;
    Camera3D mCamera;


    LandmarkTable landmarkTable = new LandmarkTable();

    float x = -1, y = -1;



    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      OpenGL Callbacks
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void GLInit() {
        super.GLInit();
        Billboard.init();


        mScene = new TouchScene();
        mScene.setScale(0.2f);

        mCamera = new Camera3D();
        mCamera.setClearColor(0, 0, 0, 0);

        landmarkTable = new LandmarkTable();
    }

    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);

        mCamera.setViewport(0, 0, width, height);
        mCamera.setPerspective(60, (float)width / height, 0.1f, 100000000000f);
    }

    @Override
    public void GLDraw() {
        super.GLDraw();

        mCamera.clear();

        if(mScene.isEmpty() && getLocation() != null)
            setupBillboards();

        if(getLocation() != null && getOrientation() != null){
            mCamera.setPositionLatLonAlt(getLocation());
            mCamera.setOrientationQuaternion(getOrientation(), 0);
            mScene.setCenterLatLonAlt(getLocation());
            mScene.update();
        }

        mScene.draw(mCamera.getProjectionMatrix(), mCamera.getViewMatrix());

        if(x >= 0){
            float[] position = new float[4];
            GeoMath.latLonAltToXYZ(getLocation(), position);
            int closestIndex = mScene.findClosestEntity(x, y, getARView().getWidth(), getARView().getHeight(), 0.2f, mCamera.getProjectionMatrix(), mCamera.getViewMatrix(), position);

            if(closestIndex >= 0 && closestIndex < landmarkTable.size()){
                Landmark l = landmarkTable.get(closestIndex);
                Log.d(TAG, l.title + "  " + l.description);
            }

            x = -1;
            y = -1;
        }

    }



    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Drawing Helper Functions
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void setupBillboards(){

        landmarkTable = new LandmarkTable();
        landmarkTable.loadCities();

        for(Landmark l : landmarkTable){
            Billboard bb = BillboardMaker.make(this, R.drawable.ara_icon, l.title, l.description);
            ScaleObject sbb = new ScaleObject(bb, 2, 1, 1);
            Entity entity = mScene.addDrawable(sbb);
            entity.setLatLonAlt(new float[]{l.latitude, l.longitude, l.altitude});
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
        }

        return true;
    }
}
