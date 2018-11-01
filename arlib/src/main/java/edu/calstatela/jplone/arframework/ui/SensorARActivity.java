package edu.calstatela.jplone.arframework.ui;

import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import edu.calstatela.jplone.arframework.sensor.ARGps;
import edu.calstatela.jplone.arframework.sensor.ARSensor;
import edu.calstatela.jplone.arframework.util.GeoMath;

public class SensorARActivity extends ARActivity {

    private ARSensor orientationSensor;
    private ARSensor rpySensor;
    private ARGps locationSensor;
    private float[] currentOrientation = null;
    private float[] currentLocation = null;
    private float[] currentrpy = null;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Activity Callbacks
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orientationSensor = new ARSensor(this, ARSensor.ROTATION_VECTOR);
        orientationSensor.addListener(orientationListener);
        rpySensor = new ARSensor(this,ARSensor.ORIENTATION);
        rpySensor.addListener(rpyListener);

        locationSensor = new ARGps(this);
        locationSensor.addListener(locationListener);


        getARView().setOnTouchListener(touchListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationSensor.stop();
        locationSensor.stop();
    }

    @Override
    protected void onResume() {
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

    private View.OnTouchListener touchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return SensorARActivity.this.onTouch(v, event);
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

    private ARSensor.Listener rpyListener = new ARSensor.Listener(){
        @Override
        public void onSensorEvent(SensorEvent event){
            if(currentrpy == null){
                currentrpy = new float[3];
            }

            currentrpy[0] = event.values[0];
            currentrpy[1] = event.values[1];
            currentrpy[2] = event.values[2];

        }
    };


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Access Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public float[] getOrientation(){
        return currentOrientation;
    }

    public float[] getrpy(){
        return currentrpy;
    }


    public float[] getLocation(){
        return currentLocation;
    }


}

// Issues
//  * This class repeats code (all the sensor and onTouch code is the same as the sensor and onTouch
//      code in SensorARView). Should find a way to remove the duplicate code.