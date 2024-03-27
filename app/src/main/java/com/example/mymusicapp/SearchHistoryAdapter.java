package com.example.mymusicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryViewHolder> {
    private static String tag="TAG-SearchHistoryAdapter";
    private Context mContext;
    private List<String> searchHistoryList=new ArrayList<String>();
    private List<String> searchHistoryListReverse=new ArrayList<String>();
    private SharedPreferences sharedPreferences;
    private String stringHistory;
    private String[] tokens;
    public SearchHistoryAdapter(Context context){
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        stringHistory=sharedPreferences.getString("history","");
        tokens = stringHistory.split(" ");
        for(String history :tokens){
            searchHistoryList.add(history);
            searchHistoryListReverse.add(history);
        }
        Collections.reverse(searchHistoryListReverse);
        mContext=context;
    }

    @NonNull
    @Override
    public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载item布局
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_search_history,parent,false);
        return new SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String nowTitle=searchHistoryListReverse.get(position);
        holder.textViewTitle.setText(nowTitle);
        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchHistoryList.remove(position);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                String newHistory=new String("");
                for(String item:searchHistoryList){
                    newHistory=newHistory+item+" ";
                }
                editor.putString("history",newHistory);
                editor.apply();
            }
        });
    }
    @Override
    public int getItemCount() {
        return searchHistoryList.size();
    }


}
