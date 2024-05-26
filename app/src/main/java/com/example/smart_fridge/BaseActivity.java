// BaseActivity.java
package com.example.smart_fridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;
    protected FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    protected void setupNavigationDrawer(int drawerLayoutId, int navViewId) {
        drawerLayout = findViewById(drawerLayoutId);
        NavigationView navigationView = findViewById(navViewId);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_my_containers) {
            Intent intent = new Intent(this, ContainersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_my_recipes) {
            Intent intent = new Intent(this, RecipeGen.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, gestionProfil.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_my_home) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent logoutIntent = new Intent(this, AuthActivity.class);
            startActivity(logoutIntent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
