package cs370.group9.mta_project;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class BusRouteInfo {
    static final String URL_ENDPOINT
            ="http://bustime.mta.info/api/where/route/{bus_id}.json"
            +"?key="+BuildConfig.MTA_KEY;

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

            result = new BusRoute(responseData.getString("id"));
            result.setColor(Integer.parseInt(
                    responseData.getString("color"),
                    16
            ));
            result.setName(responseData.getString("longName"));
            result.setRouteNumber(responseData.getString("shortName"));
            result.setDescription(responseData.getString("description"));
        }catch (Exception e){
            return null;
        }

        return result;
    }
}
