package com.example.mymusicapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment {
    public SearchResultFragment(){
        super(R.layout.fragment_search_result);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> musicsPathsList = DatabaseManager.getDatabaseManager().getMusicListAll();
        Bundle bundle=requireArguments();
        String searchContent=bundle.getString("searchContent");
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
        if(getActivity()!=null){
            RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView_search_result);
            recyclerView.setAdapter(new HomeAdapter(getActivity(),resultPathsList,""));
        }
        //下拉时触发SwipeReFreshLayout的下拉动画
        SwipeRefreshLayout swipeRefreshLayout=getActivity().findViewById(R.id.swipe_refresh_layout_result);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },200);
            }
        });

    }
}
