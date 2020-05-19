package com.machinestalk.android.components;

import android.content.Context;
import android.util.AttributeSet;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.machinestalk.android.listeners.ImageLoadListener;
import com.machinestalk.android.utilities.StringUtility;


/**
 * Subclass of {@link android.widget.ImageView} with the added functionality of
 * seamlessly loading images from the internet.
 * 
 */
public class ImageView extends android.widget.ImageView{

	/**
	 * Instantiates a new image view.
	 * 
	 * @param context the context
	 */
	public ImageView (Context context) {

		super (context);
	}

	/**
	 * Instantiates a new image view.
	 * 
	 * @param context the context
	 * @param attrs the attrs
	 */
	public ImageView (Context context, AttributeSet attrs) {

		super (context, attrs);
	}

	/**
	 * Instantiates a new image view.
	 * 
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public ImageView (Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
	}

	/**
	 * Set the default placeholder image. It will get displayed when an image is
	 * being loaded or there was an error while fetching it from the network.
	 * 
	 * @param imageResourceId Drawable resource ID
	 */

	/**
	 * Loads image from the network against the specified URL.
	 * 
	 * @param imageUrl A valid URL
	 */
	public void loadImageWithUrl (String imageUrl) {

		if(StringUtility.isEmptyOrNull(imageUrl)) {
			return;
		}

		RequestCreator requestCreator = Picasso.with(getContext())
										.load(imageUrl);
		customizeRequestCreator(requestCreator).into(this);
	}

	public void loadImageWithUrl(String imageUrl, final int defaultResourceID) {
		loadImageWithUrl(imageUrl, defaultResourceID, null);
	}

	public void loadImageWithUrl(String imageUrl, final int defaultResourceID, final ImageLoadListener imageLoadListener) {
		if(StringUtility.isEmptyOrNull(imageUrl)) {
			setImageResource(defaultResourceID);
			return;
		}

		RequestCreator requestCreator = Picasso.with(getContext())
										.load(imageUrl)
										.placeholder(defaultResourceID)
										.error(defaultResourceID);
		customizeRequestCreator(requestCreator).fit().centerInside().into(this, new Callback() {
					@Override
					public void onSuccess() {
						if(imageLoadListener != null) imageLoadListener.onImageLoadCompleted(true);
					}

					@Override
					public void onError() {
						if(imageLoadListener != null) imageLoadListener.onImageLoadCompleted(false);
					}
				});
	}

	@Override
	public void setImageResource(int resId) {

		if(isInEditMode()) {
			super.setImageResource(resId);
			return;
		}

		RequestCreator requestCreator = Picasso.with(getContext())
				.load(resId);
		customizeRequestCreator(requestCreator).into(this);
	}

	protected RequestCreator customizeRequestCreator(RequestCreator requestCreator) {
		return requestCreator;
	}
}
