package edu.calstatela.jplone.watertrekapp.Data;

import com.google.gson.annotations.SerializedName;

public class ReservoirStorageData {
    //    {"datetime":"1981-09-06T00:00:00","storage":1640.0,"units":"ac-ft"}
    @SerializedName("datetime")
    private String dateTime;
    @SerializedName("storage")
    private String  storage;
    @SerializedName("units")
    private String units;

    public String getDateTime() {
        return dateTime;
    }

    public String getStorage() {
        return storage;
    }

    public String getUnits() {
        return units;
    }

    public ReservoirStorageData(String dateTime, String storage, String units){
        this.dateTime = dateTime;
        this.storage = storage;
        this.units = units;
    }
}