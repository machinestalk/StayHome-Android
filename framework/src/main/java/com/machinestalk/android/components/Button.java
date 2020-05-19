package com.machinestalk.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.machinestalk.android.R;
import com.machinestalk.android.utilities.FontUtility;
import com.machinestalk.android.utilities.StringUtility;

/**
 * {@link android.widget.Button}'s subclass to assist in overriding default
 * drawing behavior. Mainly to allow single point of change across the whole
 * application.
 *
 */
public class Button extends android.widget.Button {

	/**
	 * Instantiates a new button.
	 * 
	 * @param context the context
	 */
	public Button (Context context) {

		super(context);
		init (null, 0);
	}

	/**
	 * Instantiates a new button.
	 * 
	 * @param context the context
	 * @param attrs the attrs
	 */
	public Button (Context context, AttributeSet attrs) {

		super(context, attrs);
		init(attrs, 0);
	}

	/**
	 * Instantiates a new button.
	 * 
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public Button (Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init (AttributeSet attrs, int defStyle) {

		if(isInEditMode()) {
			return;
		}

		if (attrs == null) {
			return;
		}

		TypedArray attributes = getContext ().obtainStyledAttributes (attrs, R.styleable.Generic, defStyle, 0);


        setCustomTypeface (attributes);

		attributes.recycle();
	}

	private void setCustomTypeface (TypedArray attributes) {

		String fontPathFromAssets;

		int attributeResourceValue = attributes.getResourceId (R.styleable.Generic_font_path_from_assets, -1);

		fontPathFromAssets = (attributeResourceValue < 0) ? attributes.getString(R.styleable.Generic_font_path_from_assets) : getContext().getString(attributeResourceValue);


		if(StringUtility.isEmptyOrNull(fontPathFromAssets)){
			fontPathFromAssets = getContext().getString(R.string.font_default);
		}

		if(StringUtility.isEmptyOrNull(fontPathFromAssets)) {
			return;
		}


		Typeface typeface = FontUtility.getFontFromAssets (fontPathFromAssets, getContext ());

		if(typeface == null) {
			return;
		}

		setTypeface (typeface, Typeface.NORMAL);
	}
}
