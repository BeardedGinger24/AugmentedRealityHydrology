package edu.calstatela.jplone.watertrekapp.Data;
public class MeshData {
    public Vector3[] Vertices;
    public int[] Triangles;
    public MeshData(Vector3[] vertices, int[] triangles) {
        Vertices = vertices;
        Triangles = triangles;
    }
}