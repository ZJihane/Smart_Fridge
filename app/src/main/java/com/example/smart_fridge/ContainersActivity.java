// ContainersActivity.java
package com.example.smart_fridge;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

public class ContainersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_containers);

        setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view);

        // Simulated data retrieval from database
        String[] productNames = {"Milk", "Eggs", "Cheese", "Butter", "Yogurt", "Juice"};
        int[] productWeights = {1000, 500, 300, 200, 150, 1200};

        // Set data to views
        EditText nameEditText1 = findViewById(R.id.container_name_1);
        TextView weightTextView1 = findViewById(R.id.container_weight_1);
        nameEditText1.setText(productNames[0]);
        weightTextView1.setText("Weight: " + productWeights[0] + "g");

        EditText nameEditText2 = findViewById(R.id.container_name_2);
        TextView weightTextView2 = findViewById(R.id.container_weight_2);
        nameEditText2.setText(productNames[1]);
        weightTextView2.setText("Weight: " + productWeights[1] + "g");

        EditText nameEditText3 = findViewById(R.id.container_name_3);
        TextView weightTextView3 = findViewById(R.id.container_weight_3);
        nameEditText3.setText(productNames[2]);
        weightTextView3.setText("Weight: " + productWeights[2] + "g");

        EditText nameEditText4 = findViewById(R.id.container_name_4);
        TextView weightTextView4 = findViewById(R.id.container_weight_4);
        nameEditText4.setText(productNames[3]);
        weightTextView4.setText("Weight: " + productWeights[3] + "g");

        EditText nameEditText5 = findViewById(R.id.container_name_5);
        TextView weightTextView5 = findViewById(R.id.container_weight_5);
        nameEditText5.setText(productNames[4]);
        weightTextView5.setText("Weight: " + productWeights[4] + "g");

        EditText nameEditText6 = findViewById(R.id.container_name_6);
        TextView weightTextView6 = findViewById(R.id.container_weight_6);
        nameEditText6.setText(productNames[5]);
        weightTextView6.setText("Weight: " + productWeights[5] + "g");
    }
}
