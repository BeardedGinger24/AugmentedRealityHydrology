    package edu.calstatela.jplone.demo;


    import android.util.Log;

    import edu.calstatela.jplone.arframework.graphics3d.camera.Camera3D;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.LitModel;
    import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
    import edu.calstatela.jplone.arframework.graphics3d.helper.MeshHelper;
    import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
    import edu.calstatela.jplone.arframework.landmark.MountainData;
    import edu.calstatela.jplone.arframework.ui.SensorARActivity;
    import edu.calstatela.jplone.arframework.util.Orientation;


    public class MountainDrawActivity extends SensorARActivity {

        private static final String TAG = "waka-shapes";


        private Camera3D camera;
        private Scene scene;

        private float scalingFactor = 2;
        private float angle = 0;

        private Entity bb1, bb2, bb3, bb4, bb5;





        ////////////////////////////////////////////////////////////////////////////////////////////////
        //
        //      OpenGL Callbacks
        //
        ////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void GLInit() {
            super.GLInit();
            Billboard.init();

            Log.d(TAG, "....... init .........");

            scene = null;

            bb1 = bb2 = bb3 = bb4 = bb5 = null;

            camera = new Camera3D();
            camera.setClearColor(0, 0, 0, 0);
            camera.setPosition(0, 0, 0);
            scene = new Scene();
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

//            Log.d(TAG, "..... draw .....");
            camera.clear();

            /* Do camera stuff */
            if(getOrientation() != null && getLocation() != null) {
                camera.setOrientationQuaternion(getOrientation(), 0);
                camera.setPositionLatLonAlt(getLocation());
            }


            /* Update Entities/Scenes */
            if(bb1 == null && getLocation() != null){
                setup();
            }
            if(bb1 != null) {
                bb1.yaw(1);
                bb2.yaw(1);
                bb3.yaw(1);
                bb4.yaw(1);
                bb5.yaw(1);
            }

            /* Draw */
            scene.draw(camera.getProjectionMatrix(), camera.getViewMatrix());


            Log.d(TAG, "Orientation: " + Orientation.getOrientationAngle(this));
        }


        private void setup(){
            Entity e;
            Billboard b;
            float[] mountain;

            LitModel pyramidModel = new LitModel();
            pyramidModel.loadVertices(MeshHelper.pyramid());
            pyramidModel.loadNormals(MeshHelper.calculateNormals(MeshHelper.pyramid()));

            mountain = MountainData.mtWilson;
            e = scene.addDrawable(pyramidModel);
            e.setLatLonAlt(new float[]{mountain[0], mountain[1], 0});
            e.setScale(scalingFactor * mountain[2] / 2, scalingFactor * mountain[2], scalingFactor * mountain[2] / 2);
            b = BillboardMaker.make2(this, R.drawable.mountain_icon, "Mt Wilson", "elevation: " + (int)mountain[2]);
            bb1 = scene.addDrawable(b);
            bb1.setLatLonAlt(new float[]{mountain[0], mountain[1], mountain[2]*3});
            bb1.setScale(2000, 2000, 2000);

            mountain = MountainData.mtLukens;
            e = scene.addDrawable(pyramidModel);
            e.setLatLonAlt(new float[]{mountain[0], mountain[1], 0});
            e.setScale(scalingFactor * mountain[2] / 2, scalingFactor * mountain[2], scalingFactor * mountain[2] / 2);
            b = BillboardMaker.make2(this, R.drawable.mountain_icon, "Mt Lukens", "elevation: " + (int)mountain[2]);
            bb2 = scene.addDrawable(b);
            bb2.setLatLonAlt(new float[]{mountain[0], mountain[1], mountain[2]*3});
            bb2.setScale(2000, 2000, 2000);

            mountain = MountainData.sanGabrielPeak;
            e = scene.addDrawable(pyramidModel);
            e.setLatLonAlt(new float[]{mountain[0], mountain[1], 0});
            e.setScale(scalingFactor * mountain[2] / 2, scalingFactor * mountain[2], scalingFactor * mountain[2] / 2);
            b = BillboardMaker.make2(this, R.drawable.mountain_icon, "San Gabriel Peak", "elevation: " + (int)mountain[2]);
            bb3 = scene.addDrawable(b);
            bb3.setLatLonAlt(new float[]{mountain[0], mountain[1], mountain[2]*3});
            bb3.setScale(2000, 2000, 2000);


            mountain = MountainData.brownMountain;
            e = scene.addDrawable(pyramidModel);
            e.setLatLonAlt(new float[]{mountain[0], mountain[1], 0});
            e.setScale(scalingFactor * mountain[2] / 2, scalingFactor * mountain[2], scalingFactor * mountain[2] / 2);
            b = BillboardMaker.make2(this, R.drawable.mountain_icon, "Brown Mountain", "elevation: " + (int)mountain[2]);
            bb4 = scene.addDrawable(b);
            bb4.setLatLonAlt(new float[]{mountain[0], mountain[1], mountain[2]*3});
            bb4.setScale(2000, 2000, 2000);

            mountain = MountainData.hoytMountain;
            e = scene.addDrawable(pyramidModel);
            e.setLatLonAlt(new float[]{mountain[0], mountain[1], 0});
            e.setScale(scalingFactor * mountain[2] / 2, scalingFactor * mountain[2], scalingFactor * mountain[2] / 2);
            b = BillboardMaker.make2(this, R.drawable.mountain_icon, "Hoyt Mountain", "elevation: " + (int)mountain[2]);
            bb5 = scene.addDrawable(b);
            bb5.setLatLonAlt(new float[]{mountain[0], mountain[1], mountain[2]*3});
            bb5.setScale(2000, 2000, 2000);
        }




    }
