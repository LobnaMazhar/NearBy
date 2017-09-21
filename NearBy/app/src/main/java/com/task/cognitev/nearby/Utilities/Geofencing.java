package com.task.cognitev.nearby.Utilities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

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
    private Activity activity;
    private static Geofencing instance;

    private Geofencing(GoogleApiClient googleApiClient, Activity activity) {
        this.googleApiClient = googleApiClient;
        this.activity = activity;
        this.geofenceList = new ArrayList<>();
    }

    public static synchronized Geofencing getInstance(GoogleApiClient googleApiClient, Activity activity) {
        if (instance == null) {
            instance = new Geofencing(googleApiClient, activity);
        }
        return instance;
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
            Log.e(TAG, securityException.getMessage());
        }
    }

    public void updateGeofencesList(double latitude, double longitude) {
        geofenceList.clear();

        Geofence geofence = new Geofence.Builder()
                .setRequestId(String.valueOf(latitude) + ",," + String.valueOf(longitude))
                .setExpirationDuration(EXPIRATION_DURATION)
                .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        geofenceList.add(geofence);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(activity, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(activity, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, String.format("Error adding/removing geofence : %s",
                result.getStatus().toString()));
    }
}
