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

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.ViewHolder> {

    private List<String> mValues;

    public GenresAdapter(List<String> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_genre_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String selectedGenre = mValues.get(position);
        holder.genre.setText(selectedGenre);
        List<Song> songsList = UtilsSong.getSongsByGenre(selectedGenre);
        Picasso.get().load(UtilsExtra.getUrifromAlbumID(songsList.get(0))).fit().centerCrop().error(R.mipmap.logo).placeholder(R.mipmap.logo).into(holder.genreArt);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Song> songsList = UtilsSong.getSongsByGenre(selectedGenre);
                Context context = holder.mView.getContext();
                AppMain.musicList.clear();
                AppMain.musicList.addAll(songsList);
                AppMain.nowPlayingList = AppMain.musicList;
                AppMain.musicService.setList(AppMain.nowPlayingList);
                Intent intent = new Intent(context, ActivityPlayingList.class);
                intent.putExtra("playlistname", selectedGenre);
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
        final TextView genre;
        final ImageView genreArt;

        ViewHolder(View view) {
            super(view);
            mView = view;
            genre = view.findViewById(R.id.GenreName);
            genreArt = view.findViewById(R.id.albumArtGenre);
        }
    }
}
