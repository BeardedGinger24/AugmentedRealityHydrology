package edu.calstatela.jplone.watertrekapp.Data;
public class MeshData {
    public Vector3[] Vertices;
    public int[] Triangles;
    public float UserElevation;
    public float[] latlonalt;
    String dir;
    String filename;
    public MeshData(Vector3[] vertices, int[] triangles,float userElevation) {
        Vertices = vertices;
        Triangles = triangles;
        UserElevation = userElevation;
    }
    public void setFilename(String filename){
        this.filename = filename;
    }
    public String getFilename(){
        return this.filename;
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