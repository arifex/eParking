package com.example.e_parking.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_parking.R;
import com.example.e_parking.model.ParkingPlace;
import com.example.e_parking.model.Reservation;
import com.example.e_parking.other.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RazorpayActivity extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = RazorpayActivity.class.getSimpleName();
    private Reservation reservation;
    private ParkingPlace parking;
    private DatabaseReference myRef;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razorpay);
        Checkout.preload(getApplicationContext());

        // Payment button created by you in XML layout
        button = (Button) findViewById(R.id.btn_pay);
        reservation=getIntent().getParcelableExtra(Const.RESERVATION);
        parking=getIntent().getParcelableExtra(Const.PARKING);
        TextView tvTitle=findViewById(R.id.tvTitle);
        TextView tvAmount=findViewById(R.id.tvAmount);
        tvTitle.setText(parking.getTitle());
        tvAmount.setText("INR "+reservation.getTotalCharge());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });
    }

    private void startPayment() {
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", parking.getTitle());
            options.put("description", "Demoing Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", reservation.getTotalCharge());

            JSONObject preFill = new JSONObject();
            preFill.put("email", "test@razorpay.com");
            preFill.put("contact", "9876543210");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }
    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            reservation.setPaymentId(razorpayPaymentID);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getReference(Const.PARKING)
                    .child(FirebaseAuth.getInstance().getUid());
            parking.setTotalReserved(parking.getTotalReserved()+Integer.parseInt(reservation.getTotalBlock()));
            myRef.child(reservation.getParkingPlaceKey()).setValue(parking)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                saveReservationInfo();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    private void saveReservationInfo() {
        FirebaseDatabase.getInstance()
                .getReference(Const.RESERVATION)
                .push().setValue(reservation).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RazorpayActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            button.setVisibility(View.GONE);
                        }     else{
                            Toast.makeText(RazorpayActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }
}
