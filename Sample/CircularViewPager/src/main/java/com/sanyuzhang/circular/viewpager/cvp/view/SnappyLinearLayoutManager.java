package com.sanyuzhang.circular.viewpager.cvp.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.sanyuzhang.circular.viewpager.cvp.adapter.CircularTabLayoutAdapter;

/**
 * Created by j_cho on 2017/10/03.
 */

public class SnappyLinearLayoutManager extends LinearLayoutManager {

    public static final String TAG = SnappyLinearLayoutManager.class.getSimpleName();

    private float mMillisecondsPerInch;

    public SnappyLinearLayoutManager(Context context) {
        this(context, VERTICAL, false);
    }

    public SnappyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setMillisecondsPerInch(float millisecondsPerInch) {
        mMillisecondsPerInch = millisecondsPerInch;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

            // I want a behavior where the scrolling always snaps to the beginning of
            // the list. Snapping to end is also trivial given the default implementation.
            // If you need a different behavior, you may need to override more
            // of the LinearSmoothScrolling methods.
            protected int getHorizontalSnapPreference() {
                return SNAP_TO_ANY;
            }

            protected int getVerticalSnapPreference() {
                return SNAP_TO_START;
            }

            @Override
            public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                if (snapPreference != SNAP_TO_ANY) {
                    return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
                } else {
                    int viewWidth = viewEnd - viewStart;
                    int boxWidth = boxEnd - boxStart;
                    return (boxStart + (boxWidth / 2 - viewWidth / 2)) - viewStart;
                }
            }

            @Override
            public int calculateDxToMakeVisible(View view, int snapPreference) {
                return super.calculateDxToMakeVisible(view, snapPreference);
            }

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return SnappyLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return mMillisecondsPerInch / displayMetrics.densityDpi;
            }
        };

        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();

        if (adapter instanceof CircularTabLayoutAdapter) {
            CircularTabLayoutAdapter CircularTabLayoutAdapter = (CircularTabLayoutAdapter) adapter;
            CircularTabLayoutAdapter.setCurrentPosition(position);
        }
    }
}
