package com.example.mymusicapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageView imageViewInfo;
    @SuppressLint("StaticFieldLeak")
    private static ImageView imageViewPlay;
    private ImageView imageViewList;
    private ImageView imageViewUserIcon;
    private EditText editTextSearch;
    //private static DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewList;
    private final List<String> musicsPathsList=new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private LinearLayout linearLayoutUserLove;
    private LinearLayout linearLayoutUserCollect;
    private TextView textViewUser;
    private TextView textViewExit;
    @SuppressLint("StaticFieldLeak")
    private static TextView textViewBottomTitle;
    @SuppressLint("StaticFieldLeak")
    private static TextView textViewBottomSinger;
    @SuppressLint("StaticFieldLeak")
    private static ImageView imageViewBottomCover;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //databaseHelper=new DatabaseHelper(this,"SongsApp.db",null,1);
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
            //databaseHelper.initSongsDB(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DatabaseManager.getDatabaseManager().initDatabase();
                }
            }).start();
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
        imageViewBottomCover=(ImageView) findViewById(R.id.iv_bottom_playing_cover);
        imageViewUserIcon=(ImageView) findViewById(R.id.iv_user);
        recyclerViewList=(RecyclerView)findViewById(R.id.recyclerView_home_list);

        drawerLayout=(DrawerLayout) findViewById(R.id.layout_main);
        linearLayoutUserLove=(LinearLayout)findViewById(R.id.layout_user_loved);
        linearLayoutUserCollect=(LinearLayout)findViewById(R.id.layout_user_collected);
        textViewUser=(TextView)findViewById(R.id.tv_user_id);
        textViewExit=(TextView)findViewById(R.id.tv_exit_login);
        textViewBottomSinger=(TextView)findViewById(R.id.tv_bottom_playing_singer);
        textViewBottomTitle=(TextView)findViewById(R.id.tv_bottom_playing_song_title);

        //musicsPathsList=databaseHelper.getStoredMusicPath();
        recyclerViewList.setAdapter(new HomeAdapter(MainActivity.this,musicsPathsList,"all"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> tempList=DatabaseManager.getDatabaseManager().getMusicListAll();
                musicsPathsList.addAll(tempList);
                Message message=handler.obtainMessage();
//                Bundle bundle=new Bundle();
//                List<String> pathList=databaseHelper.getStoredMusicPath();
//                String allPathString=new String();
//                for(String item:pathList){
//                    allPathString=allPathString+";;;;;"+item;
//                }
//                bundle.putString("pathString",allPathString);
//                message.setData(bundle);
                message.what=1;
                handler.sendMessage(message);
            }
        }).start();

    }
    private final Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int getInt=msg.what;
            if(getInt==1){
                recyclerViewList.setAdapter(new HomeAdapter(MainActivity.this,musicsPathsList,"all"));
            }
//            Bundle bundle=msg.getData();
//            String getPaths=bundle.getString("pathString");
//            String[] paths=getPaths.split(";;;;;");
//            for(String item:paths){
//                musicsPathsList.add(item);
//            }
//            recyclerViewList.setAdapter(new HomeAdapter(MainActivity.this,musicsPathsList,"all"));
        }
    };


    

    private void initListener(){

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        String userName=sharedPreferences.getString("nowUser","");
        if(userName.isEmpty()){
            imageViewInfo.setImageResource(R.drawable.user);
        }else {
            imageViewInfo.setImageResource(R.drawable.user_icon_example);
        }
        imageViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userName.isEmpty()){
                    imageViewUserIcon.setImageResource(R.drawable.user);
                    imageViewInfo.setImageResource(R.drawable.user);
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
                    imageViewUserIcon.setImageResource(R.drawable.user_icon_example);
                    imageViewInfo.setImageResource(R.drawable.user_icon_example);
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
                if(imageViewPlay.getTag().equals("toPause")&&mediaPlayer.isPlaying()){
                    //要暂停
                    mediaPlayer.pause();
                    imageViewPlay.setImageResource(R.drawable.playing_to_play);
                    imageViewPlay.setTag("toPlay");

                }else if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    imageViewPlay.setImageResource(R.drawable.playing_to_pause);
                    imageViewPlay.setTag("toPause");
                }
            }
        });
        imageViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, ListActivity.class);
                String listName=imageViewList.getTag().toString();
                intent.putExtra("checkList",listName);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null){
            editor.putInt("nowCurrentDuration",mediaPlayer.getCurrentPosition());
            editor.putBoolean("nowIsPlaying",false);
            editor.putString("fromActivity","Restart");
            editor.apply();
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String fromActivity = sharedPreferences.getString("fromActivity", "");
        Log.d("TAG", "get fromActivity:" + fromActivity);
        if (fromActivity.equals("PlayActivity")) {
            String nowPath = sharedPreferences.getString("nowPath", "");
            int nowCurrentDuration = sharedPreferences.getInt("nowCurrentDuration", 0);
            String nowList = sharedPreferences.getString("nowList", "");
            Boolean nowIsPlaying = sharedPreferences.getBoolean("nowIsPlaying", false);
            if (!nowPath.isEmpty()) {
                setInfo(nowPath, nowIsPlaying);
                setMediaPlayer(nowPath, nowCurrentDuration,nowIsPlaying);
            }
            if (!nowList.isEmpty()) {
                imageViewList.setTag(nowList);
            }
        }

    }
    private void setMediaPlayer(String path,int currentDuration,Boolean isPlaying){
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.reset();//复位播放器
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();//播放准备
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.seekTo(currentDuration);
                    if(isPlaying){
                        mediaPlayer.start();//播放开始
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Handler handlerInfo=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();
            String path=bundle.getString("path");
            boolean isPlaying=bundle.getBoolean("isPlaying");
            String title=DatabaseManager.getDatabaseManager().getTitle(path);
            String singer=DatabaseManager.getDatabaseManager().getSinger(path);
            Bitmap cover=DatabaseManager.getDatabaseManager().getCover(path);
            textViewBottomTitle.setText(title);
            textViewBottomSinger.setText(singer);
            imageViewBottomCover.setImageBitmap(cover);

//            textViewBottomTitle.setText(databaseHelper.getTitle(path));
//            textViewBottomSinger.setText(databaseHelper.getSinger(path));
//            imageViewBottomCover.setImageBitmap(databaseHelper.getCover(path));

            if(isPlaying){
                imageViewPlay.setImageResource(R.drawable.playing_to_pause);
                imageViewPlay.setTag("toPause");
            }else {
                imageViewPlay.setImageResource(R.drawable.playing_to_play);
                imageViewPlay.setTag("toPlay");
            }
        }
    };
    private void setInfo(String path,Boolean isPlaying){
        //开启子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                //传递用户输入
                Message message=handlerInfo.obtainMessage();
                Bundle bundle=new Bundle();
                bundle.putString("path", path);
                bundle.putBoolean("isPlaying",isPlaying);
                if(isPlaying.equals(false)){
                    mediaPlayer.pause();
                }
                //再将bundle封装到msg消息对象中
                message.setData(bundle);
                handlerInfo.sendMessage(message);
            }
        }).start();
    }
}
