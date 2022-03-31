package com.avinfo.avmusic.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

    private SharedPreferences preferences = null;

    public void load(Context c) {
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    public void reset() {
        preferences.edit().clear().apply();
    }

    // QUERY METHODS

    public boolean get(String key, boolean defaultValue) {
        if (preferences == null)
            return defaultValue;

        return preferences.getBoolean(key, defaultValue);
    }

    public String get(String key, String defaultValue) {
        if (preferences == null)
            return defaultValue;

        return preferences.getString(key, defaultValue);
    }

    public int get(String key, int defaultValue) {
        if (preferences == null)
            return defaultValue;

        return preferences.getInt(key, defaultValue);
    }

    public void set(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public void set(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public void set(String jumpValue, int i) {
        preferences.edit().putInt(jumpValue, i).apply();
    }
}