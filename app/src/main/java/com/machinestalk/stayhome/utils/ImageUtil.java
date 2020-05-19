package com.machinestalk.stayhome.utils;

import android.media.ExifInterface;
import android.net.Uri;

import java.io.IOException;


/**
 * Created on 1/3/2017.
 */

public class ImageUtil {

    public static int exifToDegrees(Uri uri) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(uri.getPath());
        } catch (IOException e) {

            return 0;
        }
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        int rotationInDegrees = 0;

        if (rotation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotationInDegrees = 90;
        } else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotationInDegrees = 180;
        } else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotationInDegrees = 270;
        }

        return rotationInDegrees;

    }
  }
