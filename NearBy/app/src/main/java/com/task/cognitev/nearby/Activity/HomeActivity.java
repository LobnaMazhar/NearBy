package com.task.cognitev.nearby.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.task.cognitev.nearby.Fragment.PlacesFragment;
import com.task.cognitev.nearby.R;
import com.task.cognitev.nearby.Utilities.Geofencing;
import com.task.cognitev.nearby.Utilities.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    public static final int PLACES_FRAGMENT_ID = 726;

    private static GoogleApiClient googleApiClient;
    public static Geofencing geofencing;

    private static final String PLACES_FRAGMENT_TAG = "placesFragment";
    private static FragmentManager fragmentManager;
    @BindView(R.id.swipeToRefresh)
    public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.loadingLayout)
    public LinearLayout loadingLayout;

    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary)
                , getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(this);

        firstTime = true;

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
        geofencing = Geofencing.getInstance(googleApiClient, this);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (firstTime)
            getData();
        firstTime = false;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "API Client Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "API Client Connection Failed!");
    }

    public static void getData() {
        pushFragment(PLACES_FRAGMENT_ID);
    }

    public static void pushFragment(int fragmentID) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        String tag;
        Fragment fragment;
        switch (fragmentID) {
            case PLACES_FRAGMENT_ID:
                tag = PLACES_FRAGMENT_TAG;
                fragment = new PlacesFragment();
                break;

            default:
                try {
                    throw new Exception("Invalid Fragment ID ->> " + String.valueOf(fragmentID));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    return;
                }

        }
        fragmentTransaction.replace(R.id.content_frame, fragment, tag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.action_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void changeLocation(){
        Fragment placesFragment = fragmentManager.findFragmentByTag(PLACES_FRAGMENT_TAG);
        if(placesFragment != null)
            ((PlacesFragment) placesFragment).getUserLocation();
        else getData();
    }

    @Override
    public void onRefresh() {
        changeLocation();
    }
}
