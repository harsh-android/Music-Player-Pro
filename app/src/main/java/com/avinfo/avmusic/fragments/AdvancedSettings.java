package com.avinfo.avmusic.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avinfo.avmusic.R;
import com.avinfo.avmusic.Utils.UtilsData;
import com.avinfo.avmusic.playerMain.AppMain;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class AdvancedSettings extends Fragment {


    TextView jump, sleep, rescan;
    LinearLayout jumpLL, sleepLL;
    private OnFragmentInteractionListener mListener;

    private NativeAdLayout nativeAdLayout;
    private NativeAd nativeAd;
    private AdOptionsView adOptionsView;
    private MediaView nativeAdMedia;
    String TAG = "Native";
    private LinearLayout adChoicesContainer;

    public AdvancedSettings() {
        // Required empty public constructor
    }

    public static AdvancedSettings newInstance(String param1, String param2) {
        return new AdvancedSettings();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced_settings, container, false);
        sleepLL = view.findViewById(R.id.settingSTimer);
        sleep = view.findViewById(R.id.sleepTime);
        jumpLL = view.findViewById(R.id.settingPSpeed);
        jump = view.findViewById(R.id.jumpTime);
        rescan = view.findViewById(R.id.rescan);


        nativeAdLayout = view.findViewById(R.id.native_ad_container);
        adChoicesContainer = view.findViewById(R.id.ad_choices_container);

        nativeAd = new NativeAd(getActivity(), UtilsData.FB_Native_AD);
        nativeAd.loadAd();
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                if (nativeAd == ad) {

                    Log.d(TAG, "onMediaDownloaded");
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
//                if (nativeAdStatus != null) {
//                    nativeAdStatus.setText("Ad failed to load: " + error.getErrorMessage());
//                }
//                Toast.makeText(getContext(), "Ad Failed To Load: " + adError.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (nativeAd == null || nativeAd != ad) {
                    // Race condition, load() called again before last ad was displayed
                    return;
                }

                if (nativeAdLayout == null) {
                    return;
                }

                // Unregister last ad
                nativeAd.unregisterView();

//                if (nativeAdStatus != null) {
//                    nativeAdStatus.setText("");
//                }

                if (adChoicesContainer != null) {
                    adOptionsView = new AdOptionsView(getActivity(), nativeAd, nativeAdLayout);
                    adChoicesContainer.removeAllViews();
                    adChoicesContainer.addView(adOptionsView, 0);
                }

                inflateAd(nativeAd, nativeAdLayout);

                // Registering a touch listener to log which ad component receives the touch event.
                // We always return false from onTouch so that we don't swallow the touch event (which
                // would prevent click events from reaching the NativeAd control).
                // The touch listener could be used to do animations.
                nativeAd.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            int i = view.getId();
                            if (i == R.id.native_ad_call_to_action) {
                                Log.d(TAG, "Call to action button clicked");
                            } else if (i == R.id.native_ad_media) {
                                Log.d(TAG, "Main image clicked");
                            } else {
                                Log.d(TAG, "Other ad component clicked");
                            }
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(TAG, "onLoggingImpression");
            }
        });

        jump.setText(String.format("%s sec", String.valueOf(AppMain.settings.get("jumpValue", 10))));
        sleep.setText(String.format("%s min", String.valueOf(AppMain.settings.get("sleepTimer", 5))));
        jumpLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("jump");
            }
        });
        sleepLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("sleep");
            }
        });
        rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("rescan");
            }
        });
        return view;
    }


    public void onButtonPressed(String what) {
        if (mListener != null) {
            mListener.onFragmentInteraction(what);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String what);
    }


    private void inflateAd(NativeAd nativeAd, View adView) {
        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        nativeAdMedia.setListener(getMediaViewListener());

        // Setting the Text
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        sponsoredLabel.setText(R.string.sponsored);

        // You can use the following to specify the clickable areas.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdIcon);
        clickableViews.add(nativeAdMedia);
        clickableViews.add(nativeAdCallToAction);
        nativeAd.registerViewForInteraction(
                nativeAdLayout,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);

        // Optional: tag views
        NativeAdBase.NativeComponentTag.tagView(nativeAdIcon, NativeAdBase.NativeComponentTag.AD_ICON);
        NativeAdBase.NativeComponentTag.tagView(nativeAdTitle, NativeAdBase.NativeComponentTag.AD_TITLE);
        NativeAdBase.NativeComponentTag.tagView(nativeAdBody, NativeAdBase.NativeComponentTag.AD_BODY);
        NativeAdBase.NativeComponentTag.tagView(nativeAdSocialContext, NativeAdBase.NativeComponentTag.AD_SOCIAL_CONTEXT);
        NativeAdBase.NativeComponentTag.tagView(nativeAdCallToAction, NativeAdBase.NativeComponentTag.AD_CALL_TO_ACTION);
    }

    private static MediaViewListener getMediaViewListener() {
        String TAG = "Native";
        return new MediaViewListener() {
            @Override
            public void onVolumeChange(MediaView mediaView, float volume) {
                Log.i(TAG, "MediaViewEvent: Volume " + volume);
            }

            @Override
            public void onPause(MediaView mediaView) {
                Log.i(TAG, "MediaViewEvent: Paused");
            }

            @Override
            public void onPlay(MediaView mediaView) {
                Log.i(TAG, "MediaViewEvent: Play");
            }

            @Override
            public void onFullscreenBackground(MediaView mediaView) {
                Log.i(TAG, "MediaViewEvent: FullscreenBackground");
            }

            @Override
            public void onFullscreenForeground(MediaView mediaView) {
                Log.i(TAG, "MediaViewEvent: FullscreenForeground");
            }

            @Override
            public void onExitFullscreen(MediaView mediaView) {
                Log.i(TAG, "MediaViewEvent: ExitFullscreen");
            }

            @Override
            public void onEnterFullscreen(MediaView mediaView) {
                Log.i(TAG, "MediaViewEvent: EnterFullscreen");
            }

            @Override
            public void onComplete(MediaView mediaView) {
                Log.i(TAG, "MediaViewEvent: Completed");
            }
        };
    }

}
