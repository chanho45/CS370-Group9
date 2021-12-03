package cs370.group9.mta_project;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.microsoft.maps.GeoboundingBox;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.Geoposition;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Request_SearchTrip {
    private static final String URL_ENDPOINT
            = "https://dev.virtualearth.net/REST/V1/Routes/Transit/"
            +"?wp.1={starting}"+"&wp.2={ending}"
            +"&tt={Arrival_Departure}"+"&dt={dateTime}"
            +"&output=json"+"&ra=routePath,transitStops"
            +"&ig=true"+"&maxSolns=3"
            +"&key=" + BuildConfig.CREDENTIALS_KEY;

    static class Route{
        String distance_unit;
        String time_unit;
        GeoboundingBox bbox;
        String starting;
        String ending;

        Geopoint[] path;
        RouteGroup[] routeGroups;
        RouteItem[] routeItems;

        static class RouteGroup{
            // Walking / Transit
            String mode;
            // indices.length > 0
            // 0 <= indice < routeItems.length
            int[] indices;
            double distance;
            double duration;
        }
        static class RouteItem{
            // 0 <= start indice < end indice
            int startPathIndice;
            // 0 < end indice < path.length
            int endPathIndice;
            // Walk / Bus / Train
            String type;
            String text;
            // can be null if walking
            Transit transit;

            static class Transit{
                String depart;
                String arrive;
                String id;
                String name;
                int lineColor;
                String uri;
            }
        }
    }

    interface Callback {
        void onSuccess(List<Route> results);
        void onFailure();
    }

    static void sendRequest(Context context,
                            Request_SearchLocation.Poi starting, Request_SearchLocation.Poi ending,
                            Callback callback){
        if(starting == null || ending == null){
            Toast.makeText(context, "Invalid Input", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(
                Request.Method.GET,
                URL_ENDPOINT.replace("{starting}", starting.location.toString())
                        .replace("{ending}", ending.location.toString())
                        .replace("{Arrival_Departure}", "Departure")
                        .replace("{dateTime}", ""+System.currentTimeMillis()),
                (String response) -> {
                    List<Route> results = parse(starting, ending, response);
                    if (results == null || results.isEmpty()) {
                        callback.onFailure();
                        return;
                    }
                    callback.onSuccess(results);
                },
                (VolleyError error) -> callback.onFailure()
        ));
    }

    private static List<Route> parse(Request_SearchLocation.Poi starting, Request_SearchLocation.Poi ending, String json){
        List<Route> result = new ArrayList<Route>();

        try{
            JSONObject responseJObj = new JSONObject(json);
            JSONArray resourceSetsJArr = responseJObj.getJSONArray("resourceSets");
            JSONObject resourceSetJObj = resourceSetsJArr.getJSONObject(0);
            JSONArray resourcesJArr = resourceSetJObj.getJSONArray("resources");

            for(int i=0; i<resourcesJArr.length(); i++){
                JSONObject resourceJObj = resourcesJArr.getJSONObject(i);
                Route route = new Route();

                route.starting = starting.name;
                route.ending = ending.name;

                route.distance_unit = resourceJObj.getString("distanceUnit");
                route.time_unit = resourceJObj.getString("durationUnit");

                JSONArray resourceBBox = resourceJObj.getJSONArray("bbox");
                route.bbox = new GeoboundingBox(
                        new Geoposition(resourceBBox.getDouble(0),
                                resourceBBox.getDouble(1)),
                        new Geoposition(resourceBBox.getDouble(2),
                                resourceBBox.getDouble(3)));

                JSONObject routePath = resourceJObj.getJSONObject("routePath");
                JSONObject line = routePath.getJSONObject("line");
                JSONArray pathCoordinates = line.getJSONArray("coordinates");
                Geopoint[] path = new Geopoint[pathCoordinates.length()];
                for(int j=0; j<pathCoordinates.length(); j++){
                    JSONArray coordinatesJArr = pathCoordinates.getJSONArray(j);
                    path[j] = new Geopoint(
                            coordinatesJArr.getDouble(0),
                            coordinatesJArr.getDouble(1));
                }
                route.path = path;

                JSONArray routeLegArr = resourceJObj.getJSONArray("routeLegs");
                JSONObject routeLeg = routeLegArr.getJSONObject(0);

                JSONArray itineraryGroups = routeLeg.getJSONArray("itineraryGroups");
                Route.RouteGroup[] routeGroups = new Route.RouteGroup[itineraryGroups.length()];
                for(int a=0; a<routeGroups.length; a++){
                    JSONObject group = itineraryGroups.getJSONObject(a);
                    Route.RouteGroup routeGroup = new Route.RouteGroup();

                    routeGroup.mode = group.getString("travelMode");
                    routeGroup.distance = group.getDouble("travelDistance");
                    routeGroup.duration = group.getDouble("travelDuration");

                    JSONArray indicesJArr = group.getJSONArray("itineraryIndices");
                    int[] indices = new int[indicesJArr.length()];
                    for(int b = 0; b < indicesJArr.length(); b++){
                        indices[b] = indicesJArr.getInt(b);
                    }
                    routeGroup.indices = indices;

                    routeGroups[a] = routeGroup;
                }
                route.routeGroups = routeGroups;

                JSONArray itineraryItems = routeLeg.getJSONArray("itineraryItems");
                Route.RouteItem[] routeItems = new Route.RouteItem[itineraryItems.length()];
                for(int a=0; a<routeItems.length; a++){
                    JSONObject itemJObj = itineraryItems.getJSONObject(a);
                    Route.RouteItem routeItem = new Route.RouteItem();

                    routeItem.type = itemJObj.getString("iconType");

                    JSONObject instruction = itemJObj.getJSONObject("instruction");
                    routeItem.text = instruction.getString("text");

                    JSONArray detailsJArr = itemJObj.getJSONArray("details");
                    JSONObject detailJObj = detailsJArr.getJSONObject(0);
                    routeItem.startPathIndice = detailJObj.getJSONArray("startPathIndices").getInt(0);
                    routeItem.endPathIndice = detailJObj.getJSONArray("endPathIndices").getInt(0);

                    if(routeItem.type.equals("Train") || routeItem.type.equals("Bus")){
                        JSONObject transitLine = itemJObj.getJSONObject("transitLine");
                        Route.RouteItem.Transit transit = new Route.RouteItem.Transit();

                        transit.id = transitLine.getString("abbreviatedName");
                        transit.name = transitLine.getString("verboseName");
                        transit.lineColor = transitLine.getInt("lineColor");
                        transit.uri = transitLine.getString("uri");

                        JSONArray child = itemJObj.getJSONArray("childItineraryItems");
                        JSONObject childDep = child.getJSONObject(0);
                        JSONArray childDepDetailJArr = childDep.getJSONArray("details");
                        JSONObject childDepDetailJObj = childDepDetailJArr.getJSONObject(0);
                        transit.depart = childDepDetailJObj.getJSONArray("names").getString(0);

                        JSONObject childArrv = child.getJSONObject(1);
                        JSONArray childArrvDetailArr = childArrv.getJSONArray("details");
                        JSONObject childArrvDeatilObj = childArrvDetailArr.getJSONObject(0);
                        transit.arrive = childArrvDeatilObj.getJSONArray("names").getString(0);

                        routeItem.transit = transit;
                    }

                    routeItems[a] = routeItem;
                }
                route.routeItems = routeItems;

                result.add(route);
            }
        }catch (Exception e) {
            return null;
        }

        return result;
    }
}
