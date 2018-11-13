package edu.calstatela.jplone.arframework.graphics3d.helper;

import edu.calstatela.jplone.arframework.util.VectorMath;

public class MeshHelper {
    public static float[] pyramid(){
        return new float[]{
                -0.5f, 0, -0.5f,
                0.5f, 0, -0.5f,
                0.5f, 0, 0.5f,

                0.5f, 0, 0.5f,
                -0.5f, 0, 0.5f,
                -0.5f, 0, -0.5f,

                0.5f, 0, 0.5f,
                0, 1f, 0,
                -0.5f, 0, 0.5f,

                0.5f, 0, -0.5f,
                0, 1f, 0,
                0.5f, 0, 0.5f,

                -0.5f, 0, -0.5f,
                0, 1f, 0,
                0.5f, 0, -0.5f,

                -0.5f, 0, 0.5f,
                0, 1f, 0,
                -0.5f, 0, -0.5f,
        };
    }

    // Name: calculateNormals(...)
    // Precondition: vertexList != null
    // Precondition: (vertexList.length % 9) == 0
    // Postcondition: creates new array that contains normal vectors for the input vertices
    public static float[] calculateNormals(float[] vertexList){
        float[] normals = new float[vertexList.length];

        float[] vec0 = new float[3];
        float[] vec1 = new float[3];

        float[] normal = new float[3];

        for(int i = 0; i < vertexList.length; i += 9){

            vec0[0] = vertexList[i + 3] - vertexList[i + 0];
            vec0[1] = vertexList[i + 4] - vertexList[i + 1];
            vec0[2] = vertexList[i + 5] - vertexList[i + 2];

            vec1[0] = vertexList[i + 6] - vertexList[i + 0];
            vec1[1] = vertexList[i + 7] - vertexList[i + 1];
            vec1[2] = vertexList[i + 8] - vertexList[i + 2];

            VectorMath.crossProduct(normal, vec0, vec1);
            VectorMath.normalizeInPlace(normal);

            normals[i + 0] = normal[0];
            normals[i + 1] = normal[1];
            normals[i + 2] = normal[2];

            normals[i + 3] = normal[0];
            normals[i + 4] = normal[1];
            normals[i + 5] = normal[2];

            normals[i + 6] = normal[0];
            normals[i + 7] = normal[1];
            normals[i + 8] = normal[2];
        }

        return normals;
    }

    public static float[] triangle(){
        float[] vertices = {
                -0.5f, -0.8f, 0f,
                0.5f, -0.8f, 0f,
                0f, 0.8f, 0f
        };
        return vertices;
    }

    public static float[] square(){
        float[] vertices = {
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,

                -0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
        };

        return vertices;
    }

    public static float[] cube(){
        float[] vertices = {
                // Front Face
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,

                -0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,

                // Back Face
                -0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,

                -0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,

                // Bottom Face
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f,

                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,

                // Top Face
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, -0.5f,

                -0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,

                // Left Face
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,

                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,

                // Right Face
                0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, -0.5f,

                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, -0.5f,
        };

        return vertices;
    }

}
