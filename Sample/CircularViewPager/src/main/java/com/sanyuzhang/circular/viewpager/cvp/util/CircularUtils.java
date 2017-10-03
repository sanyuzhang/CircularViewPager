package com.sanyuzhang.circular.viewpager.cvp.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by j_cho on 2017/10/03.
 */

public class CircularUtils {

    public static final int ITEM_COUNT = Integer.MAX_VALUE;

    public static final int START_POSITION = Integer.MAX_VALUE / 2;

    /**
     * @param size          real item size
     * @param dummyPosition dummy positon
     * @return
     */
    public static int calculateRealPositionFromDummyPosition(int size, int dummyPosition) {
        int offset = START_POSITION % size;
        int position = (dummyPosition - offset) % size;
        return position < 0 ? position + size : position;
    }

    /**
     * @param size                 real item size
     * @param currentDummyPosition current dummy position
     * @param realPosition         current real position
     * @return
     */
    public static int calculateDummyPositionFromRealPosition(int size, int currentDummyPosition, int realPosition) {
        return currentDummyPosition + calculateLoopDistance(size, calculateRealPositionFromDummyPosition(size, currentDummyPosition), realPosition);
    }

    /**
     * @param size real item size
     * @param from
     * @param to
     * @return
     */
    public static int calculateLoopDistance(int size, int from, int to) {
        int diff = to - from;
        if (diff < -size / 2) {
            diff += size;
        }
        if (diff > size / 2) {
            diff -= size;
        }

        return diff;
    }

    /**
     * get the center RecyclerView
     *
     * @param recyclerView
     * @return
     */
    public static View getCenterXChild(@NonNull RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterX(recyclerView, child)) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * get the position of the center RecyclerView
     *
     * @param recyclerView
     * @return
     */
    public static int getCenterXChildPosition(@NonNull RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterX(recyclerView, child)) {
                    return recyclerView.getChildAdapterPosition(child);
                }
            }
        }
        return childCount;
    }

    /**
     * get the center RecyclerView
     *
     * @param recyclerView
     * @return
     */
    public static View getCenterYChild(@NonNull RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterY(recyclerView, child)) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * get the position of the center RecyclerView
     *
     * @param recyclerView
     * @return
     */
    public static int getCenterYChildPosition(@NonNull RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterY(recyclerView, child)) {
                    return recyclerView.getChildAdapterPosition(child);
                }
            }
        }
        return childCount;
    }

    /**
     * @param recyclerView
     * @param view
     * @return
     */
    public static boolean isChildInCenterX(@NonNull RecyclerView recyclerView, @NonNull View view) {
        int childCount = recyclerView.getChildCount();
        int[] lvLocationOnScreen = new int[2];
        recyclerView.getLocationOnScreen(lvLocationOnScreen);
        int middleX = lvLocationOnScreen[0] + recyclerView.getWidth() / 2;
        if (childCount > 0) {
            int[] vLocationOnScreen = new int[2];
            view.getLocationOnScreen(vLocationOnScreen);
            int width = view.getWidth();
            if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + width >= middleX) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param recyclerView
     * @param view
     * @return
     */
    public static boolean isChildInCenterY(@NonNull RecyclerView recyclerView, @NonNull View view) {
        int childCount = recyclerView.getChildCount();
        int[] lvLocationOnScreen = new int[2];
        recyclerView.getLocationOnScreen(lvLocationOnScreen);
        int middleY = lvLocationOnScreen[1] + recyclerView.getHeight() / 2;
        if (childCount > 0) {
            int[] vLocationOnScreen = new int[2];
            view.getLocationOnScreen(vLocationOnScreen);
            if (vLocationOnScreen[1] <= middleY && vLocationOnScreen[1] + view.getHeight() >= middleY) {
                return true;
            }
        }
        return false;
    }
}
