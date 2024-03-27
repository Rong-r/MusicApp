package com.example.mymusicapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageView imageViewInfo;
    private ImageView imageViewPlay;
    private ImageView imageViewList;
    private EditText editTextSearch;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewList;
    private List<String> musicsPathsList=new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private LinearLayout linearLayoutUserLove;
    private LinearLayout linearLayoutUserCollect;
    private TextView textViewUser;
    private TextView textViewExit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper=new DatabaseHelper(this,"SongsApp.db",null,1);
        permission();
        initView();
        initListener();

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
    private void initView(){
        editTextSearch=(EditText)findViewById(R.id.et_main_search);
        imageViewPlay=(ImageView)findViewById(R.id.iv_bottom_playing_play);
        imageViewInfo=(ImageView) findViewById(R.id.iv_user_info);
        imageViewList=(ImageView) findViewById(R.id.iv_bottom_playing_list);
        recyclerViewList=(RecyclerView)findViewById(R.id.recyclerView_home_list);
        musicsPathsList=databaseHelper.getStoredMusicPath();
        recyclerViewList.setAdapter(new HomeAdapter(this,musicsPathsList));
        drawerLayout=(DrawerLayout) findViewById(R.id.layout_main);
        linearLayoutUserLove=(LinearLayout)findViewById(R.id.layout_user_loved);
        linearLayoutUserCollect=(LinearLayout)findViewById(R.id.layout_user_collected);
        textViewUser=(TextView)findViewById(R.id.tv_user_id);
        textViewExit=(TextView)findViewById(R.id.tv_exit_login);
    }
    private void initListener(){

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        imageViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=sharedPreferences.getString("nowUser","");
                if(userName.isEmpty()){
                    textViewUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    textViewExit.setAlpha(0.3F);
                    linearLayoutUserLove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this,"当前为游客模式，请先登录",Toast.LENGTH_SHORT).show();
                        }
                    });
                    linearLayoutUserCollect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this,"当前为游客模式，请先登录",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    textViewUser.setText(userName);
                    textViewExit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editor.putString("nowUser","");
                            editor.apply();
                            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    linearLayoutUserLove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(MainActivity.this, ListActivity.class);
                            intent.putExtra("checkList","loved");
                            startActivity(intent);
                        }
                    });
                    linearLayoutUserCollect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(MainActivity.this, ListActivity.class);
                            intent.putExtra("checkList","collected");
                            startActivity(intent);
                        }
                    });
                }
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
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
        imageViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("checkList","collected");
                startActivity(intent);
            }
        });
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
