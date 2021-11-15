package cs370.group9.mta_project;

import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapIcon;

public class Bus extends MapIcon {
    private final BusRoute route;

    public Bus(BusRoute route, Geopoint location){
        super();
        this.route=route;

        this.setLocation(location);
        this.setTitle(route.getRouteNumber());
    }

    public BusRoute getRoute() {
        return route;
    }
}
