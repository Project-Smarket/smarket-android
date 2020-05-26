package org.techtown.smarket_android.searchItemList.Pager.spec;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.smarketClass.spec;

import java.util.List;

public class search_detail_spec_fragment extends Fragment {

    RecyclerView.Adapter dodAdapter;

    private List<spec> specList;
    private RecyclerView recyclerView;

    private SharedPreferences itemDetail;

    private ViewGroup viewGroup;
    public search_detail_spec_fragment() {

    }

    @Nullable
    @Override
    public ViewGroup onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        savedInstanceState = getArguments();
        specList = savedInstanceState.getParcelableArrayList("spec");
        savedInstanceState.clear();

        // specList가 null인 경우 "상세보기를 지원하지 않습니다"
        if (specList == null) {
            viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_spec_none_fragment_layout, container, false);
        }
        // specList가 null이 아닌 경우 상세정보 제공
        else {
            viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_spec_fragment_layout, container, false);
            CreateList(viewGroup);
        }
        return viewGroup;
    }

    private void CreateList(ViewGroup viewGroup) {
        recyclerView = viewGroup.findViewById(R.id.dodRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dodAdapter = new specAdapter(getContext(), specList);
        recyclerView.setAdapter(dodAdapter);
    }

}