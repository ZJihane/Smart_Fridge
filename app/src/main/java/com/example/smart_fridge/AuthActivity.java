package com.example.smart_fridge;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout espace1;
    LinearLayout espace2;
    LinearLayout espace3;

    EditText nom;
    EditText prenom;
    EditText email_signup;

    EditText login;
    EditText password_login;

    EditText password_signup ;
    TextView messageAuth;
    Button button_login;
    Button button_register;
    Button button_signup;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        espace1 = findViewById(R.id.espace1);
        espace2 = findViewById(R.id.espace2);
        espace3 = findViewById(R.id.espace3);
        login = findViewById(R.id.login);
        email_signup = findViewById(R.id.email_signup);
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        password_signup = findViewById(R.id.password_signup);
        password_login = findViewById(R.id.password_login);

        button_login = findViewById(R.id.BttLogin);
        button_register = findViewById(R.id.BttRegister);
        button_signup = findViewById(R.id.BttsignUp);
        button_login.setOnClickListener(this);
        button_register.setOnClickListener(this);
        button_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.BttLogin) {
            signin(login.getText().toString(), password_login.getText().toString());
        } else if (view.getId() == R.id.BttsignUp) {
            espace2.setVisibility(View.VISIBLE);
            espace3.setVisibility(View.GONE);
            espace1.setVisibility(View.GONE);
        } else if (view.getId() == R.id.BttRegister) {
            signup(email_signup.getText().toString(),
                    password_signup.getText().toString(),
                    nom.getText().toString(),
                    prenom.getText().toString()
            );
        }
    }

    private void signup(String email, String password, String nom, String prenom) {
        if (email.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            Toast.makeText(AuthActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                User user = new User(nom, prenom, uid); // Create a User object with the provided data

                                // Create a map to store user data
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("Nom", user.getNom());
                                userMap.put("Prenom", user.getPrenom());
                                userMap.put("UID", user.getUid());
                                userMap.put("Genre", "");
                                userMap.put("Poids", "");
                                userMap.put("Taille", "");
                                userMap.put("TDEE", "");
                                userMap.put("Age", "");
                                userMap.put("Activity_Level", "");

                                // Log the user map
                                Log.d(TAG, "User map: " + userMap);

                                // Add the user to Firestore database
                                db.collection("Users").document(uid)
                                        .set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(AuthActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "User registered successfully");
                                            updateUI(firebaseUser);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(AuthActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Failed to register user", e);
                                            updateUI(null);
                                        });
                            } else {
                                Toast.makeText(AuthActivity.this, "Failed to get current user", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Failed to get current user");
                                updateUI(null);
                            }
                        } else {
                            Toast.makeText(AuthActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Authentication failed: " + task.getException().getMessage());
                            updateUI(null);
                        }
                    }
                });
    }

    private void signin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(AuthActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(AuthActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        } else {
            updateUI(null);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(AuthActivity.this, Home.class);
            startActivity(intent);
            finish();
        } else {
            espace2.setVisibility(View.GONE);
            espace3.setVisibility(View.GONE);
            espace1.setVisibility(View.VISIBLE);
        }
    }
}
