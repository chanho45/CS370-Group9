package cs370.group9.mta_project;

import android.content.Context;

import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapScene;

public class TripMap extends Map{
    private TripSearch.Route trip;

    public TripMap(Context context, TripSearch.Route trip){
        super(context);
        this.trip = trip;

        this.getMapView().setScene(
                MapScene.createFromBoundingBox(trip.bbox),
                MapAnimationKind.NONE);
    }
}
