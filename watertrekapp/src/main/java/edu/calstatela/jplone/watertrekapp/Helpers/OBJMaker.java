package edu.calstatela.jplone.watertrekapp.Helpers;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class OBJMaker implements Serializable {

    public void loadData(File file ,String data){
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(data);
            objectOut.close();
            Log.d("OBJMaker","The Object  was succesfully written to a file at "+file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void loadData(File file ,Bitmap data){
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            data.compress(Bitmap.CompressFormat.PNG,100,fileOut);
//            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
//            objectOut.writeObject(data);
//            objectOut.close();
            Log.d("OBJMaker","The Object  was succesfully written to a file at "+file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void loadData(File file, Image image){
        try {
            FileOutputStream fileOut = new FileOutputStream(file);

//            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
//            objectOut.writeObject(data);
//            objectOut.close();
            Log.d("OBJMaker","The Object  was succesfully written to a file at "+file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
