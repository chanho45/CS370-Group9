package cs370.group9.mta_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;


public class Bus extends MapIcon {
    private final BusRoute route;

    public Bus(BusRoute route, Geopoint location, float bearing, Context context){
        super();
        this.route=route;

        this.setLocation(location);
        this.setTitle(route.getRouteNumber());

        Bitmap icon = ImageChanger.changeBitmapColor(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.bus_pin),
                route.getColor());
        Bitmap arrow = ImageChanger.changeBitmapColor(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow),
                route.getColor());
        arrow = ImageChanger.rotateBitmap(arrow, 360-bearing);

        this.setImage(new MapImage(ImageChanger.combineBitmap(icon, arrow)));
    }

    public BusRoute getRoute() {
        return route;
    }
}
