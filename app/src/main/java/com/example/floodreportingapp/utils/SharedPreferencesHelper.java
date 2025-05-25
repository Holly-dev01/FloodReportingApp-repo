package com.example.floodreportingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "FloodReportingAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_DEVICE_ID = "device_id";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context context) {
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = preferences.edit();
    }

    public void saveLoginState(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.commit();
    }

    public String getAuthToken() {
        return preferences.getString(KEY_AUTH_TOKEN, null);
    }

    public void saveCredentials(String username, String password) {
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    public String getDeviceId() {
        String deviceId = preferences.getString(KEY_DEVICE_ID, null);
        if (deviceId == null) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            editor.putString(KEY_DEVICE_ID, deviceId);
            editor.commit();
        }
        return deviceId;
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}