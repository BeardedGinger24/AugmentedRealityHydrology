//  GLCameraActivity is a class that allows displaying what the camera sees, and allowing you to
//  draw on top of it using OpenGL calls.

// How to use:
//  + Make an Activity class that inherits from ARActivity
//  + Make sure to add Activity entry to Manifest
//  + Override GLInit(), GLDraw() and GLResize(int, int) methods as desired for doing openGL calls
//  + Use GLES20 class for your openGL calls, since this class currently only supports OpenGL ES 2
//  + If you want to place some Android UI Elements on top of the Camera and GL drawing, do the
//    following steps in your onCreate() method:
//      * Create the UI Elements you want and put them in a ViewGroup object
//      * Call the getTopFrameLayout() function to get ARActivity's FrameLayout
//      * Add your ViewGroup object to the FrameLayout
//  + Use the <android:screenOrientation=""> tag in the Manifest to control the Orientation of this
//    Activity.

package edu.calstatela.jplone.arframework.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class ARActivity extends AppCompatActivity {

    private static final String TAG = "waka-ARActivity";
    private ARView arView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        arView = new MyARView(this);
        setContentView(arView);
    }


    @Override
    protected void onPause() {
        super.onPause();
        arView.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        arView.onResume();


    }


    public void GLInit(){ }

    public void GLResize(int width, int height){ }

    public void GLDraw(){ }



    public View getARView(){
        return arView;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Blank Renderer Callback Object
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class MyARView extends ARView {
        public MyARView(Context context){
            super(context);
        }

        @Override
        public void GLInit(){
            super.GLInit();
            ARActivity.this.GLInit();
        }

        @Override
        public void GLResize(int width, int height){
            super.GLResize(width, height);
            ARActivity.this.GLResize(width, height);
        }

        @Override
        public void GLDraw(){
            super.GLDraw();
            ARActivity.this.GLDraw();
        }


    }


}

//  Issues:
//      + Should make way to do 2D drawings with Canvas on top of both CameraView and GLView
//      + Allow the user to specify which version of OpenGL ES they want to use
//      + Make a way to control Activity options, such as orientation, hiding ActionBar, etc
//      + Might need to make a way to pause and resume the CameraView
//      + Add way to load ui components from layout.xml file