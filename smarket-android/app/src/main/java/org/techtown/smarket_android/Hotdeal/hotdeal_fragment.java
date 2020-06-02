package org.techtown.smarket_android.Hotdeal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.techtown.smarket_android.R;

public class hotdeal_fragment extends Fragment {

    private ViewGroup viewGroup;
    private ViewPager hotdealViewPager;
    private TabLayout hotdealTabLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.hotdeal_main, container, false);


        // viewpager 생성 및 어댑터 설정
        hotdealViewPager = viewGroup.findViewById(R.id.hotdealViewPager);
        hotdealViewPagerAdapter adapter = new hotdealViewPagerAdapter(getChildFragmentManager());
        hotdealViewPager.setAdapter(adapter);

        // viewpager와 tablayout 연동
        hotdealTabLayout = viewGroup.findViewById(R.id.hotdealTapLayout);
        hotdealTabLayout.setupWithViewPager(hotdealViewPager);


        return viewGroup;
    }
}



