package com.task.cognitev.nearby.Model;

import com.google.gson.JsonObject;

/**
 * Created by Lobna on 9/14/2017.
 */

public class Place {
    private JsonObject meta;
    private PlaceResponse response;

    public JsonObject getMeta() {
        return meta;
    }

    public PlaceResponse getResponse() {
        return response;
    }
}
