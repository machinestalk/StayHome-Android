package com.machinestalk.android.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import com.google.android.material.snackbar.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.machinestalk.android.R;
import com.machinestalk.android.components.TextView;

/**
 * Encapsulates methods for UI widgets like {@link Toast}, {@link AlertDialog}
 * etc.
 */
@SuppressWarnings("WeakerAccess")
public final class UIUtility {

    private UIUtility() {
    }

    /**
     * Displays an Alert dialog with a primary button.
     *
     * @param title              Title of the dialog
     * @param message            Descriptive message for the dialog
     * @param positiveButtonText Button title
     * @param context            A valid context
     */
    public static void showAlert(CharSequence title, CharSequence message, CharSequence positiveButtonText, Context context) {
        UIUtility.showAlert(title, message, positiveButtonText, context, null);
    }

    public static void showAlert(CharSequence title, CharSequence message, CharSequence positiveButtonText, Context context, DialogInterface.OnClickListener clickListener, boolean isCancelable) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton(positiveButtonText, clickListener);
            alertDialogBuilder.setCancelable(isCancelable);
            alertDialogBuilder.show();
        } catch (Exception e) {

        }
    }

    public static void showAlert(CharSequence title, CharSequence message, CharSequence positiveButtonText, Context context, DialogInterface.OnClickListener clickListener) {
        UIUtility.showAlert(title, message, positiveButtonText, context, clickListener, false);
    }

    public static void showConfirmationAlert(CharSequence title, CharSequence message, CharSequence positiveButtonText, CharSequence negativeButtonText, Context context, DialogInterface.OnClickListener clickListener, boolean isCancelable) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton(positiveButtonText, clickListener);
            alertDialogBuilder.setNegativeButton(negativeButtonText, clickListener);
            alertDialogBuilder.setCancelable(isCancelable);
            alertDialogBuilder.show();
        } catch (Exception e) {

        }
    }

    public static void showConfirmationAlert(CharSequence title, CharSequence message, CharSequence positiveButtonText, Context context, DialogInterface.OnClickListener clickListener) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton(positiveButtonText, clickListener);
            alertDialogBuilder.setNegativeButton("Cancel", null);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.show();
        } catch (Exception e) {

        }
    }

    /**
     * Hides the soft keyboard from the phone's screen.
     *
     * @param editText A valid reference to any EditText, currently in the view
     *                 hierarchy
     * @param context  A valid context
     */
    public static void hideSoftKeyboard(EditText editText, Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Hides the soft keyboard from the phone's screen.
     *
     * @param view A valid reference to Android view
     */
    public static void hideSoftKeyboard(View view) {

        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    /**
     * Shows the soft keyboard on the phone's screen.
     *
     * @param editText A valid reference to any EditText, currently in the view
     *                 hierarchy
     * @param context  A valid context
     */
    public static void showSoftKeyboard(EditText editText, Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public static int getResourceID(Context context, String key, String resourceType) {
        String packageName = context.getPackageName();
        return context.getResources().getIdentifier(key, resourceType, packageName);
    }

    public static void showToast(Context context, CharSequence text, int duration) {
        Toast.makeText(context, text, duration).show();
    }

    public static void notImplemented(Context context) {
        showToast(context, context.getString(R.string.not_implemented), Toast.LENGTH_SHORT);
    }


    public static void showSnackBar(View view, CharSequence text, int duration) {
        Snackbar.make(view, text, duration).show();
    }

    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static Animation scrollingText(TextView txtView, float margin){

        int minDuration = 17000;
        txtView.measure(View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED);

        float width = txtView.getMeasuredWidth();

        int screenWidth = DeviceUtility.getScreenWidthInPixels(txtView.getContext());
        long duration = (long) (minDuration * (width / screenWidth));
        if(duration < minDuration) {
            duration = minDuration;
        }

        Animation animation = new TranslateAnimation(screenWidth, -width, 0, 0);
        animation.setDuration(duration);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        return animation;
    }

    public static String padString(String text, TextView txtView) {
        float width = txtView.getPaint().measureText(text);
        int screenWidth = DeviceUtility.getScreenWidthInPixels(txtView.getContext());
        while(width>=screenWidth){
            screenWidth+=screenWidth;
        }
        int textLength = text.length();
        int length  = (int) (textLength * (screenWidth/width)) + 6;
        return padRight(text, length);
    }

    public static String padRight(String s, int n) {
        if(n < 6) {
            n = 6;
        }
        return String.format("%1$-" + n + "s", s);
    }

}
