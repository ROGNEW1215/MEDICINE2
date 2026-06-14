package com.example.medicine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicine.util.PrefsHelper;

public class EmergencyActivity extends AppCompatActivity {

    private TextView tvPhone;
    private PrefsHelper prefsHelper;
    private String emergencyPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        prefsHelper = new PrefsHelper(this);

        TextView tvContactName = findViewById(R.id.tv_contact_name);
        tvPhone = findViewById(R.id.tv_contact_phone);
        Button btnCall = findViewById(R.id.btn_call_emergency);

        String elderName = prefsHelper.getElderName();
        emergencyPhone = prefsHelper.getEmergencyPhone();

        tvContactName.setText(elderName);
        if (emergencyPhone.isEmpty()) {
            tvPhone.setText(R.string.emergency_no_phone);
            btnCall.setEnabled(false);
        } else {
            tvPhone.setText(emergencyPhone);
        }

        btnCall.setOnClickListener(v -> dialEmergency());
    }

    private void dialEmergency() {
        if (emergencyPhone == null || emergencyPhone.isEmpty()) {
            Toast.makeText(this, R.string.emergency_no_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + emergencyPhone));
        startActivity(dialIntent);
    }
}
