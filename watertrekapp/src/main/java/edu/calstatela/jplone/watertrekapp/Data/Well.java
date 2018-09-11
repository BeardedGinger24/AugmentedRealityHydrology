package edu.calstatela.jplone.watertrekapp.Data;

import java.util.List;


/**
 * Created by nes on 2/13/18.
 */

//https://watertrek.jpl.nasa.gov/hydrology/rest/reservoir/site_no

//master_site_id	casgem_station_id	state_well_nbr	site_code	lat	lon
//min max count avg stddev

public class Well {
    public static final int TYPE_ID = 1;
    public static final int DBGS_ID = 2;
    public static final int AVG_ID = 3;
    public static final int MAX_ID = 4;
    public static final int MIN_ID = 5;
    public static final int STD_ID = 6;

    //Primary Fields
    private String masterSiteId,
            casgemStationId,
            stateWellNbr,
            siteCode,
            lat,
            lon;

    //Addtl. Fields
    private String min,max,count, avg, stdDev;
    private List<String> dbgs;

    //No args
    Well(){

    }

    public Well(String[] values){
        this.masterSiteId = values[0];
        this.casgemStationId = values[1];
        this.stateWellNbr = values[2];
        this.siteCode = values[3];
        this.lat = values [4];
        this.lon = values[5];
    }


    public String getMasterSiteId() {
        return masterSiteId;
    }

    public void setMasterSiteId(String masterSiteId) {
        this.masterSiteId = masterSiteId;
    }

    public String getCasgemStationId() {
        return casgemStationId;
    }

    public void setCasgemStationId(String casgemStationId) {
        this.casgemStationId = casgemStationId;
    }

    public String getStateWellNbr() {
        return stateWellNbr;
    }

    public void setStateWellNbr(String stateWellNbr) {
        this.stateWellNbr = stateWellNbr;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    //Addtl. Methods
    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getStdDev() {
        return stdDev;
    }

    public void setStdDev(String stdDev) {
        this.stdDev = stdDev;
    }

    public List getDbgs(){
        return dbgs;
    }
    public void setDbgs(List dbgs){
        this.dbgs = dbgs;
    }

    public String toString() {
        String ret = "";
        ret += "type:            well\n";
        ret += "masterSiteId:    " + masterSiteId + "\n";
        ret += "casgemStationId: " + casgemStationId + "\n";
        ret += "stateWellNbr:    " + stateWellNbr + "\n";
        ret += "siteCode:        " + siteCode + "\n";
        ret += "lat:             " + lat + "\n";
        ret += "lon:             " + lon + "\n";

        return ret;
    }
}
