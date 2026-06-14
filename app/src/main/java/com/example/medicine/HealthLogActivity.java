package com.example.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicine.db.DatabaseHelper;
import com.example.medicine.model.HealthRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HealthLogActivity extends AppCompatActivity {

    private EditText etSystolic;
    private EditText etDiastolic;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_log);

        dbHelper = new DatabaseHelper(this);
        etSystolic = findViewById(R.id.et_systolic);
        etDiastolic = findViewById(R.id.et_diastolic);
        Button btnSave = findViewById(R.id.btn_save_health);
        Button btnViewHistory = findViewById(R.id.btn_view_history);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveHealthRecord());
        btnViewHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }

    private void saveHealthRecord() {
        String systolicStr = etSystolic.getText().toString().trim();
        String diastolicStr = etDiastolic.getText().toString().trim();

        if (systolicStr.isEmpty() || diastolicStr.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        int systolic;
        int diastolic;
        try {
            systolic = Integer.parseInt(systolicStr);
            diastolic = Integer.parseInt(diastolicStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_blood_pressure, Toast.LENGTH_SHORT).show();
            return;
        }

        if (systolic < 60 || systolic > 250 || diastolic < 40 || diastolic > 150) {
            Toast.makeText(this, R.string.error_invalid_blood_pressure, Toast.LENGTH_SHORT).show();
            return;
        }

        HealthRecord record = new HealthRecord();
        record.setSystolic(systolic);
        record.setDiastolic(diastolic);
        record.setRecordTime(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date()));

        long id = dbHelper.insertHealthRecord(record);
        if (id > 0) {
            Toast.makeText(this, R.string.health_save_success, Toast.LENGTH_SHORT).show();
            etSystolic.setText("");
            etDiastolic.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
