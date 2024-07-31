package com.example.weatherapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.entities.Hourly;
import com.example.weatherapp.update.UpdateUI;

import java.util.ArrayList;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder> {
    private final ArrayList<Hourly> items;

    public HourlyAdapter(ArrayList<Hourly> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_hourly, parent, false);
        return new HourlyViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        Hourly item = items.get(position);
        holder.textHour.setText(item.getDateTime());
        holder.textTemp.setText(item.getTemp() + "°C");

        // Get the icon resource ID and set it directly
        int iconResId = UpdateUI.getIconID(item.getIcon());
        holder.imagePic.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HourlyViewHolder extends RecyclerView.ViewHolder {
        private final TextView textHour;
        private final TextView textTemp;
        private final ImageView imagePic;

        public HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            textHour = itemView.findViewById(R.id.textHour);
            textTemp = itemView.findViewById(R.id.textTemp);
            imagePic = itemView.findViewById(R.id.imagePic);
        }
    }
}
