package com.example.mymusicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    private ImageView imageViewBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        imageViewBack=(ImageView) findViewById(R.id.iv_search_back);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_under_search, HistoryFragment.class,null)
                .addToBackStack("search").commit();
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
