package com.example.medicine.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.medicine.R;
import com.example.medicine.model.Medication;
import com.google.android.material.card.MaterialCardView;

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
            holder.cardView = convertView.findViewById(R.id.card_medication);
            holder.flIconContainer = convertView.findViewById(R.id.fl_icon_container);
            holder.cbTaken = convertView.findViewById(R.id.cb_taken);
            holder.ivIcon = convertView.findViewById(R.id.iv_med_icon);
            holder.tvName = convertView.findViewById(R.id.tv_med_name);
            holder.tvDosage = convertView.findViewById(R.id.tv_dosage);
            holder.tvTime = convertView.findViewById(R.id.tv_remind_time);
            holder.tvTakenLabel = convertView.findViewById(R.id.tv_taken_label);
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
        if (taken) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.slate_100));
            holder.cardView.setAlpha(0.7f);
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.slate_100));

            holder.flIconContainer.setBackgroundResource(R.drawable.bg_med_icon_container_taken);
            holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.slate_400));

            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.slate_500));
            holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvDosage.setTextColor(ContextCompat.getColor(context, R.color.slate_400));
            holder.tvTime.setVisibility(View.GONE);

            holder.cbTaken.setVisibility(View.GONE);
            holder.cbTaken.setOnClickListener(null);
            holder.tvTakenLabel.setVisibility(View.VISIBLE);
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background));
            holder.cardView.setAlpha(1f);
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.slate_100));

            holder.flIconContainer.setBackgroundResource(R.drawable.bg_med_icon_container);
            holder.ivIcon.clearColorFilter();

            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.slate_800));
            holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvDosage.setTextColor(ContextCompat.getColor(context, R.color.slate_500));
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.tvTime.setBackgroundResource(R.drawable.bg_time_badge);
            holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.red_600));

            holder.cbTaken.setVisibility(View.VISIBLE);
            holder.tvTakenLabel.setVisibility(View.GONE);

            holder.cbTaken.setOnClickListener(v -> {
                if (onTakenListener != null && !med.isTaken()) {
                    onTakenListener.onTaken(med);
                }
            });
        }

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
        MaterialCardView cardView;
        FrameLayout flIconContainer;
        ImageButton cbTaken;
        ImageView ivIcon;
        TextView tvName;
        TextView tvDosage;
        TextView tvTime;
        TextView tvTakenLabel;
    }
}
