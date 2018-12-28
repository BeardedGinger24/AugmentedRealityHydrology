package edu.calstatela.jplone.arframework.graphics3d.drawable;

public interface Drawable {

    void draw(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix);

    void draw(float[] matrix);
}
