package edu.calstatela.jplone.arframework.graphics3d.matrix;

import edu.calstatela.jplone.arframework.graphics3d.matrix.MatrixMath;
import edu.calstatela.jplone.arframework.util.VectorMath;

public class ImprovedModelMatrix {
    private static final String TAG = "waka-ModelMatrix";

    private float[] mMatrixArray = new float[16];
    private boolean matrixIsClean = false;

    // These vectors are used to keep track of the current position and orientation of the object
    private float[] position = {0, 0, 0, 1};
    private float[] u = {1, 0, 0};
    private float[] v = {0, 1, 0};
    private float[] w = {0, 0, 1};

    // Temporary vectors for matrix calculations
    private static float[] up = new float[3];
    private static float[] t = new float[3];

    ////////////////////////////////////////////////////
    //
    //      Public Methods originally in ModelMatrix
    //
    ////////////////////////////////////////////////////

    public void lookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){
        setPosition(eyeX, eyeY, eyeZ);

        // Find u, v, w axes of object (u == new x-axis, v == new y-axis, w == new z-axis)
            // w is Normalize(center - eye)
            // u is Normalize(up x w)
            // v is w x u
        w[0] = centerX - eyeX;
        w[1] = centerY - eyeY;
        w[2] = centerZ - eyeZ;

        if(w[0] == 0 && w[1] == 0 && w[2] == 0)
            w[1] = 1;
        else
            VectorMath.normalizeInPlace(w);

        up[0] = upX;
        up[1] = upY;
        up[2] = upZ;
        VectorMath.crossProduct(u, up, w);
        VectorMath.normalizeInPlace(u);

        VectorMath.crossProduct(v, w, u);

        matrixIsClean = false;
    }

    public void setPosition(float x, float y, float z){
        position[0] = x;
        position[1] = y;
        position[2] = z;

        matrixIsClean = false;
    }

    public float[] getPosition(){
        return position;
    }

    public void lookAtPoint(float centerX, float centerY, float centerZ){
        lookAt(position[0], position[1], position[2], centerX, centerY, centerZ, 0, 1, 0);
    }

    public void lookAtPointUpright(float centerX, float centerY, float centerZ){
        lookAt(position[0], position[1], position[2], centerX, position[1], centerZ, 0, 1, 0);
    }

    public float[] getModelMatrix(){
        if(!matrixIsClean)
            cleanMatrix();

        return mMatrixArray;
    }

    private void cleanMatrix(){
        mMatrixArray[0] = u[0];   mMatrixArray[4] = v[0];   mMatrixArray[8] = w[0];   mMatrixArray[12] = position[0];
        mMatrixArray[1] = u[1];   mMatrixArray[5] = v[1];   mMatrixArray[9] = w[1];   mMatrixArray[13] = position[1];
        mMatrixArray[2] = u[2];   mMatrixArray[6] = v[2];   mMatrixArray[10] = w[2];  mMatrixArray[14] = position[2];
        mMatrixArray[3] = 0;      mMatrixArray[7] = 0;      mMatrixArray[11] = 0;     mMatrixArray[15] = 1;

        matrixIsClean = true;
    }

    ////////////////////////////////////////////////////
    //
    //      Public Methods added for ImprovedModelMatrix
    //
    ////////////////////////////////////////////////////

    public void reset(){
        MatrixMath.setIdentity(mMatrixArray);
        setPosition(0, 0, 0);
        u[0] = 1; u[0] = 0; u[2] = 0;
        v[0] = 0; v[1] = 1; v[2] = 0;
        w[0] = 0; w[1] = 0; w[2] = 1;

        matrixIsClean = false;
    }

    public void move(float dx, float dy, float dz){
        position[0] += dx;
        position[1] += dy;
        position[2] += dz;

        matrixIsClean = false;
    }

    public void slide(float du, float dv, float dw){
        // position = du * u + dv * v + dz * z
        position[0] += du * u[0] + dv * v[0] + dw * w[0];
        position[1] += du * u[1] + dv * v[1] + dw * w[1];
        position[2] += du * u[2] + dv * v[2] + dw * w[2];

        matrixIsClean = false;
    }

    public void pitch(float angle){
        // rotation around the u-axis
        angle = -VectorMath.degreesToRad(angle);
        float cs = (float)Math.cos(angle);
        float sn = (float)Math.sin(angle);

        VectorMath.copyVec(v, t, 3);

        v[0] = cs * t[0] + sn * w[0];
        v[1] = cs * t[1] + sn * w[1];
        v[2] = cs * t[2] + sn * w[2];

        w[0] = -sn * t[0] + cs * w[0];
        w[1] = -sn * t[1] + cs * w[1];
        w[2] = -sn * t[2] + cs * w[2];

        matrixIsClean = false;
    }

    public void yaw(float angle){
        // rotation around the v-axis
        angle = -VectorMath.degreesToRad(angle);
        float cs = (float)Math.cos(angle);
        float sn = (float)Math.sin(angle);

        VectorMath.copyVec(u, t, 3);

        u[0] = cs * t[0] + sn * w[0];
        u[1] = cs * t[1] + sn * w[1];
        u[2] = cs * t[2] + sn * w[2];

        w[0] = -sn * t[0] + cs * w[0];
        w[1] = -sn * t[1] + cs * w[1];
        w[2] = -sn * t[2] + cs * w[2];

        matrixIsClean = false;
    }

    public void roll(float angle){
        // rotation around the w-axis
        angle = VectorMath.degreesToRad(angle);
        float cs = (float)Math.cos(angle);
        float sn = (float)Math.sin(angle);

        VectorMath.copyVec(u, t, 3);

        u[0] = cs * t[0] + sn * v[0];
        u[1] = cs * t[1] + sn * v[1];
        u[2] = cs * t[2] + sn * v[2];

        v[0] = -sn * t[0] + cs * v[0];
        v[1] = -sn * t[1] + cs * v[1];
        v[2] = -sn * t[2] + cs * v[2];

        matrixIsClean = false;
    }

}
