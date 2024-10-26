package com.example.otpexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputConfirmPassword;
    private TextView tvLogin;
    private Button btnRegister;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword); // Initialize confirm password field

        btnRegister = findViewById(R.id.btnRegister); // Add this line

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateDataAndDoRegister();
            }
        });
    }

    private void ValidateDataAndDoRegister()
    {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();
        if (email.isEmpty())
        {
            inputEmail.setError("Enter Email Address");
            inputEmail.requestFocus();
        }
        else if (email.length()<10)
        {
            inputEmail.setError("Enter valid email");
            inputEmail.requestFocus();
        }
        if (password.isEmpty())
        {
            inputPassword.setError("Enter Password");
            inputPassword.requestFocus();
        }
        else if (inputPassword.length()<7)
        {
            inputPassword.setError("Password should be greater than 7 character");
            inputPassword.requestFocus();
        }
        else if (confirmPassword.isEmpty())
        {
            inputConfirmPassword.setError("Enter Confirm Password");
            inputConfirmPassword.requestFocus();
        }
        else if (confirmPassword.length()<7)
        {
            inputConfirmPassword.setError("Password should be greater than 7 character");
            inputConfirmPassword.requestFocus();
        }
        else if (!password.equals(confirmPassword))
        {
            inputPassword.setError("Password not matched");
            inputPassword.requestFocus();
            inputConfirmPassword.setError("Password not matched");
            inputConfirmPassword.requestFocus();
            inputPassword.setText("");
            inputConfirmPassword.setText("");
        }
        else
        {
            doRegister(email,password);
        }
    }

    private void doRegister(String email, String password)
    {
        btnRegister.setEnabled(true);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    sendVerificationEmail();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthUserCollisionException)
                {
                    btnRegister.setEnabled(true);
                    inputEmail.setError("Email Already Registered");
                    inputEmail.requestFocus();
                }
                else
                {
                    btnRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendVerificationEmail()
    {
        if (mAuth.getCurrentUser()!=null)
        {
            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        btnRegister.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, "Email has been sent to your email address", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Oops! failed to send verification email",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}