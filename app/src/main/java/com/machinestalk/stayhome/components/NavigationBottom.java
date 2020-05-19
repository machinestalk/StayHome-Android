package com.machinestalk.stayhome.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.utils.Util;

public class NavigationBottom extends LinearLayout {

    private AppCompatImageView mIconImageView;
    private TextView mTitleTextView;
    private View mSelectedLine;
    private String mTitle;
    private Drawable mImageDrawable;
    private boolean mIsSelected = false;
    private android.widget.TextView mNotificationIndexTextView;
    private boolean mIsNotification;
    private Context mContext;

    public NavigationBottom(Context context) {
        super(context);
        initView(context, null);
    }

    public NavigationBottom(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
      //  setupAttributes(attrs);
        initView(context, attrs);
    }

    public NavigationBottom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs ) {
        mContext = context;
        View.inflate(context, R.layout.view_navigation_bottom, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        mIconImageView = findViewById(R.id.viw_item_navigation_icon);
        mTitleTextView = findViewById(R.id.viw_item_navigation_title);
        mSelectedLine = findViewById(R.id.viw_item_navigation_selected_line);
        mNotificationIndexTextView = findViewById(R.id.viw_item_navigation_alert_index);

        setupAttributes(attrs);
        setupView();
    }

    /**
     * @param isSelected
     */
    public void setSelectedVIew(boolean isSelected) {
        mSelectedLine.setVisibility(isSelected ? VISIBLE : GONE);
    }

    public void setNotificationCount(int count){
        mNotificationIndexTextView.setVisibility(count > 0 ? VISIBLE : GONE);
        mNotificationIndexTextView.setText(String.valueOf(count > 99 ? mContext.getString(R.string.Gen_Gen_lbl_nine_plus) : count));
//        mNotificationIndexTextView.setText("");
    }
    /**
     *
     * @param attrs
     */
    private void setupAttributes(AttributeSet attrs) {
        TypedArray a = this.getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.NavigationBottom, 0, 0);

        try {
            mTitle = a.getString(R.styleable.NavigationBottom_titleItem);
            mImageDrawable = a.getDrawable(R.styleable.NavigationBottom_iconItem);
            mIsSelected = a.getBoolean(R.styleable.NavigationBottom_selectedItem, false);
            mIsNotification = a.getBoolean(R.styleable.NavigationBottom_notificationItem, false);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void setSelected(boolean selected) {
        mIsSelected = true;
        mTitleTextView.setTextColor(selected ? Util.getColor(mContext, R.color.blue_color) : Util.getColor(mContext, R.color.black));
    }

    /**
     *
     */
    private void setupView(){

        if (mImageDrawable != null){
            mIconImageView.setImageDrawable(mImageDrawable);
        }

        if ( !TextUtils.isEmpty( mTitle ) ) {
            mTitleTextView.setText( mTitle.trim() );
        }
        mTitleTextView.setTextColor(mIsSelected ? Util.getColor(mContext, R.color.blue_color) : Util.getColor(mContext, R.color.black));
        mSelectedLine.setVisibility(mIsSelected ? VISIBLE : GONE);
        mNotificationIndexTextView.setVisibility(mIsNotification ? VISIBLE : GONE);
    }
}
