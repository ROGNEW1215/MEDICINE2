package com.example.medicine;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicine.adapter.HistoryAdapter;
import com.example.medicine.db.DatabaseHelper;
import com.example.medicine.model.MedicationHistory;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private TextView tvHistoryEmpty;
    private ListView lvHistory;
    private HistoryAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        tvHistoryEmpty = findViewById(R.id.tv_history_empty);
        lvHistory = findViewById(R.id.lv_history);

        adapter = new HistoryAdapter(this, dbHelper.getAllMedicationHistory());
        lvHistory.setAdapter(adapter);

        refreshHistoryList();
    }

    private void refreshHistoryList() {
        List<MedicationHistory> list = dbHelper.getAllMedicationHistory();
        adapter.updateData(list);
        boolean isEmpty = list.isEmpty();
        tvHistoryEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        lvHistory.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
