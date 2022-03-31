package com.avinfo.avmusic.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avinfo.avmusic.Activities.ActivityPlayingList;
import com.avinfo.avmusic.Utils.UtilsExtra;
import com.avinfo.avmusic.Utils.UtilsSong;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.playerMain.AppMain;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private List<Long> songs;

    public RecentAdapter(List<Long> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_songs_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (getItemCount() > 0) {
            holder.songName.setText(UtilsSong.getSongById(songs.get(position)).getTitle());
            holder.songBy.setText(UtilsSong.getSongById(songs.get(position)).getArtist());
            Picasso.get().load(UtilsExtra.getUrifromAlbumID(UtilsSong.getSongById(songs.get(position)))).placeholder(R.mipmap.logo).placeholder(R.mipmap.logo).into(holder.circleImageView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = holder.mView.getContext();
                    AppMain.musicList.clear();
                    AppMain.musicList.add(UtilsSong.getSongById(songs.get(position)));
                    AppMain.nowPlayingList = AppMain.musicList;
                    AppMain.musicService.setList(AppMain.nowPlayingList);
                    Intent intent = new Intent(context, ActivityPlayingList.class);
                    intent.putExtra("playlistname", "Single Song");
                    context.startActivity(intent);

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (songs != null)
            return songs.size();
        else return 0;
    }

    public void updateData(List<Long> recentSongs) {
        this.songs = recentSongs;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView songName;
        public final TextView songBy;
        final CircleImageView circleImageView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            songName = view.findViewById(R.id.titleSongRecent);
            songBy = view.findViewById(R.id.artistSongRecent);
            circleImageView = view.findViewById(R.id.imageSongRecent);
        }

    }
}
