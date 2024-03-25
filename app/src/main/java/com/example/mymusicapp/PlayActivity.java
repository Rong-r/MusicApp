package com.example.mymusicapp;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import okhttp3.MediaType;

public class PlayActivity extends Activity {
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private ImageView imageViewPlaying;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        imageViewPlaying=(ImageView) findViewById(R.id.iv_playing);

        imageViewPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageViewPlaying.getTag().equals("toPause")){
                    //要暂停
                    imageViewPlaying.setImageResource(R.drawable.playing_to_play);
                    imageViewPlaying.setTag("toPlay");
                }else {
                    //要播放
                    imageViewPlaying.setImageResource(R.drawable.playing_to_pause);
                    imageViewPlaying.setTag("toPause");
                }
            }
        });
    }
}
