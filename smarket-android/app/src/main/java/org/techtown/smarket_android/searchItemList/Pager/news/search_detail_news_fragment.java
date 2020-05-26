package org.techtown.smarket_android.searchItemList.Pager.news;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.smarketClass.news;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Pager.news.newsAdapter;

import java.util.List;

public class search_detail_news_fragment extends Fragment {
    private List<news> newsList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter newsAdapter;

    private ViewGroup viewGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        savedInstanceState = getArguments();
        newsList = savedInstanceState.getParcelableArrayList("news");
        savedInstanceState.clear();

        if(newsList.size() == 0){
            viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_news_none_fragment_layout, container, false);
        }
        else{
            viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_news_fragment_layout, container, false);
            CreateList(viewGroup);
        }
        return viewGroup;
    }

    private void CreateList(ViewGroup viewGroup){
        recyclerView = viewGroup.findViewById(R.id.newsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new newsAdapter(getContext(), newsList);
        recyclerView.setAdapter(newsAdapter);
    }
}
