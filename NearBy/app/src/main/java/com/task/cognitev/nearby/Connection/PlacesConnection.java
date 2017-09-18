package com.task.cognitev.nearby.Connection;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

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
import com.task.cognitev.nearby.Activity.HomeActivity;
import com.task.cognitev.nearby.BuildConfig;
import com.task.cognitev.nearby.Fragment.PlacesFragment;
import com.task.cognitev.nearby.Model.PlaceGroup;
import com.task.cognitev.nearby.Model.PlaceResponse;
import com.task.cognitev.nearby.Model.Venue_Place;
import com.task.cognitev.nearby.R;
import com.task.cognitev.nearby.Utilities.GlobalVariables;
import com.task.cognitev.nearby.Utilities.Utilities;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.volley.Request.Method.GET;

/**
 * Created by Lobna on 9/15/2017.
 */

public class PlacesConnection {

    private static final String TAG = "getPlacesTag";
    private static RequestQueue requestQueue;

    public static void getPlaces(final Activity activity, final PlacesFragment placesFragment, String latitude, String longitude) {
        if (Utilities.checkNetworkConnectivity(activity)) {
            Cache cache = new DiskBasedCache(activity.getCacheDir(), 1024 * 1024);
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
                    ((HomeActivity) activity).swipeRefreshLayout.setRefreshing(false);
                    ((HomeActivity) activity).loadingLayout.setVisibility(View.GONE);
                    int code = place.getMeta().get("code").getAsInt();
                    if (code == 200) {
                        PlaceResponse placeResponse;
                        if ((placeResponse = place.getResponse()) != null) {
                            ArrayList<PlaceGroup> placeGroups = placeResponse.getGroups();
                            if (placeGroups != null) {
                                placesFragment.setPlaces(place.getResponse().getGroups());
                                return;
                            }
                        }
                    }
                    // If not returned then no data
                    Log.e(TAG, "Venues/places response error " + String.valueOf(code));
                    placesFragment.showError(activity.getString(R.string.noData));

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ((HomeActivity) activity).swipeRefreshLayout.setRefreshing(false);
                    ((HomeActivity) activity).loadingLayout.setVisibility(View.GONE);
                    Log.e(TAG, "Venues/places request error");
                    placesFragment.showError(activity.getString(R.string.noData));
                }
            });
            request.setTag(TAG);

            requestQueue.add(request);
        } else {
            try {
                ((HomeActivity) activity).swipeRefreshLayout.setRefreshing(false);
                ((HomeActivity) activity).loadingLayout.setVisibility(View.GONE);
            } catch (Exception e){

            }
            Utilities.noInternet(activity);
            placesFragment.showError(activity.getString(R.string.somethingWrong));
        }
    }
}
