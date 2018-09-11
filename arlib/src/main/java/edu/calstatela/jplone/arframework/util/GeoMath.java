package edu.calstatela.jplone.arframework.util;

// Issues:
//  - The calculations from this class for converting between xyz and latLonAlt
//    have significant (yet possibly acceptable) errors due
//    to precision of float. These problems will disappear if variables changed to double.

import android.location.Location;

public class GeoMath {

    public static float metersPerDegreeLat = 111111;
    public static float metersPerDegreeLon = 111111;
    private static float[] referenceLLA = {34, -117, 0};

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public static void setReference(float[] refLLA){
        referenceLLA[0] = refLLA[0];
        referenceLLA[1] = refLLA[1];
        referenceLLA[2] = refLLA[2];
        updateMetersPerDegree(refLLA[0]);
    }

    private static void updateMetersPerDegree(float lat){
        metersPerDegreeLon = (float)(111111 * Math.cos(lat * 2 * Math.PI / 360));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public static void latLonAltToXYZ(float[] latLonAlt, float[] xyz){
        xyz[0] = (latLonAlt[1] - referenceLLA[1]) * metersPerDegreeLon;
        xyz[1] = latLonAlt[2] - referenceLLA[2];
        xyz[2] = (referenceLLA[0] - latLonAlt[0]) * metersPerDegreeLat;
    }

    public static void xyzToLatLonAlt(float[] xyz, float[] latLonAlt){
        latLonAlt[0] = -xyz[2] / metersPerDegreeLat + referenceLLA[0];
        latLonAlt[1] = xyz[0] / metersPerDegreeLon + referenceLLA[1];
        latLonAlt[2] = xyz[1] + referenceLLA[2];
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public static float xyzDistance(float[] xyz1, float[] xyz2){
        float xDiff = xyz2[0] - xyz1[0];
        float zDiff = xyz2[2] - xyz1[2];
        return (float) Math.sqrt(xDiff * xDiff + zDiff * zDiff);
    }

    public static float xyzBearing(float[] xyz1, float[] xyz2){
        float opposite = xyz2[0] - xyz1[0];
        float adjacent = (xyz1[2] - xyz2[2]);

        if(adjacent < 0.1f && adjacent > -0.1f) {
            if(opposite > 0)
                return 90;
            else
                return 270;
        }

        float theta = (float) (Math.atan( opposite / adjacent ) * 180 / Math.PI);

        if(adjacent < 0)
            theta += 180;
        else if(opposite < 0)
            theta += 360;

        return theta;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public static float llaDistance(float[] lla1, float[] lla2){
        float xDiff = (lla2[1] - lla1[1]) * metersPerDegreeLon;
        float zDiff = (lla1[0] - lla2[0]) * metersPerDegreeLat;
        return (float) Math.sqrt(xDiff * xDiff + zDiff * zDiff);
    }

    public static float llaBearing(float[] lla1, float[] lla2){
        float opposite = (lla2[1] - lla1[1]) * metersPerDegreeLon;
        float adjacent = (lla2[0] - lla1[0]) * metersPerDegreeLat;

        if(adjacent == 0) {
            if(opposite > 0)
                return 90;
            else
                return 270;
        }

        float theta =  (float) (Math.atan( opposite / adjacent ) * 180 / Math.PI);

        if(adjacent < 0)
            theta += 180;
        else if(opposite < 0)
            theta += 360;

        return theta;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static Location makeLocation(float[] lla){
        Location l = new Location("");
        l.setLatitude(lla[0]);
        l.setLongitude(lla[1]);
        l.setAltitude(lla[2]);
        return l;
    }

    public static float LocationBearing(float[] lla1, float[] lla2){
        Location l1 = makeLocation(lla1);
        Location l2 = makeLocation(lla2);
        return l1.bearingTo(l2);
    }

    public static float locationDistance(float[] lla1, float[] lla2){
        Location l1 = makeLocation(lla1);
        Location l2 = makeLocation(lla2);
        return l1.distanceTo(l2);
    }

    public static float locationElevation(float[] lla1, float[] lla2){
        Location l1 = makeLocation(lla1);
        Location l2 = makeLocation(lla2);
        return VectorMath.radToDegrees( (float) Math.atan( (lla2[2] - lla1[2]) / locationDistance(lla1, lla2) ) );
    }


}
