package com.task.cognitev.nearby.Connection;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.task.cognitev.nearby.BuildConfig;
import com.task.cognitev.nearby.Model.Photo;
import com.task.cognitev.nearby.Model.PhotoItem;
import com.task.cognitev.nearby.Model.Venue_Photo;
import com.task.cognitev.nearby.Utilities.GlobalVariables;
import com.task.cognitev.nearby.Utilities.Utilities;
import com.task.cognitev.nearby.ViewHolder.PlacesViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.volley.Request.Method.GET;

/**
 * Created by Lobna on 9/16/2017.
 */

public class PhotoConnection {
    private static String TAG;
    private static RequestQueue requestQueue;

    public static void getPhoto(Context context, final PlacesViewHolder placesViewHolder, String venueID) {
        TAG = "getPhotosTag";
        if(Utilities.checkNetworkConnectivity(context)) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());

            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();

            String url = GlobalVariables.VENUES_URL +
                    venueID + "/" +
                    "photos?" +
                    "v=20170915&" +
                    "client_id=" + BuildConfig.CLIENT_ID + "&" +
                    "client_secret=" + BuildConfig.CLIENT_SECRET + "&" +
                    "limit=1";

            JsonObjectRequest request = new JsonObjectRequest(GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Venue_Photo photo = gson.fromJson(response.toString(), Venue_Photo.class);
                    int code = photo.getMeta().get("code").getAsInt();
                    if (code == 200) {
                        ArrayList<PhotoItem> photoItems = photo.getResponse().getPhotos().getItems();
                        if(photoItems != null && photoItems.size() > 0) {
                            placesViewHolder.setPhoto(photoItems.get(0));
                        }
                    } else {
                        Log.e(TAG, "Venues/photo response error " + String.valueOf(code));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Venues/photo request error");
                }
            });
            request.setTag(TAG);

            requestQueue.add(request);
        } else {
            Toast.makeText(context, "Error loading photo", Toast.LENGTH_SHORT).show();
        }
    }

    public static void loadPhoto(final Context context, String imageUrl, final ImageView imageView) {
        TAG = "loadPhotoTag";
        if (Utilities.checkNetworkConnectivity(context)) {
            requestQueue = Volley.newRequestQueue(context);

            // Initialize a new ImageRequest
            ImageRequest imageRequest = new ImageRequest(
                    imageUrl,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            // Do something with response
                            imageView.setImageBitmap(response);
                        }
                    },
                    0, // Image width
                    0, // Image height
                    ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.RGB_565, //Image decode configuration
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            );
            imageRequest.setTag(TAG);

            // Add ImageRequest to the RequestQueue
            requestQueue.add(imageRequest);
        } else {
            Toast.makeText(context, "Can't load photo", Toast.LENGTH_SHORT).show();
        }
    }
}
