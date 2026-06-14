package com.example.medicine.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.medicine.service.ReminderService;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_MED_ID = "extra_med_id";
    public static final String EXTRA_MED_NAME = "extra_med_name";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        long medId = intent.getLongExtra(EXTRA_MED_ID, -1);
        String medName = intent.getStringExtra(EXTRA_MED_NAME);
        if (medName == null) {
            medName = "药品";
        }

        Intent serviceIntent = new Intent(context, ReminderService.class);
        serviceIntent.putExtra(EXTRA_MED_ID, medId);
        serviceIntent.putExtra(EXTRA_MED_NAME, medName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
