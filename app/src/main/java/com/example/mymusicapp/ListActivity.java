package com.example.mymusicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity {
    private static final String tag="TAG-ListActivity";
    private RecyclerView recyclerView;
    private TextView textViewListTitle;
    private ImageView imageViewListIcon;
    private List<String> musicList;
    private final DatabaseManager databaseManager=DatabaseManager.getDatabaseManager();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent intent = getIntent();
        String listName= intent.getStringExtra("checkList");
        if(listName==null){
            listName="all";
        }
        Log.d(tag,"getListName: "+listName);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView_list);
        ImageView imageViewBack = (ImageView) findViewById(R.id.iv_search_back);
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
            listToShow.addAll(databaseManager.getMusicListCollected());
            textViewListTitle.setText("我的收藏");
            imageViewListIcon.setImageResource(R.drawable.playing_collected);
        } else if (listName.equals("loved")) {
            listToShow.addAll(databaseManager.getMusicListLoved());
            textViewListTitle.setText("我的喜爱");
            imageViewListIcon.setImageResource(R.drawable.playing_loved);
        }else {
            listToShow.addAll(databaseManager.getMusicListAll());
            textViewListTitle.setText("全部歌曲");
        }
        return listToShow;
    }
}
