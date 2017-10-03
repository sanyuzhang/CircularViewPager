package com.sanyuzhang.circular.viewpager.cvp.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.sanyuzhang.circular.viewpager.cvp.util.CircularUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by j_cho on 2017/10/03.
 */

public class CircularViewPager extends ViewPager {

    private int mCurrentPosition = CircularUtils.START_POSITION;

    private Map<OnPageChangeListener, InternalOnPageChangeListener> mOnPageChangeListeners;

    private FragmentPagerAdapter mOriginalAdapter;

    private InternalFragmentPagerAdapter mInternalFragmentPagerAdapter;

    /**
     * Constructor.
     *
     * @param context
     */
    public CircularViewPager(Context context) {
        this(context, null);
    }

    /**
     * Constructor.
     *
     * @param context
     * @param attrs
     */
    public CircularViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        mOnPageChangeListeners = new HashMap<>();
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(movetoPos(item));
    }

    @Override
    public PagerAdapter getAdapter() {
        return mOriginalAdapter;
    }

    @Override
    @Deprecated
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(null);
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListeners.put(listener, new InternalOnPageChangeListener(listener));
        super.addOnPageChangeListener(mOnPageChangeListeners.get(listener));
    }

    @Override
    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        super.removeOnPageChangeListener(mOnPageChangeListeners.remove(listener));
    }

    private int toRealPos(int dummyPos) {
        int size = mOriginalAdapter.getCount();
        int offset = size * 3 / 2 % size;
        int pos = (dummyPos - offset) % size;
        pos = pos < 0 ? pos + size : pos;
        return pos;
    }

    private int toDummyPos(int realPos) {
        int currentRealPos = toRealPos(mCurrentPosition);
        return mCurrentPosition + calcLoopDistance(currentRealPos, realPos);
    }

    private int calcLoopDistance(int from, int to) {
        int size = mOriginalAdapter.getCount();
        int diff = to - from;
        if (diff < -size / 2) {
            diff += size;
        }
        if (diff > size / 2) {
            diff -= size;
        }
        return diff;
    }

    public int movetoPos(int realPos) {
        int dummyPos = toDummyPos(realPos);
        setCurrentPosition(dummyPos);
        return dummyPos;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    private void setCurrentPosition(int pos) {
        mCurrentPosition = pos;
    }

    public boolean isSelectedItem(int pos) {
        return mCurrentPosition == pos;
    }

    public void setFragmentAdapter(@NonNull FragmentPagerAdapter adapter,
                                   @NonNull FragmentManager fm) {
        mOriginalAdapter = adapter;
        mInternalFragmentPagerAdapter = new InternalFragmentPagerAdapter(fm);

        super.setAdapter(mInternalFragmentPagerAdapter);
        mCurrentPosition = mOriginalAdapter.getCount() * 3 / 2;
        super.setCurrentItem(mOriginalAdapter.getCount() * 3 / 2, false);

        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_IDLE) {
                    int size = mOriginalAdapter.getCount();
                    if (mCurrentPosition < size / 2 + size % 2) {
                        mInternalFragmentPagerAdapter.shiftIndex(false);
                    } else if (mCurrentPosition > size * 2 + size / 2) {
                        mInternalFragmentPagerAdapter.shiftIndex(true);
                    }
                }
            }
        });
    }

    private class InternalFragmentPagerAdapter extends FragmentPagerAdapter {

        private Map<String, Integer> mTagPositionMap;

        public InternalFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

            mTagPositionMap = new HashMap<>();
        }

        @Override
        public Fragment getItem(int position) {
            return mOriginalAdapter.getItem(toRealPos(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mTagPositionMap.put(fragment.getTag(), position);
            return fragment;
        }

        @Override
        public long getItemId(int position) {
            return toRealPos(position);
        }

        @Override
        public int getCount() {
            return mOriginalAdapter.getCount() * 3;
        }

        @Override
        public int getItemPosition(Object object) {
            return mTagPositionMap.get(((Fragment) object).getTag());
        }

        public void shiftIndex(boolean left) {
            int size = mOriginalAdapter.getCount();

            if (left) {
                for (Map.Entry<String, Integer> entry : mTagPositionMap.entrySet()) {
                    entry.setValue(entry.getValue() - size);
                }
                mCurrentPosition = mCurrentPosition - size;
            } else {
                for (Map.Entry<String, Integer> entry : mTagPositionMap.entrySet()) {
                    entry.setValue(entry.getValue() + size);
                }
                mCurrentPosition = mCurrentPosition + size;
            }
            notifyDataSetChanged();
        }
    }

    private class InternalOnPageChangeListener implements OnPageChangeListener {

        private final OnPageChangeListener mOriginalOnPageChangeListener;

        public InternalOnPageChangeListener(OnPageChangeListener org) {
            mOriginalOnPageChangeListener = org;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mOriginalOnPageChangeListener.onPageScrolled(toRealPos(position), positionOffset,
                    positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            mOriginalOnPageChangeListener.onPageSelected(toRealPos(position));
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mOriginalOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }
}
