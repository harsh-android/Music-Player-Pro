package com.avinfo.avmusic.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avinfo.avmusic.Activities.ActivityPlayingList;
import com.avinfo.avmusic.Utils.UtilsExtra;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.SongData.Song;
import com.avinfo.avmusic.fragments.FileFragment;
import com.avinfo.avmusic.playerMain.AppMain;
import com.bumptech.glide.Glide;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdsManager;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private List<Song> songs;
    private List<Song> selected = new ArrayList<>();
    private OnClickAction receiver;
    Activity activity;
    Bitmap bitmap;

    private List<NativeAd> mAdItems;
    private NativeAdsManager mNativeAdsManager;
    private static final int AD_DISPLAY_FREQUENCY = 8;
    private static final int POST_TYPE = 0;
    private static final int AD_TYPE = 1;
    int aaa = 0;
    Song localItem = null;

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(songs.get(position).getTitle().charAt(0));
    }

    public void setActionModeReceiver(NativeAdsManager.Listener listener) {
        this.receiver = receiver;
    }

    public interface OnClickAction {
        void onClickAction();
    }

    public SongsAdapter(FragmentActivity activity, List<Song> items, NativeAdsManager nativeAdsManager) {
        aaa = 1;
        songs = items;
        this.activity = activity;
        mNativeAdsManager = nativeAdsManager;
        mAdItems = new ArrayList<>();
    }

    public SongsAdapter(FragmentActivity activity, List<Song> items) {
        aaa = 0;
        songs = items;
        this.activity = activity;
        mAdItems = new ArrayList<>();
    }


//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder( RecyclerView.ViewHolder parent, int viewType) {
//
//        if (viewType == AD_TYPE) {
//            NativeAdLayout inflatedView = (NativeAdLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_unit, parent, false);
//            return new AdHolder(inflatedView);
//        } else {
//            View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_songs_item, parent, false);
//            return new ViewHolder(inflatedView);
//        }
//
////        View view = LayoutInflater.from(parent.getContext())
////                .inflate(R.layout.fragment_songs_item, parent, false);
////        return new ViewHolder(view);
//    }


//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//
//    }


    @Override
    public int getItemViewType(int position) {
        return position % AD_DISPLAY_FREQUENCY == 0 ? AD_TYPE : POST_TYPE;
    }

    private void highlightView(ViewHolder holder) {
        holder.mView.setBackgroundColor(Color.parseColor("#C0C0C0"));
        holder.circleImageView.setImageResource(R.drawable.ic_check);
    }

    private void unhighlightView(ViewHolder holder, Bitmap draw) {
        holder.mView.setBackgroundColor(Color.TRANSPARENT);
        if (draw != null)
            holder.circleImageView.setImageBitmap(draw);
        else holder.circleImageView.setImageResource(R.mipmap.logo);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (aaa == 1) {
            if (viewType == AD_TYPE) {
                NativeAdLayout inflatedView = (NativeAdLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_unit, parent, false);
                return new AdHolder(inflatedView);
            } else {
                View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_songs_item, parent, false);
                return new ViewHolder(inflatedView);
            }
        } else {
            View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_songs_item, parent, false);
            return new ViewHolder(inflatedView);
        }
//        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (aaa == 1) {

            if (holder.getItemViewType() == AD_TYPE) {
                NativeAd ad = new NativeAd(activity, "YOUR_PLACEMENT_ID");

                if (mAdItems.size() > position / AD_DISPLAY_FREQUENCY) {
                    ad = mAdItems.get(position / AD_DISPLAY_FREQUENCY);
                } else {
                    ad = mNativeAdsManager.nextNativeAd();
                    if (!ad.isAdInvalidated()) {
                        mAdItems.add(ad);
                    } else {
                        Log.e(SongsAdapter.class.getSimpleName(), "Ad is invalidated!");
                    }
                }

                AdHolder adHolder = (AdHolder) holder;
                adHolder.adChoicesContainer.removeAllViews();

                if (ad != null) {

                    adHolder.tvAdTitle.setText(ad.getAdvertiserName());
                    adHolder.tvAdBody.setText(ad.getAdBodyText());
                    adHolder.tvAdSocialContext.setText(ad.getAdSocialContext());
                    adHolder.tvAdSponsoredLabel.setText(R.string.sponsored);
                    adHolder.btnAdCallToAction.setText(ad.getAdCallToAction());
                    adHolder.btnAdCallToAction.setVisibility(
                            ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                    AdOptionsView adOptionsView =
                            new AdOptionsView(activity, ad, adHolder.nativeAdLayout);
                    adHolder.adChoicesContainer.addView(adOptionsView, 0);

                    List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(adHolder.ivAdIcon);
                    clickableViews.add(adHolder.mvAdMedia);
                    clickableViews.add(adHolder.btnAdCallToAction);
                    ad.registerViewForInteraction(
                            adHolder.nativeAdLayout,
                            adHolder.mvAdMedia,
                            adHolder.ivAdIcon,
                            clickableViews);
                }

            } else {

                ViewHolder viewHolder = (ViewHolder) holder;
                final int index = position - (position / AD_DISPLAY_FREQUENCY) - 1;

                try {
                   localItem = songs.get(index);
                } catch (Exception e){}
                viewHolder.songName.setText(localItem.getTitle());
                viewHolder.songBy.setText(localItem.getArtist());
                viewHolder.songName.setSelected(true);
                Glide.with(activity).load(UtilsExtra.getUrifromAlbumID(localItem)).centerCrop().fitCenter().error(R.drawable.logo).placeholder(R.drawable.logo).into(viewHolder.circleImageView);
//        Picasso.get().load(UtilsExtra.getUrifromAlbumID(localItem)).centerCrop().fit().error(R.mipmap.logo).placeholder(R.mipmap.logo).into(holder.circleImageView);

                try {
                    bitmap = UtilsExtra.getBitmapfromAlbumId(viewHolder.mView.getContext(), localItem);
                } catch (Exception e) {
                }
                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (selected.contains(localItem)) {
                            selected.remove(localItem);
                            unhighlightView(viewHolder, bitmap);
                        } else {
                            selected.add(localItem);
                            highlightView(viewHolder);
                        }
                        try {
                            receiver.onClickAction();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                });
                if (selected.contains(localItem))
                    highlightView(viewHolder);
                else
                    unhighlightView(viewHolder, bitmap);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selected.isEmpty()) {
                            Context context = viewHolder.mView.getContext();
                            AppMain.musicList.clear();
                            AppMain.musicList.add(localItem);
                            AppMain.nowPlayingList = AppMain.musicList;
                            AppMain.musicService.setList(AppMain.nowPlayingList);
                            Intent intent = new Intent(context, ActivityPlayingList.class);
                            intent.putExtra("playlistname", "Single Song");
                            context.startActivity(intent);
                        } else {
                            if (selected.contains(localItem)) {
                                selected.remove(localItem);
                                unhighlightView(viewHolder, bitmap);
                            } else {
                                selected.add(localItem);
                                highlightView(viewHolder);
                            }
                            try {
                                receiver.onClickAction();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                if (selected.contains(localItem)) {
                    highlightView(viewHolder);
                } else {
                    unhighlightView(viewHolder, bitmap);
                }
            }

        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            final Song localItem = songs.get(position);
            viewHolder.songName.setText(localItem.getTitle());
            viewHolder.songBy.setText(localItem.getArtist());
            viewHolder.songName.setSelected(true);
            Glide.with(activity).load(UtilsExtra.getUrifromAlbumID(localItem)).centerCrop().fitCenter().error(R.drawable.logo).placeholder(R.drawable.logo).into(viewHolder.circleImageView);
//        Picasso.get().load(UtilsExtra.getUrifromAlbumID(localItem)).centerCrop().fit().error(R.mipmap.logo).placeholder(R.mipmap.logo).into(holder.circleImageView);

            try {
                bitmap = UtilsExtra.getBitmapfromAlbumId(viewHolder.mView.getContext(), localItem);
            } catch (Exception e) {
            }
            viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (selected.contains(localItem)) {
                        selected.remove(localItem);
                        unhighlightView(viewHolder, bitmap);
                    } else {
                        selected.add(localItem);
                        highlightView(viewHolder);
                    }
                    try {
                        receiver.onClickAction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            });
            if (selected.contains(localItem))
                highlightView(viewHolder);
            else
                unhighlightView(viewHolder, bitmap);

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selected.isEmpty()) {
                        Context context = viewHolder.mView.getContext();
                        AppMain.musicList.clear();
                        AppMain.musicList.add(localItem);
                        AppMain.nowPlayingList = AppMain.musicList;
                        AppMain.musicService.setList(AppMain.nowPlayingList);
                        Intent intent = new Intent(context, ActivityPlayingList.class);
                        intent.putExtra("playlistname", "Single Song");
                        context.startActivity(intent);
                    } else {
                        if (selected.contains(localItem)) {
                            selected.remove(localItem);
                            unhighlightView(viewHolder, bitmap);
                        } else {
                            selected.add(localItem);
                            highlightView(viewHolder);
                        }
                        try {
                            receiver.onClickAction();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            if (selected.contains(localItem)) {
                highlightView(viewHolder);
            } else {
                unhighlightView(viewHolder, bitmap);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if ((songs != null) && (!songs.isEmpty())) {

            if (aaa == 1) {
                return songs.size() + mAdItems.size();
            } else {
                return songs.size();
            }
        }

        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView songName;
        public final TextView songBy;
        final CircleImageView circleImageView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            songName = view.findViewById(R.id.songName);
            songBy = view.findViewById(R.id.songBy);
            circleImageView = view.findViewById(R.id.albumArt);
        }

    }

    private static class AdHolder extends RecyclerView.ViewHolder {

        NativeAdLayout nativeAdLayout;
        MediaView mvAdMedia;
        MediaView ivAdIcon;
        TextView tvAdTitle;
        TextView tvAdBody;
        TextView tvAdSocialContext;
        TextView tvAdSponsoredLabel;
        Button btnAdCallToAction;
        LinearLayout adChoicesContainer;

        AdHolder(NativeAdLayout adLayout) {
            super(adLayout);

            nativeAdLayout = adLayout;
            mvAdMedia = adLayout.findViewById(R.id.native_ad_media);
            tvAdTitle = adLayout.findViewById(R.id.native_ad_title);
            tvAdBody = adLayout.findViewById(R.id.native_ad_body);
            tvAdSocialContext = adLayout.findViewById(R.id.native_ad_social_context);
            tvAdSponsoredLabel = adLayout.findViewById(R.id.native_ad_sponsored_label);
            btnAdCallToAction = adLayout.findViewById(R.id.native_ad_call_to_action);
            ivAdIcon = adLayout.findViewById(R.id.native_ad_icon);
            adChoicesContainer = adLayout.findViewById(R.id.ad_choices_container);
        }
    }


    public void addAll(List<Song> items) {
        clearAll(false);
        this.selected = items;
        notifyDataSetChanged();
    }

    public void clearAll(boolean isNotify) {
        // items.clear();
        selected.clear();
        if (isNotify) notifyDataSetChanged();
    }

    public void clearSelected() {
        selected.clear();
        notifyDataSetChanged();
    }

    public void selectAll() {
        selected.clear();
        selected.addAll(songs);
        notifyDataSetChanged();
    }

    public void setActionModeReceiver(OnClickAction receiver) {
        this.receiver = receiver;
    }

    public void updateData(List<Song> list) {
        this.songs = list;
    }

    public List<Song> getSelected() {
        return selected;
    }
}
