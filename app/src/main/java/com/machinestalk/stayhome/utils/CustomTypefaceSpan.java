package com.machinestalk.stayhome.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import com.machinestalk.android.utilities.FontUtility;
import com.machinestalk.stayhome.R;

/**
 * Created on 12/26/2016.
 */
public class CustomTypefaceSpan extends MetricAffectingSpan
{
    private final Typeface typeface;

    public CustomTypefaceSpan( Context context) {
        this.typeface = FontUtility.getFontFromAssets( context.getString( R.string.font_semi_bold ), context );
    }

    public CustomTypefaceSpan( Context context, String path) {
        this.typeface = FontUtility.getFontFromAssets( path, context );
    }

    @Override
    public void updateDrawState(final TextPaint drawState)
    {
        apply(drawState);
    }

    @Override
    public void updateMeasureState(final TextPaint paint)
    {
        apply(paint);
    }

    private void apply(final Paint paint)
    {
        final Typeface oldTypeface = paint.getTypeface();
        final int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
        final int fakeStyle = oldStyle & ~typeface.getStyle();

        if ((fakeStyle & Typeface.BOLD) != 0)
        {
            paint.setFakeBoldText(true);
        }

        if ((fakeStyle & Typeface.ITALIC) != 0)
        {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(typeface);
    }
}
