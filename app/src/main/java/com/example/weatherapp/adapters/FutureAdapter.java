package com.example.weatherapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.entities.FutureDomain;
import com.example.weatherapp.update.UpdateUI;

import java.util.ArrayList;

public class FutureAdapter extends RecyclerView.Adapter<FutureAdapter.FutureViewHolder> {
    private final ArrayList<FutureDomain> items;
    private Context context;

    // Constructor for FutureAdapter
    public FutureAdapter(ArrayList<FutureDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FutureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_future, parent, false);
        return new FutureViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DiscouragedApi"})
    @Override
    public void onBindViewHolder(@NonNull FutureViewHolder holder, int position) {
        // Bind data to the ViewHolder
        FutureDomain item = items.get(position);
        context = holder.itemView.getContext();

        // Set text for day, status, high and low temperature
        holder.textDay.setText(item.getDay());
        holder.textStatus.setText(item.getStatus());
        holder.textHigh.setText(item.getHighTemp() + "°C");
        holder.textLow.setText(item.getLowTemp() + "°C");

        // Get the icon resource ID and set the image resource for the weather icon
        int iconResId = UpdateUI.getIconID(item.getPicPath());
        holder.imgPicNext.setImageResource(context.getResources().getIdentifier(String.valueOf(iconResId), "drawable", context.getPackageName()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder class to hold references to each item in the RecyclerView
    static class FutureViewHolder extends RecyclerView.ViewHolder {
        private final TextView textDay;
        private final TextView textStatus;
        private final TextView textHigh;
        private final TextView textLow;
        private final ImageView imgPicNext;

        // Constructor for FutureViewHolder
        public FutureViewHolder(@NonNull View itemView) {
            super(itemView);
            textDay = itemView.findViewById(R.id.textDay);
            textStatus = itemView.findViewById(R.id.textStatus);
            textHigh = itemView.findViewById(R.id.textHigh);
            textLow = itemView.findViewById(R.id.textLow);
            imgPicNext = itemView.findViewById(R.id.imgPicNext);
        }
    }
}
