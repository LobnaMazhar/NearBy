package com.task.cognitev.nearby.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Lobna on 9/19/2017.
 */

public class MyRequestQueue {
    private static final String TAG = MyRequestQueue.class.getSimpleName();

    private static MyRequestQueue instance;
    private static Context context;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private MyRequestQueue(Context context) {
        MyRequestQueue.context = context;
        requestQueue = Volley.newRequestQueue(context);
        DiskBasedCache cache = new DiskBasedCache(context.getCacheDir(), 16 * 1024 * 1024);
        requestQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        requestQueue.start();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized MyRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new MyRequestQueue(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}