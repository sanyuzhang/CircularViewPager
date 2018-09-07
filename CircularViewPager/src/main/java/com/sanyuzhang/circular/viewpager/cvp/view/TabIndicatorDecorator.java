package com.sanyuzhang.circular.viewpager.cvp.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sanyuzhang.circular.viewpager.cvp.adapter.CircularTabLayoutAdapter;

/**
 * Created by j_cho on 2017/10/03.
 */

public class TabIndicatorDecorator extends RecyclerView.ItemDecoration {

    private Paint mPaint = new Paint();

    private int mIndicationHeight;

    public TabIndicatorDecorator(Paint paint, int height) {
        mPaint = paint;
        mIndicationHeight = height;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        RecyclerView.Adapter<?> adapter = parent.getAdapter();

        if (!(adapter instanceof CircularTabLayoutAdapter)) {
            return;
        }

        CircularTabLayoutAdapter CircularTabLayoutAdapter = (CircularTabLayoutAdapter) adapter;
        View target = parent.getLayoutManager().findViewByPosition(CircularTabLayoutAdapter.getCurrentPosition());

        if (target != null) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) target.getLayoutParams();
            int top = target.getBottom() + params.bottomMargin;
            int bottom = top + mIndicationHeight;
            c.drawRect(target.getLeft(), top, target.getRight(), bottom, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, mIndicationHeight);
    }
}