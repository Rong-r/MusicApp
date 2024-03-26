package com.example.mymusicapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class HomeViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageViewCover;
    public TextView textViewTitle;
    public TextView textViewSinger;
    public LinearLayout linearLayout;
    public HomeViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewCover=itemView.findViewById(R.id.iv_list_cover);
        textViewTitle=itemView.findViewById(R.id.tv_list_song_title);
        textViewSinger=itemView.findViewById(R.id.tv_list_singer);
        linearLayout=itemView.findViewById(R.id.layout_item_view_list);
    }
}
