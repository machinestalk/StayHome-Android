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

public class EditText extends com.machinestalk.android.components.EditText {
    private boolean isRtl;

    public EditText( Context context ) {
        super( context );
        initEditText( null, 0 );
    }

    public EditText( Context context, AttributeSet attrs ) {
        super( context, attrs );
        initEditText( attrs, 0 );
    }

    public EditText( Context context, AttributeSet attrs, int defStyle ) {
        super( context, attrs, defStyle );
        initEditText( attrs, defStyle );
    }

    private void initEditText(AttributeSet attrs, int defStyle ) {
        if ( isInEditMode() ) {
            return;
        }

        if ( attrs == null ) {
            return;
        }

        TypedArray attributes = getContext().obtainStyledAttributes( attrs, com.machinestalk.android.R.styleable.Generic, defStyle, 0 );

        setCustomTypefaceEditText( attributes );

        attributes.recycle();
    }

    public void setCustomTypefaceEditText(TypedArray attributes ) {

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



    public Typeface getTypeface() {
        String   fontPathFromAssets = getContext().getString( R.string.font_light );
        Typeface typeface           = FontUtility.getFontFromAssets( fontPathFromAssets, getContext() );
        return typeface;
    }

    public void setTypefaceForArabic(Typeface typeface){
        setTypeface( typeface, Typeface.NORMAL );
    }
}
