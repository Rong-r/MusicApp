package com.example.mymusicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainActivity extends Activity {
    //private DrawerLayout drawerLayout;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //drawerLayout=(DrawerLayout) findViewById(R.id.layout_main);
        imageView=(ImageView) findViewById(R.id.iv_user_info);
/*
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


 */

    }


}
