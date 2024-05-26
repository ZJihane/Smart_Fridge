package com.example.smart_fridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class gestionProfil extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText editTextLastName, editTextFirstName, editTextPhone;
    private ProgressBar progressBar;
    private Button updateButton;

    private Button button_calcul ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_profil);
        setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view);
        EdgeToEdge.enable(this);



        editTextLastName = findViewById(R.id.nom);
        editTextFirstName = findViewById(R.id.prenom);
        progressBar = findViewById(R.id.progress_bar);
        updateButton = findViewById(R.id.BttRegister);
        button_calcul = findViewById(R.id.button_calcul);


        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Get user ID from Firebase Auth
            String uid = user.getUid();
            if (uid != null) {
                // Fetch data from Firestore
                db.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Retrieve data from document
                            String firstName = document.getString("Prenom");
                            String lastName = document.getString("Nom");

                            editTextLastName.setText(lastName);
                            editTextFirstName.setText(firstName);
                        }
                    } else {
                        // Handle the error (e.g., show a Toast message)
                        Toast.makeText(gestionProfil.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    }
                    // Hide ProgressBar
                    progressBar.setVisibility(View.GONE);
                });
            } else {
                // Hide ProgressBar if UID is null
                progressBar.setVisibility(View.GONE);
            }
        } else {
            // Hide ProgressBar if user is null
            progressBar.setVisibility(View.GONE);
        }

        button_calcul.setOnClickListener(v -> {
                    Intent intent = new Intent(gestionProfil.this, CalculKcalPersonne.class);
                    startActivity(intent);
                    finish();});

        // Set up the update button click listener
        updateButton.setOnClickListener(v -> {
            if (user != null) {
                String uid = user.getUid();
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();

                // Update user data in Firestore
                db.collection("Users").document(uid)
                        .update("Prenom", firstName, "Nom", lastName)
                        .addOnSuccessListener(aVoid -> {
                            // Profile updated successfully
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(gestionProfil.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // Error updating profile
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(gestionProfil.this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }


}
