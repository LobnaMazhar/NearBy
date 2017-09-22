package com.task.cognitev.nearby.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.task.cognitev.nearby.Activity.HomeActivity;
import com.task.cognitev.nearby.Adapter.PlacesAdapter;
import com.task.cognitev.nearby.Connection.PlacesConnection;
import com.task.cognitev.nearby.Model.PlaceGroup;
import com.task.cognitev.nearby.Model.PlaceItem;
import com.task.cognitev.nearby.R;
import com.task.cognitev.nearby.Utilities.Geofencing;
import com.task.cognitev.nearby.Utilities.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lobna on 9/14/2017.
 */

public class PlacesFragment extends Fragment {

    private static final String TAG = PlacesFragment.class.getSimpleName();

    private static final int REQUEST_ACCESS_FINE_LOCATION_PERMISSION = 639;
    private static final String SAVED_PLACES_KEY = "placesKey";
    private static Activity activity;
    private static FusedLocationProviderClient fusedLocationProviderClient;
    private static Geofencing geofencing;
    private static ArrayList<PlaceGroup> places;
    private static PlacesAdapter placesAdapter;
    private final PlacesFragment thisFragment = this;
    @BindView(R.id.places_list)
    RecyclerView placesList;
    @BindView(R.id.noDataErrorLayout)
    LinearLayout noDataError;
    @BindView(R.id.connectionErrorLayout)
    LinearLayout connectionError;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places, container, false);

        ButterKnife.bind(this, rootView);

        activity = getActivity();

        layoutManager = new LinearLayoutManager(getActivity());
        placesList.setLayoutManager(layoutManager);

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_PLACES_KEY)) {
            setPlaces(places);
        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

            geofencing = HomeActivity.geofencing;
            if (geofencing != null) {
                getUserLocation();
            } else {
                Log.e(TAG, "Google api client is null");
            }
        }
        return rootView;
    }

    public void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder
                        .setTitle("Location permission")
                        .setMessage("Location permission is needed to detect nearby places")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_ACCESS_FINE_LOCATION_PERMISSION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                activity.finish();
                            }
                        })
                        .show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_FINE_LOCATION_PERMISSION);
            }
            return;
        }

        if (Utilities.checkLocationAvailability(activity)) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        geofencing.unRegisterAllGeofences();
                        geofencing.updateGeofencesList(latitude, longitude);
                        geofencing.registerAllGeofences();
                        try {
                            Log.v(TAG, "Latitude :: " + String.valueOf(latitude) + ",, Longitude :: " + String.valueOf(longitude));
                            PlacesConnection.getPlaces(activity, thisFragment,
                                    String.valueOf(latitude), String.valueOf(longitude));
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        showError(getString(R.string.somethingWrong));
                    }
                }
            }).addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to get location " + e.getMessage());
                }
            });
        } else {
            Utilities.noLocation((HomeActivity) activity, thisFragment);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION_PERMISSION:
                getUserLocation();
        }
    }

    public void setPlaces(ArrayList<PlaceGroup> placeslist) {
        places = placeslist;

        connectionError.setVisibility(View.GONE);
        noDataError.setVisibility(View.GONE);
        placesList.setVisibility(View.VISIBLE);

        ArrayList<PlaceItem> items = new ArrayList<>();
        placesAdapter = new PlacesAdapter(activity, items);
        placesList.setAdapter(placesAdapter);
        for (PlaceGroup placeGroup : places) {
            items.addAll(placeGroup.getItems());
            placesAdapter.notifyDataSetChanged();
        }

    }

    public void showError(String error) {
        if (error.equals(activity.getString(R.string.noData))) {
            placesList.setVisibility(View.GONE);
            connectionError.setVisibility(View.GONE);
            noDataError.setVisibility(View.VISIBLE);

        } else if (error.equals(activity.getString(R.string.somethingWrong))) {
            placesList.setVisibility(View.GONE);
            noDataError.setVisibility(View.GONE);
            connectionError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utilities.LOCATION_SETTINGS_RESULT_CODE:
                getUserLocation();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (places != null) {
            outState.putParcelableArrayList(SAVED_PLACES_KEY, places);
        }
    }
}