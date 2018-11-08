package edu.calstatela.jplone.watertrekapp.Images;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import edu.calstatela.jplone.watertrekapp.activities.MeshDemoActivity;

public class GetImage{

    public static void launch(Activity currentActivity){
        File file = new File("greyscale.tiff");
        Log.d("Mesh",file.exists()+"");
        Intent intent = new Intent(currentActivity, MeshDemoActivity.class);
        intent.putExtra("path",file.getAbsolutePath());
        currentActivity.startActivity(intent);
    }
}
