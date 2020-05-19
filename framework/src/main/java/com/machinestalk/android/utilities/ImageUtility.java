package com.machinestalk.android.utilities;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * This utility methods related to manipulating images should go inside this
 * class.
 *
 * 
 */
@SuppressWarnings("WeakerAccess")
public final class ImageUtility {

	private ImageUtility() {
	}

	/**
	 * Gets a bitmap object from an image resource. Also applies compression to
	 * reduce in memory size of the image.
	 * 
	 * @param resources Resources. You can get resources by doing
	 *            {@code getResources()} on a Context or Activity.
	 * @param resourceId Resource ID for the required image.
	 * @return Compressed bitmap object against the image resource.
	 */
	public static Bitmap decodeBitmapFromResource (Resources resources, int resourceId) {

		BitmapFactory.Options options = new BitmapFactory.Options ();

		options.inScaled = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inSampleSize = 1;
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource (resources, resourceId, options);
	}

	public static Bitmap compressBitmap(Bitmap bitmap, int quality) {

		ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bitmapStream);
		return BitmapFactory.decodeStream(new ByteArrayInputStream(bitmapStream.toByteArray()));
	}

	public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality, long maxSizeInBytes)
			throws Exception{


		Bitmap resized = Bitmap.createScaledBitmap(image,(int)(image.getWidth()*0.6), (int)(image.getHeight()*0.6), true);
		ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
		resized.compress(compressFormat, quality, byteArrayOS);
		byte[] byteArray = byteArrayOS.toByteArray();
		if(maxSizeInBytes < 1 && byteArray.length > maxSizeInBytes) {
			throw new Exception();
		}
		return Base64.encodeToString(byteArray, Base64.NO_WRAP);
	}

    public static long getSizeOfBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray().length;
    }

    public static long getSizeOfBitmapEncodedString(String encodedString)
    {
        byte[] byteArray  = Base64.decode(encodedString,Base64.NO_WRAP);
        return byteArray.length;
    }

}
