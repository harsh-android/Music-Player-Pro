package com.avinfo.avmusic.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avinfo.avmusic.Activities.ActivityPlayingList;
import com.avinfo.avmusic.Utils.UtilsExtra;
import com.avinfo.avmusic.Utils.UtilsSong;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.SongData.Song;
import com.avinfo.avmusic.playerMain.AppMain;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<String> mValues;

    public AlbumAdapter(List<String> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String selectedAlbum = mValues.get(position);
        holder.albumname.setText(mValues.get(position));
        List<Song> songsList = UtilsSong.getSongsByAlbum(selectedAlbum);
        Picasso.get().load(UtilsExtra.getUrifromAlbumID(songsList.get(0))).fit().centerCrop().error(R.mipmap.logo).placeholder(R.mipmap.logo).into(holder.albumart);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Song> songsList = UtilsSong.getSongsByAlbum(selectedAlbum);
                Context context = holder.mView.getContext();
                AppMain.musicList.clear();
                AppMain.musicList.addAll(songsList);
                AppMain.nowPlayingList = AppMain.musicList;
                AppMain.musicService.setList(AppMain.nowPlayingList);
                Intent intent = new Intent(context, ActivityPlayingList.class);
                intent.putExtra("playlistname", selectedAlbum);
                context.startActivity(intent);
            }
        });
    }

    public void updateData(List<String> items) {
        this.mValues = items;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView albumname;
        public final ImageView albumart;

        ViewHolder(View view) {
            super(view);
            mView = view;
            albumname = view.findViewById(R.id.AlbumName);
            albumart = view.findViewById(R.id.albumArt1);
        }

    }
}
