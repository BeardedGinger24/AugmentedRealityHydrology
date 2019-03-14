package edu.calstatela.jplone.watertrekapp.Data;

import edu.calstatela.jplone.arframework.util.Vector3;

public class MeshData {
    public Vector3[] Vectors;
    public int[] Triangles;
    public float[] latlonalt;

    String dir;
    String filenameTerrain;
    String filenameVecs;

    public MeshData(Vector3[] vector3s, int[] triangles) {
        Vectors = vector3s;
        Triangles = triangles;
    }
    public void setFilenameTerrain(String filename){
        this.filenameTerrain = filename;
    }
    public String getFilenameTerrain(){
        return this.filenameTerrain;
    }
    public void setFilenameTerrainVecs(String filename){
        this.filenameVecs = filename;
    }
    public String getFilenameTerrainVecs(){
        return this.filenameVecs;
    }
    public void setDir(String dir){
        this.dir = dir;
    }
    public String getDir(){
        return this.dir;
    }
    public void setLatlonAlt(float[] latlonalt) {
        this.latlonalt = latlonalt;
    }
    public float[] getLatlonAlt(){
        return this.latlonalt;
    }
}