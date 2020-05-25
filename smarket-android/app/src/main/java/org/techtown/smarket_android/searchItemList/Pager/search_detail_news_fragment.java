package org.techtown.smarket_android.searchItemList.Pager;

import android.content.Context;
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

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.smarket_android.smarketClass.news;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Pager.newsAdapter.newsAdapter;
import org.techtown.smarket_android.smarketClass.spec;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class search_detail_news_fragment extends Fragment {
    private List<news> newsList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter newsAdapter;

    private SharedPreferences itemDetail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_news_fragment_layout, container, false);

        savedInstanceState = getArguments();
        newsList = savedInstanceState.getParcelableArrayList("news");
        savedInstanceState.clear();
        //get_newsList();

        CreateList(viewGroup);


        return viewGroup;
    }

    private void CreateList(ViewGroup viewGroup){
        recyclerView = viewGroup.findViewById(R.id.newsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new newsAdapter(getContext(), newsList);
        recyclerView.setAdapter(newsAdapter);
    }
}
