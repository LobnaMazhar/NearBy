package com.task.cognitev.nearby.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.task.cognitev.nearby.R;

/**
 * Created by Lobna on 9/14/2017.
 */

public class ErrorFragment extends Fragment {

    private ImageView errorImage;
    private TextView errorText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_error, container, false);

        errorImage = rootView.findViewById(R.id.errorImage);
        errorText = rootView.findViewById(R.id.errorText);

        return rootView;
    }
}
