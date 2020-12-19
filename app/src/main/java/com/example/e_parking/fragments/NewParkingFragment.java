package com.example.e_parking.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.e_parking.R;
import com.example.e_parking.model.ParkingPlace;
import com.example.e_parking.other.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewParkingFragment extends Fragment implements View.OnClickListener {

    EditText etTitle,etAddress,etHourlyCharge, etLocation, etOwner, etNumberOfBlocks, etPhoneNumber;
    Button btnSave;
    ImageButton btnPickLocation;
    Context context;
    ProgressDialog pd;
    private String key;
    private DatabaseReference myRef;

    public NewParkingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_parking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context=getContext();

        btnPickLocation=view.findViewById(R.id.btnPickLocation);

        etTitle=view.findViewById(R.id.etTitle);
        etAddress=view.findViewById(R.id.etAddress);
        etLocation=view.findViewById(R.id.etLocation);
        etOwner=view.findViewById(R.id.etOwner);
        etNumberOfBlocks=view.findViewById(R.id.etNumberOfBlocks);
        etPhoneNumber=view.findViewById(R.id.etPhoneNumber);
        etLocation.setEnabled(false);
        etHourlyCharge=view.findViewById(R.id.etHourlyCharge);
        btnSave=view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        if(getArguments()!=null){
            Bundle bundle=getArguments();
            key=bundle.getString(Const.KEY);
            ParkingPlace parkingPlace=bundle.getParcelable(Const.PARKING);
            etTitle.setText(parkingPlace.getTitle());
            etAddress.setText(parkingPlace.getAddress());
            etLocation.setText(parkingPlace.getLocation());
            etOwner.setText(parkingPlace.getOwner());
            etNumberOfBlocks.setText(parkingPlace.getNumberOfBlocks());
            etPhoneNumber.setText(parkingPlace.getPhoneNumber());
            etHourlyCharge.setText(parkingPlace.getHourlyCharge());
        }

        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new MapLocationFragment();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame,fragment)
                        .commit();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        //editor.putString("address",etAddress.getText().toString());
        editor.putString("title",etTitle.getText().toString());
        editor.putString("owner",etOwner.getText().toString());
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String title=preferences.getString("title","");
        String address=preferences.getString("diary_txt_shootloc","");
        String lat=preferences.getString("diary_txt_lat","");
        String lang=preferences.getString("diary_txt_lon","");
        String owner=preferences.getString("owner","");
        etLocation.setText(lat+","+lang);
        etAddress.setText(address);
        etTitle.setText(title);
        etOwner.setText(owner);
    }

    @Override
    public void onClick(View v) {
        ParkingPlace parkingPlace=new ParkingPlace();
        parkingPlace.setTitle(etTitle.getText().toString());
        parkingPlace.setAddress(etAddress.getText().toString());
        parkingPlace.setLocation(etLocation.getText().toString());
        parkingPlace.setOwner(etOwner.getText().toString());
        parkingPlace.setNumberOfBlocks(etNumberOfBlocks.getText().toString());
        parkingPlace.setPhoneNumber(etPhoneNumber.getText().toString());
        parkingPlace.setHourlyCharge(etHourlyCharge.getText().toString());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Const.PARKING)
                .child(FirebaseAuth.getInstance().getUid());
        if(key==null)
            myRef=myRef.push();
        else
            myRef=myRef.child(key);
        pd=ProgressDialog.show(context,"","");
        myRef.setValue(parkingPlace)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        if(task.isSuccessful()){
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.remove("title");
                            editor.remove("diary_txt_shootloc");
                            editor.remove("diary_txt_lat");
                            editor.remove("diary_txt_lon");
                            editor.remove("owner");
                            editor.commit();
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
