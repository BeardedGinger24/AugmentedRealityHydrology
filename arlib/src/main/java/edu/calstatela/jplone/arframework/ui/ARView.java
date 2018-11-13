package edu.calstatela.jplone.arframework.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.SensorEvent;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.calstatela.jplone.arframework.sensor.ARGps;
import edu.calstatela.jplone.arframework.sensor.ARSensor;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.Permissions;




public class ARView extends FrameLayout {

    private static final String TAG = "waka-ARView";
    private GLSurfaceView glSurfaceView;
    private CameraView cameraView;

    public ARView(Context context){
        super(context);

        glSurfaceView = new GLSurfaceView(context);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 24, 0);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setZOrderMediaOverlay(true);
        addView(glSurfaceView);

        if(Permissions.havePermission(context, Permissions.PERMISSION_CAMERA)) {
            cameraView = new CameraView(context);
            addView(cameraView);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Overridable Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onPause(){
        glSurfaceView.onPause();
    }

    public void onResume(){
        glSurfaceView.onResume();
    }

    public void GLInit(){
        Log.d(TAG, "ARView.GLInit()");
    }

    public void GLResize(int width, int height){ }

    public void GLDraw(){ }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Event Callbacks that call the handler methods of this class
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private GLSurfaceView.Renderer renderer = new GLSurfaceView.Renderer(){

        @Override
        public void onDrawFrame(GL10 gl) {
            GLDraw();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLResize(width, height);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLInit();
        }
    };


}

// Issues:
// * Create ways to customize ARVIEW (buffer depth, gl version, etc)
// * Make it so that camera view is released in onPause() and reconnected in onResume(), see
//      CameraView comments for more information