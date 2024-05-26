package com.example.smart_fridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeGen extends BaseActivity {

    private EditText ingredientEditText;
    private Button searchButton;

    private CheckBox lowcarbCheckBox;
    private CheckBox lowSodiumCheckBox;
    private CheckBox highFiberCheckBox;
    private CheckBox veganCheckBox;
    private CheckBox glutenCheckBox;
    private CheckBox dairyCheckBox;
    private CheckBox peanutsCheckBox;
    private CheckBox soyCheckBox;
    private CheckBox kidneyFriendlyCheckBox;
    private CheckBox eggCheckBox;
    private CheckBox balancedCheckBox;
    private CheckBox lowFatCheckBox;
    private CheckBox highProteinCheckBox;

    private EditText toEditText;
    private EditText fromEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_gen);
        setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view);

        // Initialize views
        ingredientEditText = findViewById(R.id.editTextText5);
        searchButton = findViewById(R.id.button2);

        balancedCheckBox = findViewById(R.id.Balanced);
        lowcarbCheckBox = findViewById(R.id.LowCarb);
        highProteinCheckBox = findViewById(R.id.HighProtein);
        lowFatCheckBox = findViewById(R.id.LowFat);
        lowSodiumCheckBox = findViewById(R.id.LowSodium);
        highFiberCheckBox = findViewById(R.id.HighFiber);

        glutenCheckBox = findViewById(R.id.Gluten);
        dairyCheckBox = findViewById(R.id.DairyFree);
        eggCheckBox = findViewById(R.id.EggFree);
        soyCheckBox = findViewById(R.id.SoyFree);
        veganCheckBox = findViewById(R.id.vegan);
        peanutsCheckBox = findViewById(R.id.PeanutFree);
        kidneyFriendlyCheckBox = findViewById(R.id.kidneyFriendly);

        toEditText = findViewById(R.id.To_editText);
        fromEditText = findViewById(R.id.From_editText);

        // Set click listener for search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredients = ingredientEditText.getText().toString();
                String toCal = toEditText.getText().toString();
                String fromCal = fromEditText.getText().toString();
                String diets = getSelectedDiets();
                String health = getSelectedHealth();
                if (ingredients.isEmpty()) {
                    Toast.makeText(RecipeGen.this, "Please enter ingredients", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendDataToBackend(ingredients, diets, health, fromCal, toCal);
            }
        });
    }

    private String getSelectedHealth() {
        StringBuilder health = new StringBuilder();
        if (glutenCheckBox.isChecked()) health.append("gluten-free,");
        if (dairyCheckBox.isChecked()) health.append("dairy-free,");
        if (eggCheckBox.isChecked()) health.append("egg-free,");
        if (soyCheckBox.isChecked()) health.append("soy-free,");
        if (veganCheckBox.isChecked()) health.append("vegan,");
        if (peanutsCheckBox.isChecked()) health.append("peanut-free,");
        if (kidneyFriendlyCheckBox.isChecked()) health.append("kidney-friendly,");
        if (health.length() > 0) health.setLength(health.length() - 1);
        return health.toString();
    }

    private String getSelectedDiets() {
        StringBuilder diets = new StringBuilder();
        if (balancedCheckBox.isChecked()) diets.append("balanced,");
        if (lowcarbCheckBox.isChecked()) diets.append("low-carb,");
        if (highProteinCheckBox.isChecked()) diets.append("high-protein,");
        if (lowFatCheckBox.isChecked()) diets.append("low-fat,");
        if (lowSodiumCheckBox.isChecked()) diets.append("low-sodium,");
        if (highFiberCheckBox.isChecked()) diets.append("high-fiber,");
        if (diets.length() > 0) diets.setLength(diets.length() - 1);
        return diets.toString();
    }

    private void sendDataToBackend(String ingredients, String diets, String health, String fromCal, String toCal) {
        String url = "http://10.1.11.202:5000/search-recipes"; // Change to your Flask server IP
        JSONObject requestData = new JSONObject();
        try {
            JSONArray ingredientsArray = new JSONArray();
            for (String ingredient : ingredients.split(",")) {
                ingredientsArray.put(ingredient.trim());
            }

            JSONArray dietsArray = new JSONArray();
            for (String diet : diets.split(",")) {
                dietsArray.put(diet.trim());
            }
            requestData.put("diets", dietsArray);

            JSONArray healthArray = new JSONArray();
            for (String allergy : health.split(",")) {
                healthArray.put(allergy.trim());
            }
            requestData.put("ingredients", ingredientsArray);
            requestData.put("health", healthArray);
            requestData.put("from_cal", fromCal);
            requestData.put("to_cal", toCal);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        StringBuilder searchResults = new StringBuilder();
                        searchResults.append("Search Results:\n");

                        try {
                            JSONArray recipes = response.getJSONArray("recipes");

                            for (int i = 0; i < recipes.length(); i++) {
                                JSONObject recipe = recipes.getJSONObject(i);
                                searchResults.append("Recipe: ").append(recipe.getString("label")).append("\n");
                                searchResults.append("Ingredients:\n");

                                JSONArray ingredients = recipe.getJSONArray("ingredients");
                                for (int j = 0; j < ingredients.length(); j++) {
                                    searchResults.append(" - ").append(ingredients.getString(j)).append("\n");
                                }
                                searchResults.append("\n");
                            }

                            Intent intent = new Intent(RecipeGen.this, Affichage_Recipe.class);
                            intent.putExtra("recipes", searchResults.toString()); // Pass search results as a string
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RecipeGen.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the request queue
        Volley.newRequestQueue(this).add(request);
    }
}
