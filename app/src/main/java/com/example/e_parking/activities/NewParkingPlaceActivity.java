package com.example.e_parking.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_parking.other.Const;
import com.example.e_parking.model.ParkingPlace;
import com.example.e_parking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class NewParkingPlaceActivity extends AppCompatActivity implements View.OnClickListener {

     EditText etTitle,etAddress, etLocation, etOwner, etNumberOfBlocks, etPhoneNumber;
     Button btnSave;
    Context context;
    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        etTitle=findViewById(R.id.etTitle);
        etAddress=findViewById(R.id.etAddress);
        etLocation=findViewById(R.id.etLocation);
        etOwner=findViewById(R.id.etOwner);
        etNumberOfBlocks=findViewById(R.id.etNumberOfBlocks);
        etPhoneNumber=findViewById(R.id.etPhoneNumber);

        btnSave=findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Const.PARKING)
                .child(FirebaseAuth.getInstance().getUid()).push();
        pd=ProgressDialog.show(context,"","");
        myRef.setValue(parkingPlace)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
    }
}
