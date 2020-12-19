package com.example.e_parking.fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.e_parking.R;
import com.example.e_parking.activities.RazorpayActivity;
import com.example.e_parking.model.ParkingPlace;
import com.example.e_parking.model.Reservation;
import com.example.e_parking.other.Const;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationFormFragment extends Fragment implements View.OnClickListener {

    EditText etParkingPlace, etFrom, etTo, etTotalParkingBlock,etTotalCharge;
    Button btnSave;
    private ParkingPlace parkingPlace;
    private int count;
    private String key;

    public ReservationFormFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etParkingPlace=view.findViewById(R.id.etParkingPlace);
        etFrom=view.findViewById(R.id.etFrom);
        etTo=view.findViewById(R.id.etTo);
        etTotalParkingBlock=view.findViewById(R.id.etTotalParkingBlock);
        etTotalCharge=view.findViewById(R.id.etTotalCharge);
        btnSave=view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        etFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(etFrom);
            }
        });
        etTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(etTo);
            }
        });
        if(getArguments()!=null){
            Bundle bundle=getArguments();
            key=bundle.getString(Const.KEY);
            parkingPlace=bundle.getParcelable(Const.PARKING);
            count=bundle.getInt(Const.COUNT);
            etParkingPlace.setText(parkingPlace.getTitle());
            etTotalParkingBlock.setText(count+"");
            int totalCharge=Integer.parseInt(parkingPlace.getHourlyCharge())*count;
            etTotalCharge.setText(totalCharge+"");

        }
    }

    private void showDatePickerDialog(EditText etFrom) {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etFrom.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                showTimePickerDialog(etFrom);
            }
        },year,month,day).show();
    }

    private void showTimePickerDialog(EditText etFrom) {
        Calendar calendar=Calendar.getInstance();
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                etFrom.setText(etFrom.getText()+" "+hourOfDay+":"+minute+":00");
                long totalCharge=Integer.parseInt(parkingPlace.getHourlyCharge())*count;
                long diffHours=getTotalHours();
                if(diffHours!=0)
                    totalCharge=totalCharge*diffHours;
                etTotalCharge.setText(totalCharge+"");
            }
        },hour,minute,true).show();
    }

    @Override
    public void onClick(View v) {
        String from=etFrom.getText().toString();
        String to=etTo.getText().toString();
        if(from.equals("")){
            etFrom.setError("Required");
            return;
        }
        if(to.equals("")){
            etTo.setError("Required");
            return;
        }
        Reservation reservation=new Reservation();
        reservation.setParkingPlaceKey(key);
        reservation.setFromTime(etFrom.getText().toString());
        reservation.setToTime(etTo.getText().toString());
        reservation.setTotalBlock(etTotalParkingBlock.getText().toString());
        reservation.setTotalCharge(etTotalCharge.getText().toString());
        Intent intent=new Intent(getContext(), RazorpayActivity.class);
        intent.putExtra(Const.RESERVATION,reservation);
        intent.putExtra(Const.PARKING,parkingPlace);
        startActivity(intent);
    }

    public long getTotalHours(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(etFrom.getText().toString());
            d2 = format.parse(etTo.getText().toString());

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");
            return diffHours;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }
}
