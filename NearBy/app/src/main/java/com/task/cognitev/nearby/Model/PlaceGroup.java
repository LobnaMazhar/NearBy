package com.task.cognitev.nearby.Model;

import java.util.ArrayList;

/**
 * Created by Lobna on 9/16/2017.
 */

public class PlaceGroup {
    private String type;
    private String name;
    private ArrayList<PlaceItem> items;

    public ArrayList<PlaceItem> getItems() {
        return items;
    }
}