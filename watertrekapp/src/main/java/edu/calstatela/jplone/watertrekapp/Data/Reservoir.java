package edu.calstatela.jplone.watertrekapp.Data;

/**
 * Created by nes on 2/11/18.
 *///package edu.calstatela.jplone.ardemo;

import java.util.List;

public class Reservoir {

    String siteNo,
            desc,
            lon,
            lat,
            maxDate,
            minDate,
            max,
            min;

    public static final int TYPE_ID = 9;
    public static final int ADDTL_ID = 10;

    List<String> storageValues;


    //Arr args
    public Reservoir(String[] values){
        //Strings
        this.siteNo = values[0];
        this.desc = values[1];
        this.lon = values[2];
        this.lat = values[3];
        this.maxDate = values[4];
        this.minDate = values[5];
        this.max = values[6];
        this.min = values[7];

    }

    public String getSiteNo() {
        return siteNo;
    }

    public void setSiteNo(String siteNo) {
        this.siteNo = siteNo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    public String getMinDate() {
        return minDate;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    //Addtl. Fields
    public List getStorageValues() {
        return storageValues;
    }

    public void setStorageValues(List values) {
        this.storageValues = values;
    }

    public String toString() {
        String ret = "";
        ret += "type: reservoir\n";
        ret += "siteNo: " + siteNo + "\n";
        ret += "desc: " + desc + "\n";
        ret += "lat: " + lat + "\n";
        ret += "lon: " + lon + "\n";
        ret += "maxDate: " + maxDate + "\n";
        ret += "minDate: " + minDate + "\n";
        ret += "max: " + max + "\n";
        ret += "min: " + min + "\n";

        return ret;
    }
}



