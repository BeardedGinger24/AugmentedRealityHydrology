package edu.calstatela.jplone.watertrekapp.Data;


import java.util.Date;

/**
 * Created by kz on 4/24/18.
 */

public class Record {
    double lat,lon;
    int id;
    Date date;

    public Record(){

    }
    public Record(String[] string){
        this.lat = Double.parseDouble(string[0]);
        this.lon = Double.parseDouble(string[1]);
        this.id = Integer.parseInt(string[2]);
        this.date = new Date(string[3]);
    }
    public Date getDate() {
        return date;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getType_id() {
        return id;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public void setDate(String date){
        this.date = new Date(date);
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public void setLat(String lat){
        this.lat = Double.parseDouble(lat);
    }
    public void setLon(double lon) {
        this.lon = lon;
    }
    public void setLon(String lon){
        this.lon = Double.parseDouble(lon);
    }
    public void setID(int type_id) {
        this.id = type_id;
    }
    public void setID(String type_id){
        this.id = Integer.parseInt(type_id);
    }
}
