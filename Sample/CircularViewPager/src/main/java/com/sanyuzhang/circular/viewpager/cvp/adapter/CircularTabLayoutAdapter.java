package com.sanyuzhang.circular.viewpager.cvp.adapter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanyuzhang.circular.viewpager.cvp.R;
import com.sanyuzhang.circular.viewpager.cvp.util.CircularUtils;
import com.sanyuzhang.circular.viewpager.cvp.view.CircularTabLayout;

import java.util.List;

import static android.support.v7.content.res.AppCompatResources.getDrawable;

/**
 * Created by j_cho on 2017/10/03.
 */

public class CircularTabLayoutAdapter extends RecyclerView.Adapter<CircularTabLayoutAdapter.TabViewHolder> {

    private Context mContext;

    private CircularTabLayout.ITabSelectedListener mTabSelectedListener;

    private int mCurrentPosition = CircularUtils.START_POSITION;

    /**
     * real tab
     */
    private List<CircularTabLayout.Tab> mObjectList;

    /**
     * ViewHolder.itemView listener
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mTabSelectedListener.onTabItemClicked((Integer) v.getTag());
        }
    };

    /**
     * TabView Style
     */
    private int mTabViewMinimumHeight;

    private int mTabViewBackground;

    private int[] mTabViewPadding;

    private int[] mTabViewMarings;

    private int mTabMinWidth;

    private int mTabMaxWidth;

    private int mTabTextAppearance;

    private ColorStateList mTabTextColors;

    private int mTabTextSize;

    /**
     * Constructor.
     *
     * @param context
     * @param tabSelectedListener
     */
    public CircularTabLayoutAdapter(@NonNull Context context, @NonNull CircularTabLayout.ITabSelectedListener tabSelectedListener) {
        mContext = context;
        mTabSelectedListener = tabSelectedListener;
    }

    @Override
    public TabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TabView tabView = createTabView();
        tabView.setId(R.id.recycler_tab_layout_tab_view);
        return new TabViewHolder(tabView);
    }

    @Override
    public void onBindViewHolder(TabViewHolder holder, int position) {
        int realFragmentPosition = calculateRealPositionFromDummyPosition(position);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.tabView.update(mObjectList.get(realFragmentPosition));
        holder.tabView.setSelected(isSelectedItem(position));
    }

    @Override
    public int getItemCount() {
        return CircularUtils.ITEM_COUNT;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    /**
     * @param position
     */
    public void setCurrentPosition(int position) {
        int previousPosition = mCurrentPosition;
        mCurrentPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(mCurrentPosition);
    }

    /**
     * @param position
     * @return
     */
    public boolean isSelectedItem(int position) {
        return mCurrentPosition == position;
    }

    /**
     * calculate the real position from DummyPosition
     *
     * @param dummyPosition
     * @return
     */
    public int calculateRealPositionFromDummyPosition(int dummyPosition) {
        return CircularUtils.calculateRealPositionFromDummyPosition(mObjectList.size(), dummyPosition);
    }

    /**
     * calculate DummyPosition from real position.
     *
     * @param realPosition
     * @return
     */
    public int calculateDummyPositionFromRealPosition(int realPosition) {
        return CircularUtils.calculateDummyPositionFromRealPosition(mObjectList.size(), mCurrentPosition,
                realPosition);
    }

    /**
     * @param realPosition
     * @return
     */
    public int moveToPosition(int realPosition) {
        int dummyPosition = calculateDummyPositionFromRealPosition(realPosition);
        setCurrentPosition(dummyPosition);
        return dummyPosition;
    }

    public void setTabViewStyle(int tabViewMinimumHeight, int tabViewBackground, int[] tabViewMargins,
                                int[] tabViewPadding, int tabMinWidth, int tabMaxWidth,
                                int tabTextSize, int tabTextAppearance, ColorStateList tabTextColors) {
        mTabViewMinimumHeight = tabViewMinimumHeight;
        mTabViewBackground = tabViewBackground;
        mTabViewPadding = tabViewPadding;
        mTabViewMarings = tabViewMargins;
        mTabMinWidth = tabMinWidth;
        mTabMaxWidth = tabMaxWidth;
        mTabTextSize = tabTextSize;
        mTabTextAppearance = tabTextAppearance;
        mTabTextColors = tabTextColors;
    }

    /**
     * @return
     */
    protected TabView createTabView() {
        TabView tabView = new TabView(mContext, mTabViewBackground, mTabViewMarings, mTabViewPadding, mTabMinWidth,
                mTabMaxWidth, mTabTextSize, mTabTextAppearance, mTabTextColors);
        tabView.setFocusable(true);
        tabView.setMinimumHeight(mTabViewMinimumHeight);
        return tabView;
    }

    /**
     * RecyclerView.Tab list
     *
     * @param objectList
     */
    public void setTabData(List<CircularTabLayout.Tab> objectList) {
        mObjectList = objectList;
    }

    static class TabViewHolder extends RecyclerView.ViewHolder {

        TabView tabView;

        public TabViewHolder(View itemView) {
            super(itemView);
            tabView = (TabView) itemView.findViewById(R.id.recycler_tab_layout_tab_view);
        }
    }

    static class TabView extends LinearLayout implements View.OnLongClickListener {

        private static final int MAX_TAB_TEXT_LINES = 2;

        private CircularTabLayout.Tab mTab;

        private TextView mTextView;

        private ImageView mIconView;

        private View mCustomView;

        private int mTabTextSize;

        private int mTabMinWidth;

        private int mTabMaxWidth;

        private int mTabTextAppearance;

        private ColorStateList mTabTextColors;

        public TabView(Context context, int background, int[] margins,
                       int[] paddings, int tabMinWidth, int tabMaxWidth,
                       int tabTextSize, int tabTextAppearance, ColorStateList tabTextColors) {
            super(context);
            if (background != 0) {
                setBackgroundDrawable(getDrawable(context, background));
            }

            ViewCompat.setPaddingRelative(this, paddings[0], paddings[1], paddings[2], paddings[3]);

            mTabMinWidth = tabMinWidth;
            mTabMaxWidth = tabMaxWidth;
            mTabTextSize = tabTextSize;
            mTabTextAppearance = tabTextAppearance;
            mTabTextColors = tabTextColors;

            MarginLayoutParams marginLayoutParams = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);

            marginLayoutParams.leftMargin = margins[0];
            marginLayoutParams.topMargin = margins[1];
            marginLayoutParams.rightMargin = margins[2];
            marginLayoutParams.bottomMargin = margins[3];

            setLayoutParams(marginLayoutParams);

            setGravity(Gravity.CENTER);
        }

        public void setSelected(boolean selected) {
            boolean changed = isSelected() != selected;
            super.setSelected(selected);
            if (changed && selected) {
                sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
                if (mTextView != null) {
                    mTextView.setSelected(selected);
                }
                if (mIconView != null) {
                    mIconView.setSelected(selected);
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(event);
            event.setClassName(ActionBar.Tab.class.getName());
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(ActionBar.Tab.class.getName());
        }

        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (mTabMaxWidth != 0 && getMeasuredWidth() > mTabMaxWidth) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mTabMaxWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            } else if (mTabMinWidth > 0 && getMeasuredHeight() < mTabMinWidth) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mTabMinWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            }
        }

        final void update(CircularTabLayout.Tab tab) {
            ColorStateList tabTextColors = tab.getTextColorStateList() == null ? mTabTextColors : tab.getTextColorStateList();

            mTab = tab;
            View custom = tab.getCustomView();
            if (custom != null) {
                ViewParent icon = custom.getParent();
                if (icon != this) {
                    if (icon != null) {
                        ((ViewGroup) icon).removeView(custom);
                    }
                    addView(custom);
                }

                mCustomView = custom;
                if (mTextView != null) {
                    mTextView.setVisibility(View.GONE);
                }

                if (mIconView != null) {
                    mIconView.setVisibility(View.GONE);
                    mIconView.setImageDrawable(null);
                }
            } else {
                if (mCustomView != null) {
                    removeView(mCustomView);
                    mCustomView = null;
                }

                Drawable icon1 = tab.getIcon();
                CharSequence text = tab.getText();
                if (icon1 != null) {
                    if (mIconView == null) {
                        ImageView hasText = new ImageView(getContext());
                        LayoutParams textView = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        textView.gravity = Gravity.CENTER_VERTICAL;
                        hasText.setLayoutParams(textView);
                        addView(hasText, 0);
                        mIconView = hasText;
                    }

                    mIconView.setImageDrawable(icon1);
                    mIconView.setVisibility(View.VISIBLE);
                } else if (mIconView != null) {
                    mIconView.setVisibility(View.GONE);
                    mIconView.setImageDrawable(null);
                }

                boolean hasText1 = !TextUtils.isEmpty(text);
                if (hasText1) {
                    if (mTextView == null) {
                        AppCompatTextView textView1 = new AppCompatTextView(getContext());
                        textView1.setTextAppearance(getContext(), mTabTextAppearance);
                        if (mTabTextSize != -1) {
                            textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
                        }
                        textView1.setMaxLines(MAX_TAB_TEXT_LINES);
                        textView1.setEllipsize(TextUtils.TruncateAt.END);
                        textView1.setGravity(Gravity.CENTER);
                        if (tabTextColors != null) {
                            textView1.setTextColor(tabTextColors);
                        }
                        addView(textView1, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        mTextView = textView1;
                    }
                    mTextView.setText(text);
                    mTextView.setContentDescription(tab.getContentDescription());
                    mTextView.setVisibility(View.VISIBLE);
                } else if (mTextView != null) {
                    mTextView.setVisibility(View.GONE);
                    mTextView.setText(null);
                }

                if (mIconView != null) {
                    mIconView.setContentDescription(tab.getContentDescription());
                }

                if (!hasText1 && !TextUtils.isEmpty(tab.getContentDescription())) {
                    setOnLongClickListener(this);
                    setLongClickable(true);
                } else {
                    setOnLongClickListener(null);
                    setLongClickable(false);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            getLocationOnScreen(screenPos);
            final Context context = getContext();
            final int width = getWidth();
            final int height = getHeight();
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            Toast cheatSheet = Toast.makeText(context, mTab.getContentDescription(), Toast.LENGTH_SHORT);
            // display below the tab
            cheatSheet.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, (screenPos[0] + width / 2) - screenWidth / 2, height);
            cheatSheet.show();
            return true;
        }
    }
}
