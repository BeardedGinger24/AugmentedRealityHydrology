package edu.calstatela.jplone.watertrekapp.Data;

import java.io.File;
import java.io.IOException;

import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;

public abstract class TiffTerrainMeshGenerator extends TerrainMeshGenerator{
    public MeshData[] MeshData;
    protected String filepath;
    protected int lodLevels;
    protected int baseDownsample;
    protected float heightScale;

    public TiffTerrainMeshGenerator(String filepath, float heightScale, int lodLevels, int baseDownsample) {
        this.filepath = filepath;
        this.lodLevels = lodLevels;
        this.heightScale = heightScale;
        this.baseDownsample = baseDownsample;
    }

    public void Generate() {
        File file = new File(filepath);
        TIFFImage tiffImage = null;
        try {
            tiffImage = TiffReader.readTiff(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Extract info from TIFF data.
        TiffInfo info = new TiffInfo(tiffImage);

        MeshData[] meshData = new MeshData[lodLevels + 1];

        for (int lodLevel = 0; lodLevel <= lodLevels; lodLevel++) {
            int downsample = lodLevel + baseDownsample;
            meshData = GenerateForScanlines(tiffImage, info, downsample);
        }
    }

    protected MeshData[] GenerateForScanlines(TIFFImage tiffImage, TiffInfo info, int downsample){
        MeshData[] mesh = new MeshData[lodLevels+1];



        return mesh;
    }
}
