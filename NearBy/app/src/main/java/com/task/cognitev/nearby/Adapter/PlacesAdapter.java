package com.task.cognitev.nearby.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.task.cognitev.nearby.Model.PlaceItem;
import com.task.cognitev.nearby.ViewHolder.PlacesViewHolder;
import com.task.cognitev.nearby.R;

import java.util.ArrayList;

/**
 * Created by Lobna on 9/14/2017.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesViewHolder> {

    private Activity activity;
    private ArrayList<PlaceItem> places;

    public PlacesAdapter(Activity activity, ArrayList<PlaceItem> places) {
        this.activity = activity;
        this.places = places;
    }


    @Override
    public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = activity.getLayoutInflater().inflate(R.layout.item_place,
                parent, false);

        return new PlacesViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(PlacesViewHolder holder, int position) {
        holder.bind(places.get(position));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }
}
