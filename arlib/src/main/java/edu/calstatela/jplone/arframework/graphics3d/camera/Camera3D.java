package edu.calstatela.jplone.arframework.graphics3d.camera;



import android.opengl.GLES20;

import edu.calstatela.jplone.arframework.graphics3d.matrix.Projection;
import edu.calstatela.jplone.arframework.graphics3d.matrix.ViewMatrix;
import edu.calstatela.jplone.arframework.util.GeoMath;

public class Camera3D extends ViewMatrix {

    private static String TAG = "waka-Camera";
    private boolean depthTestEnabled = true;
    private Projection projection = new Projection();

    public Camera3D(){
        setDepthTestEnabled(true);
        setClearColor(0, 0, 0, 0);
    }

    public void setPositionLatLonAlt(float[] latLonAlt){
        float[] xyz = new float[3];
        GeoMath.latLonAltToXYZ(latLonAlt, xyz);
        setPosition(xyz[0], xyz[1], xyz[2]);
    }

    public void setPerspective(float viewAngle, float aspectRatio, float nearDistance, float farDistance){
        projection.setPerspective(viewAngle, aspectRatio, nearDistance, farDistance);
    }

    public float[] getProjectionMatrix(){
        return projection.getProjectionMatrix();
    }

    public void setViewport(int x, int y, int width, int height){
        GLES20.glViewport(x, y, width, height);
    }

    public void setClearColor(float r, float g, float b, float a){
        GLES20.glClearColor(r, g, b, a);
    }

    public void setDepthTestEnabled(boolean enabled){
        depthTestEnabled = enabled;

        if(depthTestEnabled)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        else
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void clear(){
        int clearFlags = GLES20.GL_COLOR_BUFFER_BIT;
        if(depthTestEnabled)
            clearFlags = clearFlags | GLES20.GL_DEPTH_BUFFER_BIT;
        GLES20.glClear(clearFlags);
    }
}
