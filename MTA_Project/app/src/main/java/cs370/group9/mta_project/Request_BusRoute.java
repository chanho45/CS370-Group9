package cs370.group9.mta_project;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Request_BusRoute {
    static final String URL_ENDPOINT
            ="https://bustime.mta.info/api/where/route/{bus_id}.json"
            +"?key="+BuildConfig.MTA_KEY
            +"&version=2";

    interface Callback {
        void onSuccess(BusRoute busRoute);
        void onFailure();
    }

    static void sendRequest(Context context, String id, Callback callback){
        if(id == null || id.isEmpty()){
            Toast.makeText(context, "Invalid bus id", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(
                Request.Method.GET,
                URL_ENDPOINT.replace("{bus_id}", id),
                (String response) ->{
                    //TEST
                    //Toast.makeText(context, "Request Success", Toast.LENGTH_SHORT).show();

                    BusRoute result = parse(response);
                    if(result == null){
                        callback.onFailure();
                        return;
                    }
                    callback.onSuccess(result);
                },
                (VolleyError error) -> callback.onFailure()
        ));
    }

    private static BusRoute parse(String json){
        BusRoute result;

        try{
            JSONObject responseJObj = new JSONObject(json);
            JSONObject responseData = responseJObj.getJSONObject("data");
            JSONObject responseEntry = responseData.getJSONObject("entry");

            result = new BusRoute(responseEntry.getString("id"));
            result.setColor(Integer.parseInt(
                    responseEntry.getString("color"),
                    16
            ));
            result.setName(responseEntry.getString("longName"));
            result.setRouteNumber(responseEntry.getString("shortName"));
            result.setDescription(responseEntry.getString("description"));
        }catch (Exception e){
            return null;
        }

        return result;
    }
}
