package edu.calstatela.jplone.arframework.graphics3d.helper;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;


public class ShaderHelper {
    private static final String TAG = "waka_ShaderHelper";

    public static int compileShader(int shaderType, String shaderSource){
        int[] status = {0};

        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);

        if(status[0] == 0) {
            String shaderTypeString = (shaderType == GLES20.GL_VERTEX_SHADER ? "Vertex" : "Fragment");
            String message = GLES20.glGetShaderInfoLog(shader);
            Log.d(TAG, shaderTypeString + " Shader Error: \n" + message);
            GLES20.glDeleteShader(shader);
            return 0;
        }
        else{
            return shader;
        }
    }

    public static int linkProgram(int vertexShader, int fragmentShader){
        int[] status = {0};
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);

        if(status[0] == 0) {
            String message = GLES20.glGetProgramInfoLog(program);
            Log.d(TAG, "Program Link Error: \n" + message);
            GLES20.glDeleteProgram(program);
            return 0;
        }
        else{
            return program;
        }
    }

    public static int buildShaderProgram(String vertexShaderSource, String fragmentShaderSource){
        int vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);
        int program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        return program;
    }

    public static int buildShaderProgram(Context context, int vertexSourceResourceId, int fragmentSourceResourceId){
        String vertexShaderSource = ResourceHelper.stringFromResource(context, vertexSourceResourceId);
        String fragmentShaderSource = ResourceHelper.stringFromResource(context, fragmentSourceResourceId);
        return buildShaderProgram(vertexShaderSource, fragmentShaderSource);
    }
}
