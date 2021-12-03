package cs370.group9.mta_project;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Request_SearchBusRoute {
    private static final String NYCT_ENDPOINT
            = "http://bustime.mta.info/api/where/route-ids-for-agency/MTA NYCT.json"
            + "?key=" + BuildConfig.MTA_KEY;
    private static final String MTABC_ENDPOINT
            = "http://bustime.mta.info/api/where/route-ids-for-agency/MTABC.json"
            + "?key=" + BuildConfig.MTA_KEY;

    interface Callback {
        void onSuccess(List<String> results);
        void onFailure();
    }

    static void sendRequest(Context context, String query, Callback callback){
        if (query == null || query.isEmpty()) {
            Toast.makeText(context, "Invalid query", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue =  Volley.newRequestQueue(context);
        // check if asked bus route is in MTA NYCT
        queue.add(new StringRequest(
                Request.Method.GET,
                NYCT_ENDPOINT,
                (String response) -> {
                    List<String> results = parse(response, query);

                    // do MTABC too
                    queue.add(new StringRequest(
                            Request.Method.GET,
                            MTABC_ENDPOINT,
                            (String ABCres) -> {
                                results.addAll(parse(ABCres, query));

                                if (results == null || results.isEmpty()) {
                                    callback.onFailure();
                                    return;
                                }
                                callback.onSuccess(results);
                            },
                            (VolleyError error) -> callback.onFailure()
                    ));
                },
                (VolleyError error) -> callback.onFailure()
        ));
    }

    private static List<String> parse(String json, String query){
        List<String> result = new ArrayList<String>();

        try{
            JSONObject resJObj = new JSONObject(json);
            JSONObject dataJObj = resJObj.getJSONObject("data");
            JSONArray listJArr = dataJObj.getJSONArray("list");

            for(int i = 0; i < listJArr.length(); i++){
                // if exact match, send it to top
                if(listJArr.getString(i).equals(query)){
                    result.add(0, listJArr.getString(i));
                } // otherwise, add it to the bottom
                else if(listJArr.getString(i).contains(query)){
                    result.add(listJArr.getString(i));
                }
            }
        }catch (Exception e){
            return null;
        }

        return result;
    }
}
