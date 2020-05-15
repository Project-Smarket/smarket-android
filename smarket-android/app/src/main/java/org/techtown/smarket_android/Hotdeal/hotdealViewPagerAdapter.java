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
        pages.add(new hotdeal_page3_ppomppu3());
        pages.add(new hotdeal_page1_ppomppu1());
        pages.add(new hotdeal_page2_ppomppu2());
        pages.add(new hotdeal_page4_ppomppu4());
        pages.add(new hotdeal_page5_ruliweb());
        pages.add(new hotdeal_page6_fmhotdeal());
        pages.add(new hotdeal_page8_coolenjoy());
        pages.add(new hotdeal_page9_malltail());

        titles.add("뽐뿌게시판");
        titles.add("해외뽐뿌");
        titles.add("오프라인뽐뿌");
        titles.add("뽐뿌쇼핑특가");
        titles.add("루리웹게시판");
        titles.add("FM핫딜");
        titles.add("쿨엔조이");
        titles.add("몰테일");


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
