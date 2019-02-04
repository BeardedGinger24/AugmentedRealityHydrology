package edu.calstatela.jplone.arframework.graphics3d.drawable;

public class BillboardInfo{

    public BillboardInfo(int id, int iconResource, String title, String text, float lat, float lon, float alt){
        this.id = id; this.iconResource = iconResource;
        this.title = title; this.text = text;
        this.lat = lat; this.lon = lon; this.alt = alt;
    }
    public int id;
    public int iconResource;
    public String title = "";
    public String text = "";
    public float lat;
    public float lon;
    public float alt;
}
