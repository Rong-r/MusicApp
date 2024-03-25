package com.example.mymusicapp;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class PlayActivity extends Activity {
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private ImageView imageViewPlaying;
    private ImageView imageViewLove;
    private ImageView imageViewCollect;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        imageViewPlaying=(ImageView) findViewById(R.id.iv_playing);
        imageViewLove=(ImageView) findViewById(R.id.iv_playing_love);
        imageViewCollect=(ImageView) findViewById(R.id.iv_playing_collect);

        imageViewPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageViewPlaying.getTag().equals("toPause")){
                    //要暂停
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                    imageViewPlaying.setImageResource(R.drawable.playing_to_play);
                    imageViewPlaying.setTag("toPlay");
                }else {
                    //要播放
                    if(!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
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
                    /**
                     * 获取歌曲信息，修改数据库
                     */
                    imageViewLove.setImageResource(R.drawable.playing_loved);
                    imageViewLove.setTag("loved");
                }else {

                    imageViewLove.setImageResource(R.drawable.playing_love);
                    imageViewLove.setTag("notLove");
                }
            }
        });
        imageViewCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageViewCollect.getTag().equals("notCollect")){
                    /**
                     * 获取歌曲信息，修改数据库
                     */
                    imageViewCollect.setImageResource(R.drawable.playing_collected);
                    imageViewCollect.setTag("collected");
                }else {

                    imageViewCollect.setImageResource(R.drawable.playing_collect);
                    imageViewCollect.setTag("notCollect");
                }
            }
        });
    }
}
