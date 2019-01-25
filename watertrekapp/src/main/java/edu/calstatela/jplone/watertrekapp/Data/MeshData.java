package edu.calstatela.jplone.watertrekapp.Data;
public class MeshData {
    public Vector3[] Vertices;
    public int[] Triangles;
    public float UserElevation;
    public float[] latlonalt;
    public MeshData(Vector3[] vertices, int[] triangles,float userElevation) {
        Vertices = vertices;
        Triangles = triangles;
        UserElevation = userElevation+0.001f;
    }

    public void setLatlonAlt(float[] latlonalt) {
        this.latlonalt = latlonalt;
    }
    public float[] getLatlonAlt(){
        return this.latlonalt;
    }
}