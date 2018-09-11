package edu.calstatela.jplone.arframework.util;

import android.app.Activity;
import android.view.Surface;

public class Orientation {
    public static int getOrientationAngle(Activity activity){
        int orientationCode = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch(orientationCode){
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return -1;
    }
}
