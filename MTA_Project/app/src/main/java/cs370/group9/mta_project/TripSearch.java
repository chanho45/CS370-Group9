package cs370.group9.mta_project;

import com.microsoft.maps.GeoboundingBox;
import com.microsoft.maps.Geopoint;

import java.util.List;

public class TripSearch {
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

        class RouteGroup{
            // Walking / Transit
            String mode;
            // indices.length > 0
            // 0 <= indice < routeItems.length
            int[] indices;
            double distance;
            double duration;
        }
        class RouteItem{
            // 0 <= start indice < end indice
            int startPathIndice;
            // 0 < end indice < path.length
            int endPathIndice;
            // Walk / Bus / Train
            String type;
            String text;
            // can be null if walking
            Transit transit;

            class Transit{
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
        void onSuccess(List<LocalSearch.Poi> results);
        void onFailure();
    }
}
