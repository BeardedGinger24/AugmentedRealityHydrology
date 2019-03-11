package edu.calstatela.jplone.watertrekapp.DataService;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import edu.calstatela.jplone.watertrekapp.Data.MeshData;
import edu.calstatela.jplone.watertrekapp.Data.Vector3;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp.WatertrekCredentials;
import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.Rasters;
import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;

public class MeshService {

    public static class getDEM extends AsyncTask<String, Void, MeshData>{
        String TAG = "mesh-service";
        MeshData meshData = null;
        int baseDownSample = 2;
        int width = 0;
        int height = 0;
        double bboxSpaceX = 0.20;
        double bboxSpaceY = 0.20;
        @Override
        protected MeshData doInBackground(String... strings) {
            InputStream inputStream = null;
            TIFFImage tiffImage = null;
            List<FileDirectory> directories = null;
            FileDirectory directory = null;
            Rasters rasters = null;
            float lat = 0;
            float lon = 0;
            float alt = 0;

            try {
                lat = Float.parseFloat(strings[0]);
                lon = Float.parseFloat(strings[1]);
                alt = Float.parseFloat(strings[2]);

                final String user = strings[4];
                final String pw = strings[5];
                String base = strings[3];
                URL url = new URL(getURL(lat,lon,base));
                Log.d(TAG,url+"");

                HttpsURLConnection urlConnection =
                        (HttpsURLConnection) url.openConnection();
                Log.d(TAG,"after openconnection()");
                Authenticator.setDefault(new Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user,pw.toCharArray());
                    }
                });
                Log.d(TAG,"after authenticator");
                urlConnection.connect();
                Log.d(TAG,"after connect()");
                //String response = urlConnection.getResponseMessage();
                //Log.d(TAG,response);
                inputStream = urlConnection.getInputStream();
                Log.d(TAG,"after getInputStream");
                tiffImage = TiffReader.readTiff(inputStream);
                directories = tiffImage.getFileDirectories();
                directory = directories.get(0);
                rasters = directory.readRasters();
            } catch (IOException e) {
                Log.e(TAG,e.getMessage());
            }
            width = rasters.getWidth();
            height = rasters.getHeight();
            double maxHeight = 0;
            double heightVal;

            int index = 0;
            Vector3[] vector3s = new Vector3[(width/baseDownSample)*(height/baseDownSample)];
            for(int y = 0; y<(height/baseDownSample); y++) {
                for (int x = 0; x < (width / baseDownSample); x++) {
                    heightVal = rasters.getPixel(x * baseDownSample, y * baseDownSample)[0].doubleValue();

                    vector3s[index] = new Vector3(x, heightVal, y);
                    if (maxHeight < heightVal) {
                        maxHeight = heightVal;
                    }
                    index++;
                }
            }
            for(Vector3 v : vector3s){
                double midpoint_x = ((width/baseDownSample)/2)-1;
                double midpoint_z = ((height/baseDownSample)/2)-1;

                double temp_x = v.getX();
                if(v.getX()<=midpoint_x){
                    v.setX(-((midpoint_x-temp_x)/midpoint_x)*100);
                }else{
                    v.setX(((temp_x-midpoint_x)/midpoint_x)*100);
                }

                double temp = v.getY();
                v.setY(temp/100);
                Log.d(TAG,maxHeight+"");
                double temp_z = v.getZ();
                if(v.getZ()<=midpoint_z){
                    v.setZ(-((midpoint_z-temp_z)/midpoint_z)*100);
                }else{
                    v.setZ(((temp_z-midpoint_z)/midpoint_z)*100);
                }
            }
            int[] triangles = generateTriangles(width/baseDownSample,height/baseDownSample);
            meshData = new MeshData(vector3s,triangles);
            meshData.setLatlonAlt(new float[]{lat,lon,alt});
            return meshData;
        }
        public String getURL(float lat, float lon,String base) {
            String size = "size=400%2C400&";
            String format = "format=tiff&";
            String pixelType = "pixelType=U16&";
            String noDataInterpretation = "noDataInterpretation=esriNoDataMatchAny&";
            String interpolation = "interpolation=RSP_NearsetNeighbor&";
            String f = "f=image";

            double minX = lon-bboxSpaceX;
            double minY = lat-bboxSpaceY;
            double maxX = lon+bboxSpaceX;
            double maxY = lat+bboxSpaceY;
            String bbox = "bbox="+minX+"%2C"+minY+"%2C"+maxX+"%2C"+maxY+"&";
            return base + bbox + size + format + pixelType + noDataInterpretation + interpolation + f;
        }
        protected int[] generateTriangles(int hVertCount, int vVertCount) {
            // The number of quads (triangle pairs) in each dimension is
            // one less than the vertex counts in the respective dimensions.
            int hQuadCount = hVertCount - 1;
            int vQuadCount = vVertCount - 1;
            int[] result = new int[6 * hQuadCount* vQuadCount];
            int startIndex = 0;
            for (int y = 0; y < vQuadCount; y++) {
                for (int x = 0; x < hQuadCount; x++) {
                    int lt = x + y * hVertCount;
                    int rt = lt + 1;
                    int lb = lt + hVertCount;
                    int rb = lb + 1;

                    result[startIndex] = lt;
                    result[startIndex + 1] = lb;
                    result[startIndex + 2] = rb;
                    result[startIndex + 3] = rb;
                    result[startIndex + 4] = rt;
                    result[startIndex + 5] = lt;
//                    result[startIndex] = lt;
//                    result[startIndex + 1] = rt;
//                    result[startIndex + 2] = rb;
//                    result[startIndex + 3] = rb;
//                    result[startIndex + 4] = lb;
//                    result[startIndex + 5] = lt;
                    startIndex += 6;

                }
            }
            return result;
        }
        public double correctHeightVal(double maxY,double leftHeight,double rightHeight,double topHeight,double bottHeight){
            int count = 0;
            double sum = 0;

            if (leftHeight < maxY) {
                count++;
                sum += leftHeight;
            }
            if (rightHeight < maxY) {
                count++;
                sum += rightHeight;
            }
            if (topHeight < maxY) {
                sum += topHeight;
                count++;
            }
            if (bottHeight < maxY) {
                sum += bottHeight;
                count++;
            }

            return sum/count;
        }
        @Override
        protected void onPostExecute(MeshData meshData) {
            super.onPostExecute(meshData);
        }
    }
}