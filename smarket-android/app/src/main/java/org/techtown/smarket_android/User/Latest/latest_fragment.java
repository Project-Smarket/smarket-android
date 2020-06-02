package org.techtown.smarket_android.User.Latest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.smarket_android.DTO_Class.DTO;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Search.RecyclerAdapter;
import org.techtown.smarket_android.Search.RecyclerDecoration;
import org.techtown.smarket_android.Search.search_detail_fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class latest_fragment extends Fragment {

    public static latest_fragment newInstance() {
        return new latest_fragment();
    }

    private ViewGroup viewGroup;

    // 최근 본 상품 리스트
    private List<DTO> latestList;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    private SharedPreferences userFile;
    private String user_id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.latest_main, container, false);
        get_userFile();
        get_latestList();

        // 툴바 설정
        set_toolbar();

        // Toolbar의 메뉴 버튼 활성화
        setHasOptionsMenu(true);

        // latestList recyclerView 설정
        set_recyclerView();

        return viewGroup;
    }

    private void set_toolbar() {
        Toolbar toolbar = viewGroup.findViewById(R.id.latest_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    private void set_recyclerView(){
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(8);

        recyclerView = viewGroup.findViewById(R.id.latest_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(spaceDecoration);
        adapter = new RecyclerAdapter(getContext(), getActivity(), latestList);
        recyclerView.setAdapter(adapter);

        adapter.setOnRecyclerClickListener(new RecyclerAdapter.OnRecyclerClickListener() {
            @Override
            public void OnRecyclerClickListener(View v, int position, DTO item_data) {
                search_detail_fragment searchdetailFragment = new search_detail_fragment();

                // 상품 상세로 데이터 전송
                Bundle bundle = settingBundle(v, item_data);
                searchdetailFragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, searchdetailFragment, "search").addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private Bundle settingBundle(View v, DTO item_data) {
        Bundle bundle = new Bundle();

        ImageView item_image = v.findViewById(R.id.search_list_item_image);
        Drawable d = item_image.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

        bundle.putParcelable("item_image", bitmap);
        bundle.putParcelable("item_data", item_data);


        return bundle;
    }

    // SharedPreference의 latestList 데이터를 가져온다
    private void get_latestList() {
        // 저장된 latestList 있을 경우
        String key = user_id + "/latestList";
        if (userFile.getString(key, null) != null) {
            String key_latestList = userFile.getString(key, null);
            Type listType = new TypeToken<ArrayList<DTO>>() {
            }.getType();
            latestList = new GsonBuilder().create().fromJson(key_latestList, listType);

        }// 저장된 alarmList 없을 경우
        else {
            latestList = new ArrayList<>();
            save_latestList();
        }
    }

    // SharedPreference에 latestList 데이터 저장
    private void save_latestList() {
        String key = user_id + "/latestList";
        // List<DTO> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<DTO>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(latestList, listType);

        // 스트링 객체로 변환된 데이터를 latestList에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString(key, json);
        editor.apply();
    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
    }

}
