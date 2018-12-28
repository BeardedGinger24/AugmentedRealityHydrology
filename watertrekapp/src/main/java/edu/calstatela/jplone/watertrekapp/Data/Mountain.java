package edu.calstatela.jplone.watertrekapp.Data;
import edu.calstatela.jplone.arframework.landmark.Landmark;
public class Mountain extends Landmark{
    public String type;
    public String title;
    public String description;
    public float latitude;
    public float longitude;
    public float altitude;
    public Mountain(String type, String title, String description, float latitude, float longitude, float altitude) {
        super(type, title, description, latitude, longitude, altitude);
    }
}