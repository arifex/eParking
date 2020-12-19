package com.example.e_parking.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.e_parking.R;
import com.example.e_parking.adapter.ParkingBlockAdapter;
import com.example.e_parking.model.ParkingBlock;
import com.example.e_parking.model.ParkingPlace;
import com.example.e_parking.model.Reservation;
import com.example.e_parking.other.Const;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationFragment extends Fragment {

    RecyclerView recyclerView;
    Button btnConfirm;
    private ArrayList<ParkingBlock> parkingBlocks;
    private ParkingPlace parkingPlace;
    private String key;

    public ReservationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnConfirm=view.findViewById(R.id.btnConfirm);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        key=getArguments().getString(Const.KEY);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Const.PARKING)
                .child(FirebaseAuth.getInstance().getUid()).child(key);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 parkingPlace=dataSnapshot.getValue(ParkingPlace.class);
                String count=parkingPlace.getNumberOfBlocks();
                int totalReserved=parkingPlace.getTotalReserved();
                Toast.makeText(getContext(), "Total : "+count, Toast.LENGTH_SHORT).show();
                setAdapter(parkingPlace);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count=0;
                for (ParkingBlock block:parkingBlocks){
                    if(block.isSelected())
                        count++;
                }
                Fragment fragment=new ReservationFormFragment();
                Bundle bundle=new Bundle();
                bundle.putString(Const.KEY,key);
                bundle.putParcelable(Const.PARKING,parkingPlace);
                bundle.putInt(Const.COUNT,count);
                fragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame,fragment)
                        .addToBackStack(ReservationFragment.class.getName())
                        .commit();
            }
        });
    }

    private void setAdapter(ParkingPlace parkingPlace) {
        int totalBlocks= Integer.parseInt(parkingPlace.getNumberOfBlocks());
        int totalReservation= parkingPlace.getTotalReserved();
        parkingBlocks=new ArrayList<>();
        for(int i=0;i<totalBlocks;i++){
            if(i<totalReservation){
                parkingBlocks.add(new ParkingBlock(true));
            }else {
                parkingBlocks.add(new ParkingBlock());
            }
        }
        ParkingBlockAdapter adapter=new ParkingBlockAdapter(parkingPlace, parkingBlocks);
        recyclerView.setAdapter(adapter);
    }
}
