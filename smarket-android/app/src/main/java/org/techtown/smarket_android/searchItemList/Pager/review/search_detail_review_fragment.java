package org.techtown.smarket_android.searchItemList.Pager.review;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.smarketClass.review;
import org.techtown.smarket_android.searchItemList.Pager.review.reviewAdapter;

import java.util.List;

public class search_detail_review_fragment extends Fragment {

    private List<review> reviewList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter reviewAdapter;

    private ViewGroup viewGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) { viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_review_fragment, container, false);

        savedInstanceState = getArguments();
        reviewList = savedInstanceState.getParcelableArrayList("review");
        savedInstanceState.clear();

        if(reviewList.size() == 0){
            viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_review_none_fragment, container, false);
        }else{
            viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_review_fragment, container, false);
            CreateList(viewGroup);
        }
        return viewGroup;
    }

    private void CreateList(ViewGroup viewGroup){
        recyclerView = viewGroup.findViewById(R.id.reviewRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewAdapter = new reviewAdapter(reviewList);
        recyclerView.setAdapter(reviewAdapter);
    }

}
