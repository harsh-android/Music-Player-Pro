package com.avinfo.avmusic.Utils;

import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

public class UtilsRV {
    public static void makenoDataVisible(RecyclerView recyclerView, LinearLayout noData) {
        if (recyclerView.getAdapter().getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
        }

    }
}
