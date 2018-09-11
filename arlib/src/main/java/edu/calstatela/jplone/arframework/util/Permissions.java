package edu.calstatela.jplone.arframework.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class Permissions {
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
    // More permission types should be added as needed

    private static final int DEFAULT_REQUEST_CODE = 2349803;

    public static boolean havePermission(Context context, String permissionType){
        return ContextCompat.checkSelfPermission(context, permissionType) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, String permissionType){
        int requestCode = DEFAULT_REQUEST_CODE;
        ActivityCompat.requestPermissions(activity, new String[] {permissionType}, requestCode);
    }
}
