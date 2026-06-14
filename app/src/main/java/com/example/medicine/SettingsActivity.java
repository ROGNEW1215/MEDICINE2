package com.example.medicine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicine.util.PrefsHelper;

public class SettingsActivity extends AppCompatActivity {

    private EditText etElderName;
    private EditText etEmergencyPhone;
    private PrefsHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        prefsHelper = new PrefsHelper(this);

        etElderName = findViewById(R.id.et_elder_name);
        etEmergencyPhone = findViewById(R.id.et_emergency_phone);
        Button btnSave = findViewById(R.id.btn_save_settings);

        String currentName = getIntent().getStringExtra("current_name");
        if (currentName != null && !currentName.isEmpty()) {
            etElderName.setText(currentName);
        } else {
            etElderName.setText(prefsHelper.getElderName());
        }
        etEmergencyPhone.setText(prefsHelper.getEmergencyPhone());

        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        String name = etElderName.getText().toString().trim();
        String phone = etEmergencyPhone.getText().toString().trim();

        if (name.isEmpty()) {
            name = "长辈";
        }

        prefsHelper.setElderName(name);
        prefsHelper.setEmergencyPhone(phone);
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
        finish();
    }
}
