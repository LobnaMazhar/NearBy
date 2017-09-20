package com.task.cognitev.nearby.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.task.cognitev.nearby.Fragment.PlacesFragment;
import com.task.cognitev.nearby.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final int PLACES_FRAGMENT_ID = 726;

    private static final String PLACES_FRAGMENT_TAG = "placesFragment";
    private static FragmentManager fragmentManager;
    @BindView(R.id.swipeToRefresh)
    public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.loadingLayout)
    public LinearLayout loadingLayout;

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
                fragment = fragmentManager.findFragmentByTag(tag);
                if (fragment != null)
                    fragment.onDestroyView();
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

        /*if (!fragment.isVisible())
            fragmentTransaction.addToBackStack(null);*/

        fragmentTransaction.replace(R.id.content_frame, fragment, tag).commit();
    }

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

        getData();
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

    /*@Override
    public void onResume() {
        if (Utilities.getOperationalMode(this).equals(this.getString(R.string.realtimeValue))) {
            getData();
        }
        super.onResume();
    }*/

    @Override
    public void onRefresh() {
        getData();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utilities.LOCATION_SETTINGS_RESULT_CODE:
                Fragment fragment = fragmentManager.findFragmentByTag(PLACES_FRAGMENT_TAG);
                if(fragment != null)
                    ((PlacesFragment)fragment).getUserLocation();
                else
                    getData();
        }
    }*/
}
