package com.example.mymusicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryViewHolder> {
    private List<String> searchHistoryList=new ArrayList<String>();
    private List<String> searchHistoryListReverse=new ArrayList<String>();
    private final SharedPreferences sharedPreferences;

    public SearchHistoryAdapter(Context context){
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        String stringHistory = sharedPreferences.getString("history", "");
        String[] tokens = stringHistory.split(" ");
        for(String history : tokens){
            searchHistoryList.add(history);
            searchHistoryListReverse.add(history);
        }
        Collections.reverse(searchHistoryListReverse);
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
                StringBuilder newHistory= new StringBuilder();
                for(String item:searchHistoryList){
                    newHistory.append(item).append(" ");
                }
                editor.putString("history", newHistory.toString());
                editor.apply();
            }
        });
    }
    @Override
    public int getItemCount() {
        return searchHistoryList.size();
    }


}
