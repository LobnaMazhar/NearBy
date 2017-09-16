package com.task.cognitev.nearby.Model;

import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by Lobna on 9/16/2017.
 */

public class PlaceResponse {
    private JsonObject suggestedFilters;
    private String headerLocation;
    private String headerFullLocation;
    private String headerLocationGranularity;
    private int totalResults;
    private JsonObject suggestedBounds;
    private ArrayList<PlaceGroup> groups;

    public ArrayList<PlaceGroup> getGroups() {
        return groups;
    }
}
