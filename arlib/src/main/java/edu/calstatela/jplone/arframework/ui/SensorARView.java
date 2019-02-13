package edu.calstatela.jplone.arframework.ui;

import android.content.Context;
import android.hardware.SensorEvent;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import edu.calstatela.jplone.arframework.sensor.ARGps;
import edu.calstatela.jplone.arframework.sensor.ARSensor;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.VectorMath;

public class SensorARView extends ARView {
    private static final String TAG = "waka-SensorARView";

    private ARSensor orientationSensor;
    private ARGps locationSensor;
    private ARSensor rpySensor;
    private float[] currentOrientation;
    private float[] currentLocation;
    private float[] currentrpy = null;
    public String GX;
    public String GY;
    public String GZ;

    public SensorARView(Context context){
        super(context);

        setOnTouchListener(touchListener);
        rpySensor = new ARSensor(context,ARSensor.ORIENTATION);
        rpySensor.addListener(rpyListener);
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

    public float[] getrpy(){
        return currentrpy;
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
        rpySensor.stop();
    }

    public void onResume(){
        super.onResume();
        orientationSensor.start();
        locationSensor.start();
        rpySensor.start();
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
            Log.d(TAG, "Location: " +Float.toString(currentLocation[0]));
            Log.d(TAG, "Location " + Float.toString(currentLocation[1]));
            Log.d(TAG, "Location " + Float.toString(currentLocation[2]));

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
            String TG = Float.toString(event.values[0]);
             GX = Double.toString(event.values[0]* (180/Math.PI));
            GY =Double.toString(event.values[1]* (180/Math.PI));
             GZ = Double.toString(event.values[2]* (180/Math.PI));
//            getGX(Gx);
//            getGY(Gy);
//            getGZ(Gz);
        }
    };


    private ARSensor.Listener rpyListener = new ARSensor.Listener(){
        @Override
        public void onSensorEvent(SensorEvent event){
            if(currentrpy == null){
                currentrpy = new float[3];
            }

            currentrpy[0] = event.values[0];
            currentrpy[1] = event.values[1];
            currentrpy[2] = event.values[2];
           String  GZ2 = Float.toString(event.values[0]);
            String GX2 = Float.toString(event.values[1]/2);
           String GY2 = Float.toString(event.values[2]);
//            Pitch: (90 , -90)
//            Bearing/yaw: (0 – 360) in degrees.
//            roll is -90 to 90
//            pitch is 180 to -180
//            yaw is 0 to 270 or 360??

//            Log.d("ROLL: " , GZ2); // goes from 0 to 360
//            // this might be the yaw
//            Log.d("PITCH: " , GX2); // goes from 90 to -90
//            Log.d("YAW " , GY2); //quadrant 1 && 4 are negative and 2&& 3 are positive  X axis is 90






        }
    };



}
