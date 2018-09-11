package edu.calstatela.jplone.arframework.graphics3d.matrix;


import edu.calstatela.jplone.arframework.util.VectorMath;

public class ModelMatrix {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Private Member Variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private float[] position = new float[4];
    private float[] mMatrixArray = new float[16];

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Private Static Variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static final String TAG = "waka-ModelMatrix";

    // temporary vectors for lookAt calculations
    private static float[] u = new float[3];
    private static float[] v = new float[3];
    private static float[] w = new float[3];
    private static float[] up = new float[3];

    // temporary matrices for lookAt calculations
    private static float[] rotationMatrix = new float[16];
    private static float[] translationMatrix = new float[16];



    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Public Interface
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void lookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){
        setPosition(eyeX, eyeY, eyeZ);

        // Find u, v, w axes of object (u == new x-axis, v == new y-axis, w == new z-axis)
        //      w is Normalize(center - eye)
        //      u is Normalize(up x w)
        //      v is w x u
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

        // Use u, v, w to build matrix
        //  [ ux  uy  uz  eyeX ]
        //  [ vx  vy  vz  eyeY ]
        //  [ wx  wy  wz  eyeZ ]
        //  [ 0   0   0    1   ]
        mMatrixArray[0] = u[0];   mMatrixArray[4] = v[0];   mMatrixArray[8] = w[0];   mMatrixArray[12] = eyeX;
        mMatrixArray[1] = u[1];   mMatrixArray[5] = v[1];   mMatrixArray[9] = w[1];   mMatrixArray[13] = eyeY;
        mMatrixArray[2] = u[2];   mMatrixArray[6] = v[2];   mMatrixArray[10] = w[2];  mMatrixArray[14] = eyeZ;
        mMatrixArray[3] = 0;      mMatrixArray[7] = 0;      mMatrixArray[11] = 0;     mMatrixArray[15] = 1;
    }
    
    public void setPosition(float x, float y, float z){
        position[0] = x;
        position[1] = y;
        position[2] = z;
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
        return mMatrixArray;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Add these methods later (maybe in subclass)
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // public void reset()
    // public void move(float x, float y, float z)
    // public void slide(float dx, float dy, float dz)
    // public void pitch(float angle)
    // public void yaw(float angle)
    // public void roll(float angle)
}
