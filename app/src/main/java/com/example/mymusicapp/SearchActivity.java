package com.example.mymusicapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static String tag="TAG-SearchActivity";

    private ImageView imageViewBack;
    private TextView textViewSearch;
    private EditText editTextContent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Handler handler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundleFrom=msg.getData();
            String searchContent=bundleFrom.getString("searchContent");
            Boolean canSearch=bundleFrom.getBoolean("canSearch");

            if(canSearch&&!searchContent.isEmpty()){
                Bundle bundlePost=new Bundle();
                bundlePost.putString("searchContent",searchContent);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_under_search, SearchResultFragment.class,bundlePost)
                        .addToBackStack("searchResult").commit();
            }else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_under_search, SearchNullFragment.class,null)
                        .addToBackStack("searchNull").commit();
            }
            //Toast.makeText(SearchActivity.this,"收到消息",Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        imageViewBack=(ImageView) findViewById(R.id.iv_search_back);
        textViewSearch=(TextView)findViewById(R.id.tv_search);
        editTextContent=(EditText)findViewById(R.id.et_search);
        isShowHistory();
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
                String searchContent=editTextContent.getText().toString();
                addHistory(searchContent);
                //开启子线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //传递用户输入
                        Message message=handler.obtainMessage();
                        Bundle bundle=new Bundle();
                        if(canSearch(searchContent)){
                            bundle.putString("searchContent",searchContent);
                            bundle.putBoolean("canSearch",true);
                        }else {
                            bundle.putString("searchContent",null);
                            bundle.putBoolean("canSearch",false);
                        }
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }).start();
                //Toast.makeText(SearchActivity.this,"开启子线程请求",Toast.LENGTH_SHORT).show();
            }
        });
        editTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    editTextContent.setText("");
                }
            }
        });
    }
    private void addHistory(String content){
        String history=sharedPreferences.getString("history","");
        editor=sharedPreferences.edit();
        if(!history.isEmpty()){
            String[] tokens = history.split(" ");
            if (!Arrays.asList(tokens).contains(content)) {
                String newHistory=history+" "+content;
                editor.putString("history",newHistory);
            }
        }else {
            editor.putString("history",content);
        }
        editor.apply();
    }
    private boolean canSearch(String content) {
        DatabaseHelper databaseHelper=new DatabaseHelper(SearchActivity.this,"SongsApp.db",null,1);
        List<String> musicsPathsList=databaseHelper.getStoredMusicPath();
        // 使用空格作为分隔符来分割字符串
        String[] tokens = content.split(" ");
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
        if(resultPathsList!=null&& !resultPathsList.isEmpty()){
            Log.d(tag,"resultPathsList: "+resultPathsList);
            return true;
        }else return false;
    }

    private void isShowHistory(){
        String history=sharedPreferences.getString("history","");
        if(!history.isEmpty()){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_under_search, SearchHistoryFragment.class,null)
                    .addToBackStack("searchHistory").commit();
        }
    }
}
