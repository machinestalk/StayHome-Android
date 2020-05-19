package com.machinestalk.stayhome.listeners;

import androidx.annotation.NonNull;

/**
 * Created by asher.ali on 7/3/2017.
 */

public interface PermissionGrantedListener {

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
