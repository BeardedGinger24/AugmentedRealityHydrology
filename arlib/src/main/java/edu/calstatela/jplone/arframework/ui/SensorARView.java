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
import edu.calstatela.jplone.arframework.util.VectorMath;


public class SensorARView extends ARView {
    private static final String TAG = "waka-SensorARView";

    private ARSensor orientationSensor;
    private ARGps locationSensor;
    private float[] currentOrientation;
    private float[] currentLocation;

    public SensorARView(Context context){
        super(context);

        setOnTouchListener(touchListener);

        orientationSensor = new ARSensor(context, ARSensor.ROTATION_VECTOR);
        orientationSensor.addListener(orientationListener);
        locationSensor = new ARGps(context);
        locationSensor.addListener(locationListener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Access Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public float[] getOrientation(){
        return currentOrientation;
    }

    public float[] getLocation(){
        return currentLocation;
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Overridable Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onPause(){
        super.onPause();
        orientationSensor.stop();
        locationSensor.stop();
    }

    public void onResume(){
        super.onResume();
        orientationSensor.start();
        locationSensor.start();
    }

    public boolean onTouch(View v, MotionEvent event){
        return false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Event Callbacks that call the handler methods of this class
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private OnTouchListener touchListener = new OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return SensorARView.this.onTouch(v, event);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Callbacks
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ARGps.Listener locationListener = new ARGps.Listener(){
        @Override
        public void handleLocation(Location location){
            if(currentLocation == null){
                currentLocation = new float[3];
                currentLocation[0] = (float)location.getLatitude();
                currentLocation[1] = (float)location.getLongitude();
                currentLocation[2] = (float)location.getAltitude();
                GeoMath.setReference(currentLocation);
                return;
            }

            currentLocation[0] = (float)location.getLatitude();
            currentLocation[1] = (float)location.getLongitude();
            currentLocation[2] = (float)location.getAltitude();

            Log.d(TAG, "Location: " + VectorMath.vecToString(currentLocation));
        }
    };

    private ARSensor.Listener orientationListener = new ARSensor.Listener(){
        @Override
        public void onSensorEvent(SensorEvent event){
            if(currentOrientation == null){
                currentOrientation = new float[3];
            }

            currentOrientation[0] = event.values[0];
            currentOrientation[1] = event.values[1];
            currentOrientation[2] = event.values[2];

        }
    };

}
