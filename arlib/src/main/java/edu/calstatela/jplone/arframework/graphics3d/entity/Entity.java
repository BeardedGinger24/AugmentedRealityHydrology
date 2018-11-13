package edu.calstatela.jplone.arframework.graphics3d.entity;

import edu.calstatela.jplone.arframework.graphics3d.drawable.Drawable;
import edu.calstatela.jplone.arframework.graphics3d.matrix.ScaledModelMatrix;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.graphics3d.matrix.MatrixMath;


public class Entity extends ScaledModelMatrix implements Drawable {
    private static final String TAG = "waka-Entity";

    private Drawable mDrawable = null;
    private static float[] tempXYZ = new float[3];

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Additional Methods added onto ScaledModelMatrix
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void setLookAtWithConstantDistanceScale(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ, float scale){
        float distance = (eyeX - centerX) * (eyeX - centerX) + (eyeY - centerY) * (eyeY - centerY) + (eyeZ - centerZ) * (eyeZ - centerZ);
        float newScale = (float)Math.sqrt(distance) * scale;

        setLookAtWithScale(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ, newScale);
    }

    public void setLookAtWithScale(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ, float scale){
        super.lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        super.setScale(scale);
    }

    public void setLatLonAlt(float[] latLonAlt){
        GeoMath.latLonAltToXYZ(latLonAlt, tempXYZ);
        setPosition(tempXYZ[0], tempXYZ[1], tempXYZ[2]);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Methods Related to Drawable
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void setDrawable(Drawable d) {
        mDrawable = d;
    }

    public Drawable getDrawable(){
        return mDrawable;
    }

    @Override
    public void draw(float[] projection, float[] view, float[] model) {
        if (mDrawable == null)
            return;

        mDrawable.draw(projection, view, model);
    }

    @Override
    public void draw(float[] matrix) {
        if (mDrawable == null)
            return;

        mDrawable.draw(matrix);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Calculating Screen Position
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static float[] point = new float[4];
    private static float[] origin = {0, 0, 0, 1};
    private static float[] matrix = new float[16];

    public void getScreenPosition(float[] xy, float[] projection, float[] view, float[] model, float s_width, float s_height){

        MatrixMath.multiply3Matrices(matrix, projection, view, model);
        MatrixMath.multiplyMatrixVec(point, matrix, origin);

        point[0] /= point[3];
        point[1] /= point[3];
        point[2] /= point[3];

        if(point[0] < -1 || point[0] > 1 || point[1] < -1 || point[1] > 1 || point[2] < -1 || point[2] > 1){
            xy[0] = -1;
            xy[1] = -1;
        }
        else {
            xy[0] = (point[0] + 1) / 2 * s_width;
            xy[1] = (-point[1] + 1) / 2 * s_height;
        }
    }
}
