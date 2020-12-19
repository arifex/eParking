package com.example.e_parking.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_parking.OwnerDrawerActivity;
import com.example.e_parking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String FILE_NAME = "CrimeTracker";
    public static final String LOGIN = "login";
    private static final String TAG = LoginActivity.class.getName();
    private FirebaseAuth mAuth;
    EditText etUserName, etPassWord;
    Button btnLogin, btnSignup;
    CheckBox checkbox;
    TextView textpassword;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        mAuth = FirebaseAuth.getInstance();
        context=this;
        etUserName=findViewById(R.id.etUserName);
        etPassWord=findViewById(R.id.etPassWord);
        btnLogin=findViewById(R.id.btnLogin);
        btnSignup=findViewById(R.id.btnSignup);
        checkbox=findViewById(R.id.checkbox);
        textpassword=findViewById(R.id.textpassword);
        mAuth=FirebaseAuth.getInstance();


        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPassWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Username = etUserName.getText().toString();
                if (Username.equals("")) {
                    etUserName.setError("Required");
                    return;
                }
                String Password = etPassWord.getText().toString();
                if (Password.equals("")) {
                    etPassWord.setError("Required");
                    return;
                }

                login();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,InitialActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String email=etUserName.getText().toString();
        String password=etPassWord.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                           updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed. New User Click on create account",
                                    Toast.LENGTH_SHORT).show();
                          updateUI(null);
                        }

                        // ...
                    }
                });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            if(user.isEmailVerified()){
                //Intent intent = new Intent(context,ParkingListActivity.class);
                Intent intent=new Intent(context, OwnerDrawerActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

}
