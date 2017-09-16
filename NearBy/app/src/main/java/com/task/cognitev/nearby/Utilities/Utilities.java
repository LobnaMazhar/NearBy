package com.task.cognitev.nearby.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.task.cognitev.nearby.R;

/**
 * Created by Lobna on 9/16/2017.
 */

public class Utilities {
    public static boolean checkNetworkConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static void noInternet(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Internet connection");
        builder.setMessage("Connect to a network");

        builder.setPositiveButton("OK", null);
        builder.show();
    }

    public static String getOperationalMode(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String operationalMode = sharedPreferences.getString(context.getString(R.string.operationalModeKey),
                context.getResources().getString(R.string.operationalModeDefaultValue));
        return operationalMode;
    }

    public static boolean checkLocationAvailability(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    public static void noLocation(final Activity activity){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("Location")
                .setMessage("Location is not enabled");
        dialog.setPositiveButton("Open Location settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(myIntent);
                //get gps
            }
        });
        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }
}
