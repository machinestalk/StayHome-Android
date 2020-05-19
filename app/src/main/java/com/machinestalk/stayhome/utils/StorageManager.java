package com.machinestalk.stayhome.utils;

import android.os.Environment;

import androidx.annotation.NonNull;

import com.machinestalk.stayhome.constants.AppConstants;

import java.io.File;
import java.util.Date;

/**
 * Created on 3/30/2017.
 */

public class StorageManager {


    @NonNull
    public static File getFileObject() {
        String path = Environment.getExternalStorageDirectory() + "/";
        File rDir = new File(path + AppConstants.IMAGES_FOLDER);
        if (!rDir.exists())
            rDir.mkdir();

        return new File(rDir + "/u_" + new Date().getTime() + ".jpeg");
    }

    @NonNull
    public static void deleteFolder() {
        String path = Environment.getExternalStorageDirectory() + "/";
        File rDir = new File(path + AppConstants.IMAGES_FOLDER);
        if (rDir.isDirectory()) {
            deleteRecursive(rDir);
        }

    }

    static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        boolean valuedeleted = fileOrDirectory.delete();
    }

}
