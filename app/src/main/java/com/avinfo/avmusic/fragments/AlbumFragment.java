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

import com.avinfo.avmusic.Adapters.AlbumAdapter;
import com.avinfo.avmusic.Utils.UtilsData;
import com.avinfo.avmusic.Utils.UtilsRV;
import com.avinfo.avmusic.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class AlbumFragment extends Fragment {

    AlbumAdapter albumRecyclerViewAdapter;
    EditText search;
    List<String> filtered = new ArrayList<>();
    LinearLayout noData;
    SwipeRefreshLayout swipeRefreshLayout;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlbumFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        noData = view.findViewById(R.id.noData);
        swipeRefreshLayout = view.findViewById(R.id.refreshAlbums);

        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2)); //can change to create grid layout
        ArrayList<String> albums = UtilsData.getAlbums();
        albumRecyclerViewAdapter = new AlbumAdapter(albums);
        recyclerView.setAdapter(albumRecyclerViewAdapter);
        UtilsRV.makenoDataVisible(recyclerView, noData);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                albumRecyclerViewAdapter.updateData(albums);
                albumRecyclerViewAdapter.notifyDataSetChanged();
                UtilsRV.makenoDataVisible(recyclerView, noData);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        search = view.findViewById(R.id.searchAlbum);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filtered.clear();
                charSequence = charSequence.toString().toLowerCase();
                if (charSequence.length() == 0) {
                    filtered.addAll(albums);
                } else
                    for (int j = 0; j < albums.size(); j++) {
                        String genre = albums.get(j);
                        if (genre.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(albums.get(j));
                        }
                    }
                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                Collections.sort(filtered, String::compareToIgnoreCase);
                albumRecyclerViewAdapter.updateData(filtered);
                albumRecyclerViewAdapter.notifyDataSetChanged();
                UtilsRV.makenoDataVisible(recyclerView, noData);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

}
