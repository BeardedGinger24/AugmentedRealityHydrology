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

import edu.calstatela.jplone.arframework.graphics3d.helper.MeshHelper;
import edu.calstatela.jplone.arframework.util.Vector3;
import edu.calstatela.jplone.watertrekapp.Data.MeshData;
import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.Rasters;
import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;

public class MeshService {

    public static class getDEM extends AsyncTask<String, Void, String> {
        String TAG = "mesh-service";
        int baseDownSample = 2;
        int width = 0;
        int height = 0;
        double bboxSpaceX = 0.20;
        double bboxSpaceY = 0.20;

        @Override
        protected String doInBackground(String... strings) {
            InputStream inputStream = null;
            TIFFImage tiffImage = null;
            List<FileDirectory> directories = null;
            FileDirectory directory = null;
            Rasters rasters = null;
            float lat = 0;
            float lon = 0;

            try {
                lat = Float.parseFloat(strings[0]);
                lon = Float.parseFloat(strings[1]);
                String base = strings[2];
                final String user = strings[3];
                final String pw = strings[4];

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
                    index++;
                }
            }
            double midpoint_x = ((width/baseDownSample)/2)-1;
            double midpoint_z = ((height/baseDownSample)/2)-1;
            double elevationScale = 100;

            //1 lat is about 111180 meters at equator, and 111200 at poles
            //1 lon is cosine(lon)*lat at equator
            //since our mesh area covers 0.2 degrees in lat/lon directions we divide above values by 5
            double lonScale = ((Math.cos(lat)*111180)/5)/elevationScale;
            double latScale = 22236/elevationScale;

            //for our vectors x is out latitude, z is our longitude, and y is our altitude
            //this forloop will normalize all xz components in the range of -1 to 1, and y to 0 to 1
            for(Vector3 v : vector3s){

                double temp_x = v.getX();
                if(v.getX()<=midpoint_x){
                    v.setX(-((midpoint_x-temp_x)/midpoint_x)*latScale);
                }else{
                    v.setX(((temp_x-midpoint_x)/midpoint_x)*latScale);
                }

                double temp = v.getY();
                v.setY((temp/elevationScale));

                double temp_z = v.getZ();
                if(v.getZ()<=midpoint_z){
                    v.setZ(-((midpoint_z-temp_z)/midpoint_z)*lonScale);
                }else{
                    v.setZ(((temp_z-midpoint_z)/midpoint_z)*lonScale);
                }
            }
            String result = generateVertices(width/baseDownSample,height/baseDownSample,vector3s);
            return result+"# "+lat+" "+lon;

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
        protected String generateVertices(int hVertCount, int vVertCount, Vector3[] vectors) {
            // The number of quads (triangle pairs) in each dimension is
            // one less than the vertex counts in the respective dimensions.
            int hQuadCount = hVertCount - 1;
            int vQuadCount = vVertCount - 1;

            StringBuilder v = new StringBuilder();
            StringBuilder vt = new StringBuilder();
            StringBuilder f = new StringBuilder();
            float[] vertices = new float[hQuadCount*vQuadCount*6*3];
            int startIndex = 0;
            v.append("\n");
            for (int y = 0; y < vQuadCount; y++) {
                for (int x = 0; x < hQuadCount; x++) {
                    int lt = x + y * hVertCount;
                    int rt = lt + 1;
                    int lb = lt + hVertCount;
                    int rb = lb + 1;

                    f.append("f "+lt+1+"/"+lt+1+"/"+lt+1+" "+lb+1+"/"+lb+1+"/"+lb+1+" "+rb+1+"/"+rb+1+"/"+rb+1+"\n");
                    f.append("f "+rb+1+"/"+rb+1+"/"+rb+1+" "+rt+1+"/"+rt+1+"/"+rt+1+" "+lt+1+"/"+lt+1+"/"+lt+1+"\n");
                    v.append("v "+vectors[lt].getVals());
                    vertices[startIndex] = (float) vectors[lt].getX();vertices[startIndex+1] = (float) vectors[lt].getY();vertices[startIndex+2] = (float) vectors[lt].getZ();
                    v.append("v "+vectors[lb].getVals());
                    vertices[startIndex+3] = (float) vectors[lb].getX();vertices[startIndex+4] = (float) vectors[lb].getY();vertices[startIndex+5] = (float) vectors[lb].getZ();
                    v.append("v "+vectors[rb].getVals());
                    vertices[startIndex+6] = (float) vectors[rb].getX();vertices[startIndex+7] = (float) vectors[rb].getY();vertices[startIndex+8] = (float) vectors[rb].getZ();
                    v.append("v "+vectors[rb].getVals());
                    vertices[startIndex+9] = (float) vectors[rb].getX();vertices[startIndex+10] = (float) vectors[rb].getY();vertices[startIndex+11] = (float) vectors[rb].getZ();
                    v.append("v "+vectors[rt].getVals());
                    vertices[startIndex+12] = (float) vectors[rt].getX();vertices[startIndex+13] = (float) vectors[rt].getY();vertices[startIndex+14] = (float) vectors[rt].getZ();
                    v.append("v "+vectors[lt].getVals());
                    vertices[startIndex+15] = (float) vectors[lt].getX();vertices[startIndex+16] = (float) vectors[lt].getY();vertices[startIndex+17] = (float) vectors[lt].getZ();

                    startIndex =startIndex+18;
                    vt.append("vt "+(x+0.0f)/hQuadCount+" "+(y+0.0f)/hQuadCount+"\n");
                    vt.append("vt "+(x+0.0f)/hQuadCount+" "+(y+1.0f)/hQuadCount+"\n");
                    vt.append("vt "+(x+1.0f)/hQuadCount+" "+(y+1.0f)/hQuadCount+"\n");
                    vt.append("vt "+(x+1.0f)/hQuadCount+" "+(y+1.0f)/hQuadCount+"\n");
                    vt.append("vt "+(x+1.0f)/hQuadCount+" "+(y+0.0f)/hQuadCount+"\n");
                    vt.append("vt "+(x+0.0f)/hQuadCount+" "+(y+0.0f)/hQuadCount+"\n");
                }
            }
            StringBuilder n = MeshHelper.calculateNormals(vertices);
            return v.toString()+vt.toString()+n.toString()+f.toString();
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
        protected void onPostExecute(String meshData) {
            super.onPostExecute(meshData);
        }
    }
}