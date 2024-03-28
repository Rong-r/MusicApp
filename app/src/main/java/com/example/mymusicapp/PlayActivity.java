package com.example.mymusicapp;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
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

import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends Activity {
    private ImageView imageViewPlaying;
    @SuppressLint("StaticFieldLeak")
    private static ImageView imageViewLove;
    @SuppressLint("StaticFieldLeak")
    private static ImageView imageViewCollect;
    @SuppressLint("StaticFieldLeak")
    private static ImageView imageViewCover;
    @SuppressLint("StaticFieldLeak")
    private static SeekBar seekBar;
    @SuppressLint("StaticFieldLeak")
    private static TextView textViewDuration,textViewCurrentTime,textViewTitle,textViewSinger;
    private String filePath,fromList;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private ObjectAnimator animator;
    private SharedPreferences sharedPreferences;
    private final DatabaseManager databaseManager=DatabaseManager.getDatabaseManager();
    public static Handler handler=new Handler(Looper.getMainLooper()){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();//获取从子线程发送过来的音乐播放进度
            //获取当前进度currentPosition
            int currentPosition=bundle.getInt("currentPosition");
            //对进度条进行设置
            seekBar.setProgress(currentPosition);
            //歌曲当前播放时长
            int minute=currentPosition/1000/60;
            int second=currentPosition/1000%60;
            String strMinute=null;
            String strSecond=null;
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
    public static Handler handlerDuration=new Handler(Looper.getMainLooper()){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();//获取从子线程发送过来的音乐播放进度
            //获取总时长duration
            int duration=bundle.getInt("duration");
            //对进度条进行设置
            seekBar.setMax(duration);
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
        }
    };
    public static Handler handlerInfo=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();

            String nowUser=bundle.getString("nowUser");
            String title=bundle.getString("title");
            String singer=bundle.getString("singer");
            int isLoved=bundle.getInt("isLoved");
            int isCollected=bundle.getInt("isCollected");

            textViewTitle.setText(title);
            textViewSinger.setText(singer);

            if(isLoved==1&&nowUser!=null){
                imageViewLove.setImageResource(R.drawable.playing_loved);
                imageViewLove.setTag("loved");
            }else {
                imageViewLove.setImageResource(R.drawable.playing_love);
                imageViewLove.setTag("notLove");
            }
            if(isCollected==1&&nowUser!=null){
                imageViewCollect.setImageResource(R.drawable.playing_collected);
                imageViewCollect.setTag("collected");
            }else {
                imageViewCollect.setImageResource(R.drawable.playing_collect);
                imageViewCollect.setTag("notCollect");
            }
        }
    };
    private void setMediaPlayer(String path){
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
                    setDuration();
                    addTimer();
                    mediaPlayer.start();//播放开始
                    animator.start();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Intent intentCome = getIntent();
        imageViewPlaying=(ImageView) findViewById(R.id.iv_playing);
        ImageView imageViewLast = (ImageView) findViewById(R.id.iv_playing_last);
        ImageView imageViewNext = (ImageView) findViewById(R.id.iv_playing_next);
        imageViewLove=(ImageView) findViewById(R.id.iv_playing_love);
        imageViewCollect=(ImageView) findViewById(R.id.iv_playing_collect);
        ImageView imageViewBack = (ImageView) findViewById(R.id.iv_playing_back);
        ImageView imageViewList = (ImageView) findViewById(R.id.iv_playing_list);
        imageViewCover=(ImageView)findViewById(R.id.iv_playing_cover);
        seekBar=(SeekBar)findViewById(R.id.seekbar_playing);
        textViewCurrentTime=(TextView)findViewById(R.id.music_duration_played);
        textViewDuration=(TextView)findViewById(R.id.music_duration);
        textViewTitle=(TextView)findViewById(R.id.tv_playing_song_title);
        textViewSinger=(TextView)findViewById(R.id.tv_playing_singer);

        fromList= intentCome.getStringExtra("checkList");

        filePath= intentCome.getStringExtra("musicPath");
        setInfo(filePath);
        setMediaPlayer(filePath);

        //rotation和0f,360.0f就设置了动画是从0°旋转到360°
        animator=ObjectAnimator.ofFloat(imageViewCover,"rotation",0f,360.0f);
        animator.setDuration(10000);//动画旋转一周的时间为10秒
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(-1);//-1表示设置动画无限循环

        imageViewPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageViewPlaying.getTag().equals("toPause")){
                    if(mediaPlayer.isPlaying()){
                        //要暂停
                        mediaPlayer.pause();
                        animator.pause();
                    }
                    imageViewPlaying.setImageResource(R.drawable.playing_to_play);
                    imageViewPlaying.setTag("toPlay");
                }else {
                    if(!mediaPlayer.isPlaying()){
                        //要播放
                        mediaPlayer.start();
                        animator.start();
                    }
                    imageViewPlaying.setImageResource(R.drawable.playing_to_pause);
                    imageViewPlaying.setTag("toPause");
                }
            }
        });
        imageViewLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                animator.pause();
                String newFilePath=databaseManager.getLastFilePath(filePath,fromList);
                Log.d("TAG","newFilePath: "+newFilePath);
                filePath=newFilePath;
                Log.d("TAG","filePathNew: "+newFilePath);
                onResume();
            }
        });
        imageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                animator.pause();
                String newFilePath=databaseManager.getNextFilePath(filePath,fromList);
                Log.d("TAG","newFilePath: "+newFilePath);
                filePath=newFilePath;
                Log.d("TAG","filePathNew: "+filePath);
                onResume();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //进当滑动条到末端时，结束动画
                if (i==seekBar.getMax()){
                    animator.pause();//停止播放动画
                    mediaPlayer.pause();
                    animator.pause();
                    String newFilePath=databaseManager.getNextFilePath(filePath,fromList);
                    Log.d("TAG","newFilePath: "+newFilePath);
                    filePath=newFilePath;
                    Log.d("TAG","filePathNew: "+filePath);
                    onResume();
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
                mediaPlayer.seekTo(progress);//改变播放进度
            }
        });

        imageViewLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nowUser=sharedPreferences.getString("nowUser","");
                if(imageViewLove.getTag().equals("notLove")&&!nowUser.isEmpty()){
                    databaseManager.setLoved(filePath);
                    //databaseHelper.setLoved(filePath);
                    imageViewLove.setImageResource(R.drawable.playing_loved);
                    imageViewLove.setTag("loved");
                }else if(!nowUser.isEmpty()){
                    databaseManager.setLove(filePath);
                    //databaseHelper.setLove(filePath);
                    imageViewLove.setImageResource(R.drawable.playing_love);
                    imageViewLove.setTag("notLove");
                }
                else {
                    Toast.makeText(PlayActivity.this,"当前为游客模式，请先登录",Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageViewCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageViewCollect.getTag().equals("notCollect")){
                    databaseManager.setCollected(filePath);
                    //databaseHelper.setCollected(filePath);
                    imageViewCollect.setImageResource(R.drawable.playing_collected);
                    imageViewCollect.setTag("collected");
                }else {
                    databaseManager.setCollect(filePath);
                    //databaseHelper.setCollect(filePath);
                    imageViewCollect.setImageResource(R.drawable.playing_collect);
                    imageViewCollect.setTag("notCollect");
                }
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences=sharedPreferences= PreferenceManager.getDefaultSharedPreferences(PlayActivity.this);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("nowPath",filePath);
                editor.putInt("nowCurrentDuration",mediaPlayer.getCurrentPosition());
                editor.putString("nowList",fromList);
                editor.putString("fromActivity","PlayActivity");
                editor.putBoolean("nowIsPlaying", mediaPlayer.isPlaying());
                editor.apply();
                Log.d("TAG","post nowIsPlaying:"+mediaPlayer.isPlaying());
                mediaPlayer.stop();

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

    @Override
    protected void onResume() {
        super.onResume();
        setInfo(filePath);
        setMediaPlayer(filePath);
    }

    //添加计时器用于设置音乐播放器中的播放进度条
    public void addTimer(){
        //如果timer不存在，也就是没有引用实例
        if(timer==null){
            //创建计时器对象
            timer=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    if (mediaPlayer==null) return;
                    int currentPosition=mediaPlayer.getCurrentPosition();//获取播放进度
                    Message msg= handler.obtainMessage();//创建消息对象
                    //将音乐的总时长和播放进度封装至bundle中
                    Bundle bundle=new Bundle();
                    bundle.putInt("currentPosition",currentPosition);
                    //再将bundle封装到msg消息对象中
                    msg.setData(bundle);
                    //最后将消息发送到主线程的消息队列
                    handler.sendMessage(msg);
                }
            };
            //开始计时任务后的5毫秒，第一次执行task任务，以后每1000毫秒（1s）执行一次
            timer.schedule(task,5,1000);
        }
    }
    private void setInfo(String path){
        //开启子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                //传递用户输入
                String title=databaseManager.getTitle(path);
                String singer=databaseManager.getSinger(path);
                Bitmap cover=databaseManager.getCover(path);
                imageViewCover.setImageBitmap(cover);
                int isLoved=databaseManager.isLoved(path);
                int isCollected=databaseManager.isCollected(path);
                String nowUser=sharedPreferences.getString("nowUser","");
                Message message=handlerInfo.obtainMessage();
                Bundle bundle=new Bundle();
                bundle.putString("nowUser",nowUser);
                bundle.putString("title",title);
                bundle.putString("singer",singer);
                bundle.putInt("isLoved",isLoved);
                bundle.putInt("isCollected",isCollected);
                //再将bundle封装到msg消息对象中
                message.setData(bundle);
                handlerInfo.sendMessage(message);
            }
        }).start();
    }
    private void setDuration(){
        //开启子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer==null) return;
                int duration=mediaPlayer.getDuration();//获取歌曲总时长
                Message msg= handlerDuration.obtainMessage();//创建消息对象
                //将音乐的总时长封装至bundle中
                Bundle bundle=new Bundle();
                bundle.putInt("duration",duration);
                //再将bundle封装到msg消息对象中
                msg.setData(bundle);
                //最后将消息发送到主线程的消息队列
                handlerDuration.sendMessage(msg);
            }
        }).start();
    }
}
