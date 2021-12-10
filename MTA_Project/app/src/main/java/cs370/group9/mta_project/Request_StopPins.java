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

public class Request_StopPins {
    private static final String URL_ENDPOINT
            = "http://bustime.mta.info/api/siri/stop-monitoring.json"
            + "?MonitoringRef={stop_id}" + "&version=2"
            + "&key=" + BuildConfig.MTA_KEY;

    interface Callback {
        void onSuccess(SIRI_Result results);
        void onFailure();
    }

    public static void sendRequest(Context context, BusStop stop, Callback callback){
        if(stop == null || stop.getName().isEmpty()
                || stop.getRoutes() == null || stop.getRoutes().length == 0){
            Toast.makeText(context, "Invalid Bus Stop", Toast.LENGTH_LONG);
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(
                Request.Method.GET,
                URL_ENDPOINT.replace("{stop_id}", stop.getId()),
                (String response) -> {
                    SIRI_Result result = parse(response);

                    if(result == null){
                        callback.onFailure();
                        return;
                    }

                    callback.onSuccess(result);
                },
                (VolleyError error) -> callback.onFailure()
        ));
    }

    private static SIRI_Result parse(String json){
        SIRI_Result result = new SIRI_Result();

        try{
            JSONObject responseJObj = new JSONObject(json);
            JSONObject SiriJObj = responseJObj.getJSONObject("Siri");
            JSONObject serviceJObj = SiriJObj.getJSONObject("ServiceDelivery");
            // timestamp
            Timestamp timestamp = Timestamp.valueOf(serviceJObj.getString("ResponseTimestamp"));
            result.setTimestamp(timestamp);
            // vehicle
            JSONArray StopMonitorJArr = serviceJObj.getJSONArray("StopMonitoringDelivery");
            JSONObject StopMonitorJObj = StopMonitorJArr.getJSONObject(0);
            JSONArray VehicleMonitorJArr = StopMonitorJObj.getJSONArray("MonitoredStopVisit");

            List<SIRI_Result.Vehicle> vehicles = new ArrayList<SIRI_Result.Vehicle>();
            for(int i = 0; i < VehicleMonitorJArr.length(); i++){
                JSONObject VehicleMonitorJObj = VehicleMonitorJArr.getJSONObject(i);
                JSONObject VehicleJObj = VehicleMonitorJObj.getJSONObject("MonitoredVehicleJourney");

                SIRI_Result.Vehicle vehicle = new SIRI_Result.Vehicle();
                vehicle.name = VehicleJObj.getJSONArray("PublishedLineName").getString(0);
                vehicle.direction = Integer.parseInt(VehicleJObj.getString("DirectionRef"));

                JSONObject vehicleLocationJObj = VehicleJObj.getJSONObject("VehicleLocation");
                Geopoint location = new Geopoint(
                        vehicleLocationJObj.getDouble("Latitude")
                        ,vehicleLocationJObj.getDouble("Longitude"));
                vehicle.location = location;

                JSONObject monitoredCallJObj = VehicleJObj.getJSONObject("MonitoredCall");
                vehicle.aimedTime = Timestamp.valueOf(monitoredCallJObj.getString("AimedArrivalTime"));
                vehicle.expectedTime = Timestamp.valueOf(monitoredCallJObj.getString("ExpectedArrivalTime"));
                vehicle.distance = monitoredCallJObj.getInt("DistanceFromStop");

                // Situation Ref
                if(!VehicleJObj.isNull("SituationRef")) {
                    JSONArray situationRefJArr = VehicleJObj.getJSONArray("SituationRef");
                    List<String> situationRef = new ArrayList<String>();
                    for (int j = 0; j < situationRefJArr.length(); j++) {
                        JSONObject situationRefJObj = situationRefJArr.getJSONObject(j);
                        situationRef.add(situationRefJObj.getString("SituationSimpleRef"));
                    }
                    vehicle.situationRef = (String[]) situationRef.toArray();
                }

                vehicles.add(vehicle);
            }
            result.setVehicles((SIRI_Result.Vehicle[]) vehicles.toArray());

            // situation
            if(!serviceJObj.isNull("SituationExchangeDelivery")) {
                JSONArray SituationMonitorJArr = serviceJObj.getJSONArray("SituationExchangeDelivery");
                JSONObject SituationMonitorJObj = SituationMonitorJArr.getJSONObject(0);
                JSONObject SituationJObj = SituationMonitorJObj.getJSONObject("Situations");
                JSONArray SituationJArr = SituationJObj.getJSONArray("PtSituationElement");

                List<SIRI_Result.Situation> situations = new ArrayList<SIRI_Result.Situation>();
                for(int i = 0; i < SituationJArr.length(); i++){
                    JSONObject sitJObj = SituationJArr.getJSONObject(i);

                    SIRI_Result.Situation situation = new SIRI_Result.Situation();
                    situation.SituationNumber = sitJObj.getString("SituationNumber");
                    situation.sumary = sitJObj.getJSONArray("Summary").getString(0);

                    situations.add(situation);
                }

                result.setSituations((SIRI_Result.Situation[]) situations.toArray());
            }

        }catch (Exception e){
            return null;
        }

        return  result;
    }
}
