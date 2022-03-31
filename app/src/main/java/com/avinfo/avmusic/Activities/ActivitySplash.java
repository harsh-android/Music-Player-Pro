package com.avinfo.avmusic.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.avinfo.avmusic.playerMain.AppMain;
import com.google.android.material.snackbar.Snackbar;
import com.avinfo.avmusic.Utils.UtilsSong;
import com.avinfo.avmusic.Handlers.PermissionHandler;
import com.avinfo.avmusic.R;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ActivitySplash extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE_Audio = 201;
    private static final int PERMISSION_REQUEST_CODE_Storage = 202;

    public static String Resolution;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        SharedPreferences recent = getSharedPreferences("com.avinfo.avmusic.RecentSongs", 0);
        SharedPreferences count = getSharedPreferences("com.avinfo.avmusic.SongsPlayedCount", 0);
        SharedPreferences last = getSharedPreferences("com.avinfo.avmusic.LastPlaylist", 0);
        SharedPreferences defaults = PreferenceManager.getDefaultSharedPreferences(this);

        boolean firstRun = defaults.getBoolean("firstRun", true);

        Resolution = getDeviceScreenResolution();

        if (firstRun) {
            defaults.edit().clear().apply();
            defaults.edit().putBoolean("firstRun", false).apply();
            recent.edit().clear().apply();
            count.edit().clear().apply();
            last.edit().clear().apply();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionHandler.isStoragePergiven(getApplicationContext())) {
                if (AppMain.mainMenuHasNowPlayingItem) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
                            startActivity(intent);
                        }
                    },3000);

                } else {
                    AppMain.initialize(this);
                    scanSongs(false);
                }
            } else {
                PermissionHandler.requestBothPermssion(this, PERMISSION_REQUEST_CODE);
            }

        } else {
            if (AppMain.mainMenuHasNowPlayingItem) {
                Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
                startActivity(intent);
            } else {
                AppMain.initialize(this);
                scanSongs(false);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (true) {

                    if (PermissionHandler.isStoragePergiven(getApplicationContext())) {
                        if (PermissionHandler.isRecordingPergiven(getApplicationContext())) {
                            if (AppMain.mainMenuHasNowPlayingItem) {
                                Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
                                startActivity(intent);
                            } else {
                                AppMain.initialize(getApplicationContext());
                                scanSongs(false);
                            }
                        } else {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                boolean showRationale = shouldShowRequestPermissionRationale(RECORD_AUDIO);
                                if (showRationale) {
                                    PermissionHandler.requestRecording(ActivitySplash.this, PERMISSION_REQUEST_CODE_Audio);
                                } else {
                                    if (AppMain.mainMenuHasNowPlayingItem) {
                                        Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
                                        startActivity(intent);
                                    } else {
                                        AppMain.initialize(getApplicationContext());
                                        scanSongs(false);
                                    }
                                }
                            }
                        }
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            boolean showRationale = shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) && shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE);
                            if (showRationale) {
                                PermissionHandler.requestStorage(this, PERMISSION_REQUEST_CODE_Storage);
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "Storage Permission is required", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Settings", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                intent.setData(uri);
                                                startActivity(intent);
                                            }
                                        })
                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                        .show();
                            }
                        }
                    }
                }
                break;
            case PERMISSION_REQUEST_CODE_Audio:
                if (!PermissionHandler.isRecordingPergiven(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Starting without Visualizers", Toast.LENGTH_SHORT).show();
                }
                if (AppMain.mainMenuHasNowPlayingItem) {
                    Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
                    startActivity(intent);
                } else {
                    AppMain.initialize(getApplicationContext());
                    scanSongs(false);
                }

                break;
            case PERMISSION_REQUEST_CODE_Storage:
                if (PermissionHandler.isStoragePergiven(getApplicationContext())) {
                    if (AppMain.mainMenuHasNowPlayingItem) {
                        Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
                        startActivity(intent);
                    } else {
                        AppMain.initialize(getApplicationContext());
                        scanSongs(false);
                    }
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        boolean showRationale = shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) && shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE);
                        if (showRationale) {
                            PermissionHandler.requestStorage(this, PERMISSION_REQUEST_CODE_Storage);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "Storage Permission is required", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Settings", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                    .show();
                        }
                    }
                }
                break;
        }
    }

    void scanSongs(boolean forceScan) {
        Activity activity = this;
        if ((forceScan) || (!AppMain.data.isInitialized())) {
            new ActivitySplash.ScanSongs(this).execute();
        } else if (Intent.ACTION_VIEW.equals(activity.getIntent().getAction())) {
            File file = new File(activity.getIntent().getData().getPath());
            Intent intent = new Intent(activity, ActivityPlayingList.class);
            intent.putExtra("file", file);
            AppMain.musicList.clear();
            AppMain.musicList.add(UtilsSong.getSongbyFile(file));
            AppMain.nowPlayingList = AppMain.musicList;
            if (AppMain.nowPlayingList == null) {
                Toast.makeText(this, "Selected Item is not a song OR is not in mediaStore", Toast.LENGTH_SHORT).show();
                intent = new Intent(activity, ActivityMain.class);
                activity.startActivity(intent);
                finish();
            }
            AppMain.musicService.setList(AppMain.nowPlayingList);
            activity.startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(activity, ActivityMain.class);
            activity.startActivity(intent);
        }
    }

    static class ScanSongs extends AsyncTask<String, Integer, String> {

        private WeakReference<ActivitySplash> activityReference;

        ScanSongs(ActivitySplash context) {
            activityReference = new WeakReference<>(context);
        }

        /**
         * The action we'll do in the background.
         */
        @Override
        protected String doInBackground(String... params) {

            // get a reference to the activity if it is still there
            ActivitySplash activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return "lol";

            try {
                AppMain.data.scanSongs(activity, "external");
                return activity.getString(R.string.menu_main_scanning_ok);
            } catch (Exception e) {
                Log.e("Couldn't execute", e.toString());
                e.printStackTrace();
                return activity.getString(R.string.menu_main_scanning_not_ok);
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ActivitySplash activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (Intent.ACTION_VIEW.equals(activity.getIntent().getAction())) {
                File file = new File(activity.getIntent().getData().getPath());
                Intent intent = new Intent(activity, ActivityPlayingList.class);
                intent.putExtra("file", file);
                AppMain.musicList.clear();
                if (UtilsSong.getSongbyFile(file) == null) {
                    Toast.makeText(activity, "Selected Song is not in mediaStore yet, Cant play for now", Toast.LENGTH_SHORT).show();
                    intent = new Intent(activity, ActivityMain.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
                AppMain.musicList.add(UtilsSong.getSongbyFile(file));
                AppMain.nowPlayingList = AppMain.musicList;
                if (AppMain.nowPlayingList == null) {
                    Toast.makeText(activity, "Selected Item is not a song OR is not in mediaStore", Toast.LENGTH_SHORT).show();
                    intent = new Intent(activity, ActivityMain.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
                AppMain.musicService.setList(AppMain.nowPlayingList);
                activity.startActivity(intent);
                activity.finish();
            } else {
                Intent intent = new Intent(activity, ActivityMain.class);
                activity.startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppMain.startMusicService(getApplicationContext());
    }


    public String getDeviceScreenResolution() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x; //device width
        int height = size.y; //device height

        return "" + width + " x " + height; //example "480 * 800"
    }


}
