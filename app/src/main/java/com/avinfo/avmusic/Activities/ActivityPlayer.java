package com.avinfo.avmusic.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andremion.music.MusicCoverView;
import com.avinfo.avmusic.Utils.UtilsExtra;
import com.avinfo.avmusic.playerMain.AppMain;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.avinfo.avmusic.CustomViews.TransitionAdapter;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.Visualizers.CircleBarVisualizer;
import com.avinfo.avmusic.equalizer.EqualizerFragment;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class ActivityPlayer extends ActivityBase implements MediaController.MediaPlayerControl {

    ImageView next, previous, rewind, forward, shuffle, repeat, eq;
    private MusicCoverView mCoverView;
    private FloatingActionButton mFabView;
    private TextView mTimeView;
    private TextView mDurationView;
    private CircularSeekBar mProgressView;
    private CircleBarVisualizer circleBarVisualizer;
    private TextView mTitleView;


    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMediaDescription(metadata.getDescription());
                updateDuration(metadata);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppMain.settings.load(this);
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.playerview);
        RelativeLayout wq = findViewById(R.id.fdsg);
        wq.bringToFront();
        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.titleTrack);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);
        eq = findViewById(R.id.equaButton);
        circleBarVisualizer = findViewById(R.id.visualizer);
        circleBarVisualizer.setColor(UtilsExtra.getThemeAttrColor(this, R.styleable.Theme_primaryColor));

        mCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {
                // Nothing to do
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                supportFinishAfterTransition();
            }
        });

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                mCoverView.start();
            }
        });
        setclickListeners();
        prepareSeekBar();
    }

    private void setclickListeners() {

        eq.setImageDrawable(UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_equalizer)));
        next = findViewById(R.id.next);
        next.setImageDrawable(UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_skip)));
        previous = findViewById(R.id.previous);
        previous.setImageDrawable(UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_previous)));
        forward = findViewById(R.id.forward);
        forward.setImageDrawable(UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_forward)));
        rewind = findViewById(R.id.rewind);
        rewind.setImageDrawable(UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_rewind)));
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);

        if (!AppMain.musicService.isShuffle()) {
            shuffle.setImageDrawable((UtilsExtra.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_shuffle_off))));
        } else {
            shuffle.setImageDrawable((UtilsExtra.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_shuffle_on))));
        }
        if (AppMain.musicService.isRepeat() == 0) {
            repeat.setImageDrawable((UtilsExtra.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_repeat_one))));
        } else if (AppMain.musicService.isRepeat() == 1) {
            repeat.setImageDrawable((UtilsExtra.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_repeat_on))));
        } else
            repeat.setImageDrawable((UtilsExtra.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_repeat_off))));

        next.setOnClickListener(view -> playNext());

        previous.setOnClickListener(view -> playPrevious());

        forward.setOnClickListener(view -> seekTo(getCurrentPosition() + (AppMain.settings.get("jumpValue", 10) * 1000)));

        rewind.setOnClickListener(view -> seekTo(getCurrentPosition() - (AppMain.settings.get("jumpValue", 10) * 1000)));

        shuffle.setOnClickListener(view -> {
            AppMain.musicService.toggleShuffle();
            if (!AppMain.musicService.isShuffle()) {
                shuffle.setImageDrawable((UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_shuffle_off))));
            } else {
                shuffle.setImageDrawable((UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_shuffle_on))));
            }
        });

        repeat.setOnClickListener(view -> {
            AppMain.musicService.toggleRepeat();
            if (AppMain.musicService.isRepeat() == 0) {
                repeat.setImageDrawable((UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat_one))));
            } else if (AppMain.musicService.isRepeat() == 1) {
                repeat.setImageDrawable((UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat_on))));
            } else
                repeat.setImageDrawable((UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat_off))));
        });
    }


    public void onFabClick(View view) {
        AppMain.musicService.togglePlayback();
        if (!AppMain.musicService.isPaused()) {
            mFabView.setImageDrawable((UtilsExtra.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_pause))));
        } else {
            mFabView.setImageDrawable((UtilsExtra.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_play))));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            connectToSession(AppMain.musicService.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        AppMain.musicService.unpausePlayer();
        mFabView.setImageDrawable((UtilsExtra.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_pause))));
    }

    @Override
    public void pause() {
        AppMain.musicService.pausePlayer();
        mFabView.setImageDrawable((UtilsExtra.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_play))));
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
        if (AppMain.musicService != null && AppMain.musicService.musicBound)
            return AppMain.musicService.isPlaying();

        return false;
    }

    @Override
    public int getBufferPercentage() {
        // TODO Auto-generated method stub
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

    // Back to the normal methods

    public void playNext() {
        AppMain.musicService.next(true);
        AppMain.musicService.playSong();

    }

    public void playPrevious() {
        AppMain.musicService.previous(true);
        AppMain.musicService.playSong();
    }

    @Override
    protected void onStop() {
        super.onStop();

        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(ActivityPlayer.this);
        if (controllerCompat != null) {
            controllerCompat.unregisterCallback(mCallback);
        }
    }

    private void prepareSeekBar() {

        mProgressView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (fromUser) {
                    seekTo((int) progress * 1000);
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });

        Handler handler = new Handler();
        ActivityPlayer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!AppMain.mainMenuHasNowPlayingItem)
                    finish();
                if (isPlaying()) {
                    int position = getCurrentPosition() / 1000;
                    int duration = (int) AppMain.musicService.currentSong.getDurationSeconds();
                    onUpdateProgress(position, duration);
                }
                handler.postDelayed(this, 100);
            }
        });

    }

    private void onUpdateProgress(int position, int duration) {
        if (mTimeView != null) {
            mTimeView.setText(DateUtils.formatElapsedTime(position));
        }
        if (mDurationView != null) {
            mDurationView.setText(DateUtils.formatElapsedTime(duration));
        }
        if (mProgressView != null) {
            mProgressView.setProgress(position);
        }
    }

    private void updateMediaDescription(MediaDescriptionCompat description) {
        if (description == null) {
            return;
        }

        mTitleView.setText(description.getTitle());
        circleBarVisualizer.setPlayer(getAudioSessionId());
        mTitleView.setSelected(true);
        mCoverView.setImageBitmap(description.getIconBitmap());
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mProgressView.setMax(duration);
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mFabView.setImageDrawable((UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause))));
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mFabView.setImageDrawable((UtilsExtra.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play))));
                break;
            case PlaybackStateCompat.STATE_NONE:
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                finish();
                break;
            default:
            case PlaybackStateCompat.STATE_BUFFERING:
                break;
            case PlaybackStateCompat.STATE_CONNECTING:
                break;
            case PlaybackStateCompat.STATE_ERROR:
                break;
            case PlaybackStateCompat.STATE_FAST_FORWARDING:
                break;
            case PlaybackStateCompat.STATE_REWINDING:
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
                break;
        }

    }

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController == null) {
            mediaController = new MediaControllerCompat(ActivityPlayer.this, token);
        }
        if (mediaController.getMetadata() == null) {
            finish();
            return;
        }

        MediaControllerCompat.setMediaController(ActivityPlayer.this, mediaController);
        mediaController.registerCallback(mCallback);
        PlaybackStateCompat state = mediaController.getPlaybackState();
        updatePlaybackState(state);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata.getDescription());
            updateDuration(metadata);
        }
    }

    public void equalizer(View view) {
        AppMain.musicService.player.setLooping(true);
        EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(UtilsExtra.getThemeAttrColor(ActivityPlayer.this, R.styleable.Theme_primaryDarkColor))
                .setAudioSessionId(getAudioSessionId())
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, equalizerFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        mCoverView.stop();
    }

}
