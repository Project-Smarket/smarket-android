package org.techtown.smarket_android.searchItemList;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.Class.news;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Class.specList;
import org.techtown.smarket_android.Class.review;
import org.techtown.smarket_android.searchItemList.Pager.search_detail_news_fragment;
import org.techtown.smarket_android.searchItemList.Pager.search_detail_of_detail_fragment;
import org.techtown.smarket_android.searchItemList.Pager.search_detail_review_fragment;
import org.techtown.smarket_android.searchItemList.Pager.search_detail_video_fragment;
import org.techtown.smarket_android.searchItemList.Request.danawaRequest;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class searchdetail_fragment extends Fragment {
    private ViewGroup viewGroup;
    private Bundle bundle;
    private String in;
    private int pos = 0;
    private Toolbar toolbar;
    private search_detail_news_fragment detail_news_fragment;
    private search_detail_of_detail_fragment detail_of_detail_fragment;
    private search_detail_video_fragment detail_video_fragment;
    private search_detail_review_fragment detail_review_fragment;
    private FragmentManager fragmentManager;
    private ArrayList<specList> spec;
    private ArrayList<String> keyList;
    private ArrayList<String> keyValueList;
    private ArrayList<review> reviewList;
    private ArrayList<news> newsList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_item_detail, container, false);

        spec = new ArrayList<>();
        keyList = new ArrayList<>();
        keyValueList = new ArrayList<>();
        reviewList = new ArrayList<>();
        newsList = new ArrayList<>();

        ReceiveData();



        try {
            getJSon();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        settingToolbar();
        setHasOptionsMenu(true);


        return viewGroup;
    }

    private void Tab() {
        TabLayout tabLayout = viewGroup.findViewById(R.id.detail_TabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pos = tab.getPosition();
                changeView(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void ReceiveData() {
        bundle = getArguments();

        if (bundle != null) {
            in = bundle.getString("item_name");
            String iv = bundle.getString("item_value");
            Bitmap bitmap = bundle.getParcelable("item_image");
            String mn = bundle.getString("item_mallName");

            ImageView item_image = viewGroup.findViewById(R.id.detail_item_image);
            TextView item_name = viewGroup.findViewById(R.id.detail_item_name);
            TextView item_value = viewGroup.findViewById(R.id.detail_item_value);
            TextView item_mall = viewGroup.findViewById(R.id.detail_firm_name);

            item_image.setImageBitmap(bitmap);
            item_name.setText(in);
            item_value.setText(iv);
            item_mall.setText("판매처 : " + mn);
        }

    }

    private void settingToolbar() {
        toolbar = viewGroup.findViewById(R.id.detailToolbar);
        toolbar.setTitle(in);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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


    private void changeView(int index) {

        switch (index) {
            case 0: {
                if (detail_news_fragment == null) {
                    detail_news_fragment = new search_detail_news_fragment();
                    Bundle newsBundle = new Bundle();
                    List<news> list = newsList;
                    newsBundle.putSerializable("news",(Serializable) list);
                    detail_news_fragment.setArguments(newsBundle);
                    fragmentManager.beginTransaction().add(R.id.detail_frame, detail_news_fragment).addToBackStack(null).commit();
                }
                if (detail_news_fragment != null)
                    fragmentManager.beginTransaction().show(detail_news_fragment).commit();
                if (detail_of_detail_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_of_detail_fragment).commit();
                if (detail_video_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_video_fragment).commit();
                if (detail_review_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_review_fragment).commit();

                break;
            }
            case 1: {
                if (detail_of_detail_fragment == null) {
                    detail_of_detail_fragment = new search_detail_of_detail_fragment();
                    Bundle dodBundle = new Bundle();
                    List<specList> list = new ArrayList<>();
                    list = spec;
                    dodBundle.putSerializable("spec", (Serializable) list);

                    detail_of_detail_fragment.setArguments(dodBundle);
                    fragmentManager.beginTransaction().add(R.id.detail_frame, detail_of_detail_fragment).addToBackStack(null).commit();
                }

                if (detail_news_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_news_fragment).commit();
                if (detail_of_detail_fragment != null)
                    fragmentManager.beginTransaction().show(detail_of_detail_fragment).commit();
                if (detail_video_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_video_fragment).commit();
                if (detail_review_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_review_fragment).commit();

                break;
            }
            case 2: {
                if (detail_video_fragment == null) {
                    detail_video_fragment = new search_detail_video_fragment();
                    Bundle itemBundle = new Bundle();
                    itemBundle.putString("txt", in);
                    detail_video_fragment.setArguments(itemBundle);
                    fragmentManager.beginTransaction().add(R.id.detail_frame, detail_video_fragment).addToBackStack(null).commit();
                }

                if (detail_news_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_news_fragment).commit();
                if (detail_of_detail_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_of_detail_fragment).commit();
                if (detail_video_fragment != null)
                    fragmentManager.beginTransaction().show(detail_video_fragment).commit();
                if (detail_review_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_review_fragment).commit();

                break;
            }
            case 3: {
                if (detail_review_fragment == null) {
                    detail_review_fragment = new search_detail_review_fragment();
                    Bundle reviewBundle = new Bundle();
                    List<review> list = new ArrayList<>();
                    list = reviewList;
                    reviewBundle.putSerializable("review", (Serializable) list);
                    detail_review_fragment.setArguments(reviewBundle);
                    fragmentManager.beginTransaction().add(R.id.detail_frame, detail_review_fragment).addToBackStack(null).commit();
                }
                if (detail_news_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_news_fragment).commit();
                if (detail_of_detail_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_of_detail_fragment).commit();
                if (detail_video_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_video_fragment).commit();
                if (detail_review_fragment != null)
                    fragmentManager.beginTransaction().show(detail_review_fragment).commit();
            }
        }

    }

    private void getJSon() throws UnsupportedEncodingException {
        danawaRequest detailRequest = new danawaRequest(in, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    dodJson(jsonObject); //상세정보 json파싱
                    reviewJson(jsonObject); //리뷰 json파싱
                    newsJson(jsonObject); //뉴스 json파싱

                    fragmentManager = getChildFragmentManager();
                    detail_news_fragment = new search_detail_news_fragment();
                    Bundle newsbundle = new Bundle();
                    List<news> list = newsList;
                    newsbundle.putSerializable("news",(Serializable)list);
                    detail_news_fragment.setArguments(newsbundle);
                    fragmentManager.beginTransaction().replace(R.id.detail_frame, detail_news_fragment).addToBackStack(null).commit();

                    Tab();

                } catch (JSONException e) {
                    Log.d(TAG, "getJson: " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error + "", Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(detailRequest);
    }

    private void dodJson(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("spec");

        Iterator key = data.getJSONObject(0).keys();
        while (key.hasNext()) {
            String s = key.next().toString();
            keyList.add(s);
        }
        for (int j = 0; j < keyList.size(); j++) {

            if (data.getJSONObject(0).getString(keyList.get(j)).equals("○")) {
                String s = "";
                keyValueList.add(s);
            } else {
                keyValueList.add(data.getJSONObject(0).getString(keyList.get(j)));
            }
        }
        for (int i = 0; i < keyList.size(); i++) {
            spec.add(new specList(keyList.get(i), keyValueList.get(i)));
        }
    }

    private void reviewJson(JSONObject jsonObject) throws JSONException{
           JSONArray review = jsonObject.getJSONArray("review");

           for(int i=0, length=review.length(); i<length; i++){
               String title = review.getJSONObject(i).getString("title");
               String content = review.getJSONObject(i).getString("content");
               String user = review.getJSONObject(i).getString("user");
               String score = review.getJSONObject(i).getString("score");
               String mall = review.getJSONObject(i).getString("mall");
               String date = review.getJSONObject(i).getString("date");

               Log.d(TAG, "reviewJson: "+title +" "+content+" "+user);
               reviewList.add(new review(title, content, user, score, mall, date));
           }
    }

    private void newsJson(JSONObject jsonObject) throws JSONException{
        JSONArray news = jsonObject.getJSONArray("news");

        for(int i=0, length=news.length(); i<length; i++){
            String img = news.getJSONObject(i).getString("img");
            String title = news.getJSONObject(i).getString("title");
            String url = news.getJSONObject(i).getString("url");
            String user = news.getJSONObject(i).getString("user");
            String hit = news.getJSONObject(i).getString("hit");
            String date = news.getJSONObject(i).getString("date");

            newsList.add(new news(img, title, url, user, hit, date));
        }
    }

}


//package org.techtown.smarket_android.searchItemList;
//
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.viewpager.widget.ViewPager;
//
//import com.google.android.material.tabs.TabLayout;
//
//import org.techtown.smarket_android.MainActivity;
//import org.techtown.smarket_android.R;
//import org.techtown.smarket_android.searchItemList.Pager.SectionPageAdapter;
//import org.techtown.smarket_android.searchItemList.Pager.search_detail_mall_fragment;
//import org.techtown.smarket_android.searchItemList.Pager.search_detail_of_detail_fragment;
//import org.techtown.smarket_android.searchItemList.Pager.search_detail_video_fragment;
//
//
//public class searchdetail_fragment extends Fragment {
//    private ViewGroup viewGroup;
//    private Bundle bundle;
//    private String in;
//    private String txt;
//    private Toolbar toolbar;
//    private ViewPager viewPager;
//    private SectionPageAdapter S_adapter;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_item_detail, container, false);
//
//        ReceiveData();
//
//        ViewPage(viewGroup);
//
//        settingToolbar();
//        setHasOptionsMenu(true);
//
//        return viewGroup;
//    }
//
//    private void ReceiveData(){
//        bundle = getArguments();
//
//        if(bundle != null){
//            in = bundle.getString("item_name");
//            String iv = bundle.getString("item_value");
//            Bitmap bitmap = bundle.getParcelable("item_image");
//            String mn = bundle.getString("item_mallName");
//
//            TextView item_name = viewGroup.findViewById(R.id.detail_item_name);
//            TextView item_value = viewGroup.findViewById(R.id.detail_item_value);
//            ImageView item_image = viewGroup.findViewById(R.id.detail_item_image);
//            TextView item_mall = viewGroup.findViewById(R.id.detail_firm_name);
//
//            item_image.setImageBitmap(bitmap);
//            item_name.setText(in);
//            item_value.setText(iv);
//            item_mall.setText("판매처 : "+mn);
//        }
//
//    }
//
//    public void ViewPage(ViewGroup viewGroup){
//        S_adapter = new SectionPageAdapter(getFragmentManager());
//        viewPager = viewGroup.findViewById(R.id.detail_viewPage);
//        setupViewPager(viewPager, S_adapter);
//        TabLayout tabLayout = (TabLayout) viewGroup.findViewById(R.id.detail_tab);
//        tabLayout.setupWithViewPager(viewPager);
//    }
//
//    public void setupViewPager(ViewPager viewPager, SectionPageAdapter adapter){
//        search_detail_mall_fragment sdmf = new search_detail_mall_fragment();
//        search_detail_of_detail_fragment sdd = new search_detail_of_detail_fragment();
//        search_detail_video_fragment sdvf = new search_detail_video_fragment();
//
//        Bundle itemBundle = new Bundle();
//        itemBundle.putString("txt", txt);
//        sdmf.setArguments(itemBundle);
//        sdd.setArguments(itemBundle);
//        sdvf.setArguments(itemBundle);
//
//        adapter.addFragment(sdmf, "판매처");
//        adapter.addFragment(sdd, "상세보기");
//        adapter.addFragment(sdvf, "관련영상");
//
//        viewPager.setAdapter(adapter);
//    }
//
//    public void settingToolbar(){
//        toolbar = viewGroup.findViewById(R.id.detailToolbar);
//        toolbar.setTitle(in);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home: {
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                fm.beginTransaction().remove(searchdetail_fragment.this).commit();
//                fm.popBackStack();
//                return true;
//
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.detailmenu, menu);
//    }
//
//}
//

