package edu.calstatela.jplone.watertrekapp.DataService;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TextureService{

    public static class getTexture extends AsyncTask<String,Void,Bitmap> {
        String TAG = "texture-service";
        int width = 0;
        int height = 0;
        double bboxSpaceX = 0.20;
        double bboxSpaceY = 0.20;

        @Override
        protected Bitmap doInBackground(String... strings) {
            InputStream is = null;
            Bitmap bmp = null;
            float lat = 0;
            float lon = 0;

            try {
                lat = Float.parseFloat(strings[0]);
                lon = Float.parseFloat(strings[1]);
                String base = strings[2];
                final String user = strings[3];
                final String pw = strings[4];

                URL url = new URL(getURL(lat, lon, base));
                Log.d(TAG, url + "");

                HttpsURLConnection urlConnection =
                        (HttpsURLConnection) url.openConnection();
                Log.d(TAG, "after openconnection()");
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pw.toCharArray());
                    }
                });
                Log.d(TAG, "after authenticator");
                urlConnection.connect();
                Log.d(TAG, "after connect()");
                is = urlConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bmp = BitmapFactory.decodeStream(bis);

                Log.d(TAG,bmp.getByteCount()+"");
            } catch (Exception e) {
                Log.e(TAG,e+"");
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }

        public String getURL(float lat, float lon, String base) {
            String size = "size=200%2C200&";
            String format = "format=BMP&";
            String transparent = "transparent=true&";
            String f = "f=image";

            double minX = lon - bboxSpaceX;
            double minY = lat - bboxSpaceY;
            double maxX = lon + bboxSpaceX;
            double maxY = lat + bboxSpaceY;
            String bbox = "bbox=" + minX + "%2C" + minY + "%2C" + maxX + "%2C" + maxY + "&";
            return base + bbox + size + format + transparent + f;
        }
    }
}
