package edu.calstatela.jplone.watertrekapp.Images;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.io.File;

import edu.calstatela.jplone.watertrekapp.activities.MeshDemoActivity;

public class GetImage{

    public static void launch(Activity currentActivity){
        Intent intent = new Intent(currentActivity, MeshDemoActivity.class);
        currentActivity.startActivity(intent);
    }
}
