package edu.calstatela.jplone.arframework.util;

import java.io.Serializable;

public class Vector3 implements Serializable{
    double x, y, z;
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3 add(Vector3 vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
        return this;
    }
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public double getZ() {
        return z;
    }
    public void setZ(double z) {
        this.z = z;
    }
    public String getVals(){
        return x+" "+y+" "+z+"\n";
    }
}