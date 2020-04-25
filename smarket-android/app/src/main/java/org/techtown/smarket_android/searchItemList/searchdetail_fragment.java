package org.techtown.smarket_android.searchItemList;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.techtown.smarket_android.MainActivity;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Pager.SectionPageAdapter;
import org.techtown.smarket_android.searchItemList.Pager.search_detail_mall_fragment;
import org.techtown.smarket_android.searchItemList.Pager.search_detail_of_detail_fragment;
import org.techtown.smarket_android.searchItemList.Pager.search_detail_video_fragment;


public class searchdetail_fragment extends Fragment {
    private ViewGroup viewGroup;
    private Bundle bundle;
    private String in;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private SectionPageAdapter S_adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_item_detail, container, false);

        ReceiveData();

        ViewPage(viewGroup);

        settingToolbar();
        setHasOptionsMenu(true);

        return viewGroup;
    }

    private void ReceiveData(){
        bundle = getArguments();

        if(bundle != null){
            in = bundle.getString("item_name");
            String iv = bundle.getString("item_value");
            Bitmap bitmap = bundle.getParcelable("item_image");
            String mn = bundle.getString("item_mallName");

            TextView item_name = viewGroup.findViewById(R.id.detail_item_name);
            TextView item_value = viewGroup.findViewById(R.id.detail_item_value);
            ImageView item_image = viewGroup.findViewById(R.id.detail_item_image);
            TextView item_mall = viewGroup.findViewById(R.id.detail_firm_name);

            item_image.setImageBitmap(bitmap);
            item_name.setText(in);
            item_value.setText(iv);
            item_mall.setText("판매처 : "+mn);
        }

    }

    public void ViewPage(ViewGroup viewGroup){
        S_adapter = new SectionPageAdapter(getFragmentManager());
        viewPager = viewGroup.findViewById(R.id.detail_viewPage);
        setupViewPager(viewPager, S_adapter);
        TabLayout tabLayout = (TabLayout) viewGroup.findViewById(R.id.detail_tab);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setupViewPager(ViewPager viewPager, SectionPageAdapter adapter){
        adapter.addFragment(new search_detail_mall_fragment(), "판매처");
        adapter.addFragment(new search_detail_of_detail_fragment(), "상세보기");
        adapter.addFragment(new search_detail_video_fragment(), "관련영상");
        viewPager.setAdapter(adapter);
    }

    public void settingToolbar(){
        toolbar = viewGroup.findViewById(R.id.detailToolbar);
        toolbar.setTitle(in);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().remove(searchdetail_fragment.this).commit();
                fm.popBackStack();
                return true;

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.detailmenu, menu);
    }

}

