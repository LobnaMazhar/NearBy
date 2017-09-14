package com.task.cognitev.nearby.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.task.cognitev.nearby.Adapter.PlacesAdapter;
import com.task.cognitev.nearby.Model.Place;
import com.task.cognitev.nearby.R;

import java.util.ArrayList;

/**
 * Created by Lobna on 9/14/2017.
 */

public class PlacesFragment extends Fragment {

    private RecyclerView placesList;
    private RecyclerView.LayoutManager layoutManager;
    private PlacesAdapter placesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places, container, false);

        placesList = rootView.findViewById(R.id.places_list);

        layoutManager = new LinearLayoutManager(getActivity());
        placesList.setLayoutManager(layoutManager);


        // TODO fetch real data
        ArrayList<Place> places = new ArrayList<>();
        placesAdapter = new PlacesAdapter(getActivity(), places);
        placesList.setAdapter(placesAdapter);

        return rootView;
    }
}
