package edu.calstatela.jplone.watertrekapp2.activities;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.entity.ScaleObject;
import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
import edu.calstatela.jplone.arframework.ui.ARView;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.VectorMath;
import edu.calstatela.jplone.watertrekapp2.R;

public class BillboardView extends ARView{

    public BillboardView(Context context){
        super(context);
        mContext = context;
    }

    public void addBillboard(int id, int iconResource, String title, String text, float lat, float lon, float alt){
        adding = true;
        BillboardInfo info = new BillboardInfo(id, iconResource, title, text, lat, lon, alt);
        mAddList.add(info);
        adding = false;
    }

    public void removeBillboard(int id){
        removing = true;
        mRemoveList.add(id);
        removing = false;
    }

    public interface TouchCallback{
        void onTouch(int id);
    }

    public void setTouchCallback(BillboardView.TouchCallback callback){
        mTouchCallback = callback;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      GL callbacks
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void GLInit() {
        super.GLInit();
        Billboard.init();
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mProjection = new Projection();
        mCamera = new ARGLCamera();

        mEntityList = new ArrayList<>();
    }

    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);
        GLES20.glViewport(0, 0, width, height);
        mProjection.setPerspective(60, (float)width / height, 0.1f, 100000f);
    }

    @Override
    public void GLDraw() {
        super.GLDraw();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // update camera
        if(getLocation() != null)
            mCamera.setPositionLatLonAlt(getLocation());
        if(getOrientation() != null)
            mCamera.setOrientationVector(getOrientation(), 0);

        // If existing Billboards need to be re-added due to new GLContext, re-add them
        if(mEntityList.isEmpty() && !mCurrentInfos.isEmpty() && this.getLocation() != null){
            for(BillboardInfo info : mCurrentInfos){
                newEntity(info);
            }
        }

        // If new Billboards need to be added... add them
        if(!mAddList.isEmpty() && getLocation() != null && !adding){
            for(BillboardInfo info : mAddList){
                mCurrentInfos.add(info);
                newEntity(info);
            }
            mAddList.clear();
        }

        // If billboard need to be removed... remove
        if(!mRemoveList.isEmpty() && !removing){
            for(Integer id : mRemoveList){
                for(int i = 0; i < mCurrentInfos.size(); i++){
                    if(mCurrentInfos.get(i).id == id){
                        mCurrentInfos.remove(i);
                        mEntityList.remove(i);
                    }
                }
            }
        }

        // Update Entities to be properly rotated and scaled
        if(getLocation() != null) {
            float[] loc = getLocation();
            float[] xyz = new float[3];
            GeoMath.latLonAltToXYZ(loc, xyz);
            for (Entity e : mEntityList) {
                float[] pos = e.getPosition();
                e.setLookAtWithConstantDistanceScale(pos[0], pos[1], pos[2], xyz[0], xyz[1], xyz[2], 0, 1, 0, 0.2f);
            }
        }

        // Draw billboards
        if(getLocation() != null) {
            for (Entity e : mEntityList) {
                e.draw(mProjection.getProjectionMatrix(), mCamera.getViewMatrix(), e.getModelMatrix());
            }
        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      touch callback
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() != MotionEvent.ACTION_DOWN)
            return true;

        float[] xy1 = {event.getX(), event.getY()};
        float[] xy2 = new float[2];
        float largestAcceptableScreenDistance = 200;
        float shortestDistance = -1;
        int indexOfClosest = -1;

        float[] xyzLoc = new float[3];
        GeoMath.latLonAltToXYZ(getLocation(), xyzLoc);

        for(int i = 0; i < mEntityList.size(); i++){
            Entity e = mEntityList.get(i);
            e.getScreenPosition(xy2, mProjection.getProjectionMatrix(), mCamera.getViewMatrix(), e.getModelMatrix(), v.getWidth(), v.getHeight());
            if(xy2[0] < 0)
                continue;
            float[] ePos = e.getPosition();

            float screenDistance = VectorMath.distance(xy1, xy2);
            if(screenDistance <= largestAcceptableScreenDistance){
                float realDistance = VectorMath.distance(xyzLoc, ePos);
                if(indexOfClosest == -1 || realDistance < shortestDistance){
                    indexOfClosest = i;
                    shortestDistance = realDistance;
                }
            }

            BillboardInfo info = mCurrentInfos.get(i);
            float[] infoXYZ = new float[3];
            GeoMath.latLonAltToXYZ(new float[]{info.lat, info.lon, info.alt}, infoXYZ);
        }

        if(indexOfClosest >= 0) {
            int id = mCurrentInfos.get(indexOfClosest).id;
            mTouchCallback.onTouch(id);
        }


        return true;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      private helper methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void newEntity(BillboardInfo info){
        Billboard bb = BillboardMaker.make(mContext, info.iconResource, info.title, info.text);
        ScaleObject sbb = new ScaleObject(bb, 2, 1, 1);
        Entity e = new Entity();
        e.setDrawable(sbb);
        e.setLatLonAlt(new float[]{info.lat, info.lon, info.alt});
        mEntityList.add(e);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      private variables
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String TAG = "waka-bbView";
    private TouchCallback mTouchCallback = null;

    private Context mContext;

    private ArrayList<BillboardInfo> mAddList = new ArrayList<>();
    private ArrayList<Integer> mRemoveList = new ArrayList<>();

    private ArrayList<BillboardInfo> mCurrentInfos = new ArrayList<>();
    private ArrayList<Entity> mEntityList;

    private Projection mProjection;
    private ARGLCamera mCamera;

    private boolean adding = false, removing = false;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      utility class
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    class BillboardInfo{
        public BillboardInfo(int id, int iconResource, String title, String text, float lat, float lon, float alt){
            this.id = id; this.iconResource = iconResource;
            this.title = title; this.text = text;
            this.lat = lat; this.lon = lon; this.alt = alt;
        }
        public int id;
        public int iconResource;
        public String title = "";
        public String text = "";
        public float lat;
        public float lon;
        public float alt;
    }
}
