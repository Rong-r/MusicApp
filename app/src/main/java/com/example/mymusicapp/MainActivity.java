package com.example.mymusicapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageView imageView;
    private EditText editTextSearch;
    private ImageView imageViewPlay;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewList;
    private List<String> musicsPathsList;
    private MediaPlayer mediaPlayer=new MediaPlayer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper=new DatabaseHelper(this,"SongsApp.db",null,1);
        editTextSearch=(EditText)findViewById(R.id.et_main_search);
        imageViewPlay=(ImageView)findViewById(R.id.iv_bottom_playing_play);
        recyclerViewList=(RecyclerView)findViewById(R.id.recyclerView_home_list);
        musicsPathsList=databaseHelper.getStoredMusicPath();
        recyclerViewList.setAdapter(new HomeAdapter(this,musicsPathsList));
        drawerLayout=(DrawerLayout) findViewById(R.id.layout_main);
        imageView=(ImageView) findViewById(R.id.iv_user_info);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        permission();
        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    editTextSearch.setText("");
                    Intent intent=new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
            }
        });
        imageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageViewPlay.getTag().equals("toPause")){
                    //要暂停
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                    imageViewPlay.setImageResource(R.drawable.playing_to_play);
                    imageViewPlay.setTag("toPlay");
                }else {
                    //要播放
                    if(!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                    }
                    imageViewPlay.setImageResource(R.drawable.playing_to_pause);
                    imageViewPlay.setTag("toPause");
                }
            }
        });
    }
    private void permission(){
        if(checkSelfPermission(android.Manifest.permission.READ_MEDIA_AUDIO)!= PackageManager.PERMISSION_GRANTED||
                checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED||
                checkSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO},200);
        }else {
            Toast.makeText(this,"SD权限已被授予",Toast.LENGTH_SHORT).show();
            databaseHelper.initSongsDB(this);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==200){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"SD权限申请成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"SD权限申请被拒绝",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

}
