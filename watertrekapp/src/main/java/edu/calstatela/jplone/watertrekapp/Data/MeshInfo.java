package edu.calstatela.jplone.watertrekapp.Data;

import android.graphics.Bitmap;
import edu.calstatela.jplone.arframework.util.Vector3;
public class MeshInfo {
    Vector3[] vecs;
    String type;
    float[] latlonalt;
    Bitmap bmp;
    float[] verts;
    float[] textCoordinates;

    public MeshInfo(Vector3[] vecs,String type,float[] latlonalt,float[] verts) {
        this.vecs = vecs;
        this.type = type;
        this.latlonalt = latlonalt;
        this.verts = verts;
    }

    public float[] getVerts() {
        return verts;
    }

    public void setVerts(float[] verts) {
        this.verts = verts;
    }

    public float[] getLatlonalt() {
        return latlonalt;
    }

    public void setLatlonalt(float[] latlonalt) {
        this.latlonalt = latlonalt;
    }

    public Vector3[] getVecs() {
        return vecs;
    }

    public void setVecs(Vector3[] vecs) {
        this.vecs = vecs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBmp(Bitmap bmp){
        this.bmp = bmp;
    }
    public Bitmap getBmp(){
        return bmp;
    }
    public void setTextCoordinates(float[] floats){
        this.textCoordinates = floats;
    }
    public float[] getTextCoordinates(){
        return textCoordinates;
    }
}