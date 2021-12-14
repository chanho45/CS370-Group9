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
import com.microsoft.maps.Geopath;
import com.microsoft.maps.Geoposition;
import com.microsoft.maps.MapPolyline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Request_BusPath {
    private static final String URL_ENDPOINT
            ="https://bustime.mta.info/api/where/stops-for-route/{bus_id}.json"
            +"?key=" + BuildConfig.MTA_KEY
            +"&includePolylines=true" + "&version=2";

    static class RoutePath{
        List<MapPolyline> polyline;
        List<String> stopIds;
    }

    interface Callback{
        void onSuccess(List<RoutePath> result);
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
                    // TEST
                    Toast.makeText(context, "Request Success", Toast.LENGTH_SHORT);

                    List<RoutePath> result = parse(res);
                    if(result == null){
                        callback.onFailure();
                        return;
                    }
                    callback.onSuccess(result);
                },
                (VolleyError error) -> callback.onFailure()
        ));
    }

    private static List<RoutePath> parse(String json){
        List<RoutePath> result = new ArrayList<RoutePath>();

        try{
            JSONObject responseJObj = new JSONObject(json);
            JSONObject dataJObj = responseJObj.getJSONObject("data");
            JSONObject entryJObj = dataJObj.getJSONObject("entry");

            JSONArray stopGJArr = entryJObj.getJSONArray("stopGroupings");
            JSONObject stopGroupJObj = stopGJArr.getJSONObject(0);

            // path and stops for each direction
            JSONArray stopGroupJArr = stopGroupJObj.getJSONArray("stopGroups");
            for(int i=0; i<stopGroupJArr.length(); i++)
                result.add(new RoutePath());
            for(int i=0; i<stopGroupJArr.length(); i++){
                JSONObject directionJObj = stopGroupJArr.getJSONObject(i);

                // direction id
                int direction = Integer.parseInt(
                        directionJObj.getString("id"));

                // get polylines
                List<MapPolyline> polylines = new ArrayList<MapPolyline>();
                JSONArray polylineJArr = directionJObj.getJSONArray("polylines");
                for (int j = 0; j < polylineJArr.length(); j++){
                    JSONObject polylineJObj = polylineJArr.getJSONObject(j);
                    String polyString = polylineJObj.getString("points");

                    List<Geoposition> points = new ArrayList<Geoposition>();
                    List<LatLng> polyLatLons = PolyUtil.decode(polyString);
                    for(LatLng latLng: polyLatLons){
                        points.add(LatLng2Geoposition(latLng));
                    }
                    MapPolyline polyline = new MapPolyline();
                    polyline.setPath(new Geopath(points));

                    polylines.add(polyline);
                }
                result.get(direction).polyline = polylines;

                // get stops
                List<String> stopIds = new ArrayList<String>();
                JSONArray stopIdJArr = directionJObj.getJSONArray("stopIds");
                for(int j=0; j< stopIdJArr.length(); j++){
                    stopIds.add(stopIdJArr.getString(j));
                }
                result.get(direction).stopIds = stopIds;
            }

            ///*
            JSONArray polylineJArr = entryJObj.getJSONArray("polylines");
            List<MapPolyline> polylines = new ArrayList<MapPolyline>();
            for(int i = 0; i < polylineJArr.length(); i++){
                JSONObject polylineJObj = polylineJArr.getJSONObject(i);
                String polyString = polylineJObj.getString("points");

                List<Geoposition> points = new ArrayList<Geoposition>();
                List<LatLng> polyLatLons = PolyUtil.decode(polyString);
                for(LatLng latLng: polyLatLons){
                    points.add(LatLng2Geoposition(latLng));
                }
                MapPolyline polyline = new MapPolyline();
                polyline.setPath(new Geopath(points));

                polylines.add(polyline);
            }

            RoutePath routePath = new RoutePath();
            routePath.polyline = polylines;

            result.add(routePath);
            //*/
        }catch (Exception e){
            return null;
        }

        return result;
    }

    private static Geoposition LatLng2Geoposition(LatLng latLng){
        return new Geoposition(latLng.latitude, latLng.longitude);
    }
}
