package com.machinestalk.stayhome.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.webkit.URLUtil;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.github.clans.fab.FloatingActionButton;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.utilities.FontUtility;
import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.SplashScreenActivity;
import com.machinestalk.stayhome.components.EditText;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.database.AppDatabase;
import com.machinestalk.stayhome.entities.BluetoothEntity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.machinestalk.stayhome.constants.AppConstants.LANGUAGE_LITERAL_ARABIC;
import static com.machinestalk.stayhome.utils.DateUtil.LAST_TRACKING_INFO_FORMAT;

/**
 * Created on 1/5/2017.
 */

 public class Util {

    private static boolean isTextFocus = true;
    private static String PHONE_NUMBER_LOGIN_EDIT_PREFIX = "+96";
    private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");


    public static String sha1_Encode(String Signature) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(Signature.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {


        } catch (UnsupportedEncodingException e) {


        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static void setCursorDrawableColor(EditText editText, int color) {

        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[1] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (Throwable ignored) {

        }
    }

    public static void openKeyboardWithDelay(final EditText edittext, final Context context) {
        if (edittext == null || context == null)
            return;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);

    }

    public static boolean ifActionIsEnterOrDone(int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
            return true;
        else
            return false;
    }


    public static void closeKeyboard(EditText edittext, Context context) {
        if (edittext == null || context == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    }


    public static void hideSoftKeyboard(Activity activity) {

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void confirmDelete(Context context, String message, DialogInterface.OnClickListener positiveListener
            , DialogInterface.OnClickListener negativeListener, boolean isCancelable) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(message).setPositiveButton(context.getString(R.string.Gen_Gen_lbl_yes), positiveListener)
                .setNegativeButton(context.getString(R.string.Gen_Gen_lbl_dialog_no), negativeListener);

        AlertDialog alertDialog = builder.show();

        if (!isCancelable) {
            alertDialog.setCancelable(false);
        }

    }


    public static boolean isValidForSaudi(String licenceNumber) {

        Pattern sPattern = Pattern.compile("^1\\d{9}$|^2\\d{9}$");
        return sPattern.matcher(licenceNumber).matches();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Context context) {

        try {

            View view = ((Activity) context).getCurrentFocus();
            if (view != null) {
                ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } catch (Exception e) {


        }
    }
    public static void forceHideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    }

    public static boolean isTextArabic(String text) {

        for (char charac : text.toCharArray()) {
            if (Character.UnicodeBlock.of(charac) == Character.UnicodeBlock.ARABIC) {
                return true;
            }
        }
        return false;
    }

    public static String removeArabicText(String text) {

        StringBuilder nonArabicText = new StringBuilder();
        for (char charac : text.toCharArray()) {
            if (Character.UnicodeBlock.of(charac) == Character.UnicodeBlock.ARABIC) {
                nonArabicText.append(charac);
            }
        }
        return nonArabicText.toString();
    }

    public static int getZoomLevel(int radius) {

        int zoomLevel = 11;
        double calculatedRadius = (double) radius + radius / 2.0;
        double scale = calculatedRadius / 500;
        zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));

        return zoomLevel;
    }

    @SuppressLint("ResourceType")
    public static FloatingActionButton getFloatingActionButton(Context context, String id, @StringRes int label, @DrawableRes int src) {

        FloatingActionButton button = new FloatingActionButton(context);
        button.setTag(R.string.fabTagKey, id);
        button.setId(5);
        button.setColorNormal(ContextCompat.getColor(context, R.color.zxing_transparent));
        button.setColorPressed(ContextCompat.getColor(context, R.color.zxing_transparent));
        button.setLabelText(context.getString(label));
        button.setShowShadow(false);
        button.setImageResource(src);
        return button;
    }

    public static String checkInputLanguage(Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
        return ims.getLocale();
    }

    public static void setPhoneNumberInRTL(final EditText editText, boolean isTextRTL, final String preFilledData) {

        if (isTextRTL) {

            editText.setTextDirection(View.TEXT_DIRECTION_LTR);
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (hasFocus) {
                        if (editText.getText().length() < 3)
                            editText.setText(preFilledData);
                        Selection.setSelection(editText.getText(), editText.getText().length());
                        isTextFocus = false;
                    }
                }
            });

            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    editText.onTouchEvent(event);

                    if (!isTextFocus) {
                        Selection.setSelection(editText.getText(), editText.getText().length());
                        isTextFocus = true;
                    }

                    return true;
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (!s.toString().startsWith(preFilledData)) {
                        editText.setText(preFilledData);
                        Selection.setSelection(editText.getText(), editText.getText().length());
                    }
                }
            });
        }
    }


    public static void setLoginPhoneNumberInRTL(final EditText editText, boolean isTextRTL, final String preFilledData) {

        if (isTextRTL) {

            editText.setTextDirection(View.TEXT_DIRECTION_LTR);
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (hasFocus) {
                        if (editText.getText().length() < 3)
                            // editText.setText(preFilledData);
                            Selection.setSelection(editText.getText(), editText.getText().length());
                        isTextFocus = false;
                    }
                }
            });

            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    editText.onTouchEvent(event);

                    if (!isTextFocus) {
                        Selection.setSelection(editText.getText(), editText.getText().length());
                        isTextFocus = true;
                    }

                    return true;
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.toString().equals(PHONE_NUMBER_LOGIN_EDIT_PREFIX)) {
                        editText.getText().clear();
                    } else if (!s.toString().isEmpty() && !s.toString().startsWith(preFilledData)) {
                        if (s.toString().length() == 1) {
                            editText.setText(preFilledData + s.toString());
                        } else {
                            editText.setText(preFilledData);
                        }
                        Selection.setSelection(editText.getText(), editText.getText().length());
                    }
                }
            });
        }
    }

    public static void setLoginPhoneNumberInRTLDefault(final android.widget.EditText editText, boolean isTextRTL, final String preFilledData) {

        if (isTextRTL) {

            editText.setTextDirection(View.TEXT_DIRECTION_LTR);
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (hasFocus) {
                        if (editText.getText().length() < 3)
                            editText.setText(preFilledData);
                        Selection.setSelection(editText.getText(), editText.getText().length());
                        isTextFocus = false;
                    }
                }
            });

            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    editText.onTouchEvent(event);

                    if (!isTextFocus) {
                        Selection.setSelection(editText.getText(), editText.getText().length());
                        isTextFocus = true;
                    }

                    return true;
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.toString().equals(PHONE_NUMBER_LOGIN_EDIT_PREFIX)) {
                        editText.getText().clear();
                    } else if (!s.toString().isEmpty() && !s.toString().startsWith(preFilledData)) {
                        if (s.toString().length() == 1) {
                            editText.setText(preFilledData + s.toString());
                        } else {
                            editText.setText(preFilledData);
                        }
                        Selection.setSelection(editText.getText(), editText.getText().length());
                    }
                }
            });
        }
    }

    public static void setOnVibration(Controller controller, long durationInMilliseconds) {

        Vibrator v = (Vibrator) controller.getBaseActivity().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(durationInMilliseconds);
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String getCurrentTimeUTC() {
        DateFormat dateFormat = new SimpleDateFormat(LAST_TRACKING_INFO_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static int getTimeByComparingWithLastCommunicationTime(String localTime, String lastCommTime){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        long diffInSec = 0 ;
        Date Date1 = null;
        Date Date2 = null;
        try {
            Date1 = sdf.parse(localTime);
            Date2 = sdf.parse(lastCommTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (Date1 != null && Date2 != null ){
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTime(Date1);
            calendar2.setTime(Date2);
            long miliSecondForDate1 = calendar1.getTimeInMillis();
            long miliSecondForDate2 = calendar2.getTimeInMillis();
            // Calculate the difference in millisecond between two dates
            long diffInMilis = miliSecondForDate2 - miliSecondForDate1;
            /*
             * Now we have difference between two date in form of millsecond we can
             * easily convert it Minute / Hour / Days by dividing the difference
             * with appropriate value. 1 Second : 1000 milisecond 1 Hour : 60 * 1000
             * millisecond 1 Day : 24 * 60 * 1000 milisecond
             */
            long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilis);
            long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMilis);
            diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMilis);
        }
        return ((int) diffInSec );
    }

    public static String getTimeFromDate(String Date) {
        Date date1 = null;
        String outputDateString = null;
        SimpleDateFormat sdf1 = new SimpleDateFormat(LAST_TRACKING_INFO_FORMAT);
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

        try {
            date1 = sdf1.parse(Date);
            outputDateString = sdf2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateString ;
    }


    public static boolean isReminingTimeMoreThan30Minutes (String startTime ,String endTime){
        boolean isMoreThan30Minutes = false ;
        if (Integer.valueOf(getTimeDifferenceBtwDates(startTime, endTime)) > 1800){
            isMoreThan30Minutes = true ;
        }

        return isMoreThan30Minutes ;
    }

    public static String getDifferenceBtwDates(String dateDebut, String dateFin) {

        if (StringUtility.isEmptyOrNull(dateDebut) || StringUtility.isEmptyOrNull(dateFin)) {
            return "-1";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date startDate = null;
            Date finDate = null;
            try {
                startDate = sdf.parse(dateDebut);
                finDate = sdf.parse(dateFin);
            } catch (ParseException e) {
            }
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTime(startDate);
            calendar2.setTime(finDate);

            /*
             * Use getTimeInMillis() method to get the Calendar's time value in
             * milliseconds. This method returns the current time as UTC
             * milliseconds from the epoch
             */
            long miliSecondForDate1 = calendar1.getTimeInMillis();
            long miliSecondForDate2 = calendar2.getTimeInMillis();
            // Calculate the difference in millisecond between two dates
            long diffInMilis = miliSecondForDate2 - miliSecondForDate1;
            /*
             * Now we have difference between two date in form of millsecond we can
             * easily convert it Minute / Hour / Days by dividing the difference
             * with appropriate value. 1 Second : 1000 milisecond 1 Hour : 60 * 1000
             * millisecond 1 Day : 24 * 60 * 1000 milisecond
             */
            long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilis);
            long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMilis);
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMilis);

            return "" + diffInDays;

        }
    }
    public static String getTimeDifferenceBtwDates(String dateDebut, String dateFin) {

        if (StringUtility.isEmptyOrNull(dateDebut) || StringUtility.isEmptyOrNull(dateFin)) {
            return "-1";
        } else {

            SimpleDateFormat sdf = new SimpleDateFormat(LAST_TRACKING_INFO_FORMAT);

            Date startDate = null;
            Date finDate = null;
            try {
                startDate = sdf.parse(dateDebut);
                finDate = sdf.parse(dateFin);
            } catch (ParseException e) {
            }
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTime(startDate);
            calendar2.setTime(finDate);

            /*
             * Use getTimeInMillis() method to get the Calendar's time value in
             * milliseconds. This method returns the current time as UTC
             * milliseconds from the epoch
             */
            long miliSecondForDate1 = calendar1.getTimeInMillis();
            long miliSecondForDate2 = calendar2.getTimeInMillis();
            // Calculate the difference in millisecond between two dates
            long diffInMilis = miliSecondForDate2 - miliSecondForDate1;
            /*
             * Now we have difference between two date in form of millsecond we can
             * easily convert it Minute / Hour / Days by dividing the difference
             * with appropriate value. 1 Second : 1000 milisecond 1 Hour : 60 * 1000
             * millisecond 1 Day : 24 * 60 * 1000 milisecond
             */
            long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilis);
            long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMilis);
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMilis);

            return "" + diffInSec;

        }
    }









    public static void saveStatusDeviceExpiredOnPref(Context context, Map<String, Boolean> inputMap) {
        SharedPreferences pSharedPref = context.getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("hashString").commit();
            editor.putString("hashString", jsonString);
            editor.commit();
        }
    }

    public static Map<String, Boolean> loadStatusDeviceExpiredFromPref(Context context) {
        Map<String, Boolean> outputMap = new HashMap<String, Boolean>();
        SharedPreferences pSharedPref = context.getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("hashString", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Boolean value = (Boolean) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        } catch (Exception e) {
        }
        return outputMap;
    }






    public static void saveStatusDeviceConnectivityOnPref(Context context, Map<String, Boolean> inputMap) {
        SharedPreferences pSharedPref = context.getSharedPreferences("MyConnectivityStatus", Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("status").commit();
            editor.putString("status", jsonString);
            editor.commit();
        }
    }
    public static Map<Integer, Boolean> loadStatusDeviceConnectivityFromPref(Context context) {
        Map<Integer, Boolean> outputMap = new HashMap<Integer, Boolean>();
        SharedPreferences pSharedPref = context.getSharedPreferences("MyConnectivityStatus", Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("status", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Boolean value = (Boolean) jsonObject.get(key);
                    outputMap.put(Integer.parseInt(key), value);
                }
            }
        } catch (Exception e) {
        }
        return outputMap;
    }
    public static Boolean isVehicleConnectivityAvailable(Context context, int vehicleId) {
         boolean isConnectivityAvailable = false;
        SharedPreferences pSharedPref = context.getSharedPreferences("MyConnectivityStatus", Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("status", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Boolean value = (Boolean) jsonObject.get(key);
                    if (key.equals(String.valueOf(vehicleId)) && value ){
                        isConnectivityAvailable = false ;
                    }else {
                        isConnectivityAvailable = true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return isConnectivityAvailable;
    }
    public static void clearVehicleConnectivityAvailablility(Context context) {
        SharedPreferences pSharedPref = context.getSharedPreferences("MyConnectivityStatus", Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("status").commit();
            editor.commit();
        }

    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.US);
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.US);

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
        }

        return outputDate;

    }

    public static boolean isBetweenAndroidVersions(int min, int max) {

        return Build.VERSION.SDK_INT >= min && Build.VERSION.SDK_INT <= max;
    }

    public static int getStringIdentifier(Context pContext, String pString) {

        return pContext.getResources().getIdentifier(pString, "string", pContext.getPackageName());
    }

    public static void showInfoDialog(final Context context, String message) {

        new AlertDialog.Builder(context).setMessage(message).setPositiveButton(context.getString(R.string.Gen_Gen_lbl_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        }).show();
    }

    public static boolean isNumberAnInt(double number) {

        return number % 1 == 0;

    }

    public static String formatNumberToUsLocale(int number) {

        return NumberFormat.getInstance(Locale.US).format(number);
    }

    public static String getIntValueOfDouble(double number) {

        if (isNumberAnInt(number)) {

            return formatNumberToUsLocale((int) number);
        }

        return formatNumberToUsLocale(number);

    }


    public static String formatNumberToUsLocale(double number) {

        return NumberFormat.getInstance(Locale.US).format(number);
    }

    public static String formatStrings(String format, Object... values) {

        return String.format(Locale.US, format, values);

    }

    public static boolean isValidSAPlateNo(CharSequence plateNo) {

        Pattern pattern;
        Matcher matcher;
        String plateNoPattern = "^[\\u0660-\\u0669\\u0621-\\u064Aa-zA-Z0-9 -]*";
        pattern = Pattern.compile(plateNoPattern);
        matcher = pattern.matcher(plateNo);
        return matcher.matches();
    }

    public static String checkDeviceLanguage(String appLanguage) {
        if (!TextUtils.isEmpty(appLanguage))
            return appLanguage;
        else if (Locale.getDefault().getLanguage().equals(LANGUAGE_LITERAL_ARABIC))
            appLanguage = LANGUAGE_LITERAL_ARABIC;
        else
            appLanguage = AppConstants.LANGUAGE_LITERAL_ENGLISH;

        return appLanguage;
    }

    public static Typeface getTypeface(Context context) {
        String fontPathFromAssets = context.getString(R.string.font_light_separate);
        Typeface typeface = FontUtility.getFontFromAssets(fontPathFromAssets, context);
        return typeface;
    }

    public static String getIntFromDouble(double number) {

        return formatNumberToUsLocale((int) number);

    }

    public static boolean validateUrl(String input) {
        if (Pattern.matches(Patterns.WEB_URL.pattern(), input))
            return new URLUtil().isValidUrl(input);
        return false;
    }

    /**
     * @param value
     * @return
     */
    public static String getDecimalFormat(double value) {

        if (value == 0) {
            return "0";
        }
        try {
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.applyPattern("##.##");
            return formatter.format(value);
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static Pair<Integer, Integer> getScreenDimension(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();

        int width;
        int height;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            width = size.x;
            height = size.y;

        } else {
            width = display.getWidth();
            height = display.getHeight();
        }
        return new Pair<Integer, Integer>(width, height);

    }

    /**
     * return the color desired
     *
     * @param context
     * @param colorResId
     * @return
     */
    public static int getColor(Context context, @ColorRes int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(colorResId);
        } else {
            return context.getResources().getColor(colorResId);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


    /**
     * @param context
     * @return
     */
    public static int getActionBarSize(Context context) {
        int actionBarHeight = 48;
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public static @DrawableRes
    int getAlertIcon(int alertId) {

        int res = R.drawable.switch_icon;
        switch (alertId) {

            case 1: {
                res = R.drawable.ic_power_green;

            }
            break;

            case 2: {
                res = R.drawable.power_red;
            }
            break;

        }
        return res;

    }

    /**
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isIsTextFocus() {
        return isTextFocus;
    }

    public static void setIsTextFocus(boolean isTextFocus) {
        Util.isTextFocus = isTextFocus;
    }



    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.y;
    }


    /**
     * @param selectedValue
     */
    public static void showConfirmationLanguageDialog(Context context, final String selectedValue) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(context.getResources().getString(R.string.setting_restart_app_required_label));
        dialog.setMessage(context.getResources().getString(R.string.setting_restart_app_confirmation));
        dialog.setPositiveButton(context.getResources().getString(R.string.Gen_Gen_lbl_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppConfig.getInstance().setLanguage(selectedValue);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                restartApp();
                            }
                        }, 300);
                    }
                });

                dialog.setNegativeButton(context.getResources().getString(R.string.Gen_Gen_lbl_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });

        dialog.show();
    }

    public static void showBluetoothList(Context context, BluetoothEntity bluetoothEntity){

        BluetoothEntity BLEEntity = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()) ;
        if (BLEEntity == null){
            AppDatabase.getInstance(context).getBluetoothDao().insertBluetoothEntity(bluetoothEntity);
        }else {
            String name = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(name) || name.equals("Unnamed"))
                name = bluetoothEntity.getName() ;

            String frameName = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(frameName) || frameName.equals("Unnamed"))
                frameName = bluetoothEntity.getFrameName() ;

            String adresse = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(adresse) || adresse.equals("Unnamed"))
                adresse = bluetoothEntity.getAdresse() ;

            String rssi = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(rssi) || rssi.equals("Unnamed"))
                rssi = bluetoothEntity.getRssi() ;

            String txPower = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(txPower)|| txPower.equals("Unnamed"))
                txPower = bluetoothEntity.getTxPower() ;

            String namespace = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(namespace)|| namespace.equals("Unnamed"))
                namespace = bluetoothEntity.getNamespace() ;

            String instanceID = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(instanceID) || instanceID.equals("Unnamed"))
                instanceID = bluetoothEntity.getInstanceID() ;

            String minor = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(minor) || minor.equals("Unnamed"))
                minor = bluetoothEntity.getMinor() ;

            String major = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(major) || major.equals("Unnamed"))
                major = bluetoothEntity.getMajor() ;

            String udid = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(udid)|| udid.equals("Unnamed"))
                udid = bluetoothEntity.getUdid() ;

            String url = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(url)|| url.equals("Unnamed") )
                url = bluetoothEntity.getUrl() ;

            String x = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(x)|| x.equals("Unnamed"))
                x = bluetoothEntity.getX() ;

            String y = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(y)|| y.equals("Unnamed"))
                y = bluetoothEntity.getY() ;

            String z = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(z)|| z.equals("Unnamed"))
                z = bluetoothEntity.getZ() ;

            String rx = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(rx)|| rx.equals("Unnamed"))
                rx = bluetoothEntity.getRx() ;

            String tx = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(tx)|| tx.equals("Unnamed"))
                tx = bluetoothEntity.getTx() ;

            String battery = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(battery)|| battery.equals("Unnamed"))
                battery = bluetoothEntity.getBattery() ;

            String temperature = AppDatabase.getInstance(context).getBluetoothDao().getBluetoothEntityByAdress(bluetoothEntity.getAdresse()).getName();
            if (StringUtility.isEmptyOrNull(temperature)|| temperature.equals("Unnamed"))
                temperature = bluetoothEntity.getTemperature() ;


            AppDatabase.getInstance(context).getBluetoothDao().update( name, frameName, adresse,rssi
                    ,txPower,namespace,instanceID,minor
                    ,major,udid,url,x,y,z
                    ,rx,tx,battery,temperature);
        }
    }

    private static void restartApp() {
        Intent intent = new Intent(ConnectedCar.getInstance().getApplicationContext(), SplashScreenActivity.class);
        int mPendingIntentId = 5;
        PendingIntent mPendingIntent = PendingIntent.getActivity(ConnectedCar.getInstance().getApplicationContext()
                , mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) ConnectedCar.getInstance().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }




}
