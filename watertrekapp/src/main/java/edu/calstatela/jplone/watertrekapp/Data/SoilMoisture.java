package edu.calstatela.jplone.watertrekapp.Data;

/**
 * Created by kz on 2/27/18.
 */

public class SoilMoisture {
    private String wbanno, lon, lat, max, min, info;
    public static final int TYPE_ID = 7;
    public static final int ADDTL_ID = 8;

    SoilMoisture(){

    }
    public SoilMoisture(String[] values)
    {
        this.wbanno = values[0];
        this.lon = values[1];
        this.lat = values[2];
        this.max = values[3];
        this.min = values[4];
    }

    public String getInfo(){return info; }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getMax() {
        return max;
    }

    public String getMin() {
        return min;
    }

    public String getWbanno() {
        return wbanno;
    }

    public void setInfo(String info) {this.info = info; }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public void setWbanno(String wbanno) {
        this.wbanno = wbanno;
    }

    public String toString() {
        String ret = "";
        ret += "type: SoilMoisture\n";
        ret += "wbanno: " + wbanno + "\n";
        ret += "lat: " + lat + "\n";
        ret += "lon: " + lon + "\n";
        ret += "max: " + max + "\n";
        ret += "min: " + min + "\n";

        return ret;
    }
}
