package com.example.e_parking.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.e_parking.R;
import com.example.e_parking.model.ParkingBlock;
import com.example.e_parking.model.ParkingPlace;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ParkingBlockAdapter extends RecyclerView.Adapter<ParkingBlockAdapter.ParkingViewHolder> {

    private final ParkingPlace parkingPlace;
    private final ArrayList<ParkingBlock> parkingBlocks;

    public ParkingBlockAdapter(ParkingPlace parkingPlace, ArrayList<ParkingBlock> parkingBlocks) {
        this.parkingPlace=parkingPlace;
        this.parkingBlocks=parkingBlocks;
    }

    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.book_parking_row,viewGroup,false);
        return new ParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder parkingViewHolder, int i) {
        ParkingBlock parkingBlock=parkingBlocks.get(i);
        if(parkingBlock.isBooked()){
            parkingViewHolder.imageView.setColorFilter(Color.RED);
        }else if(parkingBlock.isSelected()){
            parkingViewHolder.imageView.setColorFilter(Color.GREEN);
        }else{
            parkingViewHolder.imageView.setColorFilter(Color.BLACK);
        }
            parkingViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(parkingBlock.isBooked()){

                    }
                   else if (parkingBlock.isSelected()) {
                       parkingBlock.setSelected(false);
                    } else{
                       parkingBlock.setSelected(true);
                   }
                    notifyDataSetChanged();
                }
            });
        }


    @Override
    public int getItemCount() {
        return parkingBlocks.size();
    }

    public class ParkingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView= itemView.findViewById(R.id.imageView);
        }
    }
}
