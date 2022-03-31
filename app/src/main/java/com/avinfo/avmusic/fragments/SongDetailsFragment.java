package com.avinfo.avmusic.fragments;


import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avinfo.avmusic.Utils.UtilsData;
import com.avinfo.avmusic.Utils.UtilsExtra;
import com.avinfo.avmusic.Utils.UtilsSong;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.SongData.Song;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.squareup.picasso.Picasso;

import androidx.fragment.app.Fragment;

public class SongDetailsFragment extends Fragment {

    private static final String Songparam = "param1";
    TextView share, delete, addtoPlaylist, songname, songgame1, album, artist, year, duration, location;
    ImageView albumart;
    private Song song;


    public SongDetailsFragment() {
        // Required empty public constructor
    }

    public static SongDetailsFragment newInstance(Long songid) {
        SongDetailsFragment fragment = new SongDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(Songparam, songid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Long s = getArguments().getLong(Songparam);
            song = UtilsSong.getSongById(s);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_details, container, false);
        albumart = view.findViewById(R.id.albumArt);
        share = view.findViewById(R.id.share);
        delete = view.findViewById(R.id.delete);
        addtoPlaylist = view.findViewById(R.id.addtoPlaylist);
        songname = view.findViewById(R.id.songName);
        album = view.findViewById(R.id.album);
        artist = view.findViewById(R.id.artist);
        duration = view.findViewById(R.id.duration);
        location = view.findViewById(R.id.location);
        year = view.findViewById(R.id.year);

//        bannerAd(view);

        songgame1 = view.findViewById(R.id.songName1);
        songgame1.setText(song.getTitle());
        songname.setText(song.getTitle());
        artist.setText(song.getArtist());
        album.setText(song.getAlbum());
        year.setText(String.valueOf(song.getYear()));
        duration.setText(DateUtils.formatElapsedTime(song.getDurationSeconds()));
        location.setText(song.getFilePath());
        Picasso.get().load(UtilsExtra.getUrifromAlbumID(song)).placeholder(R.drawable.logo).error(R.drawable.logo).into(albumart);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareSong(v);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSong(v);
            }
        });

        addtoPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtoPlaylist(v);
            }
        });
        return view;
    }

    private void deleteSong(View v) {

    }

    private void addtoPlaylist(View v) {

    }

    private void shareSong(View v) {

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
