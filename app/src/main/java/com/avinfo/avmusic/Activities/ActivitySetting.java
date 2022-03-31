package com.avinfo.avmusic.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avinfo.avmusic.Utils.UtilsData;
import com.avinfo.avmusic.Utils.UtilsExtra;
import com.avinfo.avmusic.playerMain.AppMain;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.avinfo.avmusic.Adapters.ThemeAdapter;
import com.avinfo.avmusic.Utils.UtilsTheme;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.fragments.AdvancedSettings;
import com.avinfo.avmusic.settings.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ActivitySetting extends ActivityBase implements AdvancedSettings.OnFragmentInteractionListener {

    public static List<Theme> mThemeList = new ArrayList<>();
    public static int selectedTheme = 0;
    LinearLayout mode, theme;
    MaterialCheckBox pauseHeadphoneUnplugged, resumeHeadphonePlugged, headphoneControl, saveRecent, savePlaylist, saveCount;
    LinearLayout llBottomSheet;
    private RecyclerView mRecyclerView;
    private ThemeAdapter mAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_backarrow)));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        bannerAd();

        mode = findViewById(R.id.settingsMode);
        theme = findViewById(R.id.settingsTheme);
        pauseHeadphoneUnplugged = findViewById(R.id.pauseHeadphoneUnplugged);
        resumeHeadphonePlugged = findViewById(R.id.resumeHeadphonePlugged);
        headphoneControl = findViewById(R.id.headphoneControl);
        saveRecent = findViewById(R.id.saveRecent);
        saveCount = findViewById(R.id.saveCount);
        savePlaylist = findViewById(R.id.savePlaylist);

        llBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        setupCheckBoxes();
        setListeners();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.resetSetting:
                resetDialog();
                return true;

        }
        return false;

    }

    private void resetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySetting.this);
        builder.setTitle("Reset App Settings");
        builder.setMessage("It will reset In-App Settings. Also Recent Songs, Counts and Last Played Playlist will be Deleted!");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppMain.settings.reset();
                Toast.makeText(ActivitySetting.this, "Reset Complete", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void bannerAd() {

        RelativeLayout bannerAdContainer;
        AdView bannerAdView = null;

        bannerAdContainer = findViewById(R.id.bannerAdContainer);

        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }

//        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        bannerAdView = new AdView(this, UtilsData.FB_Banner_AD, AdSize.BANNER_HEIGHT_50);

        bannerAdContainer.addView(bannerAdView);
        bannerAdView.loadAd();
    }


    private void setupCheckBoxes() {
        pauseHeadphoneUnplugged.setChecked(AppMain.settings.get("pauseHeadphoneUnplugged", true));
        resumeHeadphonePlugged.setChecked(AppMain.settings.get("resumeHeadphonePlugged", true));
        headphoneControl.setChecked(AppMain.settings.get("headphoneControl", true));
        saveRecent.setChecked(AppMain.settings.get("saveRecent", true));
        saveCount.setChecked(AppMain.settings.get("saveCount", true));
        savePlaylist.setChecked(AppMain.settings.get("savePlaylist", true));
    }

    private void setListeners() {

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModeDialog();
            }
        });

        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTheme();
            }
        });

        pauseHeadphoneUnplugged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppMain.settings.set("pauseHeadphoneUnplugged", true);
                } else {
                    AppMain.settings.set("pauseHeadphoneUnplugged", false);
                }
            }
        });

        resumeHeadphonePlugged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    AppMain.settings.set("resumeHeadphonePlugged", true);
                } else {
                    AppMain.settings.set("resumeHeadphonePlugged", false);
                }

            }
        });

        headphoneControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    AppMain.settings.set("headphoneControl", true);
                } else {
                    AppMain.settings.set("headphoneControl", false);
                }

            }
        });

        saveRecent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    AppMain.settings.set("saveRecent", true);
                } else {
                    AppMain.settings.set("saveRecent", false);
                }

            }
        });

        saveCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    AppMain.settings.set("saveCount", true);
                } else {
                    AppMain.settings.set("saveCount", false);
                }
            }
        });

        savePlaylist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    AppMain.settings.set("savePlaylist", true);
                } else {
                    AppMain.settings.set("savePlaylist", false);
                }

            }
        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void changeTheme() {
        selectedTheme = UtilsTheme.getCurrentActiveTheme();

        openBottomSheet();
        prepareThemeData();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void openBottomSheet() {

        mRecyclerView = findViewById(R.id.recyclerViewBottomSheet);

        mAdapter = new ThemeAdapter(mThemeList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void prepareThemeData() {
        mThemeList.clear();
        mThemeList.addAll(UtilsTheme.getThemeList());
        mAdapter.notifyDataSetChanged();
    }

    private void showModeDialog() {
        CharSequence[] values = {"Day Mode", "Nigh Mode"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySetting.this);
        builder.setTitle("Set Day/Night Mode");
        int checkeditem = AppMain.settings.get("modes", "Day").equals("Day") ? 0 : 1;
        int[] newcheckeditem = {checkeditem};
        builder.setSingleChoiceItems(values, checkeditem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        switch (item) {
                            case 0:
                                newcheckeditem[0] = 0;
                                break;
                            case 1:
                                newcheckeditem[0] = 1;
                                break;

                        }
                    }
                }
        );

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkeditem == newcheckeditem[0]) {
                    dialog.dismiss();
                } else {
                    if (newcheckeditem[0] == 1) {
                        AppMain.settings.set("modes", "Night");
                    } else {
                        AppMain.settings.set("modes", "Day");
                    }
                    Toast.makeText(ActivitySetting.this, "Changes Made", Toast.LENGTH_SHORT).show();
                    recreate();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settingsmenu, menu);
        return true;
    }

    public void sendFeedback(View view) {
        UtilsExtra.sendFeedback(ActivitySetting.this);
    }

    public void gotoFAQ(View view) {
        UtilsExtra.openCustomTabs(ActivitySetting.this, "https://github.com/iamSahdeep/Bop/blob/master/FAQs.md");
    }

    public void gotoPP(View view) {
        UtilsExtra.openCustomTabs(ActivitySetting.this, "https://github.com/iamSahdeep/Bop/blob/master/privacy_policy.md");
    }

    public void gotoGithub(View view) {
        UtilsExtra.openCustomTabs(ActivitySetting.this, "https://github.com/iamSahdeep/Bop");
    }

    public void cancelTheme(View view) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void saveTheme(View view) {
        AppMain.settings.set("themes", getResources().getStringArray(R.array.themes_values)[selectedTheme]);
        Toast.makeText(ActivitySetting.this, "Changes Made", Toast.LENGTH_SHORT).show();
        recreate();
    }

    public void AdvancedFragment(View view) {
        findViewById(R.id.scrollSettings).setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.replaceAdvaced, AdvancedSettings.newInstance("", "")).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            findViewById(R.id.scrollSettings).setVisibility(View.VISIBLE);
        } else {
//            super.onBackPressed();
            startActivity(new Intent(ActivitySetting.this, ActivityMain.class));
            finish();
        }
    }

    @Override
    public void onFragmentInteraction(String what) {
        if (what.equals("jump")) {
            createFWDialog();
        } else if (what.equals("rescan")) {
            rescanMediaStore();
        } else if (what.equals("sleep")) {
            createSTdialog();
        }
    }

    private void createSTdialog() {
        CharSequence[] values = {"5 min", "10 min", "15 min", "20 min", "25 min"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySetting.this);
        builder.setTitle("Set Sleep timer to close music player if paused");
        int checkeditem = AppMain.settings.get("sleepTimer", 5);
        int[] newcheckeditem = {checkeditem};
        builder.setSingleChoiceItems(values, (checkeditem / 5) - 1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        newcheckeditem[0] = item;
                    }
                }
        );

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkeditem == newcheckeditem[0]) {
                    dialog.dismiss();
                } else {
                    if (newcheckeditem[0] == 0) {
                        AppMain.settings.set("sleepTimer", 5);
                    } else if (newcheckeditem[0] == 1) {
                        AppMain.settings.set("sleepTimer", 10);
                    } else if (newcheckeditem[0] == 2) {
                        AppMain.settings.set("sleepTimer", 15);
                    } else if (newcheckeditem[0] == 3) {
                        AppMain.settings.set("sleepTimer", 20);
                    } else if (newcheckeditem[0] == 4) {
                        AppMain.settings.set("sleepTimer", 25);
                    } else {
                        AppMain.settings.set("sleepTimer", 5);
                    }
                    Toast.makeText(ActivitySetting.this, "Changes Made", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void createFWDialog() {
        CharSequence[] values = {"5 sec", "10 sec", "15 sec", "20 sec", "25 sec"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySetting.this);
        builder.setTitle("Set jump value in forwar/rewind");
        int checkeditem = AppMain.settings.get("jumpValue", 10);
        int[] newcheckeditem = {checkeditem};
        builder.setSingleChoiceItems(values, (checkeditem / 5) - 1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        newcheckeditem[0] = item;
                    }
                }
        );

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkeditem == newcheckeditem[0]) {
                    dialog.dismiss();
                } else {
                    if (newcheckeditem[0] == 0) {
                        AppMain.settings.set("jumpValue", 5);
                    } else if (newcheckeditem[0] == 1) {
                        AppMain.settings.set("jumpValue", 10);
                    } else if (newcheckeditem[0] == 2) {
                        AppMain.settings.set("jumpValue", 15);
                    } else if (newcheckeditem[0] == 3) {
                        AppMain.settings.set("jumpValue", 20);
                    } else if (newcheckeditem[0] == 4) {
                        AppMain.settings.set("jumpValue", 25);
                    } else {
                        AppMain.settings.set("jumpValue", 10);
                    }

                    Toast.makeText(ActivitySetting.this, "Changes Made", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void rescanMediaStore() {
        ProgressDialog lol = new ProgressDialog(ActivitySetting.this);
        lol.setMessage("Sending BroadCast to Scan");
        lol.setCancelable(false);
        lol.show();
        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{"file://" + Environment.getExternalStorageDirectory()},
                new String[]{"audio/mp3", "audio/*"},
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    public void onMediaScannerConnected() {

                    }

                    public void onScanCompleted(String path, Uri uri) {
                        lol.cancel();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivitySetting.this, "Remove from recents and restart application", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

    }
}
