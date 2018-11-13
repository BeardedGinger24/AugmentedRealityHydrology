package edu.calstatela.jplone.arframework.graphics3d.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import edu.calstatela.jplone.arframework.graphics3d.helper.TextureHelper;


public class BillboardMaker {


    public static Billboard make(Context context, int iconResourceId, String title, String text){
        Billboard billboard = new Billboard();

        Bitmap bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);

        canvas.drawARGB(255, 200, 200, 200);

        Bitmap icon = TextureHelper.bitmapFromResource(context, iconResourceId);
        canvas.drawBitmap(icon, new Rect(0, 0, 512, 512), new Rect(20, 60, 100, 140), paint);

        paint.setTextSize(30);
        paint.setFakeBoldText(true);
        canvas.drawText(title, 120, 80, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(20);
        canvas.drawText(text, 120, 120, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(0, 0, 400, 200, paint);

        billboard.setBitmap(bitmap);
        bitmap.recycle();
        icon.recycle();

        return billboard;
    }

    public static Billboard make2(Context context, int iconResourceId, String title, String text){
        Billboard billboard = new Billboard();

        Bitmap bitmap = Bitmap.createBitmap(500, 200, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);

        canvas.drawARGB(255, 200, 200, 200);

        Bitmap icon = TextureHelper.bitmapFromResource(context, iconResourceId);
        canvas.drawBitmap(icon, new Rect(0, 0, 512, 512), new Rect(20, 60, 150, 200), paint);

        paint.setTextSize(40);
        paint.setFakeBoldText(true);
        canvas.drawText(title, 120, 80, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(40);
        canvas.drawText(text, 120, 120, paint);

        billboard.setBitmap(bitmap);
        bitmap.recycle();
        icon.recycle();

        return billboard;
    }

    public static Billboard make(Context context, int iconResourceId){
        Billboard billboard = new Billboard();
        Bitmap bitmap = TextureHelper.bitmapFromResource(context, iconResourceId);
        billboard.setBitmap(bitmap);
        bitmap.recycle();
        return billboard;
    }


}
