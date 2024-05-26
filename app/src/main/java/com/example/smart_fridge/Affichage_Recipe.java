package com.example.smart_fridge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Affichage_Recipe extends BaseActivity {

    private TextView recipeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage_recipe);
        setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view);

        recipeTextView = findViewById(R.id.recipeTextView);

        Intent intent = getIntent();
        if (intent != null) {
            String recipesString = intent.getStringExtra("recipes");
            recipeTextView.setText(recipesString);
        }
    }
}
