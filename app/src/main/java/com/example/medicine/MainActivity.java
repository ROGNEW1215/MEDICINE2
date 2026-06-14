package com.example.medicine;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.medicine.adapter.MedicationAdapter;
import com.example.medicine.db.DatabaseHelper;
import com.example.medicine.model.Medication;
import com.example.medicine.model.MedicationHistory;
import com.example.medicine.util.AlarmScheduler;
import com.example.medicine.util.PrefsHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION = 100;
    private static final String PREFS_DAILY = "daily_reset_prefs";
    private static final String KEY_LAST_DATE = "last_date";

    private TextView tvWelcome;
    private TextView tvEmptyHint;
    private ListView lvMedications;
    private MedicationAdapter adapter;
    private DatabaseHelper dbHelper;
    private PrefsHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        prefsHelper = new PrefsHelper(this);

        tvWelcome = findViewById(R.id.tv_welcome);
        tvEmptyHint = findViewById(R.id.tv_empty_hint);
        lvMedications = findViewById(R.id.lv_medications);
        Button btnAddMed = findViewById(R.id.btn_add_med);
        Button btnSettings = findViewById(R.id.btn_settings);
        Button btnEmergency = findViewById(R.id.btn_emergency);
        Button btnHealthLog = findViewById(R.id.btn_health_log);
        Button btnHistory = findViewById(R.id.btn_history);

        adapter = new MedicationAdapter(this, dbHelper.getAllPlans());
        lvMedications.setAdapter(adapter);

        adapter.setOnTakenListener(this::markMedicationTaken);

        lvMedications.setOnItemClickListener((parent, view, position, id) -> {
            Medication med = adapter.getItem(position);
            Intent intent = new Intent(MainActivity.this, AddMedActivity.class);
            intent.putExtra(AddMedActivity.EXTRA_MED_ID, med.getId());
            startActivity(intent);
        });

        lvMedications.setOnItemLongClickListener((parent, view, position, id) -> {
            Medication med = adapter.getItem(position);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_delete_title)
                    .setMessage(R.string.confirm_delete_message)
                    .setPositiveButton(android.R.string.ok, (d, w) -> {
                        AlarmScheduler.cancelAlarm(this, med.getAlarmId());
                        dbHelper.deletePlan(med.getId());
                        Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                        refreshMedicationList();
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
            return true;
        });

        btnAddMed.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddMedActivity.class)));

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("current_name", prefsHelper.getElderName());
            startActivity(intent);
        });

        btnEmergency.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EmergencyActivity.class)));

        btnHealthLog.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HealthLogActivity.class)));

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)));

        requestNotificationPermission();
    }

    private void markMedicationTaken(Medication med) {
        dbHelper.updateTakenStatus(med.getId(), 1);

        MedicationHistory history = new MedicationHistory();
        history.setMedId(med.getId());
        history.setMedName(med.getMedName());
        history.setTakenTime(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date()));
        dbHelper.insertMedicationHistory(history);

        refreshMedicationList();
    }

    private void checkDailyReset() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = getSharedPreferences(PREFS_DAILY, MODE_PRIVATE);
        String lastDate = prefs.getString(KEY_LAST_DATE, "");

        if (!today.equals(lastDate)) {
            dbHelper.resetDailyTakenStatus();
            prefs.edit().putString(KEY_LAST_DATE, today).apply();
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDailyReset();
        refreshWelcome();
        refreshMedicationList();
    }

    private void refreshWelcome() {
        String name = prefsHelper.getElderName();
        tvWelcome.setText(getString(R.string.welcome_format, name));
    }

    private void refreshMedicationList() {
        List<Medication> list = dbHelper.getAllPlans();
        adapter.updateData(list);
        boolean isEmpty = list.isEmpty();
        tvEmptyHint.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        lvMedications.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
