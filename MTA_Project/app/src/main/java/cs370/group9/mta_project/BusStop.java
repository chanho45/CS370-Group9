package cs370.group9.mta_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;

public class BusStop extends MapIcon {
    private BusRoute[] routes;
    private String id;

    public BusStop(String name, String id, BusRoute[] routes, Geopoint location, Context context){
        super();
        this.id = id;
        this.routes = routes;

        this.setLocation(location);
        this.setTitle(name);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.stop_pin);
        this.setImage(new MapImage(icon));
    }

    public String getId() { return id; }
    public String getName() { return this.getTitle(); }
    public BusRoute[] getRoutes(){ return routes; }
}
