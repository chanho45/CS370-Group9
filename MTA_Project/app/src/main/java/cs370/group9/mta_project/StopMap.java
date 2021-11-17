package cs370.group9.mta_project;

import android.content.Context;

public class StopMap extends Map{
    private BusStop stop;

    public StopMap(Context context, BusStop stop){
        super(context);
        this.stop = stop;
    }
}
