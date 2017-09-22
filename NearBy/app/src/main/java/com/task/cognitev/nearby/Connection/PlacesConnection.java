package com.task.cognitev.nearby.Connection;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
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

import org.json.JSONException;
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

    private static boolean loaded = false;

    public static void getPlaces(final Activity activity, final PlacesFragment placesFragment, String latitude, String longitude) {
        CONNECTION_TAG = "getPlacesTag";

        url = GlobalVariables.VENUES_URL +
                "explore?" +
                "v=20170915&" +
                "client_id=" + BuildConfig.CLIENT_ID + "&" +
                "client_secret=" + BuildConfig.CLIENT_SECRET + "&" +
                "ll=" + latitude + "," + longitude + "&" +
                "radius=1000";

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
                if (!loaded) {
                    if (!Utilities.checkNetworkConnectivity(activity)) {
                        Utilities.noInternet(activity);
                        placesFragment.showError(activity.getString(R.string.somethingWrong));
                    } else {
                        placesFragment.showError(activity.getString(R.string.noData));
                    }
                }
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null)
                        cacheEntry = new Cache.Entry();
                    final long cacheHitButRefreshed = 3 * 60 * 1000;
                    final long cacheExpired = 3 * 24 * 60 * 60 * 1000; // 3 days to expire
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "UnsupportedEncodingException :: " + e.getMessage().toString());
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException :: " + e.getMessage().toString());
                    return Response.error(new ParseError(e));
                }
            }
        };
        request.setTag(CONNECTION_TAG);

        MyRequestQueue.getInstance(activity).addToRequestQueue(request, CONNECTION_TAG);
    }

    private static void respond(Activity activity, PlacesFragment placesFragment, String response) {
        Gson gson = new Gson();
        Venue_Place place = gson.fromJson(response, Venue_Place.class);
        ((HomeActivity) activity).swipeRefreshLayout.setRefreshing(false);
        ((HomeActivity) activity).loadingLayout.setVisibility(View.GONE);
        int code = place.getMeta().get("code").getAsInt();
        if (code == 200) {
            loaded = true;
            PlaceResponse placeResponse;
            if ((placeResponse = place.getResponse()) != null) {
                ArrayList<PlaceGroup> placeGroups = placeResponse.getGroups();
                if (placeGroups != null) {
                    placesFragment.setPlaces(place.getResponse().getGroups());
                }
            }
        }
    }
}
