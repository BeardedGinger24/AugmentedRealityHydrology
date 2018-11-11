package edu.calstatela.jplone.watertrekapp.activities;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import edu.calstatela.jplone.watertrekapp.Data.MeshData;
import edu.calstatela.jplone.watertrekapp.Data.Vector3;
import edu.calstatela.jplone.watertrekapp.R;
import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.Rasters;
import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;

import static java.lang.Math.log;
import static java.lang.Math.sin;

public class MeshDemoActivity extends AppCompatActivity {
    String TAG = "MeshDemo";
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesh_demo);

        TiffPlanarTerrainMeshGenerator task = new TiffPlanarTerrainMeshGenerator();
        task.execute();
    }

    public static void launch(Activity currentActivity) {
        Intent intent = new Intent(currentActivity, MeshDemoActivity.class);
        currentActivity.startActivity(intent);
    }


    public class TiffPlanarTerrainMeshGenerator extends AsyncTask<String, Void, Vector3[]> {
        MeshData meshData = null;

        int baseDownSample = 10;
        int width = 0;
        int height = 0;

        double bboxSpace = 500;

        protected int[] generateTriangles(int hVertCount, int vVertCount) {

            // The number of quads (triangle pairs) in each dimension is
            // one less than the vertex counts in the respective dimensions.
            int hQuadCount = hVertCount - 1;
            int vQuadCount = vVertCount - 1;

            int[] result = new int[6 * hQuadCount * vQuadCount];

            int startIndex = 0;
            for (int y = 0; y < vQuadCount; y++) {
                for (int x = 0; x < hQuadCount; x++) {

                    int lt = x + y * hVertCount;
                    int rt = lt + 1;
                    int lb = lt + hVertCount;
                    int rb = lb + 1;

                    // TODO Alternate the triangle orientation of each consecutive quad.

                    result[startIndex] = lt;
                    result[startIndex + 1] = lb;
                    result[startIndex + 2] = rb;
                    result[startIndex + 3] = rb;
                    result[startIndex + 4] = rt;
                    result[startIndex + 5] = lt;

                    startIndex += 6;
                }
            }
            return result;
        }

        public String getURL(float lon, float lat) {

            String base = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/NLCDLandCover2001/ImageServer/exportImage?";
            String format = "format=tiff&";
            String pixelType = "pixelType=U8&";
            String noDataInterpretation = "noDataInterpretation=esriNoDataMatchAny&";
            String interpolation = "interpolation=+RSP_BilinearInterpolation&";
            String f = "f=image";

            //        double[] point = ConvertMyLocationPoint(lon,lat);
            //        double x = point[0];
            //        double y = point[1];
            //
            //        double minX = x-bboxSpace;
            //        double minY = y-bboxSpace;
            //        double maxX = x+bboxSpace;
            //        double maxY = y+bboxSpace;
            //String bbox = minX+","+minY+","+maxX+","+maxY;
            String bbox = "bbox=-15.0%2C56035.0%2C45.0%2C1180435.0&";
            return base + bbox + format + pixelType + noDataInterpretation + interpolation + f;

        }

        private double[] ConvertMyLocationPoint(double lon, double lat) {
            double[] point = new double[2];

            double mercatorX = lon * 0.017453292519943295 * 6378137.0;
            double a = lat * 0.017453292519943295;
            double mercatorY = 3189068.5 * log((1.0 + sin(a)) / (1.0 - sin(a)));

            point[0] = mercatorX;
            point[1] = mercatorY;
            return point;
        }

        @Override
        protected Vector3[] doInBackground(String... strings) {
            //passing dummy values for now
            Bitmap bitmap = null;
            InputStream inputStream = null;

            TIFFImage tiffImage = null;
            List<FileDirectory> directories = null;
            FileDirectory directory = null;
            Rasters rasters = null;
            try {
                String url = getURL(0, 0);
                inputStream = new URL(url).openStream();
                //bitmap = BitmapFactory.decodeStream(inputStream);

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
            int index = 0;

            Vector3[] vector3s = new Vector3[(width/baseDownSample)*(height/baseDownSample)];

            for(int x = 0; x<(width/baseDownSample); x++){
                for(int y = 0; y<(height/baseDownSample); y++){
                    double heightVal = rasters.getPixel(x*baseDownSample,y*baseDownSample)[0].doubleValue();
                    vector3s[index] = new Vector3(x,heightVal,y);
                    if(maxHeight<height){
                        maxHeight = height;
                    }
                    index++;
                }
            }

            for(Vector3 v : vector3s){
                double midpoint_x = ((width/baseDownSample)/2)-1;
                double midpoint_z = ((width/baseDownSample)/2)-1;

                double temp_x = v.getX();
                if(v.getX()<=midpoint_x){
                    v.setX(-((midpoint_x-temp_x)/midpoint_x));
                }else{
                    v.setX(((temp_x-midpoint_x)/midpoint_x));
                }

                double temp = v.getY();
                v.setY(temp/maxHeight);

                double temp_z = v.getZ();
                if(v.getZ()<=midpoint_z){
                    v.setZ(-((midpoint_z-temp_z)/midpoint_z));
                }else{
                    v.setZ(((temp_z-midpoint_z)/midpoint_z));
                }
            }

            return vector3s;
        }

        @Override
        protected void onPostExecute(Vector3[] vector3s) {
            super.onPostExecute(vector3s);
            int[] triangles = generateTriangles(width/baseDownSample,height/baseDownSample);

            meshData = new MeshData(vector3s,triangles);
        }
    }
}
