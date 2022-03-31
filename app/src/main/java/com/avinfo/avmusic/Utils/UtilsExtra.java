package com.avinfo.avmusic.Utils;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.avinfo.avmusic.Activities.ActivitySplash;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.SongData.Song;
import com.avinfo.avmusic.fragments.SongDetailsFragment;
import com.avinfo.avmusic.playerMain.AppMain;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

public class UtilsExtra {


    /*get Themed icons, used in Navigation Drawer in MAinScreen*/
    public static Drawable getThemedIcon(Context c, Drawable drawable) {
        //Need to find the method to get day night values when automatic and System option is selected
        String theme = AppMain.settings.get("modes", "Day");
        if (theme.equals("Day"))
            drawable.mutate().setColorFilter(ContextCompat.getColor(c, R.color.md_grey_800), PorterDuff.Mode.MULTIPLY);
        else
            drawable.mutate().setColorFilter(ContextCompat.getColor(c, R.color.white), PorterDuff.Mode.MULTIPLY);
        return drawable;
    }

    /*Custom Tabs powered by chrome xD*/
    public static void openCustomTabs(Context context, String url) {
        CustomTabsIntent.Builder builderq = new CustomTabsIntent.Builder();
        builderq.setToolbarColor(context.getResources().getColor(R.color.primaryColor));
        builderq.addDefaultShareMenuItem().enableUrlBarHiding();
        CustomTabsIntent customTabsIntent = builderq.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    /*Get color from attr, but not working*/
    public static int getThemeAttrColor(Context context, int NumberfromThemeStylable) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(R.styleable.Theme);
        return ta.getColor(NumberfromThemeStylable, context.getResources().getColor(R.color.colorAccent));
    }

    public static void sendrate(Context context){
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }

    }

    public static void ShareApp(Context context) {

        Intent s = new Intent(android.content.Intent.ACTION_SEND);
        s.setType("text/plain");
        s.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.gallerylabs.mygallery");
        context.startActivity(Intent.createChooser(s, "Share App"));

    }

    public static void moreapps(Context context){
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:App Mine")));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=App Mine")));
        }
    }

    public static void sendFeedback(Context context) {
        String body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                String.valueOf(android.os.Build.VERSION.SDK_INT) + "\n App Version: " + AppMain.versionName + "\n Device Brand: " + Build.BRAND +
                "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;

        String Detail = "\n\n-----------------------------\nPlease don't remove this information\n"+"Device Brand :- "+ Build.BRAND + "\nDevice Os Version :- "+ Build.VERSION.RELEASE + "\nDevice Model :- "+Build.MODEL
                +"\nDevice Manufacturer :- "+Build.MANUFACTURER + "\nDevice Manufacturer Date :- "+Build.VERSION.SECURITY_PATCH + "\nDevice :- "+Build.DEVICE
                +"\nHost :- "+Build.HOST + "\nID :- "+Build.ID + "\nHardware :- "+Build.HARDWARE + "\nProduct :- "+Build.PRODUCT + "\nUser :- "+Build.USER
                +"\nOS :- "+Build.TAGS + "\nDevice resolution :- "+ ActivitySplash.Resolution;


        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: appzone008@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Query / Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Detail);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(emailIntent, "Send feedback"));
    }

    public static HashMap<String, Integer> sortMapByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static Bitmap getBitmapfromAlbumId(Context context,Song localItem){
        Bitmap bitmap;
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri,Long.parseLong(localItem.getAlbumid()));
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        }
        return bitmap;
    }
    public static Uri getUrifromAlbumID(Song song){
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri,Long.parseLong(song.getAlbumid()));
    }

    public static int getThemeColor(Context context, int colorPrimary, int dkgray) {
        int themeColor = 0;
        String packageName = context.getPackageName();
        try {
            Context packageContext = context.createPackageContext(packageName, 0);
            ApplicationInfo applicationInfo =
                    context.getPackageManager().getApplicationInfo(packageName, 0);
            packageContext.setTheme(applicationInfo.theme);
            Resources.Theme theme = packageContext.getTheme();
            TypedArray ta = theme.obtainStyledAttributes(new int[]{colorPrimary});
            themeColor = ta.getColor(0, dkgray);
            ta.recycle();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return themeColor;
    }

    public static void shareSong(Context context, Song song) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("content:///" + song.getFilePath()));
        context.startActivity(Intent.createChooser(share, "Share Sound File"));
        context.getContentResolver().notifyChange(Uri.parse("content://media"), null);
    }

    public static void showSongDetails(Context context, Long id) {
        AppCompatActivity activity = (AppCompatActivity) context;
        activity.getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, SongDetailsFragment.newInstance(id)).addToBackStack(null).commit();
    }

    public static int getSwatchColor(Bitmap bitmap) {

        Palette p = Palette.from(bitmap).generate();
        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
        if (vibrantSwatch != null) {
            return vibrantSwatch.getBodyTextColor();
        }
        return Color.WHITE;
    }


}
