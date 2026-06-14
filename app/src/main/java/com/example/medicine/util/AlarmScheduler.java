package com.example.medicine.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.medicine.receiver.AlarmReceiver;

import java.util.Calendar;

public class AlarmScheduler {

    public static void scheduleAlarm(Context context, int alarmId, long medId,
                                     String medName, String remindTime) {
        Calendar calendar = parseTimeToCalendar(remindTime);
        if (calendar == null) {
            return;
        }

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(context.getString(com.example.medicine.R.string.alarm_action));
        intent.putExtra(AlarmReceiver.EXTRA_MED_ID, medId);
        intent.putExtra(AlarmReceiver.EXTRA_MED_NAME, medName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    public static void cancelAlarm(Context context, int alarmId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(context.getString(com.example.medicine.R.string.alarm_action));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        pendingIntent.cancel();
    }

    public static Calendar parseTimeToCalendar(String time) {
        if (time == null || !time.matches("\\d{1,2}:\\d{2}")) {
            return null;
        }
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }
}
