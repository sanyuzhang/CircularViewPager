package com.umizhang.android.view.circularviewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by j_cho on 2017/06/13.
 */

public class SampleFragment extends Fragment {

    private String mContent;

    public void setContent(String content) {
        mContent = content;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sample, container, false);
        if (!TextUtils.isEmpty(mContent)) {
            ((TextView) view.findViewById(R.id.content)).setText(mContent);
        }
        return view;
    }


}
