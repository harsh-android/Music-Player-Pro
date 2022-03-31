package com.avinfo.avmusic.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import com.avinfo.avmusic.R;
import com.avinfo.avmusic.playerMain.AppMain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

@SuppressLint("Registered")
public class ActivityBase extends AppCompatActivity {

    protected String currentMode = "";
    protected String currentTheme = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking if changes were made, look these methods for better understanding
        refreshTheme();
        refreshMode();

        //just using it for limited time, will use File provider soon
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (refreshMode()) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        if (refreshTheme()) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /* For refreshing the Day/Night Mode */
    public boolean refreshMode() {

        String mode = AppMain.settings.get("modes", "Day");

        if (!currentMode.equals(mode)) {
            switch (mode) {
                case "Day":
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "Night":
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
            }
            currentMode = mode;
            return true;
        }
        return false;
    }

    /* Multi Color Theme for the Application, look credits for this many themes */
    public boolean refreshTheme() {

        String theme = AppMain.settings.get("themes", "Red");

        if (!currentTheme.equals(theme)) {
            switch (theme) {
                case "Red":
                    setTheme(R.style.AppTheme_RED);
                    break;
                case "Pink":
                    setTheme(R.style.AppTheme_PINK);
                    break;
                case "Purple":
                    setTheme(R.style.AppTheme_PURPLE);
                    break;
                case "DeepPurple":
                    setTheme(R.style.AppTheme_DEEPPURPLE);
                    break;
                case "Indigo":
                    setTheme(R.style.AppTheme_INDIGO);
                    break;
                case "Blue":
                    setTheme(R.style.AppTheme_BLUE);
                    break;
                case "LightBlue":
                    setTheme(R.style.AppTheme_LIGHTBLUE);
                    break;
                case "Cyan":
                    setTheme(R.style.AppTheme_CYAN);
                    break;
                case "Teal":
                    setTheme(R.style.AppTheme_TEAL);
                    break;
                case "Green":
                    setTheme(R.style.AppTheme_GREEN);
                    break;
                case "LightGreen":
                    setTheme(R.style.AppTheme_LIGHTGREEN);
                    break;
                case "Lime":
                    setTheme(R.style.AppTheme_LIME);
                    break;
                case "Yellow":
                    setTheme(R.style.AppTheme_YELLOW);
                    break;
                case "Amber":
                    setTheme(R.style.AppTheme_YELLOW);
                    break;
                case "Orange":
                    setTheme(R.style.AppTheme_ORANGE);
                    break;
                case "DeepOrange":
                    setTheme(R.style.AppTheme_DEEPORANGE);
                    break;
                case "Brown":
                    setTheme(R.style.AppTheme_BROWN);
                    break;
                case "Gray":
                    setTheme(R.style.AppTheme_GRAY);
                    break;
                case "BlueGray":
                    setTheme(R.style.AppTheme_BLUEGRAY);
                    break;
            }
            currentTheme = theme;
            return true;
        }
        return false;
    }
}