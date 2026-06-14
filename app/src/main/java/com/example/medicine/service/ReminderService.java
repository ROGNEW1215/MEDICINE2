package com.example.medicine.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.medicine.MainActivity;
import com.example.medicine.R;
import com.example.medicine.receiver.AlarmReceiver;

public class ReminderService extends Service {

    private static final int NOTIFICATION_ID = 1001;
    private MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String medName = "药品";
        if (intent != null) {
            String name = intent.getStringExtra(AlarmReceiver.EXTRA_MED_NAME);
            if (name != null) {
                medName = name;
            }
        }

        createNotificationChannel();
        Notification notification = buildNotification(medName);
        startForeground(NOTIFICATION_ID, notification);

        showToast(medName);
        playAlarmSound();

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.reminder_channel_id),
                    getString(R.string.reminder_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(getString(R.string.reminder_channel_name));
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildNotification(String medName) {
        Intent launchIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, getString(R.string.reminder_channel_id))
                .setSmallIcon(R.drawable.ic_med_pill)
                .setContentTitle(getString(R.string.reminder_notification_title))
                .setContentText(getString(R.string.reminder_notification_text, medName))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void showToast(String medName) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(
                        ReminderService.this,
                        getString(R.string.reminder_toast, medName),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    private void playAlarmSound() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.reminder_alarm);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> stopReminder());
                mediaPlayer.start();
            } else {
                stopReminder();
            }
        } catch (Exception e) {
            stopReminder();
        }
    }

    private void stopReminder() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
