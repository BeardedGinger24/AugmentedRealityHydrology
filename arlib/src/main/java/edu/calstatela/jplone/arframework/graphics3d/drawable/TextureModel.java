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

public class TextureModel implements Drawable{
    String TAG = "texture-service";
    private static final int BYTES_PER_FLOAT = 4;
    private static final int FLOATS_PER_VERTEX = 3;
    private static final int FLOATS_PER_COLOR = 4;
    private static final int FLOATS_PER_TEX_COORD = 2;

    private static FloatBuffer sVertexBuffer = null;
    private static FloatBuffer sTexCoordBuffer = null;
    static int sGLProgramId = -1;
    static int mNumVertices = 0;
    private int mGLTextureId1 = 0;
    private int mGLTextureId2 = 0;
    private static final String vertexShaderSource =
            "attribute vec4 a_Position;                 \n" +
            "attribute vec2 a_TexCoord;                 \n" +
            "                                           \n" +
            "uniform mat4 u_Matrix;                     \n" +
            "                                           \n" +
            "varying vec2 v_TexCoord;                   \n" +
            "                                           \n" +
            "void main()                                \n" +
            "{                                          \n" +
            "    gl_Position = u_Matrix * a_Position;   \n" +
            "    v_TexCoord = a_TexCoord;               \n" +
            "}                                          \n";



    private static final String fragmentShaderSource =
            "precision mediump float;                                               \n" +
                    "                                                                       \n" +
                    "uniform sampler2D u_Texture;                                           \n" +
                    "uniform sampler2D u2_Texture;                                           \n" +
                    "varying vec2 v_TexCoord;                                               \n" +
                    "                                                                       \n" +
                    "void main()                                                            \n" +
                    "{                                                                      \n" +
                    "   vec4 texel1 = texture2D(u_Texture, v_TexCoord);\n"+
                    "   vec4 texel2 = texture2D(u2_Texture, v_TexCoord);\n"+
                    "   gl_FragColor = mix(texel1,texel2,0.40);                    \n" +
                    "}                                                                  \n";

    public static void init(){
        sGLProgramId = ShaderHelper.buildShaderProgram(vertexShaderSource, fragmentShaderSource);
    }
    public void setBitmap(Bitmap bitmap1,Bitmap bitmap2){
        // Make sure texture/bitmap is only set once per object
        if(mGLTextureId1 != 0)
            return;

        mGLTextureId1 = TextureHelper.glTextureFromBitmap(bitmap1);
        mGLTextureId2 = TextureHelper.glTextureFromBitmap(bitmap2);
    }
    @Override
    public void draw(float[] matrix) {
        if(matrix == null || matrix.length != 16) {
            Log.d(TAG, "Billboard.draw() being called with improper mMatrix");
            return;
        }

        GLES20.glUseProgram(sGLProgramId);

        int positionAttribute = GLES20.glGetAttribLocation(sGLProgramId, "a_Position");
        GLES20.glEnableVertexAttribArray(positionAttribute);
        int texCoordAttribute = GLES20.glGetAttribLocation(sGLProgramId, "a_TexCoord");
        GLES20.glEnableVertexAttribArray(texCoordAttribute);

        int textureUniform = GLES20.glGetUniformLocation(sGLProgramId, "u_Texture");
        GLES20.glUniform1i(textureUniform, 0);

        int textureUniform2 = GLES20.glGetUniformLocation(sGLProgramId,"u2_Texture");
        GLES20.glUniform1i(textureUniform2,1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGLTextureId1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGLTextureId2);

        int matrixUniform = GLES20.glGetUniformLocation(sGLProgramId, "u_Matrix");
        GLES20.glUniformMatrix4fv(matrixUniform, 1, false, matrix, 0);

        GLES20.glVertexAttribPointer(positionAttribute, FLOATS_PER_VERTEX, GLES20.GL_FLOAT, false, FLOATS_PER_VERTEX * BYTES_PER_FLOAT, sVertexBuffer);
        GLES20.glVertexAttribPointer(texCoordAttribute, FLOATS_PER_TEX_COORD, GLES20.GL_FLOAT, false, FLOATS_PER_TEX_COORD * BYTES_PER_FLOAT, sTexCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mNumVertices);

        GLES20.glDisableVertexAttribArray(positionAttribute);
        GLES20.glDisableVertexAttribArray(texCoordAttribute);
    }
    @Override
    public void draw(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix){
        float[] tempDrawMatrix = new float[16];
        MatrixMath.multiply3Matrices(tempDrawMatrix, projectionMatrix, viewMatrix, modelMatrix);
        draw(tempDrawMatrix);
    }
    public void loadVertices(float[] vertexList){
        mNumVertices = vertexList.length / FLOATS_PER_VERTEX;
        sVertexBuffer = BufferHelper.arrayToBuffer(vertexList);

    }
    public void loadTextureVerctices(float[] textCoordinates){
        sTexCoordBuffer = BufferHelper.arrayToBuffer(textCoordinates);
    }

}
