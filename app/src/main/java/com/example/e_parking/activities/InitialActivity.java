package com.example.e_parking.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.e_parking.R;
import com.example.e_parking.model.User;
import com.example.e_parking.other.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {


    EditText etemail,etPassword,etConfirmPassword, etMobileNumber, etUserName;
    Button btnSubmit;
    Context context;
    RadioGroup rgUserType;
    RadioButton rbOwner, rbUser;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        context=this;
        mAuth = FirebaseAuth.getInstance();
        etUserName=findViewById(R.id.etUserName);
        etMobileNumber=findViewById(R.id.etMobileNumber);
        rgUserType=findViewById(R.id.rgUserType);
        rbOwner=findViewById(R.id.rbOwner);
        rbUser=findViewById(R.id.rbUser);
        etemail=findViewById(R.id.etemail);
        etPassword=findViewById(R.id.etPassword);
        etConfirmPassword=findViewById(R.id.etConfirmPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etemail.getText().toString();
                if (email.equals("")|| !email.matches("^[\\w\\.-]+@([\\w\\-]+\\.)+[a-z]{2,4}$")) {
                    etemail.requestFocus();
                    etemail.setError("Required");
                    return;
                }
                String Password = etPassword.getText().toString();
                if (Password.equals("") ) {
                    etPassword.setError("Required");
                    return;
                }
                String ConfirmPassword = etConfirmPassword.getText().toString();
                if (ConfirmPassword.equals("")) {
                    etConfirmPassword.setError("Required");
                    return;
                }
                if (!ConfirmPassword.equals(Password)){
                    Toast.makeText(context, "Password doesnot match", Toast.LENGTH_SHORT).show();
                }
                signup();
            }
        });
    }

    private void signup() {
        pd=ProgressDialog.show(context,"Wait","Submitting On Server");
        String email=etemail.getText().toString();
        String Password=etPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        pd.dismiss();
                        if(task.isSuccessful()){
                            saveUser();
                        }else{
                            Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    private void saveUser() {

        pd=ProgressDialog.show(context,"Wait","Submitting On Server");
        User user=new User();
        user.setUserName(etUserName.getText().toString());
        user.setMobile(etMobileNumber.getText().toString());
        String userType="";
        switch (rgUserType.getCheckedRadioButtonId()){
            case R.id.rbUser:
                userType=rbUser.getText().toString();
                break;
            case R.id.rbOwner:
                userType=rbOwner.getText().toString();
                break;
        }
        user.setUserType(userType);
        FirebaseDatabase.getInstance().getReference(Const.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        if(task.isSuccessful()){
                            FirebaseUser user=mAuth.getCurrentUser();
                            updateUI(user);
                        }else{
                            Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser().isEmailVerified()){
                    Intent intent = new Intent(context,ParkingListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){

            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Email Verification sent", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}
