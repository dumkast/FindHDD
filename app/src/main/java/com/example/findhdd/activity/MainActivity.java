package com.example.findhdd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.findhdd.PreferencesManager;
import com.example.findhdd.R;
import com.example.findhdd.api.ApiClient;
import com.example.findhdd.api.UserApi;
import com.example.findhdd.fragment.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import lombok.Getter;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private FrameLayout fragmentContainer;
    @Getter
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fragmentContainer = findViewById(R.id.fragment_container);
        PreferencesManager prefs = new PreferencesManager(this);
        String username = prefs.getUsername();
        String password = prefs.getPassword();

        if (username == null || password == null) {
            Toast.makeText(this, "Требуется авторизация", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        userApi = ApiClient.getClient(username, password).create(UserApi.class);

        setupNavigation();
    }

    private void setupNavigation() {
        bottomNavigation.getMenu().clear();
        bottomNavigation.inflateMenu(R.menu.bottom_nav_menu); // Меню без админа

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            loadFragment(new CatalogFragment());
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_catalog) {
                selectedFragment = new CatalogFragment();
            } else if (itemId == R.id.nav_favorites) {
                selectedFragment = new FavoritesFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else {
                selectedFragment = new CatalogFragment(); // fallback
            }

            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment == null) return false;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return true;
    }

    public void recreateApiClient(String username, String password) {
        this.userApi = ApiClient.getClient(username, password).create(UserApi.class);
        // При необходимости обновить UI или данные
        setupNavigation();
    }
}
