package edu.calstatela.jplone.watertrekapp.billboardview;



import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import edu.calstatela.jplone.arframework.graphics3d.camera.Camera3D;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardInfo;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.drawable.ColorHolder;
import edu.calstatela.jplone.arframework.graphics3d.drawable.LitModel;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Model;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.entity.ScaleObject;
import edu.calstatela.jplone.arframework.graphics3d.helper.MeshHelper;
import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
import edu.calstatela.jplone.arframework.ui.SensorARView;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.VectorMath;
import edu.calstatela.jplone.watertrekapp.Data.MeshData;
import edu.calstatela.jplone.watertrekapp.Data.MeshInfo;
import edu.calstatela.jplone.watertrekapp.Data.Vector3;
import edu.calstatela.jplone.watertrekapp.R;


public class BillboardView_sorting extends SensorARView{
    private static final String TAG = "waka-bbView";
    private TouchCallback mTouchCallback = null;

    private Context mContext;
    private int deviceOrientation = 0;

    private ArrayList<BillboardInfo> mAddList = new ArrayList<>();
    private ArrayList<Integer> mRemoveList = new ArrayList<>();
    private ArrayList<BillboardInfo> mCurrentInfos = new ArrayList<>();
    private ArrayList<Entity> mEntityList;

    //Scene scene;
    ArrayList<MeshInfo> meshAddList = new ArrayList<>();
    ArrayList<MeshInfo> meshCurrentInfos = new ArrayList<>();
    ArrayList<Entity> meshList;

    Scene scene;
    float[] meshLoc;
    Vector3[] vecs;
    public Camera3D mCamera;
    float[] color;

    boolean drawMesh;
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
    public void addMesh(MeshInfo info){
        vecs = info.getVecs();
        meshLoc = info.getLatlonalt();
        synchronized(meshAddList) {
            meshAddList.add(info);
        }
    }
    public boolean getMeshStatus(){
        return drawMesh;
    }
    public void setMeshStatus(boolean b){
        drawMesh=b;
    }
    public boolean meshNull(){
        return vecs==null;
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
        Log.d(TAG, "sensory: " + Integer.toString(deviceOrientation));
//        Log.d("sensory-GX: " , GX);
//        Log.d("sensory-GY: " , GY);
//        Log.d("sensory-GZ: " , GZ);
    }

    public void setRollPitchYaw(int deviceOrientation){
        switch(deviceOrientation){
            case 0:
            case 90:
            case 180:
            case 270:
                this.deviceOrientation = deviceOrientation;
                break;
        }

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
        mCamera.setDepthTestEnabled(false);
        color = new float[]{0, 0, 0, 0};
        mEntityList = new ArrayList<>();
        meshList = new ArrayList<>();

        meshLoc = new float[3];
        drawMesh = false;
    }

    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);
        mCamera.setViewport(0, 0, width, height);
        mCamera.setPerspective(60, (float)width / height, 0.01f, 1000000f);
    }

    @Override
    public void GLDraw() {
        super.GLDraw();
        mCamera.setClearColor(color[0],color[1],color[2],color[3]);
        mCamera.clear();

        // update camera
        if(getLocation() != null)
            mCamera.setPositionLatLonAlt(getLocation());
        if(getOrientation() != null)
            mCamera.setOrientationQuaternion(getOrientation(), deviceOrientation);

        // If existing Billboards/Meshes need to be re-added due to new GLContext, re-add them
        if(mEntityList.isEmpty() && !mCurrentInfos.isEmpty() && this.getLocation() != null){
            for(BillboardInfo info : mCurrentInfos){
                newEntity(info);
            }
        }
        if(meshList.isEmpty() && !meshCurrentInfos.isEmpty() && this.getLocation() != null){
            for(MeshInfo info : meshCurrentInfos){
                newMesh(info);
            }
        }

        // If new Billboards/Meshes need to be added... add them
        synchronized(mAddList) {
            if (!mAddList.isEmpty() && getLocation() != null) {
                for (BillboardInfo info : mAddList) {
                    mCurrentInfos.add(info);
                    newEntity(info);
                }
                mAddList.clear();
            }
        }
        synchronized(meshAddList) {
            if (!meshAddList.isEmpty() && getLocation() != null) {
                for (MeshInfo info : meshAddList) {
                    meshCurrentInfos.add(info);
                    newMesh(info);
                }
                meshAddList.clear();
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
        //
        //Update Entities to be properly rotated and scaled
        if(getLocation() != null) {
            float[] loc = getLocation();
            float[] xyz = GeoMath.latLonAltToXYZ(loc);
            for (Entity e : mEntityList) {
                //float[] bbloc = getbbloc();
                float[] pos = e.getPosition();
                e.setPosition(pos[0],pos[1],pos[2]);
                //e.setLookAtWithScale(pos[0], 0, pos[2], xyz[0], xyz[1], xyz[2], 0, 1, 0, 1f);
            }
            for(Entity e : meshList){
                float[] pos = e.getPosition();
                e.setPosition(pos[0],pos[1],pos[2]);
            }
        }

        // Draw billboards/Meshes
        if(getLocation() != null) {
            if(drawMesh && scene !=null) {
//                for(Entity e : meshList){
//                    e.draw(mCamera.getProjectionMatrix(), mCamera.getViewMatrix(), e.getModelMatrix());
//                }
                scene.draw(mCamera.getProjectionMatrix(), mCamera.getViewMatrix());
            }
            for (Entity e : mEntityList) {
                e.draw(mCamera.getProjectionMatrix(), mCamera.getViewMatrix(), e.getModelMatrix());
            }
        }

        //
        //Maintain Billboards sorted based on distance from location
        if(getLocation() != null) {
            float[] loc = getLocation();
            float[] xyz = GeoMath.latLonAltToXYZ(loc);

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

        float[] xyzLoc = GeoMath.latLonAltToXYZ(getLocation());

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
            float[] infoXYZ = GeoMath.latLonAltToXYZ(new float[]{info.lat, info.lon, info.alt});
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
        Billboard bb = new BillboardMaker().make(mContext, info.iconResource);
        ScaleObject sbb = new ScaleObject(bb, 0.4f, 0.2f, 0.2f);
        Entity e = new Entity();
        e.setDrawable(sbb);
        //e.setLatLonAlt(new float[]{info.lat,info.lon});
        float[] bbLoc = getbbLoc(new float[]{info.lat,info.lon},meshLoc);
        e.setPosition(bbLoc[0],-0.03f-bbLoc[1],bbLoc[2]);
        e.yaw(45);
        Log.d(TAG,"BBXYZ: "+bbLoc[0]+","+bbLoc[1]+","+bbLoc[2]);
        mEntityList.add(e);
        //scene.add(e);
    }
    private void newMesh(MeshInfo info){
        scene = new Scene();
        float[] newMeshLoc = getbbLoc(meshLoc,getLocation());

        LitModel mesh = new LitModel();
        mesh.loadVertices(info.getVerts());
        mesh.loadNormals(MeshHelper.calculateNormals(info.getVerts()));
        mesh.setDrawingModeTriangles();

        ColorHolder color = new ColorHolder(mesh, new float[]{0.3f, 0.4f, 0.3f, 0.1f});
        Entity entity1 = new Entity();
        entity1.setDrawable(color);
        entity1.setPosition(newMeshLoc[0],-0.5f-newMeshLoc[1],newMeshLoc[2]);
        //entity1.setLatLonAlt(info.getLatlonalt());
        //meshList.add(entity1);

//        Model wireFrame = new Model();
//        wireFrame.loadVertices(info.getVerts());
//        wireFrame.setDrawingModeLines();
//        ColorHolder black = new ColorHolder(wireFrame, new float[]{0,0,0,0.5f});
//        Entity entity2 = new Entity();
//        entity2.setDrawable(black);
//        entity2.setPosition(newMeshLoc[0],-0.04f-newMeshLoc[1],newMeshLoc[2]);
        //entity2.setLatLonAlt(info.getLatlonalt());
        //meshList.add(entity2);

        scene.add(entity1);
        //scene.add(entity2);
    }
    public float[] getbbLoc(float[] bbloc,float[] meshloc){
        float bbx = bbloc[1];
        float bby = bbloc[0];

        float mx = meshloc[1];
        float my = meshloc[0];

        double x = (Math.abs(mx-0.20)-Math.abs(bbx))/0.002;
        double y = Math.abs((Math.abs(my+0.20)-Math.abs(bby))/0.002);
        int index = (int) (x+(y*200));
        Log.d(TAG,index+"");

        if(index<vecs.length && index>=0) {
            Vector3 vec = vecs[index];
            float[] result = { (float) vec.getX(),(float) vec.getY(),(float) vec.getZ()};
            return result;
        }
        return new float[]{0,0,0};
    }
    public void changeBGC(boolean b){
        if(!b){
            color = new float[]{0,0,0,255};
        }else {
            color = new float[]{0, 0, 0, 0};
        }
    }
}
