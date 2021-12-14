package cs370.group9.mta_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Page_Bus extends Fragment {

    public Button searchButton;
    public ListView busList;

    public Page_Bus(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        View view = inflater.inflate(R.layout.page_bus, container, false);

        busList = view.findViewById(R.id.bus_list);

        this.searchButton = view.findViewById(R.id.bus_request_button);
        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "bus search request", Toast.LENGTH_LONG).show();
                showBusLineSearch();
            }
        });

        return view;
    }

    private void showBusLineSearch(){
        EditText input =new EditText(getContext());
        input.setHint("Enter Bus Line Number");
        input.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        new AlertDialog.Builder(getContext())
            .setView(input)
            .setPositiveButton("Search", (DialogInterface dialog, int which)->{
                Request_SearchBusRoute.sendRequest(getContext(),
                    input.getText().toString().toUpperCase(),
                    new Request_SearchBusRoute.Callback() {
                        // search for busroute with input id
                        @Override
                        public void onSuccess(List<String> results) {
                            Toast.makeText(getContext(),
                                    results.size()+" items found!",
                                    Toast.LENGTH_SHORT).show();
                            /*
                            // TEST
                            ArrayAdapter<String> arrayAdapter
                                    = new ArrayAdapter<String>(getContext(),
                                    R.layout.list_view,
                                    results);
                            busList.setAdapter(arrayAdapter);
                             */

                            // Display result Route
                            BuslineArrayAdapter arrayAdapter
                                    = new BuslineArrayAdapter(getContext());
                            busList.setAdapter(arrayAdapter);
                            busList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                // route selected, display map
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    BusRoute route = arrayAdapter.getItem(position);
                                    BusMap busMap = new BusMap(getContext(), route);
                                    busMap.setup();
                                    // call map page
                                    Page_Map page_map = new Page_Map(busMap);
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.display, page_map)
                                            .commit();
                                }
                            });

                            // request route info for each id
                            for(String id: results){
                                Request_BusRoute.sendRequest(getContext(), id,
                                        new Request_BusRoute.Callback() {
                                    @Override
                                    public void onSuccess(BusRoute busRoute) {
                                        arrayAdapter.add(busRoute);
                                    }

                                    @Override
                                    public void onFailure() {
                                        Toast.makeText(getContext(),
                                                "Failed to Find: "+id,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(getContext(),
                                    "No search results found",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
            })
            .setNegativeButton("Cancel", (DialogInterface dialog, int which)->{
                dialog.cancel();
            })
            .show();
    }


    private class BuslineArrayAdapter extends ArrayAdapter<BusRoute>{

        public BuslineArrayAdapter(Context context){
            super(context, R.layout.busline_display);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if(view == null){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.busline_display, parent, false);
            }
            BusRoute route = getItem(position);

            TextView id = (TextView)view.findViewById(R.id.icon);
            TextView name = (TextView)view.findViewById(R.id.firstLine);
            TextView desc = (TextView)view.findViewById(R.id.secondLine);

            id.setText(route.getRouteNumber());
            id.setTextColor(-16777216+route.getColor());

            name.setText(route.getName());
            desc.setText(route.getDescription());

            return view;
        }
    }
}
