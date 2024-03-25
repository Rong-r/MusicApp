package com.example.mymusicapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    //private DrawerLayout drawerLayout;
    private ImageView imageView;
    private EditText editTextSearch;
    private ImageView imageViewPlay;
    private DatabaseHelper databaseHelper;
    private MediaPlayer mediaPlayer=new MediaPlayer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        editTextSearch=(EditText)findViewById(R.id.et_search);
        imageViewPlay=(ImageView)findViewById(R.id.iv_bottom_playing_play);
        databaseHelper=new DatabaseHelper(this,"SongsApp.db",null,1);
        //setContentView(R.layout.activity_main);
        //drawerLayout=(DrawerLayout) findViewById(R.id.layout_main);
        //imageView=(ImageView) findViewById(R.id.iv_user_info);
/*
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
 */

        //动态申请权限
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            databaseHelper.initSongsDB(this);
        }
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
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    databaseHelper.initSongsDB(this);
                } else {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
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
