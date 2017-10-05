package com.sanyuzhang.circular.viewpager.cvp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.sanyuzhang.circular.viewpager.cvp.R;
import com.sanyuzhang.circular.viewpager.cvp.adapter.CircularTabLayoutAdapter;
import com.sanyuzhang.circular.viewpager.cvp.util.CircularUtils;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.content.res.AppCompatResources.getDrawable;

/**
 * Created by j_cho on 2017/10/03.
 */

public class CircularTabLayout extends RecyclerView {

    /**
     * Tab default style
     */
    private static final int DEFAULT_HEIGHT = 48;

    private static final int TAB_MIN_WIDTH_MARGIN = 56;

    /**
     * time to scroll 1 inch
     */
    private static final float MILLISECONDS_PER_INCH = 25f;

    private static final float FLING_FACTOR = 0.15f;

    private static final float TRIGGER_OFFSET = 0.25f;

    /**
     * ItemDecoration of the RecyclerView.
     */
    private final Paint mSelectedIndicatorPaint;

    /**
     * circular ViewPager.
     */
    private ViewPager mViewPager;

    /**
     * the adapter of the CircularTabLayout
     */
    private CircularTabLayoutAdapter mCircularTabLayoutAdapter;

    private boolean mScrollingByManual;

    /**
     * Tab Padding.
     */
    private int mTabPaddingStart;

    private int mTabPaddingTop;

    private int mTabPaddingEnd;

    private int mTabPaddingBottom;

    /**
     * Tab Margins.
     */
    private int mTabMarginStart;

    private int mTabMarginTop;

    private int mTabMarginEnd;

    private int mTabMarginBottom;

    /**
     * Tab Texts.
     */
    private int mTabTextAppearance;

    private int mTabTextSize;

    private ColorStateList mTabTextColors;

    /**
     * Tab Background.
     */
    private int mTabBackgroundResId;

    /**
     * Tab Size.
     */
    private int mViewHeight;

    private int mTabMinWidth;

    private int mTabMaxWidth;

    private int mRequestedTabMaxWidth;

    private int mMode;

    private int mSelectedIndicatorHeight;

    /**
     * last selected position
     */
    private int mLastCurrentItem = CircularUtils.START_POSITION;

    private float mScrollMillisecondsPerInch;

    private ItemDecoration mItemDecoration = null;

    private boolean mScrollEnabled;

    private boolean mNeedAdjust;

    private int mFirstLeftWhenDragging;

    private View mCurrentCenterView;

    private int mMaxLeftWhenDragging = Integer.MIN_VALUE;

    private int mMinLeftWhenDragging = Integer.MAX_VALUE;

    private int mMaxTopWhenDragging = Integer.MIN_VALUE;

    private int mMinTopWhenDragging = Integer.MAX_VALUE;

    private float mTriggerOffset;

    private float mFlingFactor;

    private float mTouchSpan;

    /**
     * Constructor.
     *
     * @param context
     */
    public CircularTabLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor.
     *
     * @param context
     * @param attrs
     */
    public CircularTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor.
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CircularTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabLayout, defStyle, R.style.Widget_Design_TabLayout);
        // Padding
        int defaultPadding = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabPadding, 0);
        mTabPaddingStart = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingStart, defaultPadding);
        mTabPaddingTop = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingTop, defaultPadding);
        mTabPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingEnd, defaultPadding);
        mTabPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingBottom, defaultPadding);

        // Text
        mTabTextAppearance = typedArray.getResourceId(R.styleable.TabLayout_tabTextAppearance, R.style.TextAppearance_Design_Tab);
        mTabTextColors = loadTextColorFromTextAppearance(mTabTextAppearance);
        if (typedArray.hasValue(R.styleable.TabLayout_tabTextColor)) {
            mTabTextColors = typedArray.getColorStateList(R.styleable.TabLayout_tabTextColor);
        }
        if (typedArray.hasValue(R.styleable.TabLayout_tabSelectedTextColor)) {
            int selected = typedArray.getColor(R.styleable.TabLayout_tabSelectedTextColor, 0);
            mTabTextColors = createColorStateList(mTabTextColors.getDefaultColor(), selected);
        }

        mTabMinWidth = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabMinWidth, 0);
        mRequestedTabMaxWidth = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabMaxWidth, 0);
        mTabBackgroundResId = typedArray.getResourceId(R.styleable.TabLayout_tabBackground, 0);

        mMode = typedArray.getInt(R.styleable.TabLayout_tabMode, 1);

        // Indicator.
        mSelectedIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tabIndicatorHeight, 0);
        mSelectedIndicatorPaint = new Paint();
        mSelectedIndicatorPaint.setColor(typedArray.getColor(R.styleable.TabLayout_tabIndicatorColor, 0));
        typedArray.recycle();
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularTabLayout);
        mScrollMillisecondsPerInch = typedArray.getFloat(R.styleable.CircularTabLayout_scrollMillisecondsPerInch, MILLISECONDS_PER_INCH);

        int defaultMargin = typedArray.getDimensionPixelSize(R.styleable.CircularTabLayout_tabMargins, 0);
        mTabMarginStart = typedArray.getDimensionPixelSize(R.styleable.CircularTabLayout_tabMarginStart, defaultMargin);
        mTabMarginTop = typedArray.getDimensionPixelSize(R.styleable.CircularTabLayout_tabMarginTop, defaultMargin);
        mTabMarginEnd = typedArray.getDimensionPixelSize(R.styleable.CircularTabLayout_tabMarginEnd, defaultMargin);
        mTabMarginBottom = typedArray.getDimensionPixelSize(R.styleable.CircularTabLayout_tabMarginBottom, defaultMargin);

        mScrollEnabled = typedArray.getBoolean(R.styleable.CircularTabLayout_scrollEnabled, true);
        mFlingFactor = typedArray.getFloat(R.styleable.CircularTabLayout_flingFactor, FLING_FACTOR);
        mTriggerOffset = typedArray.getFloat(R.styleable.CircularTabLayout_triggerOffset, TRIGGER_OFFSET);

        mTabTextSize = typedArray.getDimensionPixelSize(R.styleable.CircularTabLayout_tabTextSize, -1);

        typedArray.recycle();

        initialize();
    }

    /**
     * @param defaultColor
     * @param selectedColor
     * @return
     */
    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        int[][] states = new int[2][];
        int[] colors = new int[2];
        byte i = 0;
        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        int var5 = i + 1;
        states[var5] = EMPTY_STATE_SET;
        colors[var5] = defaultColor;
        ++var5;
        return new ColorStateList(states, colors);
    }

    /**
     * @param textAppearanceResId
     * @return
     */
    private ColorStateList loadTextColorFromTextAppearance(int textAppearanceResId) {
        TypedArray typedArray = getContext().obtainStyledAttributes(textAppearanceResId, R.styleable.TextAppearance);

        ColorStateList var3;
        try {
            var3 = typedArray.getColorStateList(R.styleable.TextAppearance_android_textColor);
        } finally {
            typedArray.recycle();
        }
        return var3;
    }

    /**
     * @param dps
     * @return
     */
    private int dpToPx(int dps) {
        return Math.round(this.getResources().getDisplayMetrics().density * (float) dps);
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        switch (View.MeasureSpec.getMode(heightMeasureSpec)) {
            case View.MeasureSpec.AT_MOST:
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        Math.min(dpToPx(DEFAULT_HEIGHT), View.MeasureSpec.getSize(heightMeasureSpec)),
                        View.MeasureSpec.EXACTLY);
                break;
            case View.MeasureSpec.UNSPECIFIED:
                heightMeasureSpec =
                        View.MeasureSpec.makeMeasureSpec(dpToPx(DEFAULT_HEIGHT), View.MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int defaultTabMaxWidth;
        if (mMode == 1 && getChildCount() == 1) {
            View maxTabWidth = getChildAt(0);
            defaultTabMaxWidth = getMeasuredWidth();
            if (maxTabWidth.getMeasuredWidth() > defaultTabMaxWidth) {
                int childHeightMeasureSpec =
                        getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(),
                                maxTabWidth.getLayoutParams().height);
                int childWidthMeasureSpec =
                        View.MeasureSpec.makeMeasureSpec(defaultTabMaxWidth, View.MeasureSpec.EXACTLY);
                maxTabWidth.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }

        int maxTabWidth1 = mRequestedTabMaxWidth;
        defaultTabMaxWidth = getMeasuredWidth() - dpToPx(TAB_MIN_WIDTH_MARGIN);
        if (maxTabWidth1 == 0 || maxTabWidth1 > defaultTabMaxWidth) {
            maxTabWidth1 = defaultTabMaxWidth;
        }

        mTabMaxWidth = maxTabWidth1;
        mViewHeight = getMeasuredHeight();

        setTabViewStyle();
    }

    private void initialize() {
        // SnappyLinearLayoutManagerの初期化
        SnappyLinearLayoutManager snappyLinearLayoutManager =
                new SnappyLinearLayoutManager(getContext(), HORIZONTAL, false);
        snappyLinearLayoutManager.setMillisecondsPerInch(mScrollMillisecondsPerInch);
        setLayoutManager(snappyLinearLayoutManager);
        setHasFixedSize(true);
        setIndicatorDecoration(
                new TabIndicatorDecorator(mSelectedIndicatorPaint, mSelectedIndicatorHeight));
    }

    public void setScrollEnable(boolean scrollEnable) {
        mScrollEnabled = scrollEnable;
    }

    public void setIndicatorDecoration(@NonNull ItemDecoration decoration) {
        if (mItemDecoration != null) {
            removeItemDecoration(mItemDecoration);
        }
        mItemDecoration = decoration;
        addItemDecoration(decoration);
    }

    public void setupWithViewPager(@NonNull CircularViewPager CircularViewPager) {
        setupWithViewPager((ViewPager) CircularViewPager);
    }

//    public void setupWithViewPager(@NonNull CircularViewPager CircularViewPager) {
//        // version of v4.viewpager
//        setupWithViewPager((ViewPager) CircularViewPager);
//    }

    private void setupWithViewPager(@NonNull ViewPager viewPager) {
        mViewPager = viewPager;

        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }

        setTabsFromPagerAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                if (!mScrollingByManual) {
                    smoothScrollToPosition(mCircularTabLayoutAdapter.moveToPosition(position));
                }
                mScrollingByManual = false;
            }
        });
    }

    public void setTabsFromPagerAdapter(@NonNull PagerAdapter adapter) {
        List<Tab> list = new ArrayList<>();
        if (adapter instanceof ITabSetting) {
            for (int i = 0, count = adapter.getCount(); i < count; ++i) {
                list.add(((ITabSetting) adapter).getTabItem(i));
            }
        } else {
            for (int i = 0, count = adapter.getCount(); i < count; ++i) {
                list.add(new Tab(getContext()).setText(adapter.getPageTitle(i)));
            }
        }

        mCircularTabLayoutAdapter =
                new CircularTabLayoutAdapter(getContext(), new ITabSelectedListener() {
                    @Override
                    public void onTabItemClicked(int position) {
                        smoothScrollToPositionFromTabSelect(position);
                    }
                });
        mCircularTabLayoutAdapter.setTabData(list);

        setTabViewStyle();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
                View targetView = linearLayoutManager.findViewByPosition(0);
                if (targetView != null) {
                    RecyclerView.LayoutParams params =
                            (RecyclerView.LayoutParams) targetView.getLayoutParams();
                    int start = linearLayoutManager.getPaddingLeft();
                    int end = linearLayoutManager.getWidth() - linearLayoutManager.getPaddingRight();
                    int left = linearLayoutManager.getDecoratedLeft(targetView) - params.leftMargin;
                    int right = linearLayoutManager.getDecoratedRight(targetView) + params.rightMargin;
                    int offset = (start + ((end - start) / 2 - (right - left) / 2)) - left;
                    linearLayoutManager.scrollToPositionWithOffset(
                            mCircularTabLayoutAdapter.getCurrentPosition(), offset);
                }
            }
        });
    }

    /**
     * set the style of tab view
     */
    private void setTabViewStyle() {
        if (mTabMaxWidth != 0
                && mViewHeight != 0
                && getAdapter() == null
                && mCircularTabLayoutAdapter != null) {
            mCircularTabLayoutAdapter.setTabViewStyle(mViewHeight, mTabBackgroundResId,
                    new int[]{mTabMarginStart, mTabMarginTop, mTabMarginEnd, mTabMarginBottom},
                    new int[]{mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd, mTabPaddingBottom},
                    mTabMinWidth, mTabMaxWidth, mTabTextSize, mTabTextAppearance, mTabTextColors);
            setAdapter(mCircularTabLayoutAdapter);
        }
    }

    /**
     * scroll to certain tab by click event
     *
     * @param position
     */
    public void smoothScrollToPositionFromTabSelect(int position) {
        if (mLastCurrentItem == position) {
            return;
        }

        mScrollingByManual = true;

        smoothScrollToPosition(position);
    }

    @Override
    public int computeVerticalScrollRange() {
        if (mScrollEnabled) {
            return super.computeVerticalScrollRange();
        }
        return 0;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (mScrollEnabled) {
            return super.onInterceptTouchEvent(e);
        }
        return false;
    }

    @Override
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position);

        if (mLastCurrentItem == position) {
            return;
        }

        if (mViewPager != null) {
            mViewPager.setCurrentItem(
                    mCircularTabLayoutAdapter.calculateRealPositionFromDummyPosition(position));
        }

        mCircularTabLayoutAdapter.setCurrentPosition(position);

        mLastCurrentItem = position;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        boolean flinging =
                super.fling((int) (velocityX * mFlingFactor), (int) (velocityY * mFlingFactor));

        if (flinging) {
            adjustPositionX(velocityX);
        }

        return flinging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // recording the max/min value in touch track
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mCurrentCenterView != null) {
                    mMaxLeftWhenDragging = Math.max(mCurrentCenterView.getLeft(), mMaxLeftWhenDragging);
                    mMaxTopWhenDragging = Math.max(mCurrentCenterView.getTop(), mMaxTopWhenDragging);
                    mMinLeftWhenDragging = Math.min(mCurrentCenterView.getLeft(), mMinLeftWhenDragging);
                    mMinTopWhenDragging = Math.min(mCurrentCenterView.getTop(), mMinTopWhenDragging);
                }
                break;
        }

        return super.onTouchEvent(e);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        switch (state) {
            case SCROLL_STATE_DRAGGING:
                mNeedAdjust = true;
                mCurrentCenterView = CircularUtils.getCenterXChild(this);
                if (mCurrentCenterView != null) {
                    mFirstLeftWhenDragging = mCurrentCenterView.getLeft();
                }
                mTouchSpan = 0;
                break;
            case SCROLL_STATE_SETTLING:
                mNeedAdjust = false;
                if (mCurrentCenterView != null) {
                    mTouchSpan = mCurrentCenterView.getLeft() - mFirstLeftWhenDragging;
                } else {
                    mTouchSpan = 0;
                }
                mCurrentCenterView = null;
                break;
            case SCROLL_STATE_IDLE:
                if (mNeedAdjust) {
                    int targetPosition = CircularUtils.getCenterXChildPosition(this);
                    if (mCurrentCenterView != null) {
                        targetPosition = getChildAdapterPosition(mCurrentCenterView);
                        if (targetPosition == NO_POSITION) {
                            resetDraggingStatus(true);
                            smoothScrollToPosition(mLastCurrentItem);
                            return;
                        }
                        int spanX = mCurrentCenterView.getLeft() - mFirstLeftWhenDragging;
                        // if user is tending to cancel paging action, don't perform position changing
                        if (spanX > mCurrentCenterView.getWidth() * mTriggerOffset
                                && mCurrentCenterView.getLeft() >= mMaxLeftWhenDragging) {
                            targetPosition--;
                        } else if (spanX < mCurrentCenterView.getWidth() * -mTriggerOffset
                                && mCurrentCenterView.getLeft() <= mMinLeftWhenDragging) {
                            targetPosition++;
                        }
                    }
                    smoothScrollToPosition(getTargetPosition(targetPosition, getAdapter().getItemCount()));
                    mCurrentCenterView = null;
                }
                resetDraggingStatus(false);
                break;
        }
    }

    /**
     * reset dragging status
     *
     * @param resetAll
     */
    private void resetDraggingStatus(boolean resetAll) {
        if (resetAll) {
            mNeedAdjust = false;
            mTouchSpan = 0f;
            mCurrentCenterView = null;
        }

        // reset
        mMaxLeftWhenDragging = Integer.MIN_VALUE;
        mMinLeftWhenDragging = Integer.MAX_VALUE;
        mMaxTopWhenDragging = Integer.MIN_VALUE;
        mMinTopWhenDragging = Integer.MAX_VALUE;
    }

    private void adjustPositionX(int velocityX) {
        int childCount = getChildCount();
        if (childCount > 0) {
            int curPosition = CircularUtils.getCenterXChildPosition(this);
            int childWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            int flingCount = (int) (velocityX * mFlingFactor / childWidth);
            int targetPosition = curPosition + flingCount;
            targetPosition = Math.max(targetPosition, 0);
            targetPosition = Math.min(targetPosition, getAdapter().getItemCount() - 1);
            if (targetPosition == curPosition) {
                View centerXChild = CircularUtils.getCenterXChild(this);
                if (centerXChild != null) {
                    if (mTouchSpan > centerXChild.getWidth() * mTriggerOffset * mTriggerOffset
                            && targetPosition != 0) {
                        targetPosition--;
                    } else if (mTouchSpan < centerXChild.getWidth() * -mTriggerOffset
                            && targetPosition != getAdapter().getItemCount() - 1) {
                        targetPosition++;
                    }
                }
            }
            smoothScrollToPosition(getTargetPosition(targetPosition, getAdapter().getItemCount()));
        }
    }

    /**
     * limit the position between max and min
     *
     * @param position
     * @param count
     * @return
     */
    private int getTargetPosition(int position, int count) {
        if (position < 0) {
            return 0;
        }
        if (position >= count) {
            return count - 1;
        }
        return position;
    }

    public interface ITabSelectedListener {
        void onTabItemClicked(int position);
    }

    public static final class Tab {

        private Context mContext;

        private Object mTag;

        private Drawable mIcon;

        private CharSequence mText;

        private CharSequence mContentDesc;

        private ColorStateList mColorStateList;

        private View mCustomView;

        public Tab(Context context) {
            mContext = context;
        }

        public Object getTag() {
            return mTag;
        }

        public CircularTabLayout.Tab setTag(Object tag) {
            mTag = tag;
            return this;
        }

        public View getCustomView() {
            return mCustomView;
        }

        public CircularTabLayout.Tab setCustomView(View view) {
            mCustomView = view;
            return this;
        }

        public CircularTabLayout.Tab setCustomView(int layoutResId) {
            return setCustomView(LayoutInflater.from(mContext).inflate(layoutResId, null));
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public CircularTabLayout.Tab setIcon(Drawable icon) {
            mIcon = icon;
            return this;
        }

        public CircularTabLayout.Tab setIcon(int resId) {
            return setIcon(getDrawable(mContext, resId));
        }

        public CharSequence getText() {
            return mText;
        }

        public CircularTabLayout.Tab setText(CharSequence text) {
            mText = text;
            return this;
        }

        public CircularTabLayout.Tab setText(int resId) {
            return setText(mContext.getResources().getText(resId));
        }

        public CircularTabLayout.Tab setTextColorState(ColorStateList colorStateList) {
            mColorStateList = colorStateList;
            return this;
        }

        public ColorStateList getTextColorStateList() {
            return mColorStateList;
        }

        public CircularTabLayout.Tab setContentDescription(int resId) {
            return setContentDescription(mContext.getResources().getText(resId));
        }

        public CharSequence getContentDescription() {
            return mContentDesc;
        }

        public CircularTabLayout.Tab setContentDescription(CharSequence contentDesc) {
            mContentDesc = contentDesc;
            return this;
        }
    }
}