package edu.calstatela.jplone.arframework.graphics3d.drawable;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.util.Log;

import java.nio.FloatBuffer;

import edu.calstatela.jplone.arframework.graphics3d.helper.BufferHelper;
import edu.calstatela.jplone.arframework.graphics3d.helper.ShaderHelper;
import edu.calstatela.jplone.arframework.graphics3d.helper.TextureHelper;
import edu.calstatela.jplone.arframework.graphics3d.matrix.MatrixMath;
import edu.calstatela.jplone.arframework.util.Vector3;
import edu.calstatela.jplone.arframework.util.VectorMath;

public class TextureModel implements Drawable,Colorable{
    String TAG = "texture-service";
    private static final int BYTES_PER_FLOAT = 4;
    private static final int FLOATS_PER_VERTEX = 3;
    private static final int FLOATS_PER_COLOR = 4;
    private static final int FLOATS_PER_TEX_COORD = 2;

    private static FloatBuffer sVertexBuffer = null;
    private static FloatBuffer sTexCoordBuffer = null;
    private static FloatBuffer sColorBuffer = null;
    static int sGLProgramId = 0;
    static int mNumVertices = 0;
    private int mGLTextureId = 0;
    private float[] mColor = {0.0f, 0.8f, 0.0f, 0.1f};
    private static float[] tempDrawMatrix = new float[16];

    private static final String vertexShaderSource =
            "attribute vec4 a_Position;" +
                    "attribute vec2 a_TexCoord;" +
                    "uniform mat4 u_Matrix;" +
                    "varying vec2 v_TexCoord;" +
                    "void main()" +
                    "{" +
                    "    gl_Position = u_Matrix * a_Position;" +
                    "    v_TexCoord = a_TexCoord;" +
                    "}";



    private static final String fragmentShaderSource =
            "precision mediump float;                                               \n" +
                    "                                                                       \n" +
                    "uniform sampler2D u_Texture;                                           \n" +
                    "varying vec2 v_TexCoord;                                               \n" +
                    "                                                                       \n" +
                    "void main()                                                            \n" +
                    "{                                                                      \n" +
                    "   gl_FragColor = texture2D(u_Texture, v_TexCoord);                    \n" +
                    "}                                                                      \n";
    public TextureModel(){
        sGLProgramId = ShaderHelper.buildShaderProgram(vertexShaderSource, fragmentShaderSource);
    }
    public void setBitmap(Bitmap bitmap){
        // Make sure texture/bitmap is only set once per object
        if(mGLTextureId != 0)
            return;

        mGLTextureId = TextureHelper.glTextureFromBitmap(bitmap);
        Log.d(TAG,mGLTextureId+"");
    }
    @Override
    public void draw(float[] matrix) {
        GLES20.glUseProgram(sGLProgramId);
        int positionAttribute = GLES20.glGetAttribLocation(sGLProgramId, "a_Position");
        GLES20.glEnableVertexAttribArray(positionAttribute);

//        int colorAttribute = GLES20.glGetAttribLocation(sGLProgramId, "a_Color");
//        GLES20.glEnableVertexAttribArray(colorAttribute);

        int texCoordAttribute = GLES20.glGetAttribLocation(sGLProgramId, "a_TexCoord");
        GLES20.glEnableVertexAttribArray(texCoordAttribute);

        int textureUniform = GLES20.glGetUniformLocation(sGLProgramId, "u_Texture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGLTextureId);
        GLES20.glUniform1i(textureUniform, 0);

        int matrixUniform = GLES20.glGetUniformLocation(sGLProgramId, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(matrixUniform, 1, false, matrix, 0);

        GLES20.glVertexAttribPointer(positionAttribute, FLOATS_PER_VERTEX, GLES20.GL_FLOAT, false, FLOATS_PER_VERTEX * BYTES_PER_FLOAT, sVertexBuffer);
        //GLES20.glVertexAttribPointer(colorAttribute, FLOATS_PER_COLOR, GLES20.GL_FLOAT, false, FLOATS_PER_COLOR * BYTES_PER_FLOAT, sColorBuffer);
        GLES20.glVertexAttribPointer(texCoordAttribute, FLOATS_PER_TEX_COORD, GLES20.GL_FLOAT, false, FLOATS_PER_TEX_COORD * BYTES_PER_FLOAT, sTexCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mNumVertices);

        GLES20.glDisableVertexAttribArray(positionAttribute);
        //GLES20.glDisableVertexAttribArray(colorAttribute);
        GLES20.glDisableVertexAttribArray(texCoordAttribute);
    }
    @Override
    public void draw(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix){
        tempDrawMatrix = new float[16];
        MatrixMath.multiply3Matrices(tempDrawMatrix, projectionMatrix, viewMatrix, modelMatrix);
        draw(tempDrawMatrix);
    }
    public void loadVertices(float[] vertexList){
        mNumVertices = rectangleVertexFloats.length / FLOATS_PER_VERTEX;
        sVertexBuffer = BufferHelper.arrayToBuffer(rectangleVertexFloats);
        //sColorBuffer = BufferHelper.arrayToBuffer(rectangleColorFloats);
    }
    public void loadTextureVerctices(float[] textCoordinates){
        sTexCoordBuffer = BufferHelper.arrayToBuffer(rectangleTexCoordFloats);
    }
    private static final float[] rectangleVertexFloats = {
            -0.5f,  0.0f,  -0.5f,
            0.5f,   0.0f,  -0.5f,
            0.5f,   0.0f,   0.5f,

            -0.5f,  0.0f,  -0.5f,
            0.5f,   0.0f,   0.5f,
            -0.5f,  0.0f,   0.5f
    };
    private static final float[] rectangleColorFloats = {
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.2f, 0.5f, 0.0f, 1.0f
    };
    private static final float[] rectangleTexCoordFloats = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f
    };

    @Override
    public void setColor(float[] rgbaVec){
        if(rgbaVec != null && rgbaVec.length == 4)
            VectorMath.copyVec(rgbaVec, mColor, 4);
    }

    @Override
    public void getColor(float[] color) {
        VectorMath.copyVec(mColor, color, 4);
    }
}
