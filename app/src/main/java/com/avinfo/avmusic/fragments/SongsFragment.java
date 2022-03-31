package com.avinfo.avmusic.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.avinfo.avmusic.Utils.UtilsData;
import com.avinfo.avmusic.playerMain.AppMain;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.avinfo.avmusic.Activities.ActivityPlayingList;
import com.avinfo.avmusic.Adapters.SongsAdapter;
import com.avinfo.avmusic.Utils.UtilsPlaylist;
import com.avinfo.avmusic.Utils.UtilsRV;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.SongData.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SongsFragment extends Fragment implements SongsAdapter.OnClickAction {

    SongsAdapter songsRecyclerViewAdapter;
    FloatingActionButton floatingActionButton;
    ActionMode actionMode;
    EditText name, search;
    List<Song> filtered = new ArrayList<>(AppMain.data.songs);
    LinearLayout noData;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    private NativeAdsManager mNativeAdsManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SongsFragment() {
    }


    //Action mode for selecting data and creating playlist.
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.selected, menu);
            Drawable drawable1 = menu.getItem(0).getIcon();
            Drawable drawable2 = menu.getItem(1).getIcon();
            drawable1.mutate();
            drawable2.mutate();
            if (!AppMain.settings.get("modes", "Day").equals("Day")) {
                drawable1.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                drawable2.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            } else {
                drawable1.setColorFilter(getResources().getColor(R.color.md_grey_900), PorterDuff.Mode.SRC_IN);
                drawable2.setColorFilter(getResources().getColor(R.color.md_grey_900), PorterDuff.Mode.SRC_IN);
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.selectAll:
                    selectAll();
                    Toast.makeText(getActivity(), songsRecyclerViewAdapter.getSelected().size() + " selected", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.deselectAll:
                    deselectAll();
                    Toast.makeText(getActivity(), songsRecyclerViewAdapter.getSelected().size() + " selected", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.addtoPlaylist:
                    showPlaylistDialog();
                    mode.finish();
                    return true;
                case R.id.Append:
                    Toast.makeText(getActivity(), " Not created this method yet", Toast.LENGTH_SHORT).show();
                    // mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            deselectAll();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        search = view.findViewById(R.id.searchSongs);
        noData = view.findViewById(R.id.noData);
        floatingActionButton = view.findViewById(R.id.fabplayAll);
        swipeRefreshLayout = view.findViewById(R.id.refreshSongs);
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);

        mNativeAdsManager = new NativeAdsManager(getContext(), UtilsData.FB_Native_AD, 5);
        mNativeAdsManager.loadAds();

        try {

            mNativeAdsManager.setListener(new NativeAdsManager.Listener() {
                @Override
                public void onAdsLoaded() {
                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), 1);
                    recyclerView.addItemDecoration(itemDecoration);
                    songsRecyclerViewAdapter = new SongsAdapter(getActivity(), AppMain.data.songs, mNativeAdsManager);
                    songsRecyclerViewAdapter.setHasStableIds(true);
                    songsRecyclerViewAdapter.setActionModeReceiver(this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setItemViewCacheSize(20);
                    recyclerView.setDrawingCacheEnabled(true);
                    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    recyclerView.setAdapter(songsRecyclerViewAdapter);
                    UtilsRV.makenoDataVisible(recyclerView, noData);
                }

                @Override
                public void onAdError(AdError adError) {

                }
            });

        }catch (Exception e){}

        try {

            if (!mNativeAdsManager.isLoaded()) {
//            Toast.makeText(context, "ADS Error: ", Toast.LENGTH_SHORT).show();
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), 1);
                recyclerView.addItemDecoration(itemDecoration);
                songsRecyclerViewAdapter = new SongsAdapter(getActivity(), AppMain.data.songs);
                songsRecyclerViewAdapter.setHasStableIds(true);
                songsRecyclerViewAdapter.setActionModeReceiver(this);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setHasFixedSize(true);
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                recyclerView.setAdapter(songsRecyclerViewAdapter);
                UtilsRV.makenoDataVisible(recyclerView, noData);
            }

        }catch (Exception e){
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), 1);
            recyclerView.addItemDecoration(itemDecoration);
            songsRecyclerViewAdapter = new SongsAdapter(getActivity(), AppMain.data.songs);
            songsRecyclerViewAdapter.setHasStableIds(true);
            songsRecyclerViewAdapter.setActionModeReceiver(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setAdapter(songsRecyclerViewAdapter);
            UtilsRV.makenoDataVisible(recyclerView, noData);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if (actionMode != null)
//                    actionMode.finish();

                AppMain.data.songs.clear();
                AppMain.data.updateSongs(getActivity(), "external");
                songsRecyclerViewAdapter.updateData(AppMain.data.songs);
                songsRecyclerViewAdapter.notifyDataSetChanged();
                UtilsRV.makenoDataVisible(recyclerView, noData);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filtered.clear();
                charSequence = charSequence.toString().toLowerCase();
                if (charSequence.length() == 0) {
                    filtered.addAll(AppMain.data.songs);
                } else
                    for (int j = 0; j < AppMain.data.songs.size(); j++) {
                        final Song song = AppMain.data.songs.get(j);
                        String name = song.getTitle();
                        if (name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(AppMain.data.songs.get(j));
                        }
                    }
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                Collections.sort(filtered, (u1, t1) -> u1.getTitle().compareToIgnoreCase(t1.getTitle()));
                songsRecyclerViewAdapter.updateData(filtered);
                songsRecyclerViewAdapter.notifyDataSetChanged();
                UtilsRV.makenoDataVisible(recyclerView, noData);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppMain.data.songs == null || AppMain.data.songs.size() <= 0) {
                    Toast.makeText(getActivity(), "No data to play", Toast.LENGTH_SHORT).show();
                    return;
                }
                AppMain.musicList.clear();
                AppMain.musicList.addAll(AppMain.data.songs);
                AppMain.nowPlayingList = AppMain.musicList;
                AppMain.musicService.setList(AppMain.nowPlayingList);
                AppMain.musicService.toggleShuffle();
                Intent intent = new Intent(context, ActivityPlayingList.class);
                intent.putExtra("playlistname", "All Songs");
                startActivity(intent);
            }
        });

        return view;
    }

    //Playlist dialog for creating Playlist through action mode
    private void showPlaylistDialog() {
        final ListView listView;
        Button create, cancel;
        ArrayList<Song> songArrayList = new ArrayList<>(songsRecyclerViewAdapter.getSelected());
        ArrayList<String> allPlaylists = UtilsPlaylist.getPlaylistNames();
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        @SuppressLint("InflateParams") View view = getActivity().getLayoutInflater().inflate(R.layout.newplaylistdialog, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_newplaylistdialog, allPlaylists);
        listView = view.findViewById(R.id.playlistListview);
        listView.setAdapter(arrayAdapter);
        name = view.findViewById(R.id.newPlaylistName);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String namenew = listView.getItemAtPosition(i).toString();
                name.setText(namenew);
            }
        });
        create = view.findViewById(R.id.createPlaylist);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty()) {
                    name.setError("cant be empty");
                    return;
                }
                AppMain.showProgressDialog(getActivity());
                UtilsPlaylist.newPlaylist(getActivity().getApplication(), "external", name.getText().toString(), songArrayList);
                Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                AppMain.hideProgressDialog();
                // deselectAll();
            }
        });
        cancel = view.findViewById(R.id.cancelPlaylist);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                deselectAll();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    public void selectAll() {
        songsRecyclerViewAdapter.selectAll();
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
            actionMode.setTitle("Selected: " + songsRecyclerViewAdapter.getSelected().size());
        }
    }

    public void deselectAll() {
        songsRecyclerViewAdapter.clearSelected();
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
    }

    public void onClickAction() {
        int selected = songsRecyclerViewAdapter.getSelected().size();
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
            assert actionMode != null;
            actionMode.setTitle("Selected: " + selected);
        } else {
            if (selected == 0) {
                actionMode.finish();
            } else {
                actionMode.setTitle("Selected: " + selected);
            }
        }
    }

}
