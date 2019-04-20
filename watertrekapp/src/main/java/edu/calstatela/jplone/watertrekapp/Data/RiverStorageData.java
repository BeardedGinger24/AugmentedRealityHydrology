package edu.calstatela.jplone.watertrekapp.Data;

import com.google.gson.annotations.SerializedName;

public class RiverStorageData {
    // {"datetime":"1981-09-02T00:00:00","discharge":1.500793,"units":"m^3/s"}

    @SerializedName("datetime")
    private String dateTime;
    @SerializedName("discharge")
    private String  discharge;
    @SerializedName("units")
    private String units;

    public String getDateTime() {
        return dateTime;
    }

    public String getDischarge() {
        return discharge;
    }

    public String getUnits() {
        return units;
    }

    public RiverStorageData(String dateTime, String discharge, String units){
        this.dateTime = dateTime;
        this.discharge = discharge;
        this.units = units;
    }
}
