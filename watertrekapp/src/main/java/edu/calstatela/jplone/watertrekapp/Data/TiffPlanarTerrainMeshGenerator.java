package edu.calstatela.jplone.watertrekapp.Data;

import java.io.IOException;
import java.io.InputStream;

import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;

public class TiffPlanarTerrainMeshGenerator {
    int[] triangles;
    InputStream input;
    TIFFImage tiffImage;
    TiffInfo info;
    int baseDownSample = 10;
    int heightScale = 1;
    float size;

    public MeshData TiffPlanarTerrainMeshGenerator(){
        input = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };

        tiffImage = new TiffReader.readTiff(input);
        info = new TiffInfo(tiffImage);

        // Init the byte array for holding the data read from each TIFF scanline. (The number of rows)
        byte[] scanline = new byte[tiffImage];

        // Vertex counts in the horizontal and vertical directions are the
        // same as the downsampled texture width and height, respectively.
        int hVertCount = info.width / baseDownSample;
        int vVertCount = info.height / baseDownSample;

        Vector3[] verts = new Vector3[hVertCount * vVertCount];

        // For now, the scaling factor is based on the width of the DEM file.
        float dimScale = size / (hVertCount - 1);

        // Vertex counter
        int vertexIndex = 0;

        // Lowest height value
        float min = Float.MAX_VALUE;

        float hOffset = size / 2;
        float vOffset = dimScale * (vVertCount - 1) / 2;

        for (int y = 0; y < vVertCount; y++) {
            tiffImage.ReadScanline(scanline, y * baseDownSample);
            float[] values = info.BPP == 32 ? TiffUtils.Array32ToFloat(scanline) : TiffUtils.Array16ToFloat(scanline);
            for (int x = 0; x < hVertCount; x++) {
                float value = values[x * baseDownSample] * heightScale;
                verts[vertexIndex] = new Vector3(x * dimScale - hOffset, value, y * dimScale - vOffset);
                min = value < min ? value : min;
                vertexIndex++;
            }
        }

        // TODO Is there a better way to do this?
        for (int i = 0; i < verts.length; i++) {
            verts[i].y -= min;
        }

        triangles = GenerateTriangles(hVertCount, vVertCount);
        return new MeshData(verts,triangles);
    }

    protected int[] GenerateTriangles(int hVertCount, int vVertCount) {

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
}
