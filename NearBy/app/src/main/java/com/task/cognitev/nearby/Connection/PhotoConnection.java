package com.task.cognitev.nearby.Connection;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.task.cognitev.nearby.BuildConfig;
import com.task.cognitev.nearby.Model.PhotoItem;
import com.task.cognitev.nearby.Model.Venue_Photo;
import com.task.cognitev.nearby.R;
import com.task.cognitev.nearby.Utilities.GlobalVariables;
import com.task.cognitev.nearby.Utilities.MyRequestQueue;
import com.task.cognitev.nearby.Utilities.Utilities;
import com.task.cognitev.nearby.ViewHolder.PlacesViewHolder;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.android.volley.Request.Method.GET;

/**
 * Created by Lobna on 9/16/2017.
 */

public class PhotoConnection {
    private static String TAG;
    private static String url;

    public static void getPhoto(final Context context, final PlacesViewHolder placesViewHolder, String venueID) {
        TAG = "getPhotosTag";
        Cache cache = MyRequestQueue.getInstance(context).getRequestQueue().getCache();
        url = GlobalVariables.VENUES_URL +
                venueID + "/" +
                "photos?" +
                "v=20170915&" +
                "client_id=" + BuildConfig.CLIENT_ID + "&" +
                "client_secret=" + BuildConfig.CLIENT_SECRET + "&" +
                "limit=1";
        Cache.Entry entry = cache.get(url);

        if (Utilities.checkNetworkConnectivity(context)) {
            JsonObjectRequest request = new JsonObjectRequest(GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    respond(placesViewHolder, String.valueOf(response));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Venues/photo request error");
                }
            });
            request.setTag(TAG);

            MyRequestQueue.getInstance(context).addToRequestQueue(request, TAG);
        } else if (entry != null) {
            try {
                String data = new String(entry.data, "UTF-8");
                respond(placesViewHolder, data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private static void respond(PlacesViewHolder placesViewHolder, String response) {
        Gson gson = new Gson();
        Venue_Photo photo = gson.fromJson(response, Venue_Photo.class);
        int code = photo.getMeta().get("code").getAsInt();
        if (code == 200) {
            ArrayList<PhotoItem> photoItems = photo.getResponse().getPhotos().getItems();
            if (photoItems != null && photoItems.size() > 0) {
                placesViewHolder.setPhoto(photoItems.get(0));
            } else {
                placesViewHolder.setPlaceHolder();
            }
        } else {
            Log.e(TAG, "Venues/photo response error " + String.valueOf(code));
        }
    }

    public static void loadPhoto(final Context context, String imageUrl, final NetworkImageView imageView) {
        ImageLoader imageLoader = MyRequestQueue.getInstance(context).getImageLoader();
        imageLoader.get(imageUrl, ImageLoader.getImageListener(
                imageView, R.drawable.ic_image, R.drawable.ic_image));
        imageView.setImageUrl(imageUrl, imageLoader);


    }
}
