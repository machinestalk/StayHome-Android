package com.machinestalk.stayhome.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.machinestalk.android.utilities.StringUtility;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.config.AppConfig;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * Created on 1/17/2017.
 */

public class DateUtil {

    public static final String DATE_PICKER_12HOUR = "hh:mm a";
    public static final String DOCUMENT_DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
    public static final String LAST_TRACKING_INFO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String STANDARD_FORMAT_PATTERN = "yyyy/MM/dd";

    private static String UTC = "UTC";
    private static String KSA_UTC = "UTC + 3";
    private static final HashMap<String, Integer> DAYS_MAP = new HashMap<String, Integer> ();

    public static Integer[] getYearArray() {

        List<Integer> year = new ArrayList<>();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int toYear = 1970;

        while (currentYear >= toYear) {
            year.add(currentYear);
            currentYear--;
        }

        return year.toArray(new Integer[year.size()]);
    }



	private static HashMap<String, Integer> getDay(){
		DAYS_MAP.put("Monday", Calendar.MONDAY);
		DAYS_MAP.put("Tuesday", Calendar.TUESDAY);
		DAYS_MAP.put("Wednesday", Calendar.WEDNESDAY);
		DAYS_MAP.put("Thursday", Calendar.THURSDAY);
		DAYS_MAP.put("Friday", Calendar.FRIDAY);
		DAYS_MAP.put("Saturday", Calendar.SATURDAY);
		DAYS_MAP.put("Sunday", Calendar.SUNDAY);
		return DAYS_MAP;
	}

    public static boolean areDatesInSameMonth(Date d1, Date d2) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d2);

        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    public static String getUTC() {
        return UTC;
    }

    public static void setUTC(String UTC) {
        DateUtil.UTC = UTC;
    }

    public static String getKsaUtc() {
        return KSA_UTC;
    }

    public static void setKsaUtc(String ksaUtc) {
        KSA_UTC = ksaUtc;
    }

    /**
     * check if two date are the same comparing with day of year.
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean areDatesInSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        return sameDay;
    }

    public static boolean isBetween24Hour(Date start, Date end, Date date) {

        return start.compareTo(date) * date.compareTo(end) >= 0;
    }

    public static Date convertToLocalDateFromString(String date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date myDate = null;

        try {

            myDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
        }

        return myDate;
    }

    public static int dayNameComparison(String dayName) {

        DateFormatSymbols objDaySymbol = new DateFormatSymbols();
        String symbolDayNames[] = objDaySymbol.getWeekdays();
        for (int countDayname = 0; countDayname < symbolDayNames.length; countDayname++) {
            if (dayName.equalsIgnoreCase(symbolDayNames[countDayname])) {
                return countDayname;
            }
        }
        return 1;
    }

    public static Date convertToLocalDateFromString(String date, SimpleDateFormat simpleDateFormat) {

        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date myDate = null;

        try {

            myDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
        }

        return myDate;
    }

    public static Date convertToLocalTimeFromString(String date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date myDate = null;

        try {

            myDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
        }

        return myDate;
    }
    public static Date convertToLocalTimeFromStringToString(String date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date myDate = null;

        try {

            myDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
        }

        return myDate;
    }
    public static String convertToLocalTime(String date) {
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            Date mDate = sdf.parse(date);
             timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {

        }
        return String.valueOf(timeInMilliseconds).toString() ;
    }

    public static String convertMillisToUTCTime(long milliSeconds, String dateFormat) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat.format(new Date(milliSeconds));

        } catch (Exception ex) {
            return "";
        }
    }

    public static int calculateOccurrenceOfDay(Date start, Date end, ArrayList<String> days) {

        Calendar c1 = Calendar.getInstance();
        c1.setTime(start);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(end);

        int dayCount = 0;

        while (!c1.after(c2)) {

            for (int i = 0; i < days.size(); i++) {

				if (c1.get (Calendar.DAY_OF_WEEK) == getDay().get (days.get (i))) {

					c1.set (Calendar.DAY_OF_WEEK, getDay().get (days.get (i)));

                    if (!c1.getTime().after(new Date())) {

                        continue;
                    }

                    dayCount++;
                }
            }
            c1.add(Calendar.DATE, 1);
        }

        return dayCount;
    }

    public static Date addDays(Date date, int days) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); // minus number would decrement the days
        return cal.getTime();
    }

    public static Date getCurrentDate() {

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        return now.getTime();
    }

    public static Date calculateNextOccurrence(Date start, Date end, ArrayList<String> days) {

        Calendar c1 = Calendar.getInstance();
        c1.setTime(start);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(end);

        int dayCount = 0;

        while (!c1.after(c2)) {

            for (int i = 0; i < days.size(); i++) {

				if (c1.get (Calendar.DAY_OF_WEEK) == getDay().get (days.get (i))) {

					c1.set (Calendar.DAY_OF_WEEK, getDay().get (days.get (i)));

                    if (!c1.getTime().after(new Date()) && !c1.getTime().equals(new Date())) {

                        continue;
                    }

                    return c1.getTime();
                }
            }
            c1.add(Calendar.DATE, 1);
        }

        return null;
    }

    public static boolean compareDates(Date date1, Date date2) {
        SimpleDateFormat documentDisplayDateFormat = new SimpleDateFormat(DOCUMENT_DISPLAY_DATE_FORMAT, Locale.getDefault());
        String initialDate = documentDisplayDateFormat.format(date1);
        String lastDate = documentDisplayDateFormat.format(date2);
        Date firstDate;
        Date secondDate;
        try {
            firstDate = documentDisplayDateFormat.parse(initialDate);
            secondDate = documentDisplayDateFormat.parse(lastDate);
        } catch (ParseException e) {
            firstDate = new Date();
            secondDate = new Date();
        }
        return (firstDate.equals(secondDate) || firstDate.after(secondDate));
    }

    public static boolean areDatesEqual(Date date1, Date date2) {
        SimpleDateFormat documentDisplayDateFormat = new SimpleDateFormat(DOCUMENT_DISPLAY_DATE_FORMAT, Locale.getDefault());

        String initialDate = documentDisplayDateFormat.format(date1);
        String lastDate = documentDisplayDateFormat.format(date2);
        Date firstDate;
        Date secondDate;
        try {
            firstDate = documentDisplayDateFormat.parse(initialDate);
            secondDate = documentDisplayDateFormat.parse(lastDate);
        } catch (ParseException e) {
            firstDate = new Date();
            secondDate = new Date();
        }
        return (firstDate.equals(secondDate));
    }


    public static long daysBetweenDates(Date anotherDate) {

        anotherDate.setHours(00);
        anotherDate.setMinutes(00);
        anotherDate.setSeconds(00);
        long diff = anotherDate.getTime() - getCurrentDate().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static boolean compareTime(Date currentTime, Date routeStopTime, int i) {
        SimpleDateFormat datePicker12Hour = new SimpleDateFormat(DATE_PICKER_12HOUR, new Locale(AppConfig.getInstance().getLanguage()));
        String current = datePicker12Hour.format(currentTime);
        String routeStop = datePicker12Hour.format(routeStopTime);
        Date dateFirst = new Date(), dateSecond = new Date();
        try {
            dateFirst = datePicker12Hour.parse(current);
            dateSecond = datePicker12Hour.parse(routeStop);
        } catch (ParseException e) {
        }
        if (i == 0)
            return dateFirst.after(dateSecond);
        return (dateFirst.after(dateSecond) || dateFirst.equals(dateSecond));
    }

    public static void addDaysToDate(Date date, int days) {

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        c1.add(Calendar.DAY_OF_MONTH, days);
    }

    @SuppressLint("SimpleDateFormat")
    public static long dateToTimeStamp(String dateString){
        long startDate = 0;
        if (!StringUtility.isEmptyOrNull(dateString)) {
            TimeZone timeZone = TimeZone.getTimeZone(KSA_UTC);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                sdf.setTimeZone(timeZone);
                Date date = sdf.parse(dateString);

                startDate = date.getTime();

            } catch (ParseException e) {

            }
        }
        return startDate;
    }

    /**
     * Format the Date to given date format in the given time zone
     *
     * @param date
     * @param dateFormat
     * @param timeZone
     * @return the desired formatted date string
     * <br/> Returns empty string if there is a problem
     */

    public static String format(Date date, String dateFormat, TimeZone timeZone) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            simpleDateFormat.setTimeZone((timeZone == null) ? TimeZone.getDefault() : timeZone);
            return simpleDateFormat.format(date);

        } catch (Exception ex) {
            return "";
        }
    }

    /**
     *
     * @param StringDate
     * @param dateFormat
     * @param timeZone
     * @return
     */
    public static String formatStringDate(String StringDate, String dateFormat, TimeZone timeZone) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            Date date = simpleDateFormat.parse(StringDate);

            simpleDateFormat.setTimeZone((timeZone == null) ? TimeZone.getDefault() : timeZone);

            return simpleDateFormat.format(date);

        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Format the Date to given date format in the default time zone
     *
     * @param date
     * @param dateFormat
     * @return the desired formatted date string
     * <br/> Returns empty string if there is a problem
     */

    public static String format(Date date, String dateFormat) {

        return format(date, dateFormat, null);
    }


    /**
     * format date.
     *
     * @param year
     * @param month
     * @param day
     * @param format
     * @return
     */
    public static String getDateFormat(int year, int month, int day, String format) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date date = cal.getTime();

        return format(date, format);
    }

    public static Date getDateFromYearMonthDay(int year, int month, int day){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date date = cal.getTime();

        return date;
    }
    /*
     * format time.
     *
     * @param hour
     * @param minute
     * @return
     */
    public static String getTimeFormat(int hour, int minute, String pattern) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        Date date = cal.getTime();
        return format(date, pattern);
    }

    /*
     * format time.
     *
     * @param hour
     * @param minute
     * @return
     */
    public static String getTimeFormatInUtc(int hour, int minute, String pattern) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        Date date = cal.getTime();

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat.format(date);

        } catch (Exception ex) {
            return "";
        }
       // return format(date, pattern);

    }

    /**
     *check if startDate is after endDate
     * @param starTime
     * @param endTime
     * @return
     */
    public static boolean isAfter(String starTime, String endTime, String pattern) {

      //  String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date startDate = sdf.parse(starTime);
            Date endDate = sdf.parse(endTime);

            if(startDate.after(endDate)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e){

        }
        return false;
    }

    /**
     * get format type time : AM or PM
     * @return
     */
    public static String timeType(Context context, Date date, String format){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String type = calendar.get(Calendar.AM_PM) == 0 ? context.getString(R.string.Zon_ZonDt_lbl_zone_time_am) :
                context.getString(R.string.Zon_ZonDt_lbl_zone_time_pm);

        String pattern = format + type;
        DateFormat timeFormat = new SimpleDateFormat(pattern, Locale.US);
        return timeFormat.format(date);
    }

    /**
     *
     * @param context
     * @param date
     * @return
     */
    public static String timeTypePattern(Context context, Date date, String format){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String type = calendar.get(Calendar.AM_PM) == 0 ? context.getString(R.string.Zon_ZonDt_lbl_zone_am) :
                context.getString(R.string.Zon_ZonDt_lbl_zone_pm);

        String pattern = format + type;
        DateFormat timeFormat = new SimpleDateFormat(pattern, Locale.US);
        return timeFormat.format(date);
    }



    public static String getCurrentDateFromTimeStamp(long timestamp) {
        String sDate="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        Date netDate = null;
        String yesterdayDateTimeString = null, currentDateTimeString = null;
        try {
            netDate = (new Date(timestamp));
            sdf.format(netDate);
            sDate = sdf.format(netDate);
             currentDateTimeString = sdf.format(c.getTime());
            c.add(Calendar.DATE, -1);
            yesterdayDateTimeString =  sdf.format(c.getTime());

        } catch (Exception e) {
            System.err.println("There's an error in the Date!");
        }
        return currentDateTimeString;
    }

    public static String getDateFromTimeStamp(long timestamp, String pattern) {
        String sDate="";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        Date netDate;
        try {
            netDate = (new Date(timestamp));
            sdf.format(netDate);
            sDate = sdf.format(netDate);
            return sDate;
        } catch (Exception e) {
            System.err.println("There's an error in the Date!");
        }
        return sDate;
    }



    /**
     * check if time is AM or PM.
     * @param context
     * @param date
     * @return
     */
    public static String amOrPm(Context context, Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String type = calendar.get(Calendar.AM_PM) == 0 ? context.getString(R.string.Zon_ZonDt_lbl_zone_time_am) :
                context.getString(R.string.Zon_ZonDt_lbl_zone_time_pm);

        return type;
    }
}
