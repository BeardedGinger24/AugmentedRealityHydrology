package edu.calstatela.jplone.arframework.landmark;

import android.location.Location;

public class Landmark {
    public String title;
    public String description;
    public float latitude;
    public float longitude;
    public float altitude;

    public Landmark(String title, String description, float latitude, float longitude, float altitude){
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public float distance(Landmark other){
        l1.setLatitude(latitude);
        l1.setLongitude(longitude);
        l2.setLatitude(other.latitude);
        l2.setLongitude(other.longitude);
        return l1.distanceTo(l2);
    }

    public float compassDirection(Landmark other){
        l1.setLatitude(latitude);
        l1.setLongitude(longitude);
        l2.setLatitude(other.latitude);
        l2.setLongitude(other.longitude);
        return l1.bearingTo(l2);
    }

    public boolean compare(Landmark landmark) {
        boolean equal = true;

        if(!this.title.equals(landmark.title))
            equal = false;
        if(!this.description.equals(landmark.description))
            equal = false;
        if(Math.abs(this.latitude - landmark.latitude) > 0.0001)
            equal = false;
        if(Math.abs(this.longitude - landmark.longitude) > 0.0001)
            equal = false;
        if(Math.abs(this.altitude - landmark.altitude) > 0.001)
            equal = false;

        return equal;
    }

    private static Location l1 = new Location("");
    private static Location l2 = new Location("");
}
