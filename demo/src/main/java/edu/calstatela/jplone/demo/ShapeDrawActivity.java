    package edu.calstatela.jplone.demo;


    import android.opengl.GLES20;

    import edu.calstatela.jplone.arframework.graphics3d.camera.Camera3D;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.ColorHolder;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.LitModel;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.Model;
    import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
    import edu.calstatela.jplone.arframework.graphics3d.helper.MeshHelper;
    import edu.calstatela.jplone.arframework.graphics3d.matrix.Projection;
    import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
    import edu.calstatela.jplone.arframework.ui.SensorARActivity;
    import edu.calstatela.jplone.arframework.util.Orientation;


    public class ShapeDrawActivity extends SensorARActivity {

        private static final String TAG = "waka-shapes";

        private Camera3D camera;

        private Entity entity1, entity2, entity3, entity4, entity5, entity6, entity7, entity8;
        private Scene scene;


        ////////////////////////////////////////////////////////////////////////////////////////////////
        //
        //      OpenGL Callbacks
        //
        ////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void GLInit() {
            super.GLInit();
            Billboard.init();

            entity1 = entity2 = entity3 = entity4 = entity5 = entity6 = entity7 = entity8 = null;
            scene = new Scene();

            camera = new Camera3D();

            setupScene();
        }

        private void setupScene(){
            LitModel cubeModel = new LitModel();
            cubeModel.loadVertices(MeshHelper.cube());
            cubeModel.loadNormals(MeshHelper.calculateNormals(MeshHelper.cube()));

            LitModel pyramidModel = new LitModel();
            pyramidModel.loadVertices(MeshHelper.pyramid());
            pyramidModel.loadNormals(MeshHelper.calculateNormals(MeshHelper.pyramid()));

            Billboard wellBB = BillboardMaker.make(this, R.drawable.well_icon);
            Billboard mountainBB = BillboardMaker.make(this, R.drawable.mountain_icon);
            Billboard signBB = BillboardMaker.make(this, R.drawable.ara_icon, "Sample Title", "This is Sample Text");

            Model pyramidWireframe = new Model();
            pyramidWireframe.loadVertices(MeshHelper.pyramid());
            pyramidWireframe.setDrawingModeLineStrip();



            ColorHolder redCube = new ColorHolder(cubeModel, new float[]{1, 0, 0, 1});
            entity1 = scene.addDrawable(redCube);
            entity1.setPosition(-2, 3, 10);

            ColorHolder blueCube = new ColorHolder(cubeModel, new float[]{0, 0, 1, 1});
            entity2 = scene.addDrawable(blueCube);
            entity2.setPosition(0, 0, 10);
            entity2.setScale(1, 3, 1);


            ColorHolder greenCube = new ColorHolder(cubeModel, new float[]{0, 1, 0, 1});
            entity3 = scene.addDrawable(greenCube);
            entity3.setPosition(2, -3, 10);
            entity3.setScale(1, 3, 1);

            ColorHolder brownPyramid = new ColorHolder(pyramidModel, new float[]{0.7f, 0.3f, 0, 1});
            entity4 = scene.addDrawable(brownPyramid);
            entity4.setPosition(-4, 0, 10);

            entity5 = scene.addDrawable(wellBB); entity5.setPosition(-5, -1, 10);
            entity6 = scene.addDrawable(mountainBB); entity6.setPosition(-5, 3, 10);
            entity7 = scene.addDrawable(signBB); entity7.setPosition(-9, 0, 10); entity7.setScale(4, 2, 2);

            ColorHolder purplePyramid = new ColorHolder(pyramidWireframe, new float[]{1, 0, 1, 1});
            entity8 = scene.addDrawable(purplePyramid);
            entity8.setPosition(-2, -5, 5);
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
            entity1.yaw(1);
            entity2.yaw(2);
            entity3.yaw(3);
            entity4.yaw(-1);
            entity5.yaw(-1);
            entity7.yaw(1);
            entity8.yaw(-2);

            /* Draw */
            scene.draw(camera.getProjectionMatrix(), camera.getViewMatrix());

        }





    }
