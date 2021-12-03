package cs370.group9.mta_project;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapPolyline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Request_BusPath {
    private static final String URL_ENDPOINT
            ="http://bustime.mta.info/api/where/stops-for-route/{bus_id}.json"
            +"?key=" + BuildConfig.MTA_KEY
            +"&includePolylines=true" + "&version=2";

    static class RoutePath{
        MapPolyline polyline;
        List<BusStop> stops;
    }
    interface Callback{
        void onSuccess(RoutePath result);
        void onFailure();
    }

    static void sendRequest(Context context, String bus_id, Callback callback){
        if(bus_id==null){
            Toast.makeText(context, "Invalid route id", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(
                Request.Method.GET,
                URL_ENDPOINT.replace("{bus_id}", bus_id),
                (String res) -> {
                    RoutePath result = parse(res);
                    if(result == null){
                        callback.onFailure();
                        return;
                    }
                    callback.onSuccess(result);
                },
                (VolleyError error) -> callback.onFailure()
        ));
    }

    private static RoutePath parse(String json){
        RoutePath result = new RoutePath();

        try{
            JSONObject responseJObj = new JSONObject(json);
            JSONObject dataJObj = responseJObj.getJSONObject("data");
            JSONObject entryJObj = dataJObj.getJSONObject("entry");

            JSONArray polylineJArr = entryJObj.getJSONArray("polylines");
            List<Geopoint> points = new ArrayList<Geopoint>();
            for(int i = 0; i < polylineJArr.length(); i++){
                JSONObject polylineJObj = polylineJArr.getJSONObject(i);
                String polyString = polylineJObj.getString("points");

                List<LatLng> polyLatLons = PolyUtil.decode(polyString);
                for(LatLng latLng: polyLatLons){
                    points.add(LatLng2Geopoint(latLng));
                }
            }
        }catch (Exception e){
            return null;
        }

        return result;
    }

    private static Geopoint LatLng2Geopoint(LatLng latLng){
        return new Geopoint(latLng.latitude, latLng.longitude);
    }
}
