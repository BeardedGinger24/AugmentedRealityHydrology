package edu.calstatela.jplone.watertrekapp.Data;

import java.util.List;

/**
 * Created by nes on 4/23/18.
 */

public class River {

    public static final int TYPE_ID = 69;
    public static final int DISCHARGE_UNITS = 70;
    public static final int AVGFLUX = 71;
    private String comId,lengthKm, shapeLength,fType;
    private List Multiyline;
    private String siteTpCd;
    private String state;
    private String lat;
    private String lon;


    //Primary Fields
    private String siteNo;
    private String stationName;






    // no args
    River(){

    }
    public River(String[] values){
        this.siteNo = values[0];
        this.stationName = values[1];
        this.siteTpCd = values[2];
        this.state = values[3];
        this.lat = values [4];
        this.lon = values[5];
    }

    public static int getTypeId() {
        return TYPE_ID;
    }
    public String getSiteNo() {
        return siteNo;
    }

    public void setSiteNo(String siteNo) {
        this.siteNo = siteNo;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getSiteTpCd() {
        return siteTpCd;
    }

    public void setSiteTpCd(String siteTpCd) {
        this.siteTpCd = siteTpCd;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getLengthKm() {
        return lengthKm;
    }

    public void setLengthKm(String lengthKm) {
        this.lengthKm = lengthKm;
    }

    public String getShapeLength() {
        return shapeLength;
    }

    public void setShapeLength(String shapeLength) {
        this.shapeLength = shapeLength;
    }

    public String getfType() {
        return fType;
    }

    public void setfType(String fType) {
        this.fType = fType;
    }

    public List getMultiyline() {
        return Multiyline;
    }

    public void setMultiyline(List multiyline) {
        Multiyline = multiyline;
    }

    public String toString() {
        String ret = "";
        ret += "Site_no:  " +  siteNo +        "\n";
        ret += "Station_Name:    " + stationName + "\n";
        ret += "Site_Tp_Cd: " + siteTpCd + "\n";
        ret += "State:    " + state + "\n";
        ret += "lattitude:             " + lat + "\n";
        ret += "longitude:             " + lon + "\n";

        return ret;
    }
}
