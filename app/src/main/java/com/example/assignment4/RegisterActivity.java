package com.example.assignment4;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, nameEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        registerButton = findViewById(R.id.registerButton);

        // Set click listener for register button
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();

        // Check if the fields are empty
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register the user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User created successfully, now save the name to Firestore
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(name);

                        // Save user data to Firestore
                        db.collection("users").document(userId).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Display success message
                                    Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                    finish();  // Finish the activity and go back to login or next screen
                                })
                                .addOnFailureListener(e -> {
                                    // If saving user data fails, show an error message
                                    Toast.makeText(RegisterActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // If registration fails, show an error message
                        Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
