package com.example.revalidatieapp;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TimeConverter {
    private static final String TAG = "TimeConverter";

    /**
     * @param theTime converts a integer formatted like this 6 to a String formatted like this 06.
     *                the zero is only added when the length is 1.
     * @return a string which is always formatted as two characters like this 06 instead of just this 6.
     */
    public static String convertTimeIntToString(int theTime){
        String time = theTime + "";
        if(time.length() == 1){
            time = "0" + time;
        }
        return time;
    }

    /**
     * @param hourString number of hours formatted like this: 2:00
     * @return returns the number of minuets in the given hours.
     */
    public static int convertHourStringToMinuteInt(String hourString){
        char hourChar = hourString.charAt(0);
        Log.d(TAG, "convertHourStringToMinuteInt: hourChar = " + hourChar);
        int hour = Integer.parseInt(hourChar + "");
        Log.d(TAG, "convertHourStringToMinuteInt: hour = " + hour);
        int minute = hour * 60;
        Log.d(TAG, "convertHourStringToMinuteInt: minute = " + minute);
        return minute;
    }


    /**
     * @param hour the hour that will be set to the calendar
     * @param minute the number of minutes set to the calendar
     * @return the calendar holding the right time
     */
    public static Calendar getTime(String hour, String minute) throws ParseException {
        String time = hour + ":" + minute;

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        Date date = df.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


    /**
     * @param currentTime calendar which contains the current time
     * @param interval integer which contains the number of minutes between the notifications
     * @param bedtime calendar which contains the time from which the users no longer wants notifications
     * @return a list with calendars where every calendar holds a time on which a notification has to be send
     * @throws ParseException
     */
    public static ArrayList<Calendar> getPracticeTimes(Calendar currentTime, int interval, Calendar bedtime) throws ParseException {

        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int bedtimeHour = bedtime.get(Calendar.HOUR_OF_DAY);

        int currentMinutes = currentTime.get(Calendar.MINUTE);
        int bedtimeMinutes = bedtime.get(Calendar.MINUTE);

        ArrayList<Calendar> practiceTimes = new ArrayList<>();

        currentTime.add(Calendar.SECOND, 1); //unknown if this is necessary
        practiceTimes.add(currentTime);

        //difference in minutes between currentTime and bedtime
        int diffMinutes = (bedtimeHour - currentHour) * 60 + bedtimeMinutes - currentMinutes;

        if (diffMinutes < 0) {
            diffMinutes = diffMinutes + 60 * 24;
        }

        Log.d(TAG, "getPracticeTimes1: diffMinutes = " + diffMinutes);
        Log.d(TAG, "getPracticeTimes1: interval = " + interval);

        while (diffMinutes >= interval) {
            //gets last practiceTime from practiceTimes
            Calendar lastPracticeTime = practiceTimes.get(practiceTimes.size() - 1);

            //creates a new calendar with the same time as lastPracticeTime
            Calendar newPracticeTime = Calendar.getInstance();
            newPracticeTime.set(Calendar.HOUR_OF_DAY, lastPracticeTime.get(Calendar.HOUR_OF_DAY));
            newPracticeTime.set(Calendar.MINUTE, lastPracticeTime.get(Calendar.MINUTE));
            newPracticeTime.set(Calendar.SECOND, 0);

            //adds the interval to the last practiceTime which updates it to the new practiceTime
            newPracticeTime.add(Calendar.MINUTE, interval);

            //adds the new practiceTime to the arrayList
            practiceTimes.add(newPracticeTime);

            diffMinutes = diffMinutes - interval;
        }

        return practiceTimes;
    }
}
