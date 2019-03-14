package edu.calstatela.jplone.watertrekapp.billboardview;



import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import edu.calstatela.jplone.arframework.graphics3d.camera.Camera3D;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.ColorHolder;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Model;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
import edu.calstatela.jplone.arframework.ui.SensorARView;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.Vector3;
import edu.calstatela.jplone.watertrekapp.Data.MeshInfo;


public class ARViewTest extends SensorARView{
    private static final String TAG = "waka-bbView";
    private Context mContext;
    private int deviceOrientation = 0;

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
    boolean shadedMesh;

    public ARViewTest(Context context){
        super(context);
        mContext = context;
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
        meshList = new ArrayList<>();

        scene = new Scene();
        meshLoc = new float[3];
        drawMesh = false;
    }

    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);
        mCamera.setViewport(0, 0, width, height);
        mCamera.setPerspective(60, (float)width / height, 0.0f, 100f);

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

        // If existing Meshes need to be re-added due to new GLContext, re-add them
        if(meshList.isEmpty() && !meshCurrentInfos.isEmpty() && this.getLocation() != null){
            scene.clearList();
            for(MeshInfo info : meshCurrentInfos){
                newMesh(info);
            }
        }

        // If new Meshes need to be added... add them
        synchronized(meshAddList) {
            if (!meshAddList.isEmpty() && getLocation() != null) {
                for (MeshInfo info : meshAddList) {
                    meshCurrentInfos.add(info);
                    newMesh(info);
                }
                meshAddList.clear();
            }
        }
        //
        //Update Entities to be properly rotated and scaled
        if(getLocation() != null) {
            float[] loc = getLocation();
            float[] xyz = GeoMath.latLonAltToXYZ(loc);
            for(Entity e : meshList){
                float[] pos = e.getPosition();
                e.setPosition(pos[0],pos[1],pos[2]);
            }
        }

        // Draw Meshes
        if(getLocation() != null) {
            if(scene !=null) {
//                for(Entity e : meshList){
//                    e.draw(mCamera.getProjectionMatrix(), mCamera.getViewMatrix(), e.getModelMatrix());
//                }
                scene.draw(mCamera.getProjectionMatrix(), mCamera.getViewMatrix());
            }
//            for (Entity e : mEntityList) {
//                e.draw(mCamera.getProjectionMatrix(), mCamera.getViewMatrix(), e.getModelMatrix());
//            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      private helper methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void newMesh(MeshInfo info){
        float[] newMeshLoc = getbbLoc(meshLoc,getLocation());

        Model transMesh = new Model();
        transMesh.loadVertices(info.getVerts());
        transMesh.setDrawingModeTriangles();
        ColorHolder color = new ColorHolder(transMesh,new float[]{0.3f, 0.4f, 0.3f, 0.1f});
        Entity entity1 = new Entity();
        entity1.setDrawable(color);
        entity1.setPosition(newMeshLoc[0],-0.5f-newMeshLoc[1],newMeshLoc[2]);
        //entity1.setLatLonAlt(info.getLatlonalt());
        //meshList.add(entity1);

        scene.add(entity1);

    }
    public float[] getbbLoc(float[] bbloc,float[] meshloc){
        float bbx = bbloc[1];
        float bby = bbloc[0];

        float mx = meshloc[1];
        float my = meshloc[0];

        double x = (Math.abs(mx-0.20)-Math.abs(bbx))/0.002;
        double y = Math.abs((Math.abs(my+0.20)-Math.abs(bby))/0.002);
        int index = (int) (x+(y*200));

        if(index<vecs.length && index>=0) {
            Vector3 vec = vecs[index];
            float[] result = { (float) vec.getX(),(float) vec.getY(),(float) vec.getZ()};
            return result;
        }
        return new float[]{0,0,0};
    }

}
