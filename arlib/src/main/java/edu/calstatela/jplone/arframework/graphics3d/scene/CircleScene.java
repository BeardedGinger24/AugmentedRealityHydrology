package edu.calstatela.jplone.arframework.graphics3d.scene;


import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.VectorMath;

public class CircleScene extends Scene{
    private static final String TAG = "waka-circleScene";

    private float mRadius = 10;
    private float[] mCenter = new float[3];

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setRadius(float radius){
        mRadius = radius;
    }

    public float getRadius(){
        return mRadius;
    }

    public void setCenterLatLonAlt(float[] latLonAlt){
        GeoMath.latLonAltToXYZ(latLonAlt, mCenter);
    }

    public void setCenterXYZ(float[] center){
        VectorMath.copyVec(center, mCenter, 4);
    }

    public float[] getCenter(){
        return mCenter;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void update(){
        for(int i = 0; i < mEntityList.size(); i++){
            Entity entity = mEntityList.get(i);
            updateEntity(entity);
        }
    }

    protected void updateEntity(Entity entity){
        entity.lookAtPoint(mCenter[0], mCenter[1], mCenter[2]);
    }

}
