package edu.calstatela.jplone.arframework.graphics3d.scene;

import java.util.ArrayList;

import edu.calstatela.jplone.arframework.graphics3d.drawable.Drawable;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;


public class Scene {
    private static final String TAG = "waka-scene";

    protected ArrayList<Entity> mEntityList = new ArrayList<>();

    public void add(Entity entity){
        mEntityList.add(entity);
    }

    public void draw(float[] projectionMatrix, float[] viewMatrix){
        for(Entity e : mEntityList){
            e.draw(projectionMatrix, viewMatrix, e.getModelMatrix());
        }
    }

    public boolean isEmpty(){
        return mEntityList.isEmpty();
    }

    public Entity addDrawable(Drawable drawable){
        Entity entity = new Entity();
        entity.setDrawable(drawable);
        add(entity);
        return entity;
    }

}
