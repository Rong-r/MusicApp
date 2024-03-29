package com.example.mymusicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> {
    private static final String tag="TAG-HomeAdapter";
    private final List<String> MusicsPathsListInDB;
    private final String mListName;
    private final DatabaseManager databaseManager=DatabaseManager.getDatabaseManager();
    public HomeAdapter(Context context, List<String> musicPaths,String listName){
        MusicsPathsListInDB=musicPaths;
        mListName=listName;
    }
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
        holder.textViewTitle.setText(databaseManager.getTitle(musicPath));
        holder.textViewSinger.setText(databaseManager.getSinger(musicPath));
        Bitmap bitmap = databaseManager.getCover(musicPath);
        if(bitmap != null){
            holder.imageViewCover.setImageBitmap(bitmap);
        }
        holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlayActivity.class);
                intent.putExtra("musicPath",musicPath);
                intent.putExtra("checkList",mListName);
                Log.d(tag,"postMusicPath: "+musicPath);
                Log.d(tag,"postListName: "+mListName);
                view.getContext().startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return MusicsPathsListInDB.size();
    }
}