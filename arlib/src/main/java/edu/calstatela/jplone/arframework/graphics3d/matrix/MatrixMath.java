package edu.calstatela.jplone.arframework.graphics3d.matrix;

import android.opengl.Matrix;

import edu.calstatela.jplone.arframework.util.VectorMath;

public class MatrixMath {

    public static void multiply3Matrices(float[] result, float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix){
        Matrix.multiplyMM(result, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(result, 0, projectionMatrix, 0, result, 0);
    }

    public static void multiply2Matrices(float[] result, float[] leftMatrix, float[] rightMatrix){
        Matrix.multiplyMM(result, 0, leftMatrix, 0, rightMatrix, 0);
    }

    public static void multiplyMatrixVec(float[] resultVec, float[] matrix, float[]inputVec){
        Matrix.multiplyMV(resultVec, 0, matrix, 0, inputVec, 0);
    }

    public static void setIdentity(float[] matrix){
        copy(MatrixMath.IDENTITY_MATRIX, matrix);
    }

    public static void copy(float[] srcMat, float[] destMat){
        VectorMath.copyVec(srcMat, destMat, 16);
    }

    public static final float[] IDENTITY_MATRIX = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    public static String matrixToString(float[] matrix, int m, int n){
        StringBuilder sb = new StringBuilder();
        sb.append("\n======================================\n");

        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                sb.append(String.format("  % .2f  ", matrix[i + j * n]));
            }
            sb.append("\n");
        }

        sb.append("======================================\n");

        return sb.toString();
    }
}
