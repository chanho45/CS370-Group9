package cs370.group9.mta_project;

import com.microsoft.maps.Geopoint;

import java.sql.Timestamp;
import java.util.List;

public class SIRI_Result {
    private Timestamp ts;
    private List<Vehicle> vehicles;
    private List<Situation> situations;

    public SIRI_Result(){}

    // getters
    public List<Situation> getSituations() {
        return situations;
    }
    public Timestamp getTimestamp() {
        return ts;
    }
    public List<Vehicle> getVehicles() {
        return vehicles;
    }
    // setters
    public void setSituations(List<Situation> situations) {
        this.situations = situations;
    }
    public void setTimestamp(Timestamp ts) {
        this.ts = ts;
    }
    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }


    public Situation findSituation(String situationRef){
        if(situations == null || situations.isEmpty())
            return null;
        // look for all situations that matches the situation ref
        for (Situation s: situations) {
            if(!s.SituationNumber.isEmpty() && s.SituationNumber.equals(situationRef))
                return s;
        }
        return null;
    }

    static class Vehicle{
        String name;
        int direction;
        float bearing;
        Geopoint location;

        int distance;// distance in meter
        Timestamp aimedTime;
        Timestamp expectedTime;

        List<String> situationRef;
    }

    static class Situation{
        String sumary;
        String SituationNumber;
    }
}
