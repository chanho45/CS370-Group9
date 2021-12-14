package cs370.group9.mta_project;

import android.content.Context;
import android.widget.Toast;

import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapFlyout;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapPolyline;
import com.microsoft.maps.MapScene;

import java.util.ArrayList;
import java.util.List;

public class BusMap extends Map{
    private BusRoute route;
    private List<Request_BusPath.RoutePath> path;

    public BusMap(Context context, BusRoute route){
        super(context);
        this.route = route;
    }

    public void setup(){
        // request path
        if(path == null || path.isEmpty()) {
            Request_BusPath.sendRequest(context, route.getId(), new Request_BusPath.Callback() {
                @Override
                public void onSuccess(List<Request_BusPath.RoutePath> result) {
                    path = result;
                    List<Geopoint> geopoints = new ArrayList<Geopoint>();
                    for(Request_BusPath.RoutePath r: path){
                        for(MapPolyline p: r.polyline){
                            p.setStrokeColor(-16777216+route.getColor());
                            p.setStrokeWidth(3);
                            addLine(p);

                            for(int pos=0; pos < p.getPath().size(); pos++){
                                // each point of the polyline
                                Geopoint geopoint = new Geopoint(
                                        p.getPath().get(pos).getLatitude(),
                                        p.getPath().get(pos).getLongitude());
                                geopoints.add(geopoint);
                            }
                        }
                    }
                    MapScene scene = MapScene.createFromLocations(geopoints);
                    getMapView().setScene(scene, MapAnimationKind.NONE);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, "Loading path failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        // request bus icons
        update();
    }

    @Override
    public void update(){
        Request_BusRoutePins.sendRequest(context, route.getRouteNumber(),
                new Request_BusRoutePins.Callback() {
            @Override
            public void onSuccess(SIRI_Result result) {
                List<SIRI_Result.Vehicle> vehicles = result.getVehicles();
                List<MapIcon> buses = new ArrayList<MapIcon>();
                for(SIRI_Result.Vehicle v: vehicles){
                    Bus bus = new Bus(route, v.location, v.bearing, context);

                    MapFlyout flyout = new MapFlyout();
                    flyout.setTitle(bus.getTitle());
                    String flyout_str = v.distance+"m away from stop"
                            +"\nAimed Arrival: "+(v.aimedTime.toString());
                    if(v.expectedTime != null)
                        flyout_str += "\nExpected Arrival: "+v.expectedTime.toString();
                    flyout.setDescription(flyout_str);
                    bus.setFlyout(flyout);

                    buses.add(bus);
                }
                clearPin();
                addPin(buses);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, "Loading Vehicle Information Failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
