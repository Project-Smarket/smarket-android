package org.techtown.smarket_android.searchItemList.Pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.smarketClass.review;
import org.techtown.smarket_android.searchItemList.Pager.reviewAdapter.reviewAdapter;

import java.util.ArrayList;
import java.util.List;

public class search_detail_review_fragment extends Fragment {

    private List<review> reviewList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter reviewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_review_fragment, container, false);
        reviewList = new ArrayList<>();

        getBundle();

        CreateList(viewGroup);


        return viewGroup;
    }

    private void CreateList(ViewGroup viewGroup){
        recyclerView = viewGroup.findViewById(R.id.reviewRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewAdapter = new reviewAdapter(getContext(), reviewList);
        recyclerView.setAdapter(reviewAdapter);
    }

    private void getBundle(){
        if(getArguments()!=null){
            List<review> list = (List<review>) getArguments().getSerializable("review");
            reviewList = list;
        }
    }
}
