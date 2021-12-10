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

import java.util.ArrayList;
import java.util.List;

public class Request_BusStopNear {
    private static final String URL_ENDPOINT
            = " http://bustime.mta.info/api/where/stops-for-location.json"
            + "?lat={lat}" + "&lon={lon}"
            + "&radius=200"
            + "&key=" + BuildConfig.MTA_KEY;

    interface Callback{
        void onSuccess(List<BusStop> stops);
        void onFailure();
    }

    static void sendRequest(Context context, Geopoint location, Callback callback){
        if(location == null){
            Toast.makeText(context, "Invalid location", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue =  Volley.newRequestQueue(context);
        queue.add(new StringRequest(
                Request.Method.GET,
                URL_ENDPOINT
                        .replace("{lat}", Double.toString(location.getPosition().getLatitude()))
                        .replace("{lon}", Double.toString(location.getPosition().getLongitude())),
                (String response) -> {
                    List<BusStop> results = parse(response, context);

                    if(results == null || results.isEmpty()){
                        callback.onFailure();
                        return;
                    }

                    callback.onSuccess(results);
                },
                (VolleyError error) -> callback.onFailure()
        ));
    }


    private static List<BusStop> parse(String json, Context context){
        List<BusStop> results = new ArrayList<BusStop>();

        try{
            JSONObject responseJObj = new JSONObject(json);
            JSONObject dataJObj = responseJObj.getJSONObject("data");
            JSONArray stopsJArr = dataJObj.getJSONArray("stops");

            for(int i = 0; i < stopsJArr.length(); i++){
                JSONObject stopJObj = stopsJArr.getJSONObject(i);

                String name = stopJObj.getString("name");
                String id = stopJObj.getString("id");
                Geopoint location = new Geopoint(
                        stopJObj.getDouble("lat")
                        ,stopJObj.getDouble("lon")
                );

                List<BusRoute> routes = new ArrayList<BusRoute>();
                JSONArray routesJArr = stopJObj.getJSONArray("routes");

                for(int j = 0; j < routesJArr.length(); j++){
                    JSONObject routeJObj = routesJArr.getJSONObject(j);

                    BusRoute route = new BusRoute(routeJObj.getString("id"));
                    route.setRouteNumber(routeJObj.getString("shortName"));
                    route.setName(routeJObj.getString("longName"));
                    route.setDescription(routeJObj.getString("description"));
                    route.setColor(Integer.parseInt(
                            routeJObj.getString("color"),
                            16
                    ));

                    routes.add(route);
                }

                BusStop stop = new BusStop(name, id, (BusRoute[])routes.toArray(), location, context);
                results.add(stop);
            }
        }catch (Exception e){
            return null;
        }

        return results;
    }
}
