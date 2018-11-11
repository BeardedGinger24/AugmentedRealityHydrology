package edu.calstatela.jplone.watertrekapp.activities;

import java.util.concurrent.ExecutionException;
import edu.calstatela.jplone.arframework.graphics3d.camera.Camera3D;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.ColorHolder;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Model;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
import edu.calstatela.jplone.arframework.ui.SensorARActivity;
import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.watertrekapp.Data.MeshData;

public class DisplayMeshActivity extends SensorARActivity{
    private Camera3D camera;
    private Entity entity1,entity2;
    private Scene scene;
    MeshData meshData = null;
    MeshDemoActivity.TiffPlanarTerrainMeshGenerator asyncTask =new MeshDemoActivity.TiffPlanarTerrainMeshGenerator();
    @Override
    public void GLInit() {
        super.GLInit();
        Billboard.init();

        scene = new Scene();
        entity1 = null;
        entity2 = null;
        camera = new Camera3D();
        asyncTask.execute();
        try {
            meshData = asyncTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        setupScene();
    }
    private void setupScene(){
        float[] verts = new float[meshData.Triangles.length*3];
        int index = 0;
        for(int i = 0; i<meshData.Triangles.length-1; i++){
            verts[index] = (float) meshData.Vertices[meshData.Triangles[i]].getX();
            verts[index+1] = (float) meshData.Vertices[meshData.Triangles[i]].getY();
            verts[index+2] = (float) meshData.Vertices[meshData.Triangles[i]].getZ();
            index += 3;
        }

        Model mesh = new Model();
        mesh.loadVertices(verts);
        mesh.setDrawingModeTriangles();
        ColorHolder purple = new ColorHolder(mesh, new float[]{1, 0, 1, 1});
        entity1 = scene.addDrawable(purple);
        entity1.setPosition(0, -0.5f, 2);

        Model wireFrame = new Model();
        wireFrame.loadVertices(verts);
        wireFrame.setDrawingModeLineStrip();
        ColorHolder black = new ColorHolder(wireFrame, new float[]{0,0,0,1});
        entity2 = scene.addDrawable(black);
        entity2.setPosition(0,-0.5f,2);
    }
    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);

        camera.setPerspective(60, (float)width / height, 0.01f, 100000000f);
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


        /* Update Entities/Scenes */
        //this will rotate the entity on the specified axis
        entity1.yaw(0.5f);
        entity2.yaw(0.5f);

        /* Draw */
        scene.draw(camera.getProjectionMatrix(), camera.getViewMatrix());

    }
}
