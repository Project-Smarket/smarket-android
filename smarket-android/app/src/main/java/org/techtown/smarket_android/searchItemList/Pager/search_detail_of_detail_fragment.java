package org.techtown.smarket_android.searchItemList.Pager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Pager.dodAdapter.detailAdapter;
import org.techtown.smarket_android.smarketClass.spec;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class search_detail_of_detail_fragment extends Fragment {

    RecyclerView.Adapter dodAdapter;

    private List<spec> specList;
    private RecyclerView recyclerView;

    private SharedPreferences itemDetail;

    public search_detail_of_detail_fragment() {

    }

    @Nullable
    @Override
    public ViewGroup onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_of_detail_fragment_layout, container, false);

        savedInstanceState = getArguments();
        specList = savedInstanceState.getParcelableArrayList("spec");
        savedInstanceState.clear();
        //get_specList();

        CreateList(viewGroup);


        return viewGroup;
    }

    private void CreateList(ViewGroup viewGroup) {
        recyclerView = viewGroup.findViewById(R.id.dodRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dodAdapter = new detailAdapter(getContext(), specList);
        recyclerView.setAdapter(dodAdapter);
    }

}