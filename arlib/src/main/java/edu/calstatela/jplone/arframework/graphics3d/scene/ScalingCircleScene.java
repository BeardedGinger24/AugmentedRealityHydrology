package edu.calstatela.jplone.arframework.graphics3d.scene;

import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;

public class ScalingCircleScene extends CircleScene{

    float scale = 1;
    public void setScale(float scale){
        if(scale > 0)
            this.scale = scale;
    }

    @Override
    protected void updateEntity(Entity entity) {
        float[] e = entity.getPosition();
        float[] c = getCenter();

        entity.setLookAtWithConstantDistanceScale(e[0], e[1], e[2], c[0], c[1], c[2], 0, 1, 0, scale);
    }
}
