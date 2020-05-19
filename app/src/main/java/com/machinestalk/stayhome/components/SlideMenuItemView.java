package com.machinestalk.stayhome.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.machinestalk.android.components.ImageView;
import com.machinestalk.stayhome.R;

public class SlideMenuItemView extends LinearLayout {
    private ImageView mIconMenuImageView;
    private ImageView mIconDeviceStatusMenuImageView;
    private TextView mTitleMenuTextView;
    private LinearLayout mContainerLinearLayout;
    private OnSlideMenuItemClickListener mOnSlideMenuItemClickListener;
    private boolean mIsSelected;
    private String mTitleMenu;
    private Drawable mIconMenuDrawable;
    private View mSelectedLine;

    public SlideMenuItemView(Context context) {
        super(context);
        initView(context, null);
    }

    public SlideMenuItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SlideMenuItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public ImageView getmIconDeviceStatusMenuImageView() {
        return mIconDeviceStatusMenuImageView;
    }

    private void initView(Context context, AttributeSet attrs) {

        View.inflate(context, R.layout.layout_dashboard_menu_item, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        mSelectedLine = findViewById(R.id.view_dashboard_item_selected_line);
        mIconMenuImageView = findViewById(R.id.ivDashboardMenuIcon);
        mIconDeviceStatusMenuImageView = findViewById(R.id.Dev_expiry_status_DashboardMenuIcon);
        mTitleMenuTextView = findViewById(R.id.txtDashboardMenuName);
        mContainerLinearLayout = findViewById(R.id.view_slide_menu_item_container);

        setupAttributes(attrs);
        setupView();
        initEvent();
    }

    private void initEvent() {

        mContainerLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSlideMenuItemClickListener != null) {
                    mOnSlideMenuItemClickListener.onItemClick(mTitleMenu, true);
                }
            }
        });
    }

    /**
     * @param attrs
     */
    private void setupAttributes(AttributeSet attrs) {
        TypedArray a = this.getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SlideMenuItemView, 0, 0);
        try {
            mTitleMenu = a.getString(R.styleable.SlideMenuItemView_titleSlideItem);
            mIconMenuDrawable = a.getDrawable(R.styleable.SlideMenuItemView_iconSlideItem);
            mIsSelected = a.getBoolean(R.styleable.SlideMenuItemView_selectedSlideItem, false);
        } finally {
            a.recycle();
        }
    }

    /**
     *
     */
    private void setupView() {

        if (mIconMenuDrawable != null) {
            mIconMenuImageView.setImageDrawable(mIconMenuDrawable);
        }

        if (!TextUtils.isEmpty(mTitleMenu)) {
            mTitleMenuTextView.setText(mTitleMenu.trim());
        }

        setItemSelected(mIsSelected);
    }

    /**
     * Set Listener to intercept switch actions  .
     *
     * @param listener
     */
    public void setOnSwitchListener(OnSlideMenuItemClickListener listener) {
        mOnSlideMenuItemClickListener = listener;
    }

    public void setItemSelected(boolean selected) {
        mTitleMenuTextView.setSelected(selected);
        mIconMenuImageView.setSelected(selected);
        mSelectedLine.setVisibility(selected ? VISIBLE : INVISIBLE);
    }



    /**
     *
     */
    public interface OnSlideMenuItemClickListener {

        /**
         * @param item
         * @param openFragment : if openFragment false mean that fragment is already opened
         *                     -> just change color text selector we press back button.
         */
        void onItemClick(String item, boolean openFragment);
    }

}
