package com.machinestalk.android.utilities;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;
import java.util.Map;

/**
 * This class serves as a gateway to fonts in assets directory. It also
 * maintains an internal font cache to avoid heavy memory allocation each time
 * same font is requested.
 *
 * 
 */
public final class FontUtility {

	private static final String PATH_PREFIX = "fonts/";


	@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
	private static Map<String, Typeface> fontCache = new Hashtable<> ();

	private FontUtility() {
	}

	/**
	 * Gets font {@link Typeface} from assets directory.
	 * 
	 * @param fontPathFromAssets The path to font file, relative to assets
	 *            directory
	 * @param context A valid context
	 * @return Typeface object
	 */
	public static Typeface getFontFromAssets (String fontPathFromAssets, Context context) {

		if(!fontPathFromAssets.startsWith(PATH_PREFIX)) {
			fontPathFromAssets = PATH_PREFIX + fontPathFromAssets;
		}

		Typeface typeface = fontCache.get(fontPathFromAssets);

		if(typeface == null) {

			try {
				typeface = Typeface.createFromAsset (context.getAssets (), fontPathFromAssets);
				fontCache.put(fontPathFromAssets, typeface);
			} catch (Exception ignored) {

			}
		}

		return typeface;
	}
}
