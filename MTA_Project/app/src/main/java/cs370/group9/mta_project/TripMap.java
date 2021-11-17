package cs370.group9.mta_project;

import android.content.Context;

import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.routing.MapRoute;

public class TripMap extends Map{
    private MapRoute trip;

    public TripMap(Context context, MapRoute trip){
        super(context);
        this.trip = trip;

        this.getMapView().setScene(
                MapScene.createFromBoundingBox(trip.getBoundingBox()),
                MapAnimationKind.NONE);
    }
}
