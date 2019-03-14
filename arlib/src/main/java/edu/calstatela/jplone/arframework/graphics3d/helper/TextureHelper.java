package edu.calstatela.jplone.arframework.graphics3d.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import edu.calstatela.jplone.arframework.util.Vector3;


public class TextureHelper {
    public static int glTextureFromResource(Context context, int resourceId){
        Bitmap bitmap = bitmapFromResource(context, resourceId);
        int textureHandle = glTextureFromBitmap(bitmap);
        bitmap.recycle();
        return textureHandle;
    }

    public static Bitmap bitmapFromResource(Context context, int resourceId){

        // Prevent pre-scaling
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        return bitmap;
    }

    public static int glTextureFromBitmap(Bitmap bitmap){
        int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        Log.d("TextHelper",textureHandle[0]+"");
        if (textureHandle[0] == 0){
            //throw new RuntimeException("Error generating texture name.");
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        return textureHandle[0];
    }

    public static void deleteGlTexture(int id){
        int[] textureHandle = new int[]{id};

        GLES20.glDeleteTextures(1, textureHandle, 0);
    }
    public static float[] generateTextureCoordinates(Bitmap bitmap, Vector3[] vectors){
        int hQuadCount = bitmap.getWidth()-1;
        int vQuadCount = bitmap.getHeight()-1;
        int hVertCount = bitmap.getWidth();

        float[] result = new float[(6 * hQuadCount* vQuadCount)*3];
        int startIndex = 0;
        for (int y = 0; y < vQuadCount; y++) {
            for (int x = 0; x < hQuadCount; x++) {
                int lt = x + y * hVertCount;
                int rt = lt + 1;
                int lb = lt + hVertCount;
                int rb = lb + 1;

                result[startIndex] = x;
                result[startIndex+1] = (int) vectors[lt].getY();
                result[startIndex+2] = y;

                result[startIndex + 3] = x;
                result[startIndex+4] = (int) vectors[lb].getY();
                result[startIndex+5] = y+1;

                result[startIndex + 6] = x+1;
                result[startIndex+7]= (int) vectors[rb].getY();;;
                result[startIndex+8] = y+1;

                result[startIndex + 9] = x+1;
                result[startIndex+10] = (int) vectors[rb].getY();;;
                result[startIndex+11] = y+1;

                result[startIndex + 12] = x+1;
                result[startIndex+13] = (int) vectors[rt].getY();;;
                result[startIndex+14] = y;

                result[startIndex + 15] = x;
                result[startIndex+16] = (int) vectors[lt].getY();;;
                result[startIndex+17] = y;

                startIndex =startIndex+ 18;

            }
        }
        return result;
    }
}
