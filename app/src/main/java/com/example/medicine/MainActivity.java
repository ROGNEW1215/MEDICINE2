package com.example.medicine;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.medicine.adapter.MedicationAdapter;
import com.example.medicine.db.DatabaseHelper;
import com.example.medicine.model.Medication;
import com.example.medicine.model.MedicationHistory;
import com.example.medicine.util.AlarmScheduler;
import com.example.medicine.util.PrefsHelper;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION = 100;
    private static final String PREFS_DAILY = "daily_reset_prefs";
    private static final String KEY_LAST_DATE = "last_date";

    private TextView tvDate;
    private TextView tvWelcome;
    private TextView tvEmptyHint;
    private TextView tvDailyProgress;
    private TextView tvProgressPercent;
    private CircularProgressIndicator progressDaily;
    private ListView lvMedications;
    private View navHome;
    private View navHealthLog;
    private View navSettings;
    private ImageView navHomeIcon;
    private ImageView navHealthIcon;
    private ImageView navSettingsIcon;
    private TextView navHomeLabel;
    private TextView navHealthLabel;
    private TextView navSettingsLabel;
    private MedicationAdapter adapter;
    private DatabaseHelper dbHelper;
    private PrefsHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupStatusBar();

        dbHelper = new DatabaseHelper(this);
        prefsHelper = new PrefsHelper(this);

        tvDate = findViewById(R.id.tv_date);
        tvWelcome = findViewById(R.id.tv_welcome);
        tvEmptyHint = findViewById(R.id.tv_empty_hint);
        tvDailyProgress = findViewById(R.id.tv_daily_progress);
        tvProgressPercent = findViewById(R.id.tv_progress_percent);
        progressDaily = findViewById(R.id.progress_daily);
        lvMedications = findViewById(R.id.lv_medications);
        navHome = findViewById(R.id.nav_home);
        navHealthLog = findViewById(R.id.nav_health_log);
        navSettings = findViewById(R.id.nav_settings);
        navHomeIcon = findViewById(R.id.nav_home_icon);
        navHealthIcon = findViewById(R.id.nav_health_icon);
        navSettingsIcon = findViewById(R.id.nav_settings_icon);
        navHomeLabel = findViewById(R.id.nav_home_label);
        navHealthLabel = findViewById(R.id.nav_health_label);
        navSettingsLabel = findViewById(R.id.nav_settings_label);
        View btnAddMed = findViewById(R.id.btn_add_med);
        View btnEmergency = findViewById(R.id.btn_emergency);
        View btnRecordBp = findViewById(R.id.btn_record_bp);

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

        btnEmergency.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EmergencyActivity.class)));

        btnRecordBp.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HealthLogActivity.class)));

        navHome.setOnClickListener(v -> setNavSelected(R.id.nav_home));

        navHealthLog.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HealthLogActivity.class));
        });

        navSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("current_name", prefsHelper.getElderName());
            startActivity(intent);
        });

        requestNotificationPermission();
    }

    private void setupStatusBar() {
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, true);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
        }
    }

    private void setNavSelected(int selectedId) {
        int activeColor = ContextCompat.getColor(this, R.color.teal_500);
        int inactiveColor = ContextCompat.getColor(this, R.color.slate_400);

        setNavItemStyle(navHomeIcon, navHomeLabel, selectedId == R.id.nav_home, activeColor, inactiveColor);
        setNavItemStyle(navHealthIcon, navHealthLabel, selectedId == R.id.nav_health_log, activeColor, inactiveColor);
        setNavItemStyle(navSettingsIcon, navSettingsLabel, selectedId == R.id.nav_settings, activeColor, inactiveColor);
    }

    private void setNavItemStyle(ImageView icon, TextView label, boolean selected,
                                 int activeColor, int inactiveColor) {
        int color = selected ? activeColor : inactiveColor;
        icon.setImageTintList(ColorStateList.valueOf(color));
        label.setTextColor(color);
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
        updateProgressUI();
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
        updateProgressUI();
        setNavSelected(R.id.nav_home);
    }

    private void refreshWelcome() {
        String name = prefsHelper.getElderName();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int resId;
        if (hour < 11) {
            resId = R.string.greeting_morning;
        } else if (hour < 13) {
            resId = R.string.greeting_noon;
        } else if (hour < 18) {
            resId = R.string.greeting_afternoon;
        } else {
            resId = R.string.greeting_evening;
        }
        tvWelcome.setText(getString(resId, name));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日 EEEE", Locale.CHINA);
        tvDate.setText(dateFormat.format(new Date()));
    }

    private void refreshMedicationList() {
        List<Medication> list = dbHelper.getAllPlans();
        adapter.updateData(list);
        boolean isEmpty = list.isEmpty();
        tvEmptyHint.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        lvMedications.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void updateProgressUI() {
        List<Medication> list = dbHelper.getAllPlans();
        int total = list.size();
        int taken = 0;
        for (Medication m : list) {
            if (m.getIsTaken() == 1) {
                taken++;
            }
        }

        String countText = String.valueOf(taken);
        String suffixText = getString(R.string.today_progress_suffix, total);
        SpannableString progressText = new SpannableString(countText + suffixText);
        progressText.setSpan(new StyleSpan(Typeface.BOLD), 0, countText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        progressText.setSpan(new RelativeSizeSpan(1.0f), 0, countText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int suffixStart = countText.length();
        progressText.setSpan(new RelativeSizeSpan(0.75f), suffixStart, progressText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        progressText.setSpan(new ForegroundColorSpan(0xB3FFFFFF), suffixStart, progressText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDailyProgress.setText(progressText);

        int percent = total > 0 ? (taken * 100 / total) : 0;
        tvProgressPercent.setText(getString(R.string.progress_percent_format, percent));
        progressDaily.setMax(total > 0 ? total : 1);
        progressDaily.setProgressCompat(taken, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
