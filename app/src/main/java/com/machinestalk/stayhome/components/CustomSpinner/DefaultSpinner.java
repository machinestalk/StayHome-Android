package com.machinestalk.stayhome.components.CustomSpinner;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;

import androidx.appcompat.widget.AppCompatSpinner;

public class DefaultSpinner extends AppCompatSpinner {

    AdapterView.OnItemSelectedListener listener;
    int prevPos = 0;
    public DefaultSpinner(Context context) {
        super(context);
    }

    public DefaultSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (position == getSelectedItemPosition() && prevPos == position) {
            getOnItemSelectedListener().onItemSelected(null, null, position, 0);
        }
        prevPos = position;
    }
}
