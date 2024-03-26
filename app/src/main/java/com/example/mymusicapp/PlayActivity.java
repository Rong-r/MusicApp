package com.example.mymusicapp;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public class PlayActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private ImageView imageViewPlaying;
    private ImageView imageViewBack;
    private ImageView imageViewLove;
    private ImageView imageViewCollect;
    private ImageView imageViewList;
    private ImageView imageViewCover;
    private static SeekBar seekBar;
    private static TextView textViewDuration;
    private static TextView textViewCurrentTime;
    private TextView textViewTitle;
    private TextView textViewSinger;
    private Intent intentCome;
    private Intent intentPost;
    private String filePath;
    private String fromList;
    private MusicService.MusicControl musicControl;
    private MyServiceConn conn;
    //记录服务是否被解绑，默认没有
    private boolean isUnbind =false;
    private ObjectAnimator animator;
    public static Handler handler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();//获取从子线程发送过来的音乐播放进度
            //获取当前进度currentPosition和总时长duration
            int duration=bundle.getInt("duration");
            int currentPosition=bundle.getInt("currentPosition");
            //对进度条进行设置
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            //歌曲是多少分钟多少秒钟
            int minute=duration/1000/60;
            int second=duration/1000%60;
            String strMinute=null;
            String strSecond=null;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+"";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+"";
            }
            //这里就显示了歌曲总时长
            textViewDuration.setText(strMinute+":"+strSecond);
            //歌曲当前播放时长
            minute=currentPosition/1000/60;
            second=currentPosition/1000%60;
            if(minute<10){//如果歌曲的时间中的分钟小于10
                strMinute="0"+minute;//在分钟的前面加一个0
            }else{
                strMinute=minute+" ";
            }
            if (second<10){//如果歌曲中的秒钟小于10
                strSecond="0"+second;//在秒钟前面加一个0
            }else{
                strSecond=second+" ";
            }
            //显示当前歌曲已经播放的时间
            textViewCurrentTime.setText(strMinute+":"+strSecond);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        databaseHelper=new DatabaseHelper(this,"SongsApp.db",null,1);
        intentCome=getIntent();
        fromList=intentCome.getStringExtra("musicList");
        Log.d("TAG",fromList);
        filePath=intentCome.getStringExtra("musicPath");
        Log.d("TAG",filePath);
        initView();
        intentPost=new Intent(this,MusicService.class);//创建服务连接对象
        conn=new MyServiceConn();//创建服务连接对象
        bindService(intentPost,conn,BIND_AUTO_CREATE);//绑定服务
        initInfo();
        initListener();



//注册监听
        //mediaPlayer.setOnBufferingUpdateListener();


    }

    private void initView(){
        imageViewPlaying=(ImageView) findViewById(R.id.iv_playing);
        imageViewLove=(ImageView) findViewById(R.id.iv_playing_love);
        imageViewCollect=(ImageView) findViewById(R.id.iv_playing_collect);
        imageViewBack=(ImageView)findViewById(R.id.iv_playing_back);
        imageViewList=(ImageView)findViewById(R.id.iv_playing_list);
        imageViewCover=(ImageView)findViewById(R.id.iv_playing_cover);
        seekBar=(SeekBar)findViewById(R.id.seekbar_playing);
        textViewCurrentTime=(TextView)findViewById(R.id.music_duration_played);
        textViewDuration=(TextView)findViewById(R.id.music_duration);
        textViewTitle=(TextView)findViewById(R.id.tv_playing_song_title);
        textViewSinger=(TextView)findViewById(R.id.tv_playing_singer);

        //rotation和0f,360.0f就设置了动画是从0°旋转到360°
        animator=ObjectAnimator.ofFloat(imageViewCover,"rotation",0f,360.0f);
        animator.setDuration(10000);//动画旋转一周的时间为10秒
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(-1);//-1表示设置动画无限循环
    }
    private void initListener(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //进当滑动条到末端时，结束动画
                if (i==seekBar.getMax()){
                    animator.pause();//停止播放动画
                }
            }
            //滑动条开始滑动时调用
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            //滑动条停止滑动时调用
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //根据拖动的进度改变音乐播放进度
                int progress=seekBar.getProgress();//获取seekBar的进度
                musicControl.seekTo(progress);//改变播放进度
            }
        });
        imageViewPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageViewPlaying.getTag().equals("toPause")){
                    //要暂停
                    musicControl.pausePlay();
                    imageViewPlaying.setImageResource(R.drawable.playing_to_play);
                    imageViewPlaying.setTag("toPlay");
                }else {
                    //要播放
                    if(musicControl.isFirstPlay==1){
                        musicControl.play(filePath);
                        musicControl.setIsFirstPlay(0);
                    }else {
                        musicControl.continuePlay();
                    }
                    imageViewPlaying.setImageResource(R.drawable.playing_to_pause);
                    imageViewPlaying.setTag("toPause");
                }
            }
        });
        imageViewLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageViewLove.getTag().equals("notLove")){
                    databaseHelper.setLoved(filePath);
                    imageViewLove.setImageResource(R.drawable.playing_loved);
                    imageViewLove.setTag("loved");
                }else {
                    databaseHelper.setLove(filePath);
                    imageViewLove.setImageResource(R.drawable.playing_love);
                    imageViewLove.setTag("notLove");
                }
            }
        });
        imageViewCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageViewCollect.getTag().equals("notCollect")){
                    databaseHelper.setCollected(filePath);
                    imageViewCollect.setImageResource(R.drawable.playing_collected);
                    imageViewCollect.setTag("collected");
                }else {
                    databaseHelper.setCollect(filePath);
                    imageViewCollect.setImageResource(R.drawable.playing_collect);
                    imageViewCollect.setTag("notCollect");
                }
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imageViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PlayActivity.this, ListActivity.class);
                intent.putExtra("checkList",fromList);
                startActivity(intent);
            }
        });

    }
    private void initInfo(){
        textViewTitle.setText(databaseHelper.getTitle(filePath));
        textViewSinger.setText(databaseHelper.getSinger(filePath));
        imageViewCover.setImageBitmap(databaseHelper.getCover(filePath));
    }
    //用于实现连接服务，比较模板化，不需要详细知道内容
    class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            musicControl=(MusicService.MusicControl) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    }
    //判断服务是否被解绑
    private void unbind(boolean isUnbind){
        //如果解绑了
        if(!isUnbind){
            musicControl.pausePlay();//音乐暂停播放
            unbindService(conn);//解绑服务
        }
    }
    protected void onDestroy(){
        super.onDestroy();
        unbind(isUnbind);//解绑服务
    }
}
