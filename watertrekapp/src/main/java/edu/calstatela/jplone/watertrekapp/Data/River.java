package edu.calstatela.jplone.watertrekapp.Data;

import java.util.List;

/**
 * Created by nes on 4/23/18.
 */

public class River {
    private String comId,lengthKm, shapeLength,fType;
    private List Multiyline;

    River(){

    }

    River(String[] values){

    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getLengthKm() {
        return lengthKm;
    }

    public void setLengthKm(String lengthKm) {
        this.lengthKm = lengthKm;
    }

    public String getShapeLength() {
        return shapeLength;
    }

    public void setShapeLength(String shapeLength) {
        this.shapeLength = shapeLength;
    }

    public String getfType() {
        return fType;
    }

    public void setfType(String fType) {
        this.fType = fType;
    }

    public List getMultiyline() {
        return Multiyline;
    }

    public void setMultiyline(List multiyline) {
        Multiyline = multiyline;
    }
}
