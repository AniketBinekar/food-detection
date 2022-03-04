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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class SignupActivity extends AppCompatActivity {

    Button btn;
    EditText email_edtxt, password_edtxt;
    TextView txt;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email_edtxt = findViewById(R.id.emailtxt);
        password_edtxt = findViewById(R.id.passwordtxt);
        btn = findViewById(R.id.registerbtn);
//        c1=(CardView)findViewById(R.id.firstbuttons);
//        e2=(EditText)findViewById(R.id.editTextmail);
//        e3=(EditText)findViewById(R.id.editTextPassword);
        txt = (TextView) findViewById(R.id.login25);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = email_edtxt.getText().toString().trim();
                String pass = password_edtxt.getText().toString().trim();
                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "enter all information", Toast.LENGTH_SHORT).show();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email_edtxt.getText().toString()).matches()) {
                    email_edtxt.setError("enter valid email");
                }
//                else if(!e1.getText().toString().matches("[a-zA-Z]"))
//                {
//                    e1.setError("enter valid name");
//                }
//                else if (pass.length() <= 6) {
//                    password_edtxt.setError("enter at least 6 character");
//                    if (password_edtxt.getText().toString().matches("[a-z,A-Z,0-9]"))
//                        password_edtxt.setError("enter valid password");
//                }
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "user created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });


    }
}