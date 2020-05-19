package com.machinestalk.stayhome.utils;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.machinestalk.stayhome.R;

/**
 * Created by asher.ali on 9/7/2017.
 */

public class ClickSpan extends ClickableSpan {

    private OnClickListener mListener;
    private Context mContext;
    public ClickSpan(Context context, OnClickListener listener) {
        mListener = listener;
        mContext = context;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mContext.getResources().getColor(R.color.blue_color));
        ds.setUnderlineText(true);
    }

    @Override
    public void onClick(View widget) {
        if (mListener != null) mListener.onClick();
    }

    public interface OnClickListener {
        void onClick();
    }
}
