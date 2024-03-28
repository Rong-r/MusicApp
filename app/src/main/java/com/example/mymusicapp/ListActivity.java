package com.example.mymusicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity {
    private static String tag="TAG-ListActivity";
    private RecyclerView recyclerView;
    private TextView textViewListTitle;
    private ImageView imageViewBack;
    private ImageView imageViewListIcon;
    private List<String> musicList;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        databaseHelper=new DatabaseHelper(this,"SongsApp.db",null,1);
        Intent intent = getIntent();
        String listName= intent.getStringExtra("checkList");
        if(listName.isEmpty()){
            listName="all";
        }
        Log.d(tag,"getListName: "+listName);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView_list);
        imageViewBack=(ImageView) findViewById(R.id.iv_search_back);
        imageViewListIcon=(ImageView) findViewById(R.id.iv_list_icon);
        textViewListTitle=(TextView)findViewById(R.id.tv_list_title);
        //开启子线程
        String finalListName = listName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                musicList=getList(finalListName);
                recyclerView.setAdapter(new HomeAdapter(ListActivity.this,musicList, finalListName));
            }
        }).start();

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private List<String> getList(String listName){
        List<String> listToShow=new ArrayList<>();
        if(listName.equals("collected")){
            listToShow=databaseHelper.getCollectedMusicPath();
            textViewListTitle.setText("我的收藏");
            imageViewListIcon.setImageResource(R.drawable.playing_loved);
        } else if (listName.equals("loved")) {
            listToShow=databaseHelper.getLovedMusicPath();
            textViewListTitle.setText("我的喜爱");
            imageViewListIcon.setImageResource(R.drawable.playing_collected);
        }else {
            listToShow=databaseHelper.getStoredMusicPath();
            textViewListTitle.setText("全部歌曲");
        }
        return listToShow;
    }
}
