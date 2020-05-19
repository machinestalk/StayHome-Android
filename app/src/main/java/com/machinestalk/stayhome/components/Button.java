package com.machinestalk.stayhome.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.machinestalk.android.utilities.FontUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.R;

/**
 * Created on 12/21/2016.
 */

public class Button extends com.machinestalk.android.components.Button {

    public Button( Context context ) {
        super( context );
        initButton( null, 0 );
    }

    public Button( Context context, AttributeSet attrs ) {
        super( context, attrs );
        initButton( attrs, 0 );
    }

    public Button( Context context, AttributeSet attrs, int defStyle ) {
        super( context, attrs, defStyle );
        initButton( attrs, defStyle );
    }


    private void initButton(AttributeSet attrs, int defStyle ) {

        if ( isInEditMode() ) {
            return;
        }

        if ( attrs == null ) {
            return;
        }

        TypedArray attributes = getContext().obtainStyledAttributes( attrs, com.machinestalk.android.R.styleable.Generic, defStyle, 0 );

        setCustomTypefaceButton( attributes );

        attributes.recycle();
    }

    private void setCustomTypefaceButton(TypedArray attributes ) {

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
}
