package com.avinfo.avmusic.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avinfo.avmusic.Activities.ActivitySetting;
import com.avinfo.avmusic.CustomViews.ColorView;
import com.avinfo.avmusic.R;
import com.avinfo.avmusic.settings.Theme;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.MyViewHolder> {

    private List<Theme> themeList;

    public ThemeAdapter(List<Theme> themeList) {
        this.themeList = themeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_theme, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Theme theme = themeList.get(position);
        holder.themeView.addColors(theme);

        if (ActivitySetting.selectedTheme == position) {
            holder.themeView.setActivated(true);
        } else {
            holder.themeView.setActivated(false);
        }

    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ColorView themeView;

        public MyViewHolder(View view) {
            super(view);
            themeView = view.findViewById(R.id.themeView);
            themeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivitySetting.selectedTheme = getAdapterPosition();
                    themeView.setActivated(true);
                    ThemeAdapter.this.notifyDataSetChanged();
                }
            });
        }

    }
}
