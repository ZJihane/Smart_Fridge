package com.example.smart_fridge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import model.Tache;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ListeCourses extends BaseActivity {
    LinkedList<Tache> taches = new LinkedList<>();
    RecyclerView myRecycler;
    private MyAdapter myAdapter;
    ImageView add;
    FirebaseFirestore db;
    ImageView cancel;
    private EditText name;
    private Button addButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_courses);
        setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        myRecycler = findViewById(R.id.myRecycler);
        add = findViewById(R.id.add);
        cancel = findViewById(R.id.cancel);
        LinearLayout layoutAddTask=findViewById(R.id.layout_add_task);

        name = findViewById(R.id.name);
        addButton = findViewById(R.id.btt_add_contact);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        taches = new LinkedList<>();
        myAdapter = new MyAdapter(taches, getApplicationContext());
        Key utils=new Key();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskToFirestore();
                layoutAddTask.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
                utils.hideKeyboard(getApplicationContext(), v);

            }
        });


        // Create and set the adapter
        myAdapter = new MyAdapter(taches, this);
        myRecycler.setAdapter(myAdapter);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAddTask.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
                utils.hideKeyboard(getApplicationContext(), v);



            }
        });
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRecycler.setLayoutManager(layoutManager);
        taches.add(new Tache("Bananes"));
        taches.add(new Tache("Oeufs"));
        taches.add(new Tache("Tomates"));
        taches.add(new Tache("Concombre"));



        // Fetch data from Firestore
        fetchDataFromFirestore();

        // Set click listener for add button
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAddTask.setVisibility(View.VISIBLE);
                add.setVisibility(View.GONE);

            }
        });
    }

    private void fetchDataFromFirestore() {
        CollectionReference coursesRef = db.collection("courses");

        coursesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String title = document.getString("title");
                        if (title != null) {
                            taches.add(new Tache(title));
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ListeCourses.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void addTaskToFirestore() {
        String title = name.getText().toString().trim();

        if (!title.isEmpty()) {
            Map<String, Object> task = new HashMap<>();
            task.put("title", title);

            db.collection("courses")
                    .add(task)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Tache newTask = new Tache(title);
                            taches.add(0, newTask);
                            myAdapter.notifyItemInserted(0);
                            name.setText("");  // Clear the EditText
                            Toast.makeText(getApplicationContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to add task", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please enter a task name", Toast.LENGTH_SHORT).show();
        }
    }
}
