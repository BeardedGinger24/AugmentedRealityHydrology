package edu.calstatela.jplone.watertrekapp.billboardview;



import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edu.calstatela.jplone.arframework.graphics3d.camera.Camera3D;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardInfo;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.drawable.ColorHolder;
import edu.calstatela.jplone.arframework.graphics3d.drawable.LitModel;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Model;
import edu.calstatela.jplone.arframework.graphics3d.drawable.TextureModel;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.entity.ScaleObject;
import edu.calstatela.jplone.arframework.graphics3d.helper.MeshHelper;
import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
import edu.calstatela.jplone.arframework.ui.SensorARView;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.Vector3;
import edu.calstatela.jplone.arframework.util.VectorMath;
import edu.calstatela.jplone.watertrekapp.DataService.ElevationObstructionService;
import edu.calstatela.jplone.watertrekapp.Helpers.OBJLoader;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTaskJSON;
import edu.calstatela.jplone.watertrekapp.R;


public class BillboardView_sorting extends SensorARView{
    private static final String TAG = "waka-bbView";
    private TouchCallback mTouchCallback = null;

    private Context mContext;
    private int deviceOrientation = 0;
    private float userElevation;
    float[] latlonalt;

    //1 for near, 2 for mid, 3 for far. Default is 1:near
    int bbFilter = 1;
    private ArrayList<BillboardInfo> mAddList = new ArrayList<>();
    private ArrayList<Integer> mRemoveList = new ArrayList<>();
    private ArrayList<BillboardInfo> mCurrentInfos = new ArrayList<>();
    private ArrayList<Entity> mEntityList;
    private ArrayList<Entity> entityListNear;
    private ArrayList<Entity> entityListMid;
    private ArrayList<Entity> entiyiListFar;



    boolean transMade;
    boolean litMade;
    boolean textMade;

    ArrayList<OBJLoader> meshAddList = new ArrayList<>();
    private ArrayList<Entity>removeMeshList = new ArrayList<>();
    ArrayList<OBJLoader> meshCurrentInfos = new ArrayList<>();
    ArrayList<Entity> meshList;

    Scene scene;
    float[] meshLoc;
    public Camera3D mCamera;
    float[] color;
    Bitmap bitmapTerrain,bitmapRiver;

    boolean drawMesh;
    boolean shadedMesh;
    boolean textureMesh;

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
    public void addBitMap(Bitmap terrain,Bitmap river){
        bitmapTerrain = terrain;
        bitmapRiver = river;
    }
    public void addMesh(OBJLoader info){
        latlonalt = getLocation();
        meshLoc = info.getLoc();
        ElevationObstructionService.getObstruction(obstructNetworkCallback,latlonalt[0],latlonalt[1],"0","-90");

        synchronized(meshAddList) {
            meshAddList.add(info);
        }
    }
    public void removeMesh(Entity e){
        synchronized (removeMeshList){
            removeMeshList.add(e);
        }
    }
    public boolean getMeshStatus(){
        return drawMesh;
    }
    public void setMeshStatus(boolean b){
        drawMesh=b;
    }

    public void setShadedMesh(boolean b){
        if(!meshCurrentInfos.isEmpty()) {
            shadedMesh = b;
            textureMesh = false;
            removeMesh(null);
            newMesh(meshCurrentInfos.get(0));
        }
    }
    public void setTexturedMesh(boolean b){
        if(!meshCurrentInfos.isEmpty()) {
            shadedMesh = false;
            textureMesh = b;
            removeMesh(null);
            newMesh(meshCurrentInfos.get(0));
        }
    }
    public boolean meshNull(){
        return meshLoc==null;
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
        TextureModel.init();
        Model.init();
        LitModel.init();

        mCamera = new Camera3D();
        mCamera.setDepthTestEnabled(false);
        color = new float[]{0, 0, 0, 0};

        mEntityList = new ArrayList<>();
        meshList = new ArrayList<>();

        drawMesh = false;
        shadedMesh = false;
        textureMesh = false;

        transMade = false;
        litMade = false;
        textMade = false;

        userElevation = -1;
    }

    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);
        mCamera.setViewport(0, 0, width, height);
        mCamera.setPerspective(53.3f, (float)width / height, 0.0f, 100f);

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
            for(OBJLoader info : meshCurrentInfos){
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
                for (OBJLoader info : meshAddList) {
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
//            mRemoveList.clear();
//            mCurrentInfos.clear();
//            mEntityList.clear();
//            entityListNear.clear();
//            entityListMid.clear();
//            entiyiListFar.clear();
        }
        synchronized (removeMeshList){
            if(!removeMeshList.isEmpty()) {
                meshList.clear();
                removeMeshList.clear();
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
            if(drawMesh) {
                for (Entity e : meshList) {
                    e.draw(mCamera.getProjectionMatrix(), mCamera.getViewMatrix(), e.getModelMatrix());
                }

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
        Billboard bb = new BillboardMaker().make(mContext,info.iconResource);
        ScaleObject sbb = new ScaleObject(bb, 0.8f, 0.4f, 0.4f);
        Entity e = new Entity();
        e.setDrawable(sbb);
        //e.setLatLonAlt(new float[]{info.lat,info.lon});
        float[] bbLoc = getbbLoc(new float[]{info.lat,info.lon,info.alt},meshLoc);
        e.setPosition(bbLoc[0],-(bbLoc[1]+0.1f),bbLoc[2]);
        e.yaw(45);
        mEntityList.add(e);
//        entityListNear.add(e);
//        entityListMid.add(e);
//        entiyiListFar.add(e);
    }
    private void newMesh(OBJLoader info){
        ColorHolder color;
        Entity entity = new Entity();
        if(textureMesh){
            TextureModel texturedMesh = new BillboardMaker().makeM2(bitmapTerrain,bitmapRiver);
            texturedMesh.loadVertices(info.getV());
            texturedMesh.loadTextureVerctices(info.getVt());
            entity.setDrawable(texturedMesh);
        }else if(shadedMesh){
            LitModel litMesh = new LitModel();
            litMesh.loadVertices(info.getV());
            litMesh.loadNormals(info.getN());
            litMesh.setDrawingModeTriangles();
            color = new ColorHolder(litMesh, new float[]{0.3f, 0.4f, 0.3f, 0.1f});
            entity.setDrawable(color);
        }else{
            Model transMesh = new Model();
            transMesh.loadVertices(info.getV());
            transMesh.setDrawingModeTriangles();
            color = new ColorHolder(transMesh,new float[]{0.3f, 0.4f, 0.3f, 0.1f});
            entity.setDrawable(color);
        }

        if(userElevation==-1){
            userElevation = getLocation()[2]+30;
        }
        entity.setPosition(0f,-(userElevation+15)/100,0f);
        entity.yaw(200);
        meshList.add(entity);

        //scene.add(entity);
    }
    NetworkTaskJSON.NetworkCallback obstructNetworkCallback = new NetworkTaskJSON.NetworkCallback() {
        @Override
        public String onResult(int type, String result) {
            Log.d(TAG,result+"");
            try {
                JSONObject results = new JSONObject(result);
                JSONObject temp = results.getJSONObject("obstruction_point");
                userElevation = Float.parseFloat(temp.get("elevation").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    public float[] getbbLoc(float[] bbloc,float[] meshloc){
        float bbx = bbloc[1];
        float bby = bbloc[0];

        float mx = meshloc[1];
        float my = meshloc[0];

        float elevationScale = 100;
        //1 lat is about 111180 meters at equator, and 111200 at poles
        //1 lon is cosine(lon)*lat at equator
        //since our mesh area covers 0.2 degrees in lat/lon directions we divide above values by 5
        double lonScale = 22236/elevationScale;
        double latScale = 18425/elevationScale;

        float x = (float) ((mx-bbx)*latScale);
        float z = (float) ((bby-my)*lonScale);

        float[] result = new float[3];
        result[0] = x;
        result[1] = bbloc[2];
        result[2] = z;
        return result;
    }
    public void changeBGC(boolean b){
        if(!b){
            color = new float[]{0,0,0,255};
        }else {
            color = new float[]{0, 0, 0, 0};
        }
    }
}
