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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    List<String> mValues;

    public ArtistAdapter(List<String> names) {
        mValues = names;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_artist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String selectedArtist = mValues.get(position);
        holder.artistname.setText(selectedArtist);
        List<Song> songsList = UtilsSong.getSongsByArtist(selectedArtist);
        Picasso.get().load(UtilsExtra.getUrifromAlbumID(songsList.get(0))).fit().centerCrop().error(R.mipmap.logo).placeholder(R.mipmap.logo).into(holder.albumart);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Song> songsList = UtilsSong.getSongsByArtist(selectedArtist);
                Context context = holder.mView.getContext();
                AppMain.musicList.clear();
                AppMain.musicList.addAll(songsList);
                AppMain.nowPlayingList = AppMain.musicList;
                AppMain.musicService.setList(AppMain.nowPlayingList);
                Intent intent = new Intent(context, ActivityPlayingList.class);
                intent.putExtra("playlistname", "Songs by " + selectedArtist);
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
        public final TextView artistname;
        public final ImageView albumart;

        ViewHolder(View view) {
            super(view);
            mView = view;
            artistname = view.findViewById(R.id.ArtistName);
            albumart = view.findViewById(R.id.albumArtArtist);
        }

    }
}
