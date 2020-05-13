package org.techtown.smarket_android.Hotdeal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class hotdealViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> pages;
    private ArrayList<String> titles = new ArrayList<>();

    public hotdealViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        pages = new ArrayList<>();
        pages.add(new ppomppu1_fragment());
        pages.add(new ppomppu2_fragment());
        pages.add(new ppomppu3_fragment());
        pages.add(new ppomppu4_fragment());
        pages.add(new ruliweb_fragment());
        pages.add(new fmkorea_fragment());

        titles.add("뽐뿌1");
        titles.add("뽐뿌2");
        titles.add("뽐뿌3");
        titles.add("뽐뿌4");
        titles.add("루리웹게시판");
        titles.add("FM핫딜");

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }
}
