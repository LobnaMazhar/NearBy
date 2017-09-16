package com.task.cognitev.nearby.Model;

import com.google.gson.JsonObject;

/**
 * Created by Lobna on 9/16/2017.
 */

public class PlaceItem {
    private JsonObject reasons;
    private PlaceVenue venue;
    private String referralId;

    public PlaceVenue getVenue() {
        return venue;
    }
}
