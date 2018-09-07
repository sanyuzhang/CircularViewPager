package com.sanyuzhang.circular.viewpager.sample;

import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class SampleFragment extends Fragment {

    public static SampleFragment newInstance(String message) {
        SampleFragment fragment = new SampleFragment();
        Bundle arg = new Bundle();
        arg.putString("message", message);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arg = getArguments();
        ((TextView) view.findViewById(R.id.message)).setText(arg.getString("message", "1"));
    }
}
