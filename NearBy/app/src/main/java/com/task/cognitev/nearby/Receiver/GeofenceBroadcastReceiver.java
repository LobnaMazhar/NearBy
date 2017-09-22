package com.task.cognitev.nearby.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.task.cognitev.nearby.Activity.HomeActivity;
import com.task.cognitev.nearby.R;
import com.task.cognitev.nearby.Utilities.Utilities;

/**
 * Created by Lobna on 9/16/2017.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        int geofenceTransition = GeofencingEvent.fromIntent(intent).getGeofenceTransition();
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                if (Utilities.getOperationalMode(context).equals(context.getString(R.string.realtimeKey))) {
                    HomeActivity.changeLocation();
                }
                break;
            default:
                Log.e(TAG, String.format("Unknown transition : %d", geofenceTransition));
                return;

        }
    }
}
