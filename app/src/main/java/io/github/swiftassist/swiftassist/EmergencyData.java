package io.github.swiftassist.swiftassist;

/**
 * Created by avik on 2/7/2016.
 */
public class EmergencyData {
    double lat;
    double lon;
    String type;
    public EmergencyData(){}

    public EmergencyData(double _lat, double _lon, String _type){
        lat = _lat;
        lon = _lon;
        type = _type;
    }

    public double getLat(){
        return lat;
    }

    public double getLon(){
        return lon;
    }

    public String getType(){
        return type;
    }
}
