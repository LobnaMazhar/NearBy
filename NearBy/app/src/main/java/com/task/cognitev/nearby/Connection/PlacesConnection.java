package com.task.cognitev.nearby.Connection;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.task.cognitev.nearby.BuildConfig;
import com.task.cognitev.nearby.Fragment.PlacesFragment;
import com.task.cognitev.nearby.Model.Venue_Place;
import com.task.cognitev.nearby.Utilities.GlobalVariables;
import com.task.cognitev.nearby.Utilities.Utilities;

import org.json.JSONObject;

import static com.android.volley.Request.Method.GET;

/**
 * Created by Lobna on 9/15/2017.
 */

public class PlacesConnection {

    private static final String TAG = "getPlacesTag";
    private static RequestQueue requestQueue;

    public static void getPlaces(Context context, String latitude, String longitude) {
        if(Utilities.checkNetworkConnectivity(context)) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());

            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();

            String url = GlobalVariables.VENUES_URL +
                    "explore?" +
                    "v=20170915&" +
                    "client_id=" + BuildConfig.CLIENT_ID + "&" +
                    "client_secret=" + BuildConfig.CLIENT_SECRET + "&" +
                    "ll=" + latitude + "," + longitude + "&" +
                    "radius=1000";

            JsonObjectRequest request = new JsonObjectRequest(GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Venue_Place place = gson.fromJson(response.toString(), Venue_Place.class);
                    int code = place.getMeta().get("code").getAsInt();
                    if (code == 200) {
                        PlacesFragment.setPlaces(place.getResponse().getGroups());
                    } else {
                        Log.e(TAG, "Venues/places response error " + String.valueOf(code));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Venues/places request error");
                }
            });
            request.setTag(TAG);

            requestQueue.add(request);
        } else {
            Utilities.noInternet(context);
            //TODO push no network
        }
    }
}
