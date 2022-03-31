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
import com.avinfo.avmusic.Utils.UtilsRecent;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.SongData.Song;
import com.avinfo.avmusic.playerMain.AppMain;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MostPlayedAdapter extends RecyclerView.Adapter<MostPlayedAdapter.ViewHolder> {

    private List<Song> songs;
    private Context context;

    public MostPlayedAdapter(Context context, List<Song> songs) {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.most_played_songs_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (getItemCount() != 0) {
            holder.songName.setText(songs.get(position).getTitle());
            holder.songBy.setText(songs.get(position).getArtist());
            Picasso.get().load(UtilsExtra.getUrifromAlbumID(songs.get(position))).placeholder(R.mipmap.logo).error(R.mipmap.logo).into(holder.imageView);
            holder.TimesPlayed.setText(String.valueOf(UtilsRecent.getcountSongsPlayed(context, songs.get(position))));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                AppMain.musicList.clear();
                AppMain.musicList.add(songs.get(position));
                AppMain.nowPlayingList = AppMain.musicList;
                AppMain.musicService.setList(AppMain.nowPlayingList);
                Intent intent = new Intent(context, ActivityPlayingList.class);
                intent.putExtra("playlistname", "Single Song");
                context.startActivity(intent);

            }
        });


    }


    @Override
    public int getItemCount() {
        if (songs != null)
            return songs.size();
        else return 0;
    }

    public void updateData(List<Song> mostPlayedSongs) {
        this.songs = mostPlayedSongs;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView songName;
        public final TextView TimesPlayed;
        public final TextView songBy;
        final ImageView imageView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            songName = view.findViewById(R.id.mostPlayedSongName);
            songBy = view.findViewById(R.id.mostPlayedArtistName);
            imageView = view.findViewById(R.id.mostplayedAlbumArt);
            TimesPlayed = view.findViewById(R.id.mostPlayedCount);
        }

    }
}
