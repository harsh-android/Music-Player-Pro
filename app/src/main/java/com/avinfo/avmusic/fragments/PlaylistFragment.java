package com.avinfo.avmusic.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.avinfo.avmusic.Adapters.PlaylistAdapter;
import com.avinfo.avmusic.Utils.UtilsData;
import com.avinfo.avmusic.Utils.UtilsPlaylist;
import com.avinfo.avmusic.Utils.UtilsRV;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.playerMain.AppMain;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PlaylistFragment extends Fragment {

    PlaylistAdapter playlistRecyclerViewAdapter;
    LinearLayout noData;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaylistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        noData = view.findViewById(R.id.noData);
        refreshLayout = view.findViewById(R.id.refreshPlaylists);
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        playlistRecyclerViewAdapter = new PlaylistAdapter(getPlaylists());
        recyclerView.setAdapter(playlistRecyclerViewAdapter);
        UtilsRV.makenoDataVisible(recyclerView, noData);
        ArrayList<String> filtered = new ArrayList<>(getPlaylists());
        EditText search = view.findViewById(R.id.searchPlaylist);

//        bannerAd(view);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filtered.clear();
                charSequence = charSequence.toString().toLowerCase();
                if (charSequence.length() == 0) {
                    filtered.addAll(getPlaylists());
                } else
                    for (int j = 0; j < getPlaylists().size(); j++) {
                        String playlist = getPlaylists().get(j);
                        if (playlist.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(getPlaylists().get(j));
                        }
                    }
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                Collections.sort(filtered, String::compareToIgnoreCase);
                playlistRecyclerViewAdapter.UpdateData(filtered);
                playlistRecyclerViewAdapter.notifyDataSetChanged();
                UtilsRV.makenoDataVisible(recyclerView, noData);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPlaylists();
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


    private void refreshPlaylists() {
        AppMain.data.updatePlaylists(getActivity(), "external");
        playlistRecyclerViewAdapter.UpdateData(getPlaylists());
        playlistRecyclerViewAdapter.notifyDataSetChanged();
        UtilsRV.makenoDataVisible(recyclerView, noData);
        refreshLayout.setRefreshing(false);
    }

    private ArrayList<String> getPlaylists() {
        return UtilsPlaylist.getPlaylistNames();
    }

}
