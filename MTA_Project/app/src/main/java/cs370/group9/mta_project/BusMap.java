package cs370.group9.mta_project;

import android.content.Context;

import com.microsoft.maps.MapPolyline;

public class BusMap extends Map{
    private BusRoute route;
    private MapPolyline path;

    public BusMap(Context context, BusRoute route){
        super(context);
        this.route = route;
    }
}
