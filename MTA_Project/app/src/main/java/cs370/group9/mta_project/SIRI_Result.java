package cs370.group9.mta_project;

import com.microsoft.maps.Geopoint;

import java.sql.Timestamp;

public class SIRI_Result {
    private Timestamp ts;
    private Vehicle[] vehicles;
    private Situation[] situations;

    public SIRI_Result(){}

    // getters
    public Situation[] getSituations() {
        return situations;
    }
    public Timestamp getTimestamp() {
        return ts;
    }
    public Vehicle[] getVehicles() {
        return vehicles;
    }
    // setters
    public void setSituations(Situation[] situations) {
        this.situations = situations;
    }
    public void setTimestamp(Timestamp ts) {
        this.ts = ts;
    }
    public void setVehicles(Vehicle[] vehicles) {
        this.vehicles = vehicles;
    }


    public Situation findSituation(String situationRef){
        if(situations == null || situations.length == 0)
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
        Geopoint location;

        int distance;// distance in meter
        Timestamp aimedTime;
        Timestamp expectedTime;

        String[] situationRef;
    }

    static class Situation{
        String sumary;
        String SituationNumber;
    }
}
