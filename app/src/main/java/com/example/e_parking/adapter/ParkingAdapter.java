package com.example.e_parking.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_parking.R;
import com.example.e_parking.model.ParkingPlace;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {
    private final ArrayList<HashMap<String, ParkingPlace>> parkings;
    public interface OnParkingClickListener{
        void onParkingClick(String key, ParkingPlace parkingPlace);
        void onGalleryClick(String key, ParkingPlace parkingPlace);
        void onParkingLocation(String key, ParkingPlace parkingPlace);
        void onParkingRemove(String key, ParkingPlace parkingPlace);
    }
    OnParkingClickListener listener;
    public ParkingAdapter(ArrayList<HashMap<String, ParkingPlace>> parkings, OnParkingClickListener listener) {
        this.parkings=parkings;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.parking_row_item,parent,false);
        return new ParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder holder, int position) {
        HashMap<String,ParkingPlace> hashMap=parkings.get(position);
        final String key=hashMap.keySet().iterator().next();
        final ParkingPlace parkingPlace=hashMap.get(key);
        holder.tvTitle.setText(parkingPlace.getTitle());
        holder.tvAddress.setText(parkingPlace.getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onParkingClick(key,parkingPlace);
            }
        });
        holder.btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGalleryClick(key,parkingPlace);
            }
        });
        holder.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onParkingLocation(key,parkingPlace);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onParkingRemove(key,parkingPlace);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkings.size();
    }


    public static class ParkingViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvAddress;
        public ImageView btnGallery, btnMap, btnDelete;
        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDelete=itemView.findViewById(R.id.btnDelete);
            btnMap=itemView.findViewById(R.id.btnLocation);
            btnGallery=itemView.findViewById(R.id.btnGallery);
            tvTitle=itemView.findViewById(R.id.tvTitle);
            tvAddress=itemView.findViewById(R.id.tvAddress);
        }
    }
}
