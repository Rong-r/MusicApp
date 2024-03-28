package com.example.mymusicapp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class SearchHistoryFragment extends Fragment {
    public SearchHistoryFragment(){
        super(R.layout.fragment_search_history);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getActivity()!=null){
            RecyclerView recyclerViewHistory = (RecyclerView) getActivity().findViewById(R.id.recyclerView_search_history);
            recyclerViewHistory.setAdapter(new SearchHistoryAdapter(getActivity()));
        }
    }
}
