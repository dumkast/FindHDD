package com.example.findhdd.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import com.example.findhdd.PreferencesManager;
import com.example.findhdd.R;
import com.example.findhdd.fragment.LoginFragment;
import com.example.findhdd.fragment.RegisterFragment;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        PreferencesManager prefs = new PreferencesManager(this);
        String username = prefs.getUsername();
        String password = prefs.getPassword();

        if (username != null && password != null) {
            // Есть сохранённые данные — сразу перейти в MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_auth);

        // Загружаем LoginFragment
        loadLoginFragment();
    }

    public void loadLoginFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LoginFragment());
        transaction.commit();
    }

    public void loadRegisterFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new RegisterFragment());
        transaction.addToBackStack(null); // Для возможности возврата по кнопке "Назад"
        transaction.commit();
    }
}