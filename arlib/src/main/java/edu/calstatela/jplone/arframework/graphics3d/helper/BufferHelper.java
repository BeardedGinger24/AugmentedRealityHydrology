package edu.calstatela.jplone.arframework.graphics3d.helper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BufferHelper {
    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;

    public static FloatBuffer arrayToBuffer(float[] array){
        FloatBuffer buffer = ByteBuffer.allocateDirect(array.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(array).position(0);
        return buffer;
    }

    public static ShortBuffer arrayToBuffer(short[] array){
        ShortBuffer buffer = ByteBuffer.allocateDirect(array.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        buffer.put(array).position(0);
        return buffer;
    }
}
