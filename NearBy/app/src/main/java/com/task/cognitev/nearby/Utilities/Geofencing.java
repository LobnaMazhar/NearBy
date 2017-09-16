package com.task.cognitev.nearby.Utilities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.task.cognitev.nearby.Receiver.GeofenceBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lobna on 9/16/2017.
 */

public class Geofencing implements ResultCallback {
    private static final String TAG = Geofencing.class.getSimpleName();

    private static final long EXPIRATION_DURATION = 5 * 60 * 60 * 1000; // 5 Hours
    private static final int GEOFENCE_RADIUS = 500;

    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;
    private GoogleApiClient googleApiClient;
    private Context context;

    public Geofencing(GoogleApiClient googleApiClient, Context context) {
        this.googleApiClient = googleApiClient;
        this.context = context;
    }

    public void registerAllGeofences() {
        // Check that the API client is connected and that the list has Geofences in it
        if (googleApiClient == null || !googleApiClient.isConnected() ||
                geofenceList == null || geofenceList.size() == 0) {
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }

    public void unRegisterAllGeofences() {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    googleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }

    public void updateGeofencesList(Location location) {
        geofenceList = new ArrayList<>();
        if (location == null) return;

        // Build a Geofence object
        Geofence geofence = new Geofence.Builder()
                .setRequestId(String.valueOf(location.getLatitude()) + ",," + String.valueOf(location.getLongitude()))
                .setExpirationDuration(EXPIRATION_DURATION)
                .setCircularRegion(location.getLatitude(), location.getLongitude(), GEOFENCE_RADIUS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        // Add it to the list
        geofenceList.add(geofence);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Result result) {
        /*Log.e(TAG, String.format("Error adding/removing geofence : %s",
                result.getStatus().toString()));*/
        // TODO on results leeh error ??
    }
}
