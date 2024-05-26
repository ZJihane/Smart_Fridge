package com.example.smart_fridge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class Home extends BaseActivity {

    private TextView humidityText, temperatureText, freshnessText;
    private RecyclerView recyclerViewStock;
    private EditText etToken;
    private Button buttonRecipes, buttonShoppingList;
    private static final String TAG = "ChartActivity";
    private DatabaseReference mDatabase;
    private BarChart barChart;
    private ArrayList<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view);

        humidityText = findViewById(R.id.humidityText);
        temperatureText = findViewById(R.id.temperatureText);
        freshnessText = findViewById(R.id.freshnessText);
        recyclerViewStock = findViewById(R.id.recyclerViewStock);
        etToken = findViewById(R.id.etToken);
        buttonRecipes = findViewById(R.id.buttonRecipes);
        buttonShoppingList = findViewById(R.id.buttonShoppingList);

        recyclerViewStock.setLayoutManager(new LinearLayoutManager(this));

        buttonRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, RecipeGen.class);
            startActivity(intent);
        });

        buttonShoppingList.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, SplashActivity.class);
            startActivity(intent);
        });

        // Fetch FCM registration token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(Home.this, "Fetching FCM registration token failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    if (token != null) {
                        etToken.setText(token);
                    }
                });

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Initialize the Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize the BarChart
        barChart = findViewById(R.id.barChart);

        // Create labels for the items
        labels = new ArrayList<>();
        labels.add("Orange");
        labels.add("Tomato");
        labels.add("Cucumber");
        labels.add("Banana");
        labels.add("Potato");
        labels.add("Blueberry");

        // Fetch sensor data from Firebase Realtime Database
        fetchSensorData();
    }
    private void fetchSensorData() {
        mDatabase.child("sensorData").child("Weight").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<BarEntry> barEntries = new ArrayList<>();

                // Add static data for other bars
                barEntries.add(new BarEntry(0, 201)); // Orange
                barEntries.add(new BarEntry(1, 0f)); // Placeholder for Tomato
                barEntries.add(new BarEntry(2, 360.27f)); // Cucumber
                barEntries.add(new BarEntry(3, 812.14f)); // Banana
                barEntries.add(new BarEntry(4, 403)); // Potato
                barEntries.add(new BarEntry(5, 607.23f)); // Blueberry

                // Retrieve the weight value from the database
                Float weight = dataSnapshot.child("weight").getValue(Float.class);
                if (weight != null) {
                    Log.d(TAG, "Tomato weight: " + weight);
                    // Update the value for Tomato
                    barEntries.set(1, new BarEntry(1, weight));
                } else {
                    Log.d(TAG, "Tomato weight is null");
                }

                updateChart(barEntries);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void updateChart(List<BarEntry> barEntries) {
        // Create a dataset to hold the data and customize its appearance
        BarDataSet barDataSet = new BarDataSet(barEntries, "Poids des Aliments");
        barDataSet.setColors(new int[]{
                Color.rgb(255, 140, 0), // Orange
                Color.rgb(255, 99, 71), // Tomato
                Color.rgb(60, 179, 113), // Cucumber
                Color.rgb(255, 255, 0), // Banana
                Color.rgb(171, 156, 115), // Potato
                Color.rgb(138, 43, 226) // Blueberry
        });
        barDataSet.setValueTextColor(Color.BLACK);  // Set text color
        barDataSet.setValueTextSize(12f);  // Set text size

        // Create a BarData object to hold the dataset
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f);  // Adjust bar width

        // Set the data to the bar chart
        barChart.setData(barData);

        // Customize chart appearance
        barChart.getDescription().setEnabled(false);  // Disable description
        barChart.setDrawGridBackground(false);  // Disable grid background

        // Customize X-axis appearance
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // Set position to bottom
        xAxis.setDrawGridLines(false);  // Disable grid lines
        xAxis.setTextSize(10f);  // Set text size
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));  // Set custom labels
        xAxis.setGranularity(1f); // Only intervals of 1
        xAxis.setLabelCount(labels.size());

        // Customize Y-axis appearance
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);  // Enable grid lines
        leftAxis.setDrawAxisLine(true);  // Enable the Y-axis line
        leftAxis.setDrawLabels(true);  // Enable Y-axis labels
        leftAxis.setTextSize(12f);  // Set text size
        leftAxis.setGranularityEnabled(true); // Enable granularity
        leftAxis.setGranularity(5f); // Set granularity to 5
        leftAxis.setAxisMinimum(0f); // Start Y-axis at 0

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable right Y-axis

        // Customize legend to hide the text but show the form
        Legend legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE); // Set the form to circle or any other shape
        legend.setTextSize(0f);  // Set text size to 0 to hide the text
        legend.setTextColor(0xFFFFFFFF);  // Set text color to white

        // Refresh the bar chart to update its display
        barChart.invalidate();
    }
}
