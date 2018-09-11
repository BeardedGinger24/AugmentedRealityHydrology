package edu.calstatela.jplone.arframework.graphics3d.drawable;

import edu.calstatela.jplone.arframework.util.VectorMath;

public class ColorHolder implements Colorable, Drawable{

    private float[] color = {0, 0, 1, 1};
    private Drawable drawable = null;
    private Colorable colorable = null;


    public ColorHolder(){}
    public <T extends Drawable & Colorable> ColorHolder(T drawable, float[] color){
        setColor(color);
        setDrawable(drawable);
    }


    @Override
    public void setColor(float[] color) {
        VectorMath.copyVec(color, this.color, 4);
    }

    @Override
    public void getColor(float[] color) {
        VectorMath.copyVec(this.color, color, 4);
    }

    public <T extends Drawable & Colorable> void setDrawable(T d){
        drawable = d;
        colorable = d;
    }

    @Override
    public void draw(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix) {
        if(drawable == null)
            return;

        colorable.setColor(color);
        drawable.draw(projectionMatrix, viewMatrix, modelMatrix);
    }

    @Override
    public void draw(float[] matrix) {
        if(drawable == null)
            return;

        colorable.setColor(color);
        drawable.draw(matrix);
    }
}
