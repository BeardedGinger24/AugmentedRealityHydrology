package edu.calstatela.jplone.arframework.graphics3d.entity;

import edu.calstatela.jplone.arframework.graphics3d.drawable.Drawable;

public class ScaleObject implements Drawable{

    private float[] scale = {1, 1, 1};
    private float[] tempMatrix = new float[16];
    private Drawable drawable = null;

    public ScaleObject(){}
    public ScaleObject(Drawable d){
        drawable = d;
    }
    public ScaleObject(Drawable d, float x, float y, float z){
        drawable = d;
        setScale(x, y, z);
    }

    public void setScale(float x, float y, float z){
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;
    }

    public void setScale(float scale){
        setScale(scale, scale, scale);
    }

    public void setDrawable(Drawable d){
        drawable = d;
    }

    @Override
    public void draw(float[] matrix) {
        if(drawable == null)
            return;

        matrixTimesScale(tempMatrix, matrix, scale);
        drawable.draw(tempMatrix);
    }

    @Override
    public void draw(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix) {
        if(drawable == null)
            return;

        matrixTimesScale(tempMatrix, modelMatrix, scale);
        drawable.draw(projectionMatrix, viewMatrix, tempMatrix);
    }

    private static void matrixTimesScale(float[] result, float[] m, float[] s){
        result[0] = m[0] * s[0];    result[4] = m[4] * s[1];    result[8] = m[8] * s[2];    result[12] = m[12];
        result[1] = m[1] * s[0];    result[5] = m[5] * s[1];    result[9] = m[9] * s[2];    result[13] = m[13];
        result[2] = m[2] * s[0];    result[6] = m[6] * s[1];    result[10] = m[10] * s[2];  result[14] = m[14];
        result[3] = m[3] * s[0];    result[7] = m[7] * s[1];    result[11] = m[11] * s[2];  result[15] = m[15];
    }
}
