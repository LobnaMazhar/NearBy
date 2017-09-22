package com.task.cognitev.nearby.Model;

import com.google.gson.JsonObject;

/**
 * Created by Lobna on 9/17/2017.
 */

public class Venue_Photo {
    private JsonObject meta;
    private PhotoResponse response;

    public JsonObject getMeta() {
        return meta;
    }

    public PhotoResponse getResponse() {
        return response;
    }
}
