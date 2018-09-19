package edu.calstatela.jplone.watertrekapp.billboardview;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import edu.calstatela.jplone.arframework.graphics3d.camera.Camera3D;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.entity.ScaleObject;
import edu.calstatela.jplone.arframework.ui.SensorARView;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.VectorMath;

public class BillboardView_sorting extends SensorARView{

    public BillboardView_sorting(Context context){
        super(context);
        mContext = context;
    }

    public void addBillboard(int id, int iconResource, String title, String text, float lat, float lon, float alt){
        BillboardInfo info = new BillboardInfo(id, iconResource, title, text, lat, lon, alt);
        synchronized(mAddList) {
            mAddList.add(info);
        }
    }

    public void removeBillboard(int id){
        synchronized(mRemoveList) {
            mRemoveList.add(id);
        }
    }

    public interface TouchCallback{
        void onTouch(int id);
    }

    public void setTouchCallback(BillboardView_sorting.TouchCallback callback){
        mTouchCallback = callback;
    }

    public void setDeviceOrientation(int deviceOrientation){
        switch(deviceOrientation){
            case 0:
            case 90:
            case 180:
            case 270:
                this.deviceOrientation = deviceOrientation;
                break;
        }
        Log.d(TAG, "deviceOrientation: " + deviceOrientation);
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

        mCamera = new Camera3D();
        mCamera.setClearColor(0, 0, 0, 0);
        mCamera.setDepthTestEnabled(false);

        mEntityList = new ArrayList<>();
    }

    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);
        mCamera.setViewport(0, 0, width, height);
        mCamera.setPerspective(60, (float)width / height, 0.1f, 1000000f);
    }

    @Override
    public void GLDraw() {
        super.GLDraw();
        mCamera.clear();

        // update camera
        if(getLocation() != null)
            mCamera.setPositionLatLonAlt(getLocation());
        if(getOrientation() != null)
            mCamera.setOrientationQuaternion(getOrientation(), deviceOrientation);

        // If existing Billboards need to be re-added due to new GLContext, re-add them
        if(mEntityList.isEmpty() && !mCurrentInfos.isEmpty() && this.getLocation() != null){
            for(BillboardInfo info : mCurrentInfos){
                newEntity(info);
            }
        }

        // If new Billboards need to be added... add them
        synchronized(mAddList) {
            if (!mAddList.isEmpty() && getLocation() != null) {
                for (BillboardInfo info : mAddList) {
                    mCurrentInfos.add(info);
                    newEntity(info);
                }
                mAddList.clear();
            }
        }

        // If billboard need to be removed... remove
        synchronized(mRemoveList) {
            if (!mRemoveList.isEmpty()) {
                for (Integer id : mRemoveList) {
                    for (int i = 0; i < mCurrentInfos.size(); i++) {
                        if (mCurrentInfos.get(i).id == id) {
                            mCurrentInfos.remove(i);
                            mEntityList.remove(i);
                        }
                    }
                }
                mRemoveList.clear();
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
                e.draw(mCamera.getProjectionMatrix(), mCamera.getViewMatrix(), e.getModelMatrix());
            }
        }


        // Maintain Billboards sorted based on distance from location
        if(getLocation() != null) {
            float[] loc = getLocation();
            float[] xyz = new float[3];
            GeoMath.latLonAltToXYZ(loc, xyz);

            float prevDistance = 0;
            for (int i = 0; i < mEntityList.size(); i++) {
                Entity e = mEntityList.get(i);
                float[] pos = e.getPosition();
                float distance = VectorMath.distance(xyz, pos);
                if(distance > prevDistance && i > 0){
                    mEntityList.set(i, mEntityList.get(i-1));
                    mEntityList.set(i-1, e);
                    BillboardInfo temp = mCurrentInfos.get(i);
                    mCurrentInfos.set(i, mCurrentInfos.get(i-1));
                    mCurrentInfos.set(i-1, temp);
                }
                prevDistance = distance;
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
            e.getScreenPosition(xy2, mCamera.getProjectionMatrix(), mCamera.getViewMatrix(), e.getModelMatrix(), v.getWidth(), v.getHeight());
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
    private int deviceOrientation = 0;

    private ArrayList<BillboardInfo> mAddList = new ArrayList<>();
    private ArrayList<Integer> mRemoveList = new ArrayList<>();

    private ArrayList<BillboardInfo> mCurrentInfos = new ArrayList<>();
    private ArrayList<Entity> mEntityList;

    private Camera3D mCamera;


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
