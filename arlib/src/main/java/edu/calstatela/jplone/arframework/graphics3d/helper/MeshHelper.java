package edu.calstatela.jplone.arframework.graphics3d.helper;

import edu.calstatela.jplone.arframework.util.VectorMath;

public class MeshHelper {
    // Name: calculateNormals(...)
    // Precondition: vertexList != null
    // Precondition: (vertexList.length % 9) == 0
    // Postcondition: creates new array that contains normal vectors for the input vertices
    public static StringBuilder calculateNormals(float[] vertexList){
        String[] normals = new String[vertexList.length];

        float[] vec0 = new float[3];
        float[] vec1 = new float[3];

        float[] normal = new float[3];
        StringBuilder n = new StringBuilder();
        for(int i = 0; i < vertexList.length; i += 9){

            vec0[0] = vertexList[i + 3] - vertexList[i + 0];
            vec0[1] = vertexList[i + 4] - vertexList[i + 1];
            vec0[2] = vertexList[i + 5] - vertexList[i + 2];

            vec1[0] = vertexList[i + 6] - vertexList[i + 0];
            vec1[1] = vertexList[i + 7] - vertexList[i + 1];
            vec1[2] = vertexList[i + 8] - vertexList[i + 2];

            VectorMath.crossProduct(normal, vec0, vec1);
            VectorMath.normalizeInPlace(normal);

            normals[i + 0] = String.valueOf(normal[0]);
            normals[i + 1] = String.valueOf(normal[1]);
            normals[i + 2] = String.valueOf(normal[2]);

            normals[i + 3] = String.valueOf(normal[0]);
            normals[i + 4] = String.valueOf(normal[1]);
            normals[i + 5] = String.valueOf(normal[2]);

            normals[i + 6] = String.valueOf(normal[0]);
            normals[i + 7] = String.valueOf(normal[1]);
            normals[i + 8] = String.valueOf(normal[2]);
            n.append("vn "+normal[0]+" "+normal[1]+" "+normal[2]+"\n");
            n.append("vn "+normal[0]+" "+normal[1]+" "+normal[2]+"\n");
            n.append("vn "+normal[0]+" "+normal[1]+" "+normal[2]+"\n");
        }

        return n;
    }
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
