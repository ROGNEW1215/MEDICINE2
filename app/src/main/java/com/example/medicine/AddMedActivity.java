package com.example.medicine;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicine.db.DatabaseHelper;
import com.example.medicine.model.Medication;
import com.example.medicine.util.AlarmScheduler;

public class AddMedActivity extends AppCompatActivity {

    public static final String EXTRA_MED_ID = "med_id";

    private EditText etMedName;
    private EditText etDosage;
    private EditText etRemindTime;
    private AutoCompleteTextView spinnerIconType;
    private TextView tvTitle;
    private DatabaseHelper dbHelper;
    private long editMedId = -1;
    private int existingAlarmId = -1;
    private int selectedIconType = 0;
    private ArrayAdapter<CharSequence> iconAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_med);

        dbHelper = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tv_add_med_title);
        etMedName = findViewById(R.id.et_med_name);
        etDosage = findViewById(R.id.et_dosage);
        etRemindTime = findViewById(R.id.et_remind_time);
        spinnerIconType = findViewById(R.id.spinner_icon_type);
        Button btnSave = findViewById(R.id.btn_save);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        iconAdapter = ArrayAdapter.createFromResource(
                this, R.array.icon_types, R.layout.dropdown_item_icon_type);
        spinnerIconType.setAdapter(iconAdapter);
        spinnerIconType.setText(iconAdapter.getItem(0), false);
        spinnerIconType.setOnItemClickListener((parent, view, position, id) ->
                selectedIconType = position);

        if (getIntent().hasExtra(EXTRA_MED_ID)) {
            editMedId = getIntent().getLongExtra(EXTRA_MED_ID, -1);
            loadMedicationForEdit();
        }

        btnSave.setOnClickListener(v -> saveMedication());
    }

    private void loadMedicationForEdit() {
        Medication medication = dbHelper.getById(editMedId);
        if (medication == null) {
            return;
        }

        tvTitle.setText(R.string.edit_med_title);
        etMedName.setText(medication.getMedName());
        etDosage.setText(medication.getDosage());
        etRemindTime.setText(medication.getRemindTime());
        selectedIconType = medication.getIconType();
        spinnerIconType.setText(iconAdapter.getItem(selectedIconType), false);
        existingAlarmId = medication.getAlarmId();
    }

    private void saveMedication() {
        String medName = etMedName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String remindTime = etRemindTime.getText().toString().trim();
        int iconType = selectedIconType;

        if (medName.isEmpty() || dosage.isEmpty() || remindTime.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (AlarmScheduler.parseTimeToCalendar(remindTime) == null) {
            Toast.makeText(this, R.string.error_invalid_time, Toast.LENGTH_SHORT).show();
            return;
        }

        if (editMedId > 0) {
            updateExistingMedication(medName, dosage, remindTime, iconType);
        } else {
            insertNewMedication(medName, dosage, remindTime, iconType);
        }
    }

    private void insertNewMedication(String medName, String dosage, String remindTime, int iconType) {
        int alarmId = dbHelper.getNextAlarmId();
        Medication medication = new Medication();
        medication.setMedName(medName);
        medication.setDosage(dosage);
        medication.setRemindTime(remindTime);
        medication.setIconType(iconType);
        medication.setAlarmId(alarmId);
        medication.setIsTaken(0);

        long medId = dbHelper.insertPlan(medication);
        if (medId > 0) {
            AlarmScheduler.scheduleAlarm(this, alarmId, medId, medName, remindTime);
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    private void updateExistingMedication(String medName, String dosage, String remindTime, int iconType) {
        Medication medication = dbHelper.getById(editMedId);
        if (medication == null) {
            return;
        }

        AlarmScheduler.cancelAlarm(this, medication.getAlarmId());

        medication.setMedName(medName);
        medication.setDosage(dosage);
        medication.setRemindTime(remindTime);
        medication.setIconType(iconType);

        int rows = dbHelper.updatePlan(medication);
        if (rows > 0) {
            AlarmScheduler.scheduleAlarm(this, existingAlarmId, editMedId, medName, remindTime);
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
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
