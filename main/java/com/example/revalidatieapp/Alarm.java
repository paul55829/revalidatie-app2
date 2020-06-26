package com.example.revalidatieapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Alarm {

    Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Alarm";

    private String userId;

    public Alarm(Context context, String userId){
        this.context = context;
        this.userId = userId;
    }

    /**
     * cancels all alarms and sets the new alarms
     * updates the database with the new practiceTimes
     */
    public void setAlarmWithNewPracticeTimes() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try{
                            //get bedtime
                            Map user = documentSnapshot.getData();
                            HashMap<String, String> goals = (HashMap<String, String>) user.get("bedtime");
                            String hour = goals.get("hour");
                            String minute = goals.get("minute");
                            Calendar bedtime = TimeConverter.getTime(hour, minute);

                            //get practiceDuration
                            int practiceDuration = Integer.parseInt(documentSnapshot.getLong("practiceDuration").toString());

                            //get restDuration
                            String restDurationString = documentSnapshot.getString("restDuration");
                            int restDuration = TimeConverter.convertHourStringToMinuteInt(restDurationString);

                            //interval is the time between the notifications that have to be send.
                            Log.d(TAG, "onSuccess: practiceDuration = " + practiceDuration);
                            Log.d(TAG, "onSuccess: restDuration = " + restDuration);
                            int interval = practiceDuration + restDuration;

                            //gets current time and puts it into a calendar
                            Calendar currentTime = Calendar.getInstance();

                            //gets arrayList with calendars containing the times on which the user is supposed to practice.
                            ArrayList<Calendar> practiceTimes = TimeConverter.getPracticeTimes(currentTime, interval, bedtime);

                            //get receiveReminders
                            Boolean receiveReminders = documentSnapshot.getBoolean("receiveReminders");

                            //removes all previous practiceTimes in the database
                            removePracticeTimes();

                            //removes the previously set alarms
                            cancelAlarms();

                            int id = 1;
                            for (Calendar time : practiceTimes){
                                Log.d(TAG, "onSuccess: practiceTime = " + time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE) + ":" + time.get(Calendar.SECOND));
                                Log.d(TAG, "onSuccess: id = " + id);

                                if (receiveReminders) {
                                    //schedules a notification at the given time
                                    createNotification(time, id);
                                }

                                //creates the timeStamp that will be stored in the database
                                Timestamp update = new Timestamp(time.getTime());

                                //field contains the name of the field that has to be updated
                                String field = "practiceTimes.practiceTime" + id;

                                //update the practiceTime in the database
                                db.collection("users").document(userId)
                                        .update(
                                                field, update
                                        );

                                id++;
                            }
                        }
                        catch(Exception e){
                            Log.d(TAG, "onSuccess: " + e.toString());
                        }

                    }
                });
    }

    /**
     * sets the alarms to the practiceTimes in the database
     */
    public void setAlarmWithCurrentTimes() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            //the id that will be given to the Alarm Manager
                            int id = 1;

                            //gets the practiceTimes Hashmap
                            Map user = documentSnapshot.getData();
                            HashMap<String, Timestamp> practiceTimes = (HashMap<String, Timestamp>) user.get("practiceTimes");

                            //for each practiceTime in practiceTimes there is an notification created
                            for (Map.Entry practiceTime : practiceTimes.entrySet()) {
                                //gets the timeStamp of the practiceTimes
                                Timestamp time = (Timestamp) practiceTime.getValue();

                                //converts the Timestamp into a calendar
                                Date date = time.toDate();
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);

                                //creates the notification with the calendar and id
                                createNotification(calendar, id);

                                //increases the id by 1 so the next alarm set by alarmManager has a different id
                                id++;
                            }
                        }
                        catch(Exception e){
                            Log.d(TAG, "onSuccess: " + e.toString());
                        }

                    }
                });
    }

    /**
     * cancels all alarms
     */
    public void cancelAlarms() {
        int id = 1;
        while(id < 25) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);

            alarmManager.cancel(pendingIntent);
            id++;
        }
    }

    /**
     * removes all practiceTimes of a user in the database
     */
    private void removePracticeTimes() {
        HashMap<String,Object> deletePracticeTimes = new HashMap<String, Object>();
        deletePracticeTimes.put("practiceTimes", FieldValue.delete());
        db.collection("users").document(userId).update(deletePracticeTimes);
    }

    /**
     *
     * @param calendar time on which the user will get a notification
     * @param id the id that will be given to the pendingIntent
     */
    private void createNotification(Calendar calendar, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);

        //increases date by one if time in calendar is before the current time.
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() , pendingIntent);
    }
}
