package com.task.cognitev.nearby.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.task.cognitev.nearby.Fragment.ErrorFragment;
import com.task.cognitev.nearby.Fragment.PlacesFragment;
import com.task.cognitev.nearby.R;

public class HomeActivity extends AppCompatActivity {

    public static final int PLACES_FRAGMENT_ID = 726;
    public static final int ERROR_FRAGMENT_ID = 103;

    private static final String PLACES_FRAGMENT_TAG = "placesFragment";
    private static final String ERROR_FRAGMENT_TAG = "errorFragment";

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        pushFragment(PLACES_FRAGMENT_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID){
            case R.id.action_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void pushFragment(int fragmentID){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        String tag;
        Fragment fragment;
        switch (fragmentID){
            case PLACES_FRAGMENT_ID:
                tag = PLACES_FRAGMENT_TAG;
                fragment = fragmentManager.findFragmentByTag(tag);
                if(fragment == null)
                    fragment = new PlacesFragment();
                break;

            case ERROR_FRAGMENT_ID:
                tag = ERROR_FRAGMENT_TAG;
                fragment = fragmentManager.findFragmentByTag(tag);
                if(fragment == null)
                    fragment = new ErrorFragment();
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

        if(!fragment.isVisible())
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.content_frame, fragment, tag).commit();
    }

    //TODO on shared pref. change list.
}
