package com.example.ok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance("https://okkk-588f2-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");

        TextView logiinback = findViewById(R.id.logiinback);
        logiinback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLogin();
            }
        });
        Button ButtonSignUp = findViewById(R.id.buttonSignUp);
        ButtonSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                registerUser();
            }

        });
    }

    private void registerUser(){
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText usernamee2 = findViewById(R.id.usernamee2);
        EditText PSN2 = findViewById(R.id.PSN2);
        EditText inputPassword3 = findViewById(R.id.inputPassword3);
        String Email = inputEmail.getText().toString();
        String fullname = usernamee2.getText().toString();
        String psn = PSN2.getText().toString();
        String Password = inputPassword3.getText().toString();
        if (Email.isEmpty() || fullname.isEmpty() || psn.isEmpty() || Password.isEmpty()) {
            Toast.makeText(this, "Wypelnij wszystkie pola", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(Email, fullname, psn);
                                    mData.child("users").child("polska").setValue(user)//.child("Email").setValue(Email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    showLoginActivity();
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "nie dziala", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void showLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void switchToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}