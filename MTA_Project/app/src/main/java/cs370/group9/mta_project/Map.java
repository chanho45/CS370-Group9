package cs370.group9.mta_project;

import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapPolyline;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapView;

import java.util.List;

public class Map {
    private final MapView map;
    private final MapElementLayer pinLayer;
    private final MapElementLayer polylineLayer;
    protected final Context context;

    protected FusedLocationProviderClient fusedLocationClient;
    private int PERMISSION_ID = 42;

    public Map(Context context) {
        this.context = context;
        map = new MapView(context, MapRenderMode.VECTOR);
        polylineLayer = new MapElementLayer();
        pinLayer = new MapElementLayer();

        map.setCredentialsKey(BuildConfig.CREDENTIALS_KEY);
        map.getLayers().add(polylineLayer);
        map.getLayers().add(pinLayer);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public MapView getMapView() {
        return map;
    }

    // ======================== Pin ==============================
    public void addPin(MapIcon pin) {
        pinLayer.getElements().add(pin);
    }

    public void addPin(List<MapIcon> pins) {
        for (MapIcon pin : pins)
            pinLayer.getElements().add(pin);
    }

    public void clearPin() {
        pinLayer.getElements().clear();
    }

    // ====================== PolyLine ==========================
    public void addLine(MapPolyline line) {
        polylineLayer.getElements().add(line);
    }

    public void addLine(List<MapPolyline> lines) {
        for (MapPolyline line : lines)
            polylineLayer.getElements().add(line);
    }

    public void clearLine() {
        polylineLayer.getElements().clear();
    }

    public void update() {}
}
