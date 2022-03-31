package com.avinfo.avmusic.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avinfo.avmusic.Activities.ActivityPlayingList;
import com.avinfo.avmusic.Adapters.MostPlayedAdapter;
import com.avinfo.avmusic.Adapters.RecentAdapter;
import com.avinfo.avmusic.Utils.UtilsData;
import com.avinfo.avmusic.Utils.UtilsRecent;
import com.avinfo.avmusic.Utils.UtilsSong;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.SongData.Song;
import com.avinfo.avmusic.playerMain.AppMain;
import com.bumptech.glide.Glide;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

public class HomeFragment extends Fragment {

    List<Long> recentSongs;
    List<Song> mostPlayedSongs;

    RecentAdapter recentSongsAdapter;
    MostPlayedAdapter mostPlayedSongsAdapter;
    LinearLayout recents, mostPlayed, openAllSongs, openAlbums, openPlaylists, openGenres, openArtists;
    TextView recentAll, MPAll;

    private ImageView a1,a2,a3,a4,a5;



    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    SwipeRefreshLayout pulltorefresh;
    private InterstitialAd interstitialAd25;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


//        bannerAd(view);

        if (interstitialAd25 != null) {
            interstitialAd25.destroy();
            interstitialAd25 = null;
        }

        interstitialAd25 = new InterstitialAd(getContext(), UtilsData.FB_Interstitial_AD);
        interstitialAd25.loadAd(EnumSet.of(CacheFlag.VIDEO));


        pulltorefresh = view.findViewById(R.id.pulltorefresh);

        a1 = view.findViewById(R.id.a1);
        a2 = view.findViewById(R.id.a2);
        a3 = view.findViewById(R.id.a3);
        a4 = view.findViewById(R.id.a4);
        a5 = view.findViewById(R.id.a5);

        Glide.with(getContext()).load(R.drawable.all_song).into(a1);
        Glide.with(getContext()).load(R.drawable.playlist).into(a2);
        Glide.with(getContext()).load(R.drawable.albums).into(a3);
        Glide.with(getContext()).load(R.drawable.genres).into(a4);
        Glide.with(getContext()).load(R.drawable.artist).into(a5);

        recents = view.findViewById(R.id.noRecentSongs);
        mostPlayed = view.findViewById(R.id.noMostPlayedSongs);
        openAllSongs = view.findViewById(R.id.openAllSongs);
        openAlbums = view.findViewById(R.id.openAlbums);
        openPlaylists = view.findViewById(R.id.openPlaylists);
        openGenres = view.findViewById(R.id.openGenres);
        openArtists = view.findViewById(R.id.openArtists);
        recentAll = view.findViewById(R.id.playAllRecents);
        MPAll = view.findViewById(R.id.playAllMP);

        ViewPager mViewPager = getActivity().findViewById(R.id.container);

        openAllSongs.setOnClickListener(view14 -> mViewPager.setCurrentItem(1, true));

        openGenres.setOnClickListener(view13 -> mViewPager.setCurrentItem(3, true));

        openPlaylists.setOnClickListener(view12 -> mViewPager.setCurrentItem(2, true));

        openAlbums.setOnClickListener(view1 -> mViewPager.setCurrentItem(4, true));

        openArtists.setOnClickListener(view15 -> mViewPager.setCurrentItem(5, true));

        recentSongs = UtilsRecent.getRecentSongs(getActivity());
        mostPlayedSongs = UtilsRecent.getMostPlayedSongs(getActivity());

        if (recentSongs == null || recentSongs.size() == 0)
            recents.setVisibility(View.VISIBLE);
        else recents.setVisibility(View.GONE);

        if (mostPlayedSongs == null || mostPlayedSongs.size() == 0) {
            mostPlayed.setVisibility(View.VISIBLE);
        } else mostPlayed.setVisibility(View.GONE);

        RecyclerView recentRecycler = view.findViewById(R.id.recyclerRecent);
        recentRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recentRecycler.setItemViewCacheSize(20);
        recentRecycler.setDrawingCacheEnabled(true);
        recentRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recentRecycler.setNestedScrollingEnabled(false);
        recentSongsAdapter = new RecentAdapter(recentSongs);
        recentRecycler.setAdapter(recentSongsAdapter);

        RecyclerView mostPlayedRecycler = view.findViewById(R.id.recyclerMostPlayed);
        mostPlayedRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mostPlayedRecycler.setHasFixedSize(true);
        mostPlayedRecycler.setItemViewCacheSize(20);
        mostPlayedRecycler.setDrawingCacheEnabled(true);
        mostPlayedRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mostPlayedRecycler.setNestedScrollingEnabled(false);
        mostPlayedSongsAdapter = new MostPlayedAdapter(getActivity(), mostPlayedSongs);
        mostPlayedRecycler.setAdapter(mostPlayedSongsAdapter);

        recentAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd25 == null || !interstitialAd25.isAdLoaded()) {
                    AppMain.musicList.clear();
                    List<Long> temmp = UtilsRecent.getRecentSongs(getActivity());
                    if (temmp == null || temmp.size() <= 0) {
                        Toast.makeText(getActivity(), "No Songs to play", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < temmp.size(); i++) {
                        AppMain.musicList.add(UtilsSong.getSongById(temmp.get(i)));
                    }
                    AppMain.nowPlayingList = AppMain.musicList;
                    AppMain.musicService.setList(AppMain.nowPlayingList);
                    Intent intent = new Intent(getActivity(), ActivityPlayingList.class);
                    intent.putExtra("playlistname", "Recent Songs");
                    getActivity().startActivity(intent);
                } else {
                    interstitialAd25.show();
                }
                interstitialAd25.setAdListener(new AbstractAdListener() {
                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        super.onInterstitialDismissed(ad);
                        AppMain.musicList.clear();
                        List<Long> temmp = UtilsRecent.getRecentSongs(getActivity());
                        if (temmp == null || temmp.size() <= 0) {
                            Toast.makeText(getActivity(), "No Songs to play", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < temmp.size(); i++) {
                            AppMain.musicList.add(UtilsSong.getSongById(temmp.get(i)));
                        }
                        AppMain.nowPlayingList = AppMain.musicList;
                        AppMain.musicService.setList(AppMain.nowPlayingList);
                        Intent intent = new Intent(getActivity(), ActivityPlayingList.class);
                        intent.putExtra("playlistname", "Recent Songs");
                        getActivity().startActivity(intent);
                    }
                });

            }
        });

        MPAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interstitialAd25 == null || !interstitialAd25.isAdLoaded()) {
                    AppMain.musicList.clear();
                    AppMain.musicList = (ArrayList<Song>) UtilsRecent.getMostPlayedSongs(getActivity());
                    if (AppMain.musicList == null || AppMain.musicList.size() <= 0) {
                        Toast.makeText(getActivity(), "No Songs to play", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AppMain.nowPlayingList = AppMain.musicList;
                    AppMain.musicService.setList(AppMain.nowPlayingList);
                    Intent intent = new Intent(getActivity(), ActivityPlayingList.class);
                    intent.putExtra("playlistname", "Most played");
                    getActivity().startActivity(intent);
                } else {
                    interstitialAd25.show();
                }
                interstitialAd25.setAdListener(new AbstractAdListener() {
                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        super.onInterstitialDismissed(ad);
                        AppMain.musicList.clear();
                        AppMain.musicList = (ArrayList<Song>) UtilsRecent.getMostPlayedSongs(getActivity());
                        if (AppMain.musicList == null || AppMain.musicList.size() <= 0) {
                            Toast.makeText(getActivity(), "No Songs to play", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        AppMain.nowPlayingList = AppMain.musicList;
                        AppMain.musicService.setList(AppMain.nowPlayingList);
                        Intent intent = new Intent(getActivity(), ActivityPlayingList.class);
                        intent.putExtra("playlistname", "Most played");
                        getActivity().startActivity(intent);
                    }
                });
            }
        });

        pulltorefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (interstitialAd25 != null) {
                    interstitialAd25.destroy();
                    interstitialAd25 = null;
                }
//                mostPlayedRecycler.setAdapter(mostPlayedSongsAdapter);
//                recentRecycler.setAdapter(recentSongsAdapter);
                interstitialAd25 = new InterstitialAd(getContext(), UtilsData.FB_Interstitial_AD);
                interstitialAd25.loadAd(EnumSet.of(CacheFlag.VIDEO));
                mostPlayedSongsAdapter.updateData(mostPlayedSongs);
                recentSongsAdapter.updateData(recentSongs);
                mostPlayedSongsAdapter.notifyDataSetChanged();
                recentSongsAdapter.notifyDataSetChanged();
                pulltorefresh.setRefreshing(false);
            }
        });

        return view;
    }

//    private void bannerAd(View view) {
//
//        RelativeLayout bannerAdContainer;
//        AdView bannerAdView = null;
//
//        bannerAdContainer = (RelativeLayout)view.findViewById(R.id.bannerAdContainer);
//
//        if (bannerAdView != null) {
//            bannerAdView.destroy();
//            bannerAdView = null;
//        }
//
////        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
//        bannerAdView = new AdView(this.getActivity(), UtilsData.FB_Banner_AD, AdSize.BANNER_HEIGHT_50);
//
//        bannerAdContainer.addView(bannerAdView);
//        bannerAdView.loadAd();
//    }

}
