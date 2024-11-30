package com.example.assignment4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Login_Activity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton, forgotPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);

        // Login Button Click Listener
        loginButton.setOnClickListener(v -> loginUser());

        // Register Button Click Listener
        registerButton.setOnClickListener(v -> startActivity(new Intent(Login_Activity.this, RegisterActivity.class)));

        // Forgot Password Button Click Listener
        forgotPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successfully logged in
                        startActivity(new Intent(Login_Activity.this, ShoppingListActivity.class));
                        finish();
                    } else {
                        // Failed to log in
                        Toast.makeText(Login_Activity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login_Activity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login_Activity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}