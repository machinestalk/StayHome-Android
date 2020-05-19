package com.machinestalk.stayhome.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;

import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

/**
 * Created by asher.ali on 9/8/2017.
 */

public class BitmapUtil {


    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }



    public static Bitmap ScaleBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1,1,source.getWidth()/2f,source.getHeight()/2f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap setRotateBitmap(Context context, int drawableResId, float rotate)
    {
        Bitmap bmpOriginal = getBitmap(context, drawableResId);
        int dimension = Math.max(bmpOriginal.getWidth(), bmpOriginal.getHeight());
        Bitmap bmResult = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888);

        Canvas tempCanvas = new Canvas(bmResult);
        float pivot = Math.max(bmpOriginal.getWidth() / 2, bmpOriginal.getHeight() / 2);
        tempCanvas.rotate(rotate, pivot, pivot);
        tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);
        return bmResult;
    }

    public static Bitmap getBitmap(Context context, int drawableResId)
    {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context,drawableResId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat) {
            return getBitmap((VectorDrawableCompat) drawable);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

    private static Bitmap getBitmap(VectorDrawableCompat vectorDrawable)
    {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable)
    {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

}
