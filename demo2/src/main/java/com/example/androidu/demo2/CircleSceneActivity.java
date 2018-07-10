    package com.example.androidu.demo2;


    import android.opengl.GLES20;
    import android.util.Log;
    import android.view.MotionEvent;
    import android.view.View;

    import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.ColorHolder;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.LitModel;
    import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
    import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
    import edu.calstatela.jplone.arframework.graphics3d.scene.CircleScene;
    import edu.calstatela.jplone.arframework.graphics3d.scene.ScalingCircleScene;
    import edu.calstatela.jplone.arframework.ui.SensorARActivity;
    import edu.calstatela.jplone.arframework.util.GeoMath;


    public class CircleSceneActivity extends SensorARActivity {

        private static final String TAG = "waka-mountain";


        private Projection projection;
        private ARGLCamera camera;
        private ScalingCircleScene scene;
        private Entity centerCube;




        ////////////////////////////////////////////////////////////////////////////////////////////////
        //
        //      OpenGL Callbacks
        //
        ////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void GLInit() {
            super.GLInit();
            Billboard.init();

            scene = new ScalingCircleScene();
            scene.setRadius(10);
            scene.setScale(0.2f);
            centerCube = new Entity();

            projection = new Projection();
            camera = new ARGLCamera();

            GLES20.glClearColor(0, 0, 0, 0);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }



        @Override
        public void GLResize(int width, int height) {
            super.GLResize(width, height);

            projection.setPerspective(60, (float)width / height, 0.01f, 100000000f);
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void GLDraw() {
            super.GLDraw();

            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            /* Set up Entities when the conditions are right */
            if(scene.isEmpty() && getLocation() != null)
                setupScene();

            /* Update Entities/Scenes */
            if(getLocation() != null) {
                scene.update();
                scene.setCenterLatLonAlt(getLocation());
                float[] xyzLocation = new float[3];
                GeoMath.latLonAltToXYZ(getLocation(), xyzLocation);
                centerCube.lookAtPoint(xyzLocation[0], xyzLocation[1], xyzLocation[2]);
            }

            /* Do camera stuff */
            if(getOrientation() != null && getLocation() != null) {
                camera.setOrientationVector(getOrientation(), 0);
                camera.setPositionLatLonAlt(getLocation());
            }

            /* Draw */
            scene.draw(projection.getProjectionMatrix(), camera.getViewMatrix());
            centerCube.draw(projection.getProjectionMatrix(), camera.getViewMatrix(), centerCube.getModelMatrix());
        }

        private void setupScene(){
            Billboard riverBB = BillboardMaker.make(this, R.drawable.river_icon);
            Billboard wellBB = BillboardMaker.make(this, R.drawable.well_icon);
            Billboard mountainBB = BillboardMaker.make(this, R.drawable.mountain_icon);


            float[] center = scene.getCenter();

            Entity e;
            e = scene.addDrawable(riverBB); e.setPosition(center[0] + 0, center[1] + 0, center[2] + -40);
            e = scene.addDrawable(mountainBB); e.setPosition(center[0] + 7, center[1] + 0, center[2] + -3);
            e = scene.addDrawable(wellBB); e.setPosition(center[0] + -7, center[1] + 0.5f, center[2] + -10);
            e = scene.addDrawable(riverBB); e.setPosition(center[0] + 77, center[1] + 0, center[2] + -15);
            e = scene.addDrawable(mountainBB); e.setPosition(center[0] + 4, center[1] + 0, center[2] + 3);
            e = scene.addDrawable(mountainBB); e.setPosition(center[0] + 8, center[1] + 0, center[2] + 33);
            e = scene.addDrawable(riverBB); e.setPosition(center[0] + 4, center[1] + 0, center[2] + -10);
            e = scene.addDrawable(wellBB); e.setPosition(center[0] + 1, center[1] + 0.5f, center[2] + -10);
            e = scene.addDrawable(riverBB); e.setPosition(center[0] + 1, center[1] + 0, center[2] + 5);
            e = scene.addDrawable(mountainBB); e.setPosition(center[0] + 12, center[1] + 0, center[2] + 0);
            e = scene.addDrawable(wellBB); e.setPosition(center[0] + -4, center[1] + 0.5f, center[2] + 0);



            LitModel cubeModel = LitModel.cube();
            ColorHolder redCube = new ColorHolder(cubeModel, new float[]{1, 0, 0, 1});
            centerCube.setDrawable(redCube);
            centerCube.setPosition(center[0], center[1], center[2]);
        }


    }
