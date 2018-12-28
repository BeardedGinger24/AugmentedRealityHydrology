package edu.calstatela.jplone.watertrekapp.activities;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import edu.calstatela.jplone.arframework.ui.SensorARActivity;
import edu.calstatela.jplone.watertrekapp.Data.MeshData;
import edu.calstatela.jplone.watertrekapp.Data.Vector3;
import edu.calstatela.jplone.watertrekapp.R;
import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.Rasters;
import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;


public class MeshDemo extends SensorARActivity{

    public static class TiffPlanarTerrainMeshGenerator extends AsyncTask<String, Void, MeshData>{
        String TAG = "MeshDemo";
        MeshData meshData = null;
        int baseDownSample = 1;
        int width = 0;
        int height = 0;
        double bboxSpaceX = 0.25;
        double bboxSpaceY = 0.10;

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

        public String getURL(float lon, float lat,String base) {
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
        protected MeshData doInBackground(String... strings) {
            InputStream inputStream = null;
            TIFFImage tiffImage = null;
            List<FileDirectory> directories = null;
            FileDirectory directory = null;
            Rasters rasters = null;
            try {
                float lon = Float.parseFloat(strings[0]);
                float lat = Float.parseFloat(strings[1]);
                String base = strings[2];
                String url = getURL(lon,lat,base);
                Log.d(TAG,url);
                inputStream = new URL(url).openStream();
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
            double maxY=3000;
            double left = 255,right=255,top=255,bottom = 255;
            int index = 0;
            Vector3[] vector3s = new Vector3[(width/baseDownSample)*(height/baseDownSample)];
            for(int y = 0; y<(height/baseDownSample); y++) {
                for (int x = 0; x < (width / baseDownSample); x++) {
                    heightVal = rasters.getPixel(x * baseDownSample, y * baseDownSample)[0].doubleValue();
                    if (heightVal > maxY) {
                        if ((x - 1) >= 0) {
                            left = rasters.getPixel((x - 1) * baseDownSample, y * baseDownSample)[0].doubleValue();
                        }
                        if ((x + 1) < (width / baseDownSample)) {
                            right = rasters.getPixel((x + 1) * baseDownSample, y * baseDownSample)[0].doubleValue();
                        }
                        if ((y - 1) >= 0) {
                            top = rasters.getPixel(x * baseDownSample, (y - 1) * baseDownSample)[0].doubleValue();
                        }
                        if ((y + 1) < (height / baseDownSample)) {
                            bottom = rasters.getPixel(x * baseDownSample, (y + 1) * baseDownSample)[0].doubleValue();
                        }

                        heightVal = correctHeightVal(maxY,left, right, top, bottom);
                        vector3s[index] = new Vector3(x, heightVal, y);
                    }
                    vector3s[index] = new Vector3(x, heightVal, y);
                    if (maxHeight < heightVal) {
                        Log.d(TAG,"Before:"+maxHeight);
                        maxHeight = heightVal;
                        Log.d(TAG,"After:"+maxHeight);
                    }
                    index++;
                }
            }
            for(Vector3 v : vector3s){
                double midpoint_x = ((width/baseDownSample)/2)-1;
                double midpoint_z = ((height/baseDownSample)/2)-1;

                double temp_x = v.getX();
                if(v.getX()<=midpoint_x){
                    v.setX(-((midpoint_x-temp_x)/midpoint_x));
                }else{
                    v.setX(((temp_x-midpoint_x)/midpoint_x));
                }

                double temp = v.getY();
                v.setY(temp/(maxHeight*3));

                double temp_z = v.getZ();
                if(v.getZ()<=midpoint_z){
                    v.setZ(-((midpoint_z-temp_z)/midpoint_z));
                }else{
                    v.setZ(((temp_z-midpoint_z)/midpoint_z));
                }
            }
            int[] triangles = generateTriangles(width/baseDownSample,height/baseDownSample);
            meshData = new MeshData(vector3s,triangles,(float)vector3s[(vector3s.length/2)-1].getY());
            return meshData;
        }
        @Override
        protected void onPostExecute(MeshData meshData) {
            super.onPostExecute(meshData);
        }
    }
}