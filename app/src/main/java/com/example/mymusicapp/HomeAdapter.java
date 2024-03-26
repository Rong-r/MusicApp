package com.example.mymusicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> {
    private Context mContext;
    private List<String> MusicsPathsListInDB;
    public HomeAdapter(Context context,List<String> musicPaths){
        mContext=context;
        MusicsPathsListInDB=musicPaths;
    }
    DatabaseHelper helper = new DatabaseHelper(mContext,"SongsApp.db",null,1);
    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载item布局
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_song_list,parent,false);
        return new HomeViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String musicPath=MusicsPathsListInDB.get(position);
        holder.textViewTitle.setText(helper.getTitle(musicPath));
        holder.textViewSinger.setText(helper.getSinger(musicPath));
        holder.imageViewCover.setImageBitmap(helper.getCover(musicPath));
        holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlayActivity.class);
                intent.putExtra("musicPath",musicPath);
                intent.putExtra("musicList","all");
                view.getContext().startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return MusicsPathsListInDB.size();
    }
    public void sendMessage(){

    }
}
