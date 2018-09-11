package edu.calstatela.jplone.arframework.graphics3d.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;


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

        if (textureHandle[0] == 0){
            throw new RuntimeException("Error generating texture name.");
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
}
