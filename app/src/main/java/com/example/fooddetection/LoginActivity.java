package com.example.fooddetection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;


public class LoginActivity extends AppCompatActivity {
    EditText email_edtxt, password_edtxt;
    Button btn;
    TextView txt;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_edtxt = findViewById(R.id.emailtxt);
        password_edtxt = findViewById(R.id.passwordtxt);
        btn = findViewById(R.id.loginbtn);
        txt = findViewById(R.id.login25);
        firebaseAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emails = email_edtxt.getText().toString().trim();
                String passs = password_edtxt.getText().toString().trim();
                if (emails.isEmpty() || passs.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "enter all information", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email_edtxt.getText().toString()).matches()) {
                    email_edtxt.setError("enter valid email");
                }
//                else if (passs.length() <= 6) {
//                    password_edtxt.setError("enter at least 6 character");
//                    if (password_edtxt.getText().toString().matches("[a-z,A-Z,0-9]"))
//                        password_edtxt.setError("enter valid password");
//                }
                firebaseAuth.signInWithEmailAndPassword(emails, passs).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "user created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });


    }
}