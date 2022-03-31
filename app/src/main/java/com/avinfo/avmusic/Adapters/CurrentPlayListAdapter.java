package com.avinfo.avmusic.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avinfo.avmusic.Activities.ActivityPlayingList;
import com.avinfo.avmusic.Utils.UtilsExtra;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.SongData.Song;
import com.avinfo.avmusic.Visualizers.barVisuals;
import com.avinfo.avmusic.playerMain.AppMain;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentPlayListAdapter extends RecyclerView.Adapter<CurrentPlayListAdapter.ViewHolder> {

    private final List<Song> mValues;
    Activity activity;

    public CurrentPlayListAdapter(ActivityPlayingList playingNowList, List<Song> items) {
        mValues = items;
        activity = playingNowList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_songs_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Song localItem = mValues.get(position);
        holder.songName.setText(localItem.getTitle());
        holder.songBy.setText(localItem.getArtist());
        holder.songName.setSelected(true);
        Picasso.get().load(UtilsExtra.getUrifromAlbumID(localItem)).centerCrop().fit().error(R.mipmap.logo).into(holder.circleImageView);

        if (AppMain.mainMenuHasNowPlayingItem) {
            if (AppMain.musicService.currentSong.getTitle().equals(localItem.getTitle())) {
                holder.barVisuals.setVisibility(View.VISIBLE);
                holder.barVisuals.setColor(ContextCompat.getColor(holder.barVisuals.getContext(), R.color.accent));
                holder.barVisuals.setDensity(1);
                holder.barVisuals.setPlayer(AppMain.musicService.getAudioSession());
            } else {
                holder.barVisuals.setVisibility(View.GONE);
            }
        } else {
            holder.barVisuals.setVisibility(View.GONE);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppMain.musicService.setSong(holder.getAdapterPosition());
                AppMain.musicService.playSong();
            }
        });
        holder.songOptions.setImageDrawable(UtilsExtra.getThemedIcon(holder.mView.getContext(), holder.mView.getContext().getDrawable(R.drawable.ic_3dots)));
        holder.songOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(view.getContext(), holder.songOptions);
                popup.inflate(R.menu.song_options);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.one:
                            if (AppMain.musicService.currentSongPosition == holder.getAdapterPosition()) {
                                if (AppMain.nowPlayingList.size() == 1) {
                                    AppMain.musicService.stopMusicPlayer();
                                    return true;
                                }
                                AppMain.musicService.next(true);
                                AppMain.musicService.playSong();
                            }
                            AppMain.nowPlayingList.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeChanged(holder.getAdapterPosition(), mValues.size());
                            return true;
                        /*case R.id.two:
                            //handle menu2 click
                            return true;
                        case R.id.three:
                            //handle menu3 click
                            return true;*/
                        default:
                            return false;
                    }
                });
                popup.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView songName;
        public final TextView songBy;
        final View mView;
        final CircleImageView circleImageView;
        barVisuals barVisuals;
        ImageView songOptions;

        ViewHolder(View view) {
            super(view);
            mView = view;
            songName = view.findViewById(R.id.songName);
            songBy = view.findViewById(R.id.songBy);
            circleImageView = view.findViewById(R.id.albumArt);
            barVisuals = view.findViewById(R.id.barvisuals);
            songOptions = view.findViewById(R.id.songOptions);
        }
    }
}