package com.example.mymusicapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity {
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private TextView textViewListTitle;
    private List<String> musicsPathsList;
    private ImageView imageViewBack;
    private ImageView imageViewListIcon;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        databaseHelper=new DatabaseHelper(this,"SongsApp.db",null,1);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView_list);
        imageViewBack=(ImageView) findViewById(R.id.iv_search_back);
        imageViewListIcon=(ImageView) findViewById(R.id.iv_list_icon);
        textViewListTitle=(TextView)findViewById(R.id.tv_list_title);
        musicsPathsList=getList();
        recyclerView.setAdapter(new HomeAdapter(this,musicsPathsList));
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private List<String> getList(){
        String checkList=sharedPreferences.getString("checkList","all");
        List<String> list=new ArrayList<String>();
        if(checkList.equals("collected")){
            textViewListTitle.setText("我的收藏");
            imageViewListIcon.setImageResource(R.drawable.playing_loved);
            list=databaseHelper.getCollectedMusicPath();
        } else if (checkList.equals("loved")) {
            textViewListTitle.setText("我的喜爱");
            imageViewListIcon.setImageResource(R.drawable.playing_collected);
            list=databaseHelper.getLovedMusicPath();
        }else {
            textViewListTitle.setText("全部歌曲");
            list=databaseHelper.getStoredMusicPath();
        }
        return list;
    }
}
