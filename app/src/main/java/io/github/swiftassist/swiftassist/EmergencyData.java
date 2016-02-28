package io.github.swiftassist.swiftassist;

/**
 * Created by avik on 2/7/2016.
 */
public class EmergencyData {
    double lat;
    double lon;
    String type;
    String src = "Unknown";
    public String getSrc(){return src;}
    public void setSrc(String src){this.src=src;};
    public EmergencyData(){}

    public EmergencyData(double _lat, double _lon, String _type, String _src){
        lat = _lat;
        lon = _lon;
        type = _type;
        src = _src;
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

    public void setLat(double lat){
        this.lat = lat;
    }

    public void setLon(double lon){
        this.lon = lon;
    }

    public void setType(String type){
        this.type = type;
    }
}
