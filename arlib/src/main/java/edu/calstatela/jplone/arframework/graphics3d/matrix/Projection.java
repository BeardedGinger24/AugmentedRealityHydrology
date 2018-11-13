package edu.calstatela.jplone.arframework.graphics3d.matrix;

import android.opengl.Matrix;

public class Projection {

    private final float[] projectionMatrix = new float[16];

    public Projection(){
        MatrixMath.setIdentity(projectionMatrix);
    }

    public void setPerspective(float viewAngle, float aspectRatio, float nearDistance, float farDistance){
        Matrix.perspectiveM(projectionMatrix, 0, viewAngle, aspectRatio, nearDistance, farDistance);
    }

    public float[] getProjectionMatrix(){
        return projectionMatrix;
    }

}
