package com.machinestalk.stayhome.listeners;

import android.graphics.Bitmap;

/**
 * Created on 2/3/17.
 */

public interface ImageSelectionListener {

    void willStartSelectingImage(int requestCode);
    void onImageSelected(Bitmap bitmap, int requestCode);
    void onRemoveImage();
}
