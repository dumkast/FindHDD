package com.example.findhdd;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_ADMIN = "isAdmin";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveCredentials(String username, String password, boolean isAdmin) {
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_IS_ADMIN, isAdmin)
                .apply();
    }
    public void saveCredentials(String username, String password) {
        saveCredentials(username, password, false);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getPassword() {
        return prefs.getString(KEY_PASSWORD, null);
    }

    public boolean isAdmin() {
        return prefs.getBoolean(KEY_IS_ADMIN, false);
    }

    public void clearCredentials() {
        prefs.edit()
                .remove(KEY_USERNAME)
                .remove(KEY_PASSWORD)
                .apply();
    }
}