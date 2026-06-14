package com.example.medicine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.medicine.R;
import com.example.medicine.model.Medication;

import java.util.List;

public class MedicationAdapter extends BaseAdapter {

    public interface OnTakenListener {
        void onTaken(Medication medication);
    }

    private final Context context;
    private List<Medication> medications;
    private final LayoutInflater inflater;
    private OnTakenListener onTakenListener;

    public MedicationAdapter(Context context, List<Medication> medications) {
        this.context = context;
        this.medications = medications;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnTakenListener(OnTakenListener listener) {
        this.onTakenListener = listener;
    }

    public void updateData(List<Medication> medications) {
        this.medications = medications;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return medications == null ? 0 : medications.size();
    }

    @Override
    public Medication getItem(int position) {
        return medications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return medications.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_medication, parent, false);
            holder = new ViewHolder();
            holder.cbTaken = convertView.findViewById(R.id.cb_taken);
            holder.ivIcon = convertView.findViewById(R.id.iv_med_icon);
            holder.tvName = convertView.findViewById(R.id.tv_med_name);
            holder.tvDosage = convertView.findViewById(R.id.tv_dosage);
            holder.tvTime = convertView.findViewById(R.id.tv_remind_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Medication med = getItem(position);
        holder.tvName.setText(med.getMedName());
        holder.tvDosage.setText(med.getDosage());
        holder.tvTime.setText(med.getRemindTime());
        holder.ivIcon.setImageResource(getIconRes(med.getIconType()));

        boolean taken = med.isTaken();
        int textColor = taken ? R.color.text_taken : R.color.text_primary;
        int secondaryColor = taken ? R.color.text_taken : R.color.text_secondary;
        holder.tvName.setTextColor(context.getResources().getColor(textColor, null));
        holder.tvDosage.setTextColor(context.getResources().getColor(secondaryColor, null));
        holder.tvTime.setTextColor(context.getResources().getColor(
                taken ? R.color.text_taken : R.color.primary, null));

        holder.cbTaken.setOnCheckedChangeListener(null);
        holder.cbTaken.setChecked(taken);
        holder.cbTaken.setEnabled(!taken);
        holder.cbTaken.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && onTakenListener != null && !med.isTaken()) {
                onTakenListener.onTaken(med);
            }
        });

        return convertView;
    }

    private int getIconRes(int iconType) {
        switch (iconType) {
            case 1:
                return R.drawable.ic_med_capsule;
            case 2:
                return R.drawable.ic_med_liquid;
            default:
                return R.drawable.ic_med_pill;
        }
    }

    private static class ViewHolder {
        CheckBox cbTaken;
        ImageView ivIcon;
        TextView tvName;
        TextView tvDosage;
        TextView tvTime;
    }
}
