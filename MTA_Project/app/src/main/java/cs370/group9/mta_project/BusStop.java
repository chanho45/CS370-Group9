package cs370.group9.mta_project;

import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapIcon;

public class BusStop extends MapIcon {
    private BusRoute[] routes;
    private String id;

    public BusStop(String name, String id, BusRoute[] routes, Geopoint location){
        super();
        this.id = id;
        this.routes = routes;

        this.setLocation(location);
        this.setTitle(name);
    }
}
