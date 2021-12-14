package cs370.group9.mta_project;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.microsoft.maps.Geopoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Request_BusRoutePins {
    private static final String URL_ENDPOINT
            = "https://bustime.mta.info/api/siri/vehicle-monitoring.json"
            + "?LineRef={lineName}"
            + "&VehicleMonitoringDetailLevel=normal"
            + "&version=2"
            + "&key=" + BuildConfig.MTA_KEY;

    interface Callback{
        void onSuccess(SIRI_Result result);
        void onFailure(Exception e);
    }

    static void sendRequest(Context context, String name, Callback callback){
        if(name.isEmpty()){
            Toast.makeText(context, "Invalid Bus Route", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(
                Request.Method.GET,
                URL_ENDPOINT.replace("{lineName}",name),
                (String response) ->{
                    //TEST
                    Toast.makeText(context, "Request Success", Toast.LENGTH_SHORT);
                    SIRI_Result result = parse(response);

                    if(result == null){
                        callback.onFailure(new Exception("Invalid Input"));
                        return;
                    }

                    callback.onSuccess(result);
                },
                (VolleyError e)->{
                    callback.onFailure(e);
                }
        ));
    }

    private static SIRI_Result parse(String json){
        SIRI_Result result = new SIRI_Result();

        try{
            JSONObject responseJObj = new JSONObject(json);
            JSONObject SiriJObj = responseJObj.getJSONObject("Siri");
            JSONObject ServiceJObj = SiriJObj.getJSONObject("ServiceDelivery");

            // TimeStamp
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            result.setTimestamp(timestamp);

            // Vehicle
            List<SIRI_Result.Vehicle> vehicles = new ArrayList<SIRI_Result.Vehicle>();
            JSONArray VehicleMonitorJArr = ServiceJObj.getJSONArray("VehicleMonitoringDelivery");
            JSONObject VehicleMonitorJObj = VehicleMonitorJArr.getJSONObject(0);
            JSONArray VehicleActivityJArr = VehicleMonitorJObj.getJSONArray("VehicleActivity");

            for(int i = 0; i < VehicleActivityJArr.length(); i++){
                JSONObject VehicleJObj = VehicleActivityJArr.getJSONObject(i)
                        .getJSONObject("MonitoredVehicleJourney");

                SIRI_Result.Vehicle vehicle = new SIRI_Result.Vehicle();
                // name
                JSONArray lineNameJArr = VehicleJObj.getJSONArray("PublishedLineName");
                vehicle.name = lineNameJArr.getString(0);
                // direction
                vehicle.direction = Integer.parseInt(
                        VehicleJObj.getString("DirectionRef")
                );
                // bearing
                vehicle.bearing = (float) VehicleJObj.getDouble("Bearing");
                // location
                JSONObject locationJObj = VehicleJObj.getJSONObject("VehicleLocation");
                vehicle.location = new Geopoint(
                        locationJObj.getDouble("Latitude"),
                        locationJObj.getDouble("Longitude")
                );

                // MonitoredCall
                JSONObject MonitoredCallJObj = VehicleJObj.getJSONObject("MonitoredCall");
                // distance
                vehicle.distance = MonitoredCallJObj.getInt("DistanceFromStop");
                // aimed time
                vehicle.aimedTime = Timestamp.valueOf(
                        MonitoredCallJObj.getString("AimedArrivalTime")
                                .replace("T", " ")
                                .replace("-05:00", ""));
                // expected time
                if(!MonitoredCallJObj.isNull("ExpectedArrivalTime")) {
                    vehicle.expectedTime = Timestamp.valueOf(
                            MonitoredCallJObj.getString("ExpectedArrivalTime")
                                    .replace("T", " ")
                                    .replace("-05:00", ""));
                }

                // situation ref
                if(!VehicleJObj.isNull("SituationRef")){
                    JSONArray situationRefJArr = VehicleJObj.getJSONArray("SituationRef");
                    List<String> situationRef = new ArrayList<String>();
                    for (int j = 0; j < situationRefJArr.length(); j++) {
                        JSONObject situationRefJObj = situationRefJArr.getJSONObject(j);
                        situationRef.add(situationRefJObj.getString("SituationSimpleRef"));
                    }
                    vehicle.situationRef = situationRef;
                }

                vehicles.add(vehicle);
            }
            result.setVehicles(vehicles);

            // Situation
            if(!ServiceJObj.isNull("SituationExchangeDelivery")) {
                JSONArray SituationMonitorJArr = ServiceJObj.getJSONArray("SituationExchangeDelivery");
                JSONObject SituationMonitorJObj = SituationMonitorJArr.getJSONObject(0);
                JSONObject SituationJObj = SituationMonitorJObj.getJSONObject("Situations");
                JSONArray SituationJArr = SituationJObj.getJSONArray("PtSituationElement");

                List<SIRI_Result.Situation> situations = new ArrayList<SIRI_Result.Situation>();
                for (int i = 0; i < SituationJArr.length(); i++) {
                    JSONObject sitJObj = SituationJArr.getJSONObject(i);

                    SIRI_Result.Situation situation = new SIRI_Result.Situation();
                    situation.SituationNumber = sitJObj.getString("SituationNumber");
                    situation.sumary = sitJObj.getJSONArray("Summary").getString(0);

                    situations.add(situation);
                }

                result.setSituations(situations);
            }
        }catch (Exception e){
            return null;
        }
        return result;
    }
}
