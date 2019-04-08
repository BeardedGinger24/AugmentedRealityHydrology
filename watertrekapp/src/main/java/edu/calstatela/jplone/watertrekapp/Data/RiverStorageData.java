package edu.calstatela.jplone.watertrekapp.Data;

import com.google.gson.annotations.SerializedName;

public class RiverStorageData {
    // {"datetime":"1981-09-02T00:00:00","discharge":1.500793,"units":"m^3/s"}

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

    public RiverStorageData(String dateTime, String storage, String units){
        this.dateTime = dateTime;
        this.storage = storage;
        this.units = units;
    }
}
