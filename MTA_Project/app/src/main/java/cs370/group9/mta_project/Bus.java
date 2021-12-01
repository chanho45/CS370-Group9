package cs370.group9.mta_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;


public class Bus extends MapIcon {
    private final BusRoute route;

    public Bus(BusRoute route, Geopoint location, Context context){
        super();
        this.route=route;

        this.setLocation(location);
        this.setTitle(route.getRouteNumber());

        Bitmap icon = ColorChanger.changeBitmapColor(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.bus),
                route.getColor());
        this.setImage(new MapImage(icon));
    }

    public BusRoute getRoute() {
        return route;
    }
}
