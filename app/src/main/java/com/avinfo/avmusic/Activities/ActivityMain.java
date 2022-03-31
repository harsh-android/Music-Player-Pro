package com.avinfo.avmusic.Activities;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avinfo.avmusic.Utils.UtilsData;
import com.avinfo.avmusic.Utils.UtilsExtra;
import com.avinfo.avmusic.playerMain.AppMain;
import com.avinfo.avmusic.services.ServicePlayMusic;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAd;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;
import com.avinfo.avmusic.Utils.UtilsRecent;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.fragments.FileFragment;
import com.avinfo.avmusic.fragments.AlbumFragment;
import com.avinfo.avmusic.fragments.ArtistFragment;
import com.avinfo.avmusic.fragments.GenresFragment;
import com.avinfo.avmusic.fragments.PlaylistFragment;
import com.avinfo.avmusic.fragments.SongsFragment;
import com.avinfo.avmusic.fragments.HomeFragment;

import java.util.EnumSet;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import me.tankery.lib.circularseekbar.CircularSeekBar;

import static com.avinfo.avmusic.R.string.*;


public class ActivityMain extends ActivityBase implements MediaController.MediaPlayerControl {


    int i = 0;
    private static final int BACK_PRESSED_DELAY = 2000;
    /*AlbumArt in Sliding Panel*/
    ImageView albumArtSP, next, previous, forward, rewind;
    /*Song name, time left and Total time in Sliding Panel*/
    TextView songNameSP;
    Drawer drawer;
    MiniDrawer miniDrawer;
    AccountHeader accountHeader;
    /* Next two are for Navigation Drawer*/
    CrossfadeDrawerLayout crossfadeDrawerLayout;

    private InterstitialAd interstitialAd;

    /*Our non Sliding Panel*/
    ConstraintLayout bottomControls;

    private boolean playbackPaused = false;
    private boolean backPressedOnce = false;
    private final Runnable backPressedTimeoutAction = () -> backPressedOnce = false;
    private CircularSeekBar mProgressView;
    private Handler backPressedHandler = new Handler();
    private ViewPager mViewPager;

    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMediaDescription(metadata.getDescription());
                updateDuration(metadata);
            }
        }
    };
    private ServicePlayMusic ServicePlayMusic;

    public static void addNowPlayingItem() {
        if (AppMain.mainMenuHasNowPlayingItem)
            return;
        AppMain.mainMenuHasNowPlayingItem = true;
    }

//    SwipeRefreshLayout swip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppMain.settings.load(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        bannerAd();

        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }

        interstitialAd = new InterstitialAd(this, UtilsData.FB_Interstitial_AD);
        interstitialAd.loadAd(EnumSet.of(CacheFlag.VIDEO));


        bottomControls = findViewById(R.id.bottomViewControls);
        songNameSP = findViewById(R.id.bottomtextView);
        albumArtSP = findViewById(R.id.bottomImageview);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        rewind = findViewById(R.id.rewind);
        forward = findViewById(R.id.forward);

        mProgressView = findViewById(R.id.footerseek);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_ham)));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            if (drawer.isDrawerOpen()) {
                drawer.closeDrawer();
            } else drawer.openDrawer();
        });

        mProgressView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (fromUser) {
                    seekTo((int) (progress * 1000));
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });

        mViewPager = findViewById(R.id.container);

        setupViewPager(mViewPager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(ActivityMain.this, "Viewpager Position :- " + position, Toast.LENGTH_SHORT).show();
                i = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        createDrawer();

        if (AppMain.settings.get("modes", "Day").equals("Day"))
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.md_white_1000));
        else getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.md_grey_900));
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
        
        bannerAdView.setAdListener(new AbstractAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                super.onError(ad, error);
//                Toast.makeText(ActivityMain.this, error.getErrorMessage() , Toast.LENGTH_SHORT).show();
            }
        });
        
    }


    private void createDrawer() {

        accountHeader = new AccountHeaderBuilder().withActivity(this)
                .withSelectionListEnabled(false)
                .addProfiles(
                        new ProfileDrawerItem().withName(app_name).withIcon(R.mipmap.logo)
                ).build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(accountHeader)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withHasStableIds(true)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withCloseOnClick(true)
                .addDrawerItems(

                        new PrimaryDrawerItem().withName("Now Playing").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_play))).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName("Home").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_shuffle_on))).withIdentifier(6).withSelectable(true),
                        new PrimaryDrawerItem().withName("All Songs").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_music))).withIdentifier(2).withSelectable(true),
                        new PrimaryDrawerItem().withName("Playlist").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_playlist))).withIdentifier(3).withSelectable(true),
                        new PrimaryDrawerItem().withName("Genres").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_genre))).withIdentifier(4).withSelectable(true),
                        new PrimaryDrawerItem().withName("Albums").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_album))).withIdentifier(5).withSelectable(true),
                        new PrimaryDrawerItem().withName("Artists").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_artist))).withIdentifier(7).withSelectable(true),
                        new PrimaryDrawerItem().withName("Files").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_folder))).withIdentifier(8).withSelectable(true),
                        new SectionDrawerItem().withName("More").withDivider(true),
                        new SecondaryDrawerItem().withName("Share App").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_share))).withIdentifier(19).withSelectable(false),
                        new SecondaryDrawerItem().withName("Rate App").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_rate))).withIdentifier(20).withSelectable(false),
                        new SecondaryDrawerItem().withName("More App").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_more_app))).withIdentifier(22).withSelectable(false),
                        new SecondaryDrawerItem().withName("Feedback").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_feedback))).withIdentifier(21).withSelectable(false))
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName("Settings").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_setting))).withIdentifier(321).withSelectable(false),
                        new SecondaryDrawerItem().withName("Exit").withIcon(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_exit))).withIdentifier(342).withSelectable(false)
                ).withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem != null) {
                        Intent intent;
                        if (drawerItem.getIdentifier() == 1) {
                            if (AppMain.mainMenuHasNowPlayingItem) {
                                intent = new Intent(this, ActivityPlayingList.class);
                                startActivity(intent);
                            } else
                                Toast.makeText(getApplicationContext(), "no playlist", Toast.LENGTH_SHORT).show();
                        } else if (drawerItem.getIdentifier() == 2) {
                            mViewPager.setCurrentItem(1, true);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 3) {
                            mViewPager.setCurrentItem(2, true);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 4) {
                            mViewPager.setCurrentItem(3, true);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 5) {
                            mViewPager.setCurrentItem(4, true);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 6) {
                            mViewPager.setCurrentItem(0, true);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 7) {
                            mViewPager.setCurrentItem(5, true);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 8) {
                            mViewPager.setCurrentItem(6, true);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 19) {
                            UtilsExtra.ShareApp(ActivityMain.this);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 20) {
                            UtilsExtra.sendrate(ActivityMain.this);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 21) {
                            UtilsExtra.sendFeedback(ActivityMain.this);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 22) {
                            UtilsExtra.moreapps(ActivityMain.this);
                            drawer.closeDrawer();
                        } else if (drawerItem.getIdentifier() == 321) {
                            intent = new Intent(this, ActivitySetting.class);
                            startActivity(intent);
                            finish();
                        } else if (drawerItem.getIdentifier() == 342) {
                            AppMain.forceExit(this);
                            finishAffinity();
                        }
                    }
                    return false;
                })
                .build();

        //noinspection deprecation
        crossfadeDrawerLayout = (CrossfadeDrawerLayout) drawer.getDrawerLayout();
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));
        miniDrawer = drawer.getMiniDrawer();
        View view = miniDrawer.build(this);
        if (currentMode.equals("Night")) {
            view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialize.R.color.background_material_dark));
        } else {
            view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialize.R.color.background_material_light));
        }
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        miniDrawer.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(500);
                if (isFaded) {
                    drawer.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });


    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        setTitle(app_name);
                        drawer.setSelection(-1);
                        break;
                    case 1:
                        setTitle("Songs");
                        break;
                    case 2:
                        setTitle("Playlists");
                        break;
                    case 3:
                        setTitle("Genres");
                        break;
                    case 4:
                        setTitle("Albums");
                        break;
                    case 5:
                        setTitle("Artists");
                        break;
                    case 6:
                        setTitle("Files");
                        break;
                    default:
                        setTitle(app_name);

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void openPlayer(View view) {
        startActivity(new Intent(this, ActivityPlayingList.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppMain.startMusicService(this);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        } else if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0, true);
            return;
        } else if (this.backPressedOnce) {
            super.onBackPressed();
            AppMain.forceExit(this);
            finishAffinity();
            return;
        }

        this.backPressedOnce = true;

        Toast.makeText(this, getString(menu_main_back_to_exit), Toast.LENGTH_SHORT).show();

        backPressedHandler.postDelayed(backPressedTimeoutAction, BACK_PRESSED_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (backPressedHandler != null)
            backPressedHandler.removeCallbacks(backPressedTimeoutAction);

    }

    @Override
    protected void onStop() {
        super.onStop();

        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(ActivityMain.this);
        if (controllerCompat != null) {
            controllerCompat.unregisterCallback(mCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ActivityMain.this.invalidateOptionsMenu();

        if (AppMain.mainMenuHasNowPlayingItem) {
            AppMain.musicService.notifyCurrentSong();
            bottomControls.setVisibility(View.VISIBLE);
            workonSlidingPanel();

            if (AppMain.musicService.isPlaying()) {
                if (playbackPaused) {
                    playbackPaused = false;
                }
            }

        } else {
            bottomControls.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Default options specified on the XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);

        // Extra option to go to Now Playing screen
        // (only activated when there's an actual Now Playing screen)
        if (AppMain.musicService.isPlaying())
            menu.findItem(R.id.nowPlayingIcon).setVisible(true);

        Drawable drawable = menu.findItem(R.id.nowPlayingIcon).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        menu.findItem(R.id.nowPlayingIcon).setIcon(UtilsExtra.getThemedIcon(getApplicationContext(), drawable));

        drawable = menu.findItem(R.id.lastPlaylist).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        menu.findItem(R.id.lastPlaylist).setIcon(UtilsExtra.getThemedIcon(getApplicationContext(), drawable));

        if (!AppMain.settings.get("savePlaylist", true))
            menu.findItem(R.id.lastPlaylist).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            // I know it's bad to force quiting the program,
            // but I just love when applications have this option. xD
            case R.id.context_menu_end:
                AppMain.forceExit(this);
                break;

            case R.id.context_menu_settings:
                if (interstitialAd == null || !interstitialAd.isAdLoaded()) {
                    startActivity(new Intent(this, ActivitySetting.class));
                    finish();
                } else {
                    interstitialAd.show();
                }
                interstitialAd.setAdListener(new AbstractAdListener() {
                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        super.onInterstitialDismissed(ad);
                        startActivity(new Intent(ActivityMain.this, ActivitySetting.class));
                        finish();
                    }
                });
                break;

            case R.id.nowPlayingIcon:
                if (AppMain.mainMenuHasNowPlayingItem)
                    startActivity(new Intent(this, ActivityPlayingList.class));
                break;

            case R.id.lastPlaylist:
//                playLastPlayList();
                Toast.makeText(this, ""+R.attr.colorAccent, Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void playLastPlayList() {
        AppMain.musicList.clear();
        AppMain.musicList.addAll(UtilsRecent.getLastPlayList(ActivityMain.this));
        if (AppMain.musicList == null || AppMain.musicList.isEmpty()) {
            Toast.makeText(ActivityMain.this, "Can't Find Songs", Toast.LENGTH_SHORT).show();
            return;
        }
        AppMain.nowPlayingList = AppMain.musicList;
        AppMain.musicService.setList(AppMain.nowPlayingList);

        Intent intent = new Intent(ActivityMain.this, ActivityPlayingList.class);
        intent.putExtra("playlistname", "LastPlayed");
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void setControlListeners() {
        next.setImageDrawable(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_skip)));
        next.setOnClickListener(view -> playNext());

        previous.setImageDrawable(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_previous)));
        previous.setOnClickListener(view -> playPrevious());

        forward.setImageDrawable(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_forward)));
        forward.setOnClickListener(view -> seekTo(getCurrentPosition() + 10000));

        rewind.setImageDrawable(UtilsExtra.getThemedIcon(this, getDrawable(R.drawable.ic_rewind)));
        rewind.setOnClickListener(view -> seekTo(getCurrentPosition() - 10000));

    }

    private void workonSlidingPanel() {

        setControlListeners();
        prepareSeekBar();
        try {
            connectToSession(AppMain.musicService.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void prepareSeekBar() {
        mProgressView.setLockEnabled(true);

        Handler handler = new Handler();
        ActivityMain.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!AppMain.mainMenuHasNowPlayingItem) {
                    bottomControls.setVisibility(View.GONE);
                }
                if (isPlaying()) {
                    mProgressView.setProgress((float) getCurrentPosition() / 1000);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void start() {
        AppMain.musicService.unpausePlayer();
    }

    @Override
    public void pause() {
        AppMain.musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (AppMain.musicService != null && AppMain.musicService.musicBound
                && AppMain.musicService.isPlaying())
            return AppMain.musicService.getDuration();
        else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (AppMain.musicService != null && AppMain.musicService.musicBound
                && AppMain.musicService.isPlaying())
            return AppMain.musicService.getPosition();
        else
            return 0;
    }

    @Override
    public void seekTo(int position) {
        AppMain.musicService.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        return AppMain.musicService != null && AppMain.musicService.musicBound && AppMain.musicService.isPlaying();

    }

    public void playNext() {
        AppMain.musicService.next(true);
        AppMain.musicService.playSong();

        // To prevent the MusicPlayer from behaving
        // unexpectedly when we pause the song playback.
        if (playbackPaused) {
            playbackPaused = false;
        }

/*
        musicController.show();
*/
    }

    public void playPrevious() {
        AppMain.musicService.previous(true);
        AppMain.musicService.playSong();

        // To prevent the MusicPlayer from behaving
        // unexpectedly when we pause the song playback.
        if (playbackPaused) {
            playbackPaused = false;
        }

/*
        musicController.show();
*/
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return AppMain.musicService.getAudioSession();
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
/*
            return PlaceholderFragment.newInstance(position + 1);
*/
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new SongsFragment();
                case 2:
                    return new PlaylistFragment();
                case 3:
                    return new GenresFragment();
                case 4:
                    return new AlbumFragment();
                case 5:
                    return new ArtistFragment();
                case 6:
                    return new FileFragment();
                default:
                    return new HomeFragment();


            }
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                    return "Songs";
                case 2:
                    return "Playlist";
                case 3:
                    return "Genres";
                case 4:
                    return "Albums";
                case 5:
                    return "Artists";
                case 6:
                    return "Files";
            }
            return null;
        }
    }

    private void updateMediaDescription(MediaDescriptionCompat description) {
        if (description == null) {
            return;
        }
        songNameSP.setText(description.getTitle());
        albumArtSP.setImageBitmap(description.getIconBitmap());
        accountHeader.setHeaderBackground(new ImageHolder(description.getIconBitmap()));

    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mProgressView.setMax(duration);
    }


    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController == null) {
            mediaController = new MediaControllerCompat(ActivityMain.this, token);
        }
        if (mediaController.getMetadata() == null) {
            finish();
            return;
        }

        MediaControllerCompat.setMediaController(ActivityMain.this, mediaController);
        mediaController.registerCallback(mCallback);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata.getDescription());
            updateDuration(metadata);
        }
    }


}
