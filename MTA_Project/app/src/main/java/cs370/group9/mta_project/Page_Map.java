package cs370.group9.mta_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

public class Page_Map extends Fragment {
    private Map map;
    public Button refresh_button;

    public Page_Map(Map map){
        this.map = map;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        View view = inflater.inflate(R.layout.page_map, container, false);

        refresh_button = view.findViewById(R.id.refresh_button);
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.update();
            }
        });

        ((FrameLayout)view.findViewById(R.id.map_view)).addView(map.getMapView());

        return view;
    }
}
