package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import edu.calstatela.jplone.watertrekapp.R;
import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;

public class MeshDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesh_demo);
        //File file = new File(getIntent().getStringExtra("path"));
//        TIFFImage tiffImage = null;
//        try {
//            tiffImage = TiffReader.readTiff(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String x = getIntent().getStringExtra("path");
        Log.d("Mesh",x+"");
    }
}
