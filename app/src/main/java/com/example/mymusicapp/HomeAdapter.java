package com.example.mymusicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;


public class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> {
    private Context context;
    private List<File> MusicsListInDB;
    DatabaseHelper helper = new DatabaseHelper(context,"SongsApp.db",null,1);


    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载item布局
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_song_list,parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, @SuppressLint("RecyclerView") int position) {
     /*   holder.textViewTitle.setText();
        holder.textViewSinger.setText();

        holder.imageViewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击播放同一首
                Intent intent = new Intent(view.getContext(), PlayActivity.class);
                view.getContext().startActivity(intent);
            }
        });

      */


    }

    @Override
    public int getItemCount() {
        return MusicsListInDB.size();
    }

    public HomeAdapter(List<File> musicList){
        MusicsListInDB=musicList;
    }


}
