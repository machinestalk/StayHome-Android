package com.machinestalk.stayhome.helpers;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.utilities.ImageUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.listeners.ImageSelectionListener;
import com.machinestalk.stayhome.utils.PermissionUtils;
import com.machinestalk.stayhome.utils.StorageManager;
import com.orhanobut.logger.Logger;
import com.theartofdev.edmodo.cropper.CropImage;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created on 2/3/17.
 */

public class ImagePickerManager {

    public static final int CAMERA_REQUEST_CODE = 1022;
    public static final int GALLERY_REQUEST_CODE = 1023;

    private final String DEFAULT_EXTENSION = "jpg";

    private int requestCode = 0;

    private Controller controller;
    private ImageSelectionListener imageSelectionListener;

    private String imageExtension;
    private Bitmap imageBitmap;
    private String imageBase64;

    private long maxSizeInMbs;

    private Uri cameraUri;

    private boolean canRemoveImage = false;

    private boolean hasRemovedImage = false;

    public ImagePickerManager(Controller controller, ImageSelectionListener imageSelectionListener, int requestCode) {
        this.controller = controller;
        this.imageSelectionListener = imageSelectionListener;
        this.requestCode = requestCode;
        imageBase64 = "";
        imageExtension = DEFAULT_EXTENSION;
    }


    public boolean hasNecessaryPermissions() {

        if (hasCameraPermission() && hasExternalStorePermission()) {
            return true;
        }

        if (controller != null) {
            requestForAllPermissions();
        }

        return false;
    }

    private boolean hasExternalStorePermission() {
        return hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private boolean hasCameraPermission() {
        return hasPermission(Manifest.permission.CAMERA);
    }

    private void requestForAllPermissions() {

        PermissionUtils.checkAndRequestPermissionCamera(controller);
    }

    private boolean hasPermission(String permission) {

        if (controller == null)
            return false;
        else
            return ContextCompat.checkSelfPermission(controller.getBaseActivity(), permission)
                    == PackageManager.PERMISSION_GRANTED;
    }

    public void showImagePicker(int maxSizeInMbs) {

        if (controller == null) {
            return;
        }

        this.maxSizeInMbs = maxSizeInMbs;

        if (imageSelectionListener != null) {
            imageSelectionListener.willStartSelectingImage(requestCode);
        }

        new AlertDialog.Builder(controller.getBaseActivity(), R.style.AppCompatAlertDialogStyle)
                .setItems(controller.getBaseActivity().getResources().getStringArray((canRemoveImage) ? R.array.Gen_Gen_lbl_image_picker_extended : R.array.Gen_Gen_lbl_image_picker),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        openCamera();
                                        break;
                                    case 1:
                                        openGallery();
                                        break;
                                    case 2:
                                        removeImage();
                                        break;

                                }
                                dialog.dismiss();
                            }
                        })
                .setCancelable(true)
                .show();
    }

    private void removeImage() {

        imageBase64 = null;
        imageBitmap = null;
        imageExtension = null;
        hasRemovedImage = true;

        if (imageSelectionListener != null) {
            imageSelectionListener.onRemoveImage();
        }

    }

    private void showImageSizeBigAlert() {
        new AlertDialog.Builder(controller.getBaseActivity(), R.style.AppCompatAlertDialogStyle)
                .setMessage("Image size is big, please try again")
                .setPositiveButton(R.string.Gen_Gen_lbl_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public boolean isAnImageSelected() {

        if (canRemoveImage) {
            return hasRemovedImage || !StringUtility.isEmptyOrNull(imageBase64);
        } else {
            return !StringUtility.isEmptyOrNull(imageBase64);
        }
    }

    public String getImageExtension() {
        return StringUtility.validateEmptyString(imageExtension);
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public String getImageBase64() {
        return StringUtility.validateEmptyString(imageBase64);
    }

    private void openGallery() {

        if (controller == null) {
            return;
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        controller.getBaseActivity().
                startActivityForResult(Intent.createChooser(intent,
                        controller.getBaseActivity().getString(R.string.Gen_Gen_lbl_select_image)), GALLERY_REQUEST_CODE);
    }

    private void openCamera() {

        if (controller == null) {
            return;
        }

        if (Build.VERSION.SDK_INT > M) {
            cameraUri = FileProvider.getUriForFile(controller.getBaseActivity(), controller.getBaseActivity().getApplicationContext().getPackageName() + ".provider", StorageManager.getFileObject());
        } else
            cameraUri = Uri.fromFile(StorageManager.getFileObject());

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        controller.getBaseActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }


    public void handleCropIntent(Intent data, int requestCode) {
        if (controller == null || requestCode != CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            return;
        }

        Bitmap bitmap = null;


        try {
            bitmap = MediaStore.Images.Media.getBitmap(controller.getBaseActivity().getBaseContext().getContentResolver()
                    , data.getData());
            bitmap = ImageOrientation.modifyOrientation(controller.getBaseActivity(), bitmap, data.getData());

        } catch (Exception | OutOfMemoryError e) {
            Logger.e("error when get image :" + e.getMessage());
        }

        if (bitmap == null) {
            return;
        }

        try {
            imageBitmap = ImageUtility.compressBitmap(bitmap, 20);
            imageBase64 = ImageUtility.encodeToBase64(imageBitmap, Bitmap.CompressFormat.JPEG, 60, maxSizeInMbs * 1024 * 1024);
            imageExtension = "jpg";

            if (imageSelectionListener != null) {
                imageSelectionListener.onImageSelected(imageBitmap, requestCode);
            }
        } catch (Exception | OutOfMemoryError ex) {
            Logger.e("error when get image :" + ex.getMessage());
            showImageSizeBigAlert();
        }
    }

    public Uri getCameraUri() {
        return cameraUri;
    }

    public void setCameraUri(Uri cameraUri) {
        this.cameraUri = cameraUri;
    }


    public boolean isCanRemoveImage() {
        return canRemoveImage;
    }

    public void setCanRemoveImage(boolean canRemoveImage) {
        this.canRemoveImage = canRemoveImage;
    }
}
