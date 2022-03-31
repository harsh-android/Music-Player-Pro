package com.avinfo.avmusic.playerMain;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.avinfo.avmusic.R;
import com.avinfo.avmusic.SongData.Data;
import com.avinfo.avmusic.SongData.Song;
import com.avinfo.avmusic.services.ServicePlayMusic;
import com.avinfo.avmusic.settings.Settings;

import java.util.ArrayList;
// AppMain Logic u can say. Every thing works around this class, again KMP <3

public class AppMain {

    public static Data data = new Data();

    public static ProgressDialog mProgressDialog;

    public static Settings settings = new Settings();

    public static ServicePlayMusic musicService = null;

    public static ArrayList<Song> musicList = new ArrayList<>();

    public static ArrayList<Song> nowPlayingList = null;

    public static boolean mainMenuHasNowPlayingItem = false;

    // GENERAL PROGRAM INFO
    public static String applicationName = String.valueOf(R.string.app_name);
    public static String packageName = "<unknown>";
    public static String versionName = "<unknown>";
    public static int versionCode = -1;
    public static long firstInstalledTime = -1;
    public static long lastUpdatedTime = -1;
    public static ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServicePlayMusic.MusicBinder binder = (ServicePlayMusic.MusicBinder) service;

            // Here's where we finally create the MusicService
            musicService = binder.getService();
            musicService.setList(AppMain.data.songs);
            musicService.musicBound = true;
            Log.w("service", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService.musicBound = false;
            Log.w("service", "onServiceDisconnected");
        }
    };
    private static Intent musicServiceIntent = null;

    public static void initialize(Context c) {

        AppMain.packageName = c.getPackageName();

        try {
            // Retrieving several information
            PackageInfo info = c.getPackageManager().getPackageInfo(AppMain.packageName, 0);

            AppMain.versionName = info.versionName;
            AppMain.versionCode = info.versionCode;
            AppMain.firstInstalledTime = info.firstInstallTime;
            AppMain.lastUpdatedTime = info.lastUpdateTime;

        } catch (PackageManager.NameNotFoundException e) {}

        startMusicService(c);
    }

    public static void destroy() {
        data.destroy();
    }

    public static void startMusicService(Context c) {

        if (musicServiceIntent != null)
            return;

        if (AppMain.musicService != null)
            return;

        // Create an intent to bind our Music Connection to
        // the MusicService.
        musicServiceIntent = new Intent(c, ServicePlayMusic.class);
        c.bindService(musicServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        c.startService(musicServiceIntent);
        Log.w("service", "startMusicService");

    }

    public static void stopMusicService(Context c) {

        if (musicServiceIntent == null)
            return;

        Log.w("service", "stoppedService");
        c.stopService(musicServiceIntent);
        c.unbindService(musicConnection);
        musicServiceIntent = null;
        AppMain.musicService = null;
    }

    public static void forceExit(Activity c) {

        c.finish();
        c.finishAffinity();

    }

    public static void showProgressDialog(Context c) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(c, R.style.progressTheme);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mProgressDialog.show();
    }

    public static void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}