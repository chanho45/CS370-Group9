package cs370.group9.mta_project;

import android.content.Context;

import org.junit.Test;

import java.sql.Timestamp;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;

public class Request_BusRoutePinsTest {

    @Test
    public void sendRequest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("cs370.group9.mta_project", appContext.getPackageName());

        System.out.println(new Timestamp(System.currentTimeMillis()).toString());
        System.out.println("TEST START: Q17");
        Request_BusRoutePins.sendRequest(appContext, "Q17", new Request_BusRoutePins.Callback() {
            @Override
            public void onSuccess(SIRI_Result result) {
                PrintResult(result);
            }
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private void PrintResult(SIRI_Result result){
        String vFormat = "{name} ({direction}): <{location}>\n"
                +"{distance} m away from stop\n"
                +"Aimed Arrival Time: {aimedTime}\n"
                +"Expected Arrival Time: {ExpectedTime}";
        String sFormat = "{number}: {sumary}";

        System.out.println("******************************************");
        System.out.println("Timestamp: " + result.getTimestamp().toString());

        System.out.println("Vehicles:");
        for(SIRI_Result.Vehicle vehicle: result.getVehicles()){
            System.out.println(vFormat
                    .replace("{name}", vehicle.name)
                    .replace("{direction}", String.valueOf(vehicle.direction))
                    .replace("{location}", vehicle.location.toString())
                    .replace("{distance}", String.valueOf(vehicle.distance))
                    .replace("{aimedTime}", vehicle.aimedTime.toString())
                    .replace("{ExpectedTime}", vehicle.expectedTime.toString()));
            // situation of each vehicle
            if(vehicle.situationRef != null && vehicle.situationRef.length > 0){
                for(String ref: vehicle.situationRef){
                    for(SIRI_Result.Situation situation: result.getSituations())
                        if(situation.SituationNumber != null || situation.SituationNumber.equals(ref))
                            System.out.println(sFormat
                                    .replace("{number}", situation.SituationNumber)
                                    .replace("{sumary}", situation.sumary));
                }
            }
        }
        // Situations
        if(result.getSituations() != null && result.getSituations().length > 0){
            System.out.println("Vehicles:");
            for(SIRI_Result.Situation situation: result.getSituations()){
                System.out.println(sFormat
                        .replace("{number}", situation.SituationNumber)
                        .replace("{sumary}", situation.sumary));
            }
        }
    }
}