package com.task.cognitev.nearby.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.task.cognitev.nearby.Model.Place;
import com.task.cognitev.nearby.Model.PlaceItem;
import com.task.cognitev.nearby.Model.PlaceVenue;
import com.task.cognitev.nearby.R;

/**
 * Created by Lobna on 9/14/2017.
 */

public class PlacesViewHolder extends RecyclerView.ViewHolder {

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeAddress;

    public PlacesViewHolder(View itemView) {
        super(itemView);

        placeImage = itemView.findViewById(R.id.place_image);
        placeName = itemView.findViewById(R.id.place_name);
        placeAddress = itemView.findViewById(R.id.place_address);
    }

    public void bind(PlaceItem place){
        PlaceVenue placeVenue = place.getVenue();
        // TODO image
        placeName.setText(placeVenue.getName());
        placeAddress.setText(placeVenue.getLocation().getAddress());
    }
}
