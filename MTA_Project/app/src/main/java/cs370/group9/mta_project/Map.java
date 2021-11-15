package cs370.group9.mta_project;

import android.content.Context;

import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapPolyline;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapView;

public class Map {
    private final MapView map;
    private final MapElementLayer pinLayer;
    private final MapElementLayer polylineLayer;

    public Map(Context context){
        map = new MapView(context, MapRenderMode.VECTOR);
        pinLayer = new MapElementLayer();
        polylineLayer = new MapElementLayer();
        map.getLayers().add(pinLayer);
        map.getLayers().add(polylineLayer);
    }

    public MapView getMapView(){ return map; }

    // ======================== Pin ==============================
    public void addPin(MapIcon pin){
        pinLayer.getElements().add(pin);
    }
    public void addPin(MapIcon[] pins){
        for (MapIcon pin:pins)
            pinLayer.getElements().add(pin);
    }
    public void clearPin(){ pinLayer.getElements().clear(); }

    // ====================== PolyLine ==========================
    public void addLine(MapPolyline line){
        polylineLayer.getElements().add(line);
    }
    public void addLine(MapPolyline[] lines){
        for (MapPolyline line:lines)
            polylineLayer.getElements().add(line);
    }
    public void clearLine(){ polylineLayer.getElements().clear(); }
}
