package com.machinestalk.stayhome.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;

import com.machinestalk.android.utilities.FontUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.R;

/**
 * Created on 12/21/2016.
 */

public class TextView extends com.machinestalk.android.components.TextView {

    public TextView( Context context ) {
        super( context );
        initTextView( null, 0 );
    }

    public TextView( Context context, AttributeSet attrs ) {
        super( context, attrs );
        initTextView( attrs, 0 );
    }

    public TextView( Context context, AttributeSet attrs, int defStyle ) {
        super( context, attrs, defStyle );
        initTextView( attrs, defStyle );
    }

    private void initTextView(AttributeSet attrs, int defStyle ) {

        if ( isInEditMode() ) {
            return;
        }

        if ( attrs == null ) {
            return;
        }

        TypedArray attributes = getContext().obtainStyledAttributes( attrs, com.machinestalk.android.R.styleable.Generic, defStyle, 0 );

        setCustomTypefaceTextView( attributes );

        attributes.recycle();
    }

    private void setCustomTypefaceTextView(TypedArray attributes ) {

        String fontPathFromAssets;

        int attributeResourceValue = attributes.getResourceId( com.machinestalk.android.R.styleable.Generic_font_path_from_assets, -1 );

        fontPathFromAssets = ( attributeResourceValue < 0 ) ?
                attributes.getString( com.machinestalk.android.R.styleable.Generic_font_path_from_assets ) :
                getContext().getString( attributeResourceValue );

        if ( StringUtility.isEmptyOrNull( fontPathFromAssets ) ) {
            fontPathFromAssets = getContext().getString( R.string.font_light );
        }

        if ( StringUtility.isEmptyOrNull( fontPathFromAssets ) ) {
            return;
        }

        Typeface typeface = FontUtility.getFontFromAssets( fontPathFromAssets, getContext() );

        if ( typeface == null ) {
            return;
        }

        setTypeface( typeface, Typeface.NORMAL );
    }

    @Override
    public void setTextAppearance( int resId ) {
        if ( Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ) {
            super.setTextAppearance( resId );
        } else {
            super.setTextAppearance( getContext(), resId );
        }
    }

    public void setHtml( String htmlText ) {
        Spanned result;
        if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ) {
            result = Html.fromHtml( htmlText, Html.FROM_HTML_MODE_LEGACY );
        } else {
            result = Html.fromHtml( htmlText );
        }
        setText( result );
    }
}
