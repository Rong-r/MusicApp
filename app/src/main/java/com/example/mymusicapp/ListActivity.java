package com.example.mymusicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListActivity extends Activity {
    private RecyclerView recyclerView;
    private TextView textViewListTitle;
    private ImageView imageViewBack;
    private ImageView imageViewListIcon;
    private Intent intent;
    private String listName;
    private List<String> musicList;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        databaseHelper=new DatabaseHelper(this,"SongsApp.db",null,1);
        intent=getIntent();
        listName=intent.getStringExtra("checkList");
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView_list);
        imageViewBack=(ImageView) findViewById(R.id.iv_search_back);
        imageViewListIcon=(ImageView) findViewById(R.id.iv_list_icon);
        textViewListTitle=(TextView)findViewById(R.id.tv_list_title);
        getList();
        recyclerView.setAdapter(new HomeAdapter(this,musicList));
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void getList(){
        if(listName.equals("collected")){
            musicList=databaseHelper.getCollectedMusicPath();
            textViewListTitle.setText("我的收藏");
            imageViewListIcon.setImageResource(R.drawable.playing_loved);
        } else if (listName.equals("loved")) {
            musicList=databaseHelper.getLovedMusicPath();
            textViewListTitle.setText("我的喜爱");
            imageViewListIcon.setImageResource(R.drawable.playing_collected);

        }else {
            musicList=databaseHelper.getStoredMusicPath();
            textViewListTitle.setText("全部歌曲");
        }
    }
}
