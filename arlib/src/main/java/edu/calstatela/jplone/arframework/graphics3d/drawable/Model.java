package edu.calstatela.jplone.arframework.graphics3d.drawable;


import android.opengl.GLES20;


import java.nio.FloatBuffer;

import edu.calstatela.jplone.arframework.graphics3d.helper.BufferHelper;
import edu.calstatela.jplone.arframework.graphics3d.helper.ShaderHelper;
import edu.calstatela.jplone.arframework.graphics3d.matrix.MatrixMath;
import edu.calstatela.jplone.arframework.util.VectorMath;


public class Model implements Drawable, Colorable{

    private FloatBuffer mBuffer = null;
    private float[] mColor = {0.0f, 0.8f, 0.0f, 0.1f};

    private static int mShaderProgram = -1;
    private int mNumVertices = 0;
    private int mDrawingMode = GLES20.GL_TRIANGLES;

    private static final int FLOATS_PER_VERTEX = 3;


    private static final String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "uniform mat4 uMVPMatrix;" +
            "void main() " +
            "{" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private static final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() " +
            "{" +
            "  gl_FragColor = vColor;" +
            "}";




    public Model(){
        // find a way to only make this happen once
        mShaderProgram = ShaderHelper.buildShaderProgram(vertexShaderCode, fragmentShaderCode);
    }


    public void setDrawingModeLineStrip(){
        mDrawingMode = GLES20.GL_LINE_STRIP;
    }

    public void setDrawingModeTriangles(){
        mDrawingMode = GLES20.GL_TRIANGLES;
    }


    @Override
    public void draw(float[] MVPMatrix){
        if(mBuffer == null)
            return;

        GLES20.glUseProgram(mShaderProgram);

        int positionAttrib = GLES20.glGetAttribLocation(mShaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionAttrib);
        GLES20.glVertexAttribPointer(positionAttrib, 3, GLES20.GL_FLOAT, false, 3 * 4, mBuffer);

        int matrixUniform = GLES20.glGetUniformLocation(mShaderProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(matrixUniform, 1, false, MVPMatrix, 0);

        int colorUniform = GLES20.glGetUniformLocation(mShaderProgram, "vColor");
        GLES20.glUniform4fv(colorUniform, 1, mColor, 0);

        GLES20.glDrawArrays(mDrawingMode, 0, mNumVertices);

        GLES20.glDisableVertexAttribArray(positionAttrib);
    }


    @Override
    public void draw(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix){
        float[] tempMatrix = new float[16];
        MatrixMath.multiply3Matrices(tempMatrix, projectionMatrix, viewMatrix, modelMatrix);
        draw(tempMatrix);
    }

    @Override
    public void setColor(float[] rgbaVec){
        if(rgbaVec != null && rgbaVec.length == 4)
            VectorMath.copyVec(rgbaVec, mColor, 4);
    }

    @Override
    public void getColor(float[] color) {
        VectorMath.copyVec(mColor, color, 4);
    }

    public void loadVertices(float[] vertexList){
        mNumVertices = vertexList.length / FLOATS_PER_VERTEX;
        mBuffer = BufferHelper.arrayToBuffer(vertexList);
    }


}
