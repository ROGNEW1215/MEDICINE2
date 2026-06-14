package com.example.medicine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.medicine.R;
import com.example.medicine.model.MedicationHistory;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    private final Context context;
    private List<MedicationHistory> historyList;
    private final LayoutInflater inflater;

    public HistoryAdapter(Context context, List<MedicationHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<MedicationHistory> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return historyList == null ? 0 : historyList.size();
    }

    @Override
    public MedicationHistory getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return historyList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_history, parent, false);
            holder = new ViewHolder();
            holder.tvMedName = convertView.findViewById(R.id.tv_history_med_name);
            holder.tvTime = convertView.findViewById(R.id.tv_history_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MedicationHistory history = getItem(position);
        holder.tvMedName.setText(history.getMedName());
        holder.tvTime.setText(history.getTakenTime());

        return convertView;
    }

    private static class ViewHolder {
        TextView tvMedName;
        TextView tvTime;
    }
}
