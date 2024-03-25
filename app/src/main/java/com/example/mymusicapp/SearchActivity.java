package com.example.mymusicapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ImageView imageViewBack;
    private TextView textViewSearch;
    private EditText editTextContent;
    private Handler handler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String searchContent=(String) msg.obj;
            DatabaseHelper databaseHelper=new DatabaseHelper(SearchActivity.this,"SongsApp.db",null,1);
            List<String> musicsPathsList=databaseHelper.getStoredMusicPath();
            // 使用空格作为分隔符来分割字符串
            String[] tokens = searchContent.split(" ");
            // 筛选并组成新的列表
            List<String> resultPathsList = new ArrayList<>();
            for (String item : musicsPathsList) {
                for (String filterWord : tokens) {
                    if (item.contains(filterWord)) {
                        resultPathsList.add(item);
                        break;
                    }
                }
            }
            //结果数组不为空即可以解析出数据
            if(resultPathsList!=null&& !resultPathsList.isEmpty()){
                Bundle bundle=new Bundle();
                bundle.putString("searchContent",searchContent);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_under_search, SearchResultFragment.class,bundle)
                        .addToBackStack("searchResult").commit();

            }else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_under_search, SearchNullFragment.class,null)
                        .addToBackStack("searchNull").commit();
            }
            Toast.makeText(SearchActivity.this,"收到消息",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        imageViewBack=(ImageView) findViewById(R.id.iv_search_back);
        textViewSearch=(TextView)findViewById(R.id.tv_search);
        editTextContent=(EditText)findViewById(R.id.et_search);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_under_search, SearchHistoryFragment.class,null)
                .addToBackStack("searchHistory").commit();
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        textViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击搜索后，隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                //开启子线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //传递用户输入
                        String searchContent=editTextContent.getText().toString();
                        Message message=new Message();
                        message.obj=searchContent;
                        handler.sendMessage(message);
                    }
                }).start();
                Toast.makeText(SearchActivity.this,"开启子线程请求",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
