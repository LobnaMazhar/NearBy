package com.task.cognitev.nearby.Connection;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.task.cognitev.nearby.Utilities.MyRequestQueue;
import com.task.cognitev.nearby.Utilities.Utilities;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.android.volley.Request.Method.GET;

/**
 * Created by Lobna on 9/15/2017.
 */

public class PlacesConnection {
    private static final String TAG = PlacesConnection.class.getSimpleName();

    private static String CONNECTION_TAG;
    private static String url;

    public static void getPlaces(final Activity activity, final PlacesFragment placesFragment, String latitude, String longitude) {
        CONNECTION_TAG = "getPlacesTag";

        Cache cache = MyRequestQueue.getInstance(activity).getRequestQueue().getCache();
        url = GlobalVariables.VENUES_URL +
                "explore?" +
                "v=20170915&" +
                "client_id=" + BuildConfig.CLIENT_ID + "&" +
                "client_secret=" + BuildConfig.CLIENT_SECRET + "&" +
                "ll=" + latitude + "," + longitude + "&" +
                "radius=1000";
        Cache.Entry entry = cache.get(url);

        if (Utilities.checkNetworkConnectivity(activity)) {
            JsonObjectRequest request = new JsonObjectRequest(GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    respond(activity, placesFragment, response.toString());
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
            request.setTag(CONNECTION_TAG);

            MyRequestQueue.getInstance(activity).addToRequestQueue(request, CONNECTION_TAG);
        } else if (entry != null) {
            try {
                String data = new String(entry.data, "UTF-8");
                respond(activity, placesFragment, data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            try {
                ((HomeActivity) activity).swipeRefreshLayout.setRefreshing(false);
                ((HomeActivity) activity).loadingLayout.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            Utilities.noInternet(activity);
            placesFragment.showError(activity.getString(R.string.somethingWrong));
        }
    }

    private static void respond(Activity activity, PlacesFragment placesFragment, String response) {
        Gson gson = new Gson();
        Venue_Place place = gson.fromJson(response, Venue_Place.class);
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
        Log.e(TAG, "Venues/places respond error " + String.valueOf(code));
        placesFragment.showError(activity.getString(R.string.noData));
    }
}
