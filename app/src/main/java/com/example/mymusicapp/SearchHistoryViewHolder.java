package com.example.mymusicapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewTitle;
    public ImageView imageViewDelete;
    public SearchHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewTitle=itemView.findViewById(R.id.tv_search_history_song_title);
        imageViewDelete=itemView.findViewById(R.id.iv_search_history_delete);


    }
}
