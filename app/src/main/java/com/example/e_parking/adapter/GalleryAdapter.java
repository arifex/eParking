package com.example.e_parking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.e_parking.R;
import com.example.e_parking.model.ParkingPlace;
import com.example.e_parking.other.Const;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private final ArrayList<HashMap<String, String>> galleries;
    private final Context context;
    public interface OnGalleryClickListener{
        void onGalleryRemove(String key, String name);
    }
    OnGalleryClickListener listener;
    public GalleryAdapter(Context context,ArrayList<HashMap<String, String>> galleries, OnGalleryClickListener listener) {
        this.galleries=galleries;
        this.listener=listener;
        this.context=context;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_row_item,parent,false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        HashMap<String,String> hashMap=galleries.get(position);
        String key=hashMap.keySet().iterator().next();
        String name=hashMap.get(key);
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference(Const.IMAGES+"/" + name + ".jpg");

        Glide.with(context)
                .load(storageReference)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imageView);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGalleryRemove(key,name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleries.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton btnDelete;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDelete=itemView.findViewById(R.id.btnDelete);
            imageView=itemView.findViewById(R.id.imageView);
        }
    }
}
