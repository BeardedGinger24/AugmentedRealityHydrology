package edu.calstatela.jplone.arframework.graphics3d.entity;

import android.opengl.Matrix;

import edu.calstatela.jplone.arframework.graphics3d.drawable.Drawable;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.MatrixMath;
import edu.calstatela.jplone.arframework.util.VectorMath;


public class Entity extends Drawable{
    private static final String TAG = "waka-Entity";

    private float[] scale = {1, 1, 1};
    private float[] pos = new float[3];
    private float[] latLonAlt = new float[3];
    private float yawAngle, pitchAngle, rollAngle;
    private float[] modelMatrix = new float[16];

    private Drawable drawable = null;
    private float[] color = null;

    private boolean matrixIsClean = false;
    private boolean latLonAltIsClean = false;

    private static float[] tempMatrix = new float[16];
    private static float[] tempMatrix2 = new float[16];

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void reset() {
        scale[0] = scale[1] = scale[2] = 1;
        pos[0] = pos[1] = pos[2] = 0;
        yawAngle = 0;
        matrixIsClean = false;
        latLonAltIsClean = false;
    }


    public void setLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){

        Matrix.setLookAtM(tempMatrix, 0, eyeX, eyeY, eyeZ, 2 * eyeX - centerX, 2 * eyeY - centerY, 2 * eyeZ - centerZ, upX, upY, upZ);
        Matrix.invertM(modelMatrix, 0, tempMatrix, 0);

        matrixIsClean = true;
    }

    public void setLookAtWithConstantDistanceScale(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ, float scale){

        float distance = (eyeX - centerX) * (eyeX - centerX) + (eyeY - centerY) * (eyeY - centerY) + (eyeZ - centerZ) * (eyeZ - centerZ);
        float newScale = (float)Math.sqrt(distance) * scale;

        setLookAtWithScale(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ, newScale);
    }

    public void setLookAtWithScale(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ, float scale){

        Matrix.setLookAtM(modelMatrix, 0, eyeX, eyeY, eyeZ, 2 * eyeX - centerX, 2 * eyeY - centerY, 2 * eyeZ - centerZ, upX, upY, upZ);
        Matrix.invertM(tempMatrix, 0, modelMatrix, 0);

        Matrix.scaleM(tempMatrix2, 0, MatrixMath.IDENTITY_MATRIX, 0, scale, scale, scale);
        MatrixMath.multiply2Matrices(modelMatrix, tempMatrix, tempMatrix2);

        matrixIsClean = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setScale(float x, float y, float z) {
        this.scale[0] = x;
        this.scale[1] = y;
        this.scale[2] = z;
        matrixIsClean = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setPosition(float x, float y, float z) {
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
        matrixIsClean = false;
        latLonAltIsClean = false;
    }

    public void move(float dx, float dy, float dz) {
        pos[0] += dx;
        pos[1] += dy;
        pos[2] += dz;
        matrixIsClean = false;
        latLonAltIsClean = false;
    }

    public void slide(float dx, float dy, float dz) {
        pos[0] += dx * Math.cos(VectorMath.degreesToRad(yawAngle)) + dz * Math.sin(VectorMath.degreesToRad(yawAngle));
        pos[1] += dy;
        pos[2] += dz * Math.cos(VectorMath.degreesToRad(yawAngle)) - dx * Math.sin(VectorMath.degreesToRad(yawAngle));
        matrixIsClean = false;
        latLonAltIsClean = false;
    }

    public float[] getPosition() {
        return pos;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setLatLonAlt(float[] latLonAlt) {
        VectorMath.copyVec(latLonAlt, this.latLonAlt, 3);
        latLonAltIsClean = true;
        GeoMath.latLonAltToXYZ(latLonAlt, pos);
        matrixIsClean = false;
    }

    public float[] getLatLonAlt() {
        if (!latLonAltIsClean) {
            GeoMath.xyzToLatLonAlt(pos, latLonAlt);
            latLonAltIsClean = true;
        }

        return latLonAlt;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setYaw(float angle) {
        yawAngle = angle;
        matrixIsClean = false;
    }

    public void yaw(float dAngle) {
        yawAngle += dAngle;
        matrixIsClean = false;
    }

    public float getYaw() {
        return yawAngle;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setPitch(float angle){
        pitchAngle = angle;
        matrixIsClean = false;
    }

    public void pitch(float dAngle){
        pitchAngle += dAngle;
        matrixIsClean = false;
    }

    public float getPitch(){ return pitchAngle; }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public float[] getModelMatrix() {
        if (!matrixIsClean)
            updateModelMatrix();

        return modelMatrix;
    }

    private void updateModelMatrix() {
        Matrix.translateM(modelMatrix, 0, MatrixMath.IDENTITY_MATRIX, 0, pos[0], pos[1], pos[2]);
        Matrix.rotateM(modelMatrix, 0, yawAngle, 0, 1, 0);
        Matrix.scaleM(modelMatrix, 0, scale[0], scale[1], scale[2]);

        matrixIsClean = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setDrawable(Drawable d) {
        drawable = d;
    }

    @Override
    public void setColor(float[] color) {
        if (color == null || color.length != 4) {
            this.color = null;
            return;
        }

        if (this.color == null)
            this.color = new float[4];

        VectorMath.copyVec(color, this.color, 4);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void draw(float[] projection, float[] view, float[] model) {
        if (drawable == null)
            return;

        if (color != null) {
            drawable.drawColor(projection, view, model, color);
        }

        drawable.draw(projection, view, model);
    }

    @Override
    public void draw(float[] matrix) {
        if (drawable == null)
            return;

        if (color != null)
            drawable.drawColor(matrix, color);

        drawable.draw(matrix);
    }
}
