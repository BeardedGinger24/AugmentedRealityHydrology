package edu.calstatela.jplone.watertrekapp.Data;

/**
 * Created by nes on 3/15/18.
 */

public class Snotel {
    String stationId;
    String lon;
    String lat;
    String date;
    String swe;
    String units;


    public static final int TYPE_ID = 12;
    public static final int ADDTL_ID = 13;


    public Snotel(String[] values){
        this.stationId = values[0];
        this.lon = values[1];
        this.lat = values[2];
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSwe() {
        return swe;
    }

    public void setSwe(String swe) {
        this.swe = swe;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
