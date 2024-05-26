package com.example.smart_fridge;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class CalculKcalPersonne extends BaseActivity {

    private EditText editTextAge, editTextPoids , editTextTaille;

    private TextView Resultat ;
    private RadioGroup radioGroupGenre, radioGroupActivityLevel;
    private Button buttonCalculate;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcul_kcal_personne);
        setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view);

        ImageButton backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> finish());

        Resultat = findViewById(R.id.Resultat);
        editTextAge = findViewById(R.id.editTextAge);
        editTextPoids = findViewById(R.id.editTextPoids);
        editTextTaille = findViewById(R.id.editTextTaille);
        radioGroupGenre = findViewById(R.id.radioGroupGenre);
        radioGroupActivityLevel = findViewById(R.id.radioGroupActivityLevel);
        buttonCalculate = findViewById(R.id.button_calcul);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        loadDataFromFirestore(); // Charger les données existantes de l'utilisateur

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAndSaveTDEE();
            }
        });
    }

    private void loadDataFromFirestore() {
        db.collection("Users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String age = documentSnapshot.getString("Age");
                        String poids = documentSnapshot.getString("Poids");
                        String taille = documentSnapshot.getString("Taille");

                        // Charger les données existantes dans les champs EditText
                        editTextAge.setText(age);
                        editTextPoids.setText(poids);
                        editTextTaille.setText(taille);
                    }
                })
                .addOnFailureListener(e -> {
                    // Erreur lors du chargement des données
                    Toast.makeText(CalculKcalPersonne.this, "Erreur lors du chargement des données.", Toast.LENGTH_LONG).show();
                });
    }

    private void calculateAndSaveTDEE() {
        String ageStr = editTextAge.getText().toString().trim();
        String poidsStr = editTextPoids.getText().toString().trim();
        String tailleStr = editTextTaille.getText().toString().trim();
        String genre = ((RadioButton) findViewById(radioGroupGenre.getCheckedRadioButtonId())).getText().toString();
        String activityLevel = ((RadioButton) findViewById(radioGroupActivityLevel.getCheckedRadioButtonId())).getText().toString();

        // Convertir les chaînes en valeurs numériques
        int age = Integer.parseInt(ageStr);
        double poids = Double.parseDouble(poidsStr);
        double taille = Double.parseDouble(tailleStr);

        // Calcul du BMR (Basal Metabolic Rate) basé sur le genre
        double BMR;
        if (genre.equals("Male")) {
            BMR = 88.362 + (13.397 * poids) + (4.799 * taille) - (5.677 * age);
        } else {
            BMR = 447.593 + (9.247 * poids) + (3.098 * taille) - (4.330 * age);
        }

        // Appliquer le facteur d'activité
        double factor;
        switch (activityLevel) {
            case "Sedentary (little or no exercise)":
                factor = 1.2;
                break;
            case "Lightly Active (light exercise/sports 1-3 days per week)":
                factor = 1.375;
                break;
            case "Moderately Active (moderate exercise/sports 3-5 days per week)":
                factor = 1.55;
                break;
            case "Very Active (intense exercise/sports 6-7 days per week)":
                factor = 1.725;
                break;
            case "Super Active (very hard physical work/exercise twice a day)":
                factor = 1.9;
                break;
            default:
                factor = 1.0;
                break;
        }

        // Calcul du TDEE
        double TDEE = BMR * factor;
        Resultat.setText(String.format("TDEE Calculé: %.2f", TDEE));

        // Afficher un Toast avec le TDEE calculé
        Toast.makeText(CalculKcalPersonne.this, "TDEE Calculé: " + String.format("%.2f", TDEE), Toast.LENGTH_LONG).show();

        // Stocker les valeurs dans Firestore
        saveDataToFirestore(ageStr, poidsStr, tailleStr, genre, activityLevel, String.format("%.2f", TDEE));
    }

    private void saveDataToFirestore(String age, String poids, String taille, String genre, String activityLevel, String tdee) {
        // Récupérer les données existantes de l'utilisateur dans Firestore
        db.collection("Users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Récupérer les valeurs existantes de Nom, Prenom et UID
                        String nom = documentSnapshot.getString("Nom");
                        String prenom = documentSnapshot.getString("Prenom");
                        String uid = documentSnapshot.getString("UID");

                        // Création d'un objet Map pour stocker les nouvelles données sans écraser les champs existants
                        Map<String, Object> user = new HashMap<>();
                        user.put("Activity_Level", activityLevel);
                        user.put("Age", age);
                        user.put("Genre", genre);
                        user.put("Poids", poids);
                        user.put("TDEE", tdee);
                        user.put("Taille", taille);

                        // Ajouter les valeurs existantes de Nom, Prenom et UID à l'objet Map
                        user.put("Nom", nom);
                        user.put("Prenom", prenom);
                        user.put("UID", uid);

                        // Ajout ou mise à jour des données dans Firestore
                        db.collection("Users").document(currentUser.getUid()).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Opération réussie
                                    Toast.makeText(CalculKcalPersonne.this, "Données enregistrées avec succès!", Toast.LENGTH_LONG).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Erreur lors de l'ajout ou de la mise à jour des données
                                    Toast.makeText(CalculKcalPersonne.this, "Erreur lors de l'enregistrement des données.", Toast.LENGTH_LONG).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Erreur lors de la récupération des données existantes
                    Toast.makeText(CalculKcalPersonne.this, "Erreur lors de la récupération des données existantes.", Toast.LENGTH_LONG).show();
                });
    }

}
