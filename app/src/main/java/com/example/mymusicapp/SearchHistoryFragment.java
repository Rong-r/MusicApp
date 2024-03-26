package com.example.mymusicapp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class SearchHistoryFragment extends Fragment {
    private RecyclerView recyclerViewHistory;
    public SearchHistoryFragment(){
        super(R.layout.fragment_search_history);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewHistory=(RecyclerView)getActivity().findViewById(R.id.recyclerView_search_history);
        recyclerViewHistory.setAdapter(new SearchHistoryAdapter(getActivity()));
    }
}
