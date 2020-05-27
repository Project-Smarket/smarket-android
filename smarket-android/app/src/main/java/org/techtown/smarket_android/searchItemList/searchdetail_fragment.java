package org.techtown.smarket_android.searchItemList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import org.techtown.smarket_android.Hotdeal.hotdeal_webView;
import org.techtown.smarket_android.smarketClass.news;
import org.techtown.smarket_android.smarketClass.review;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.smarketClass.spec;
import org.techtown.smarket_android.searchItemList.Pager.news.search_detail_news_fragment;
import org.techtown.smarket_android.searchItemList.Pager.spec.search_detail_spec_fragment;
import org.techtown.smarket_android.searchItemList.Pager.review.search_detail_review_fragment;
import org.techtown.smarket_android.searchItemList.Request.danawaRequest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import static com.android.volley.VolleyLog.TAG;

public class searchdetail_fragment extends Fragment {
    private ViewGroup viewGroup;
    private Bundle bundle;
    private String in;
    private int pos = 0;
    private Toolbar toolbar;
    private search_detail_news_fragment detail_news_fragment;
    private search_detail_spec_fragment detail_of_detail_fragment;
    private search_detail_review_fragment detail_review_fragment;
    private FragmentManager fragmentManager;
    private ArrayList<spec> specList;
    private ArrayList<String> keyList;
    private ArrayList<String> keyValueList;
    private ArrayList<review> reviewList;
    private ArrayList<news> newsList;
    private String item_link = "";

    private String item_productType;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_item_detail, container, false);

        progressDialog = ProgressDialog.show(getContext(),"","로딩");

        specList = new ArrayList<>();
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

        Button gotoMall = viewGroup.findViewById(R.id.detail_gotoMall);
        gotoMall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item_link.equals("")) {
                    Intent intent = new Intent(getActivity(), hotdeal_webView.class);
                    intent.putExtra("url", item_link);
                    getContext().startActivity(intent);
                }
            }
        });

        settingToolbar();
        setHasOptionsMenu(true);


        Tab();


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
            String[] item_data = bundle.getStringArray("item_data");

            ImageView item_image = viewGroup.findViewById(R.id.detail_item_image);
            TextView item_name = viewGroup.findViewById(R.id.detail_item_name);
            TextView item_value = viewGroup.findViewById(R.id.detail_item_value);
            TextView item_mall = viewGroup.findViewById(R.id.detail_firm_name);
            TextView item_brand = viewGroup.findViewById(R.id.detail_item_brand);
            TextView item_maker = viewGroup.findViewById(R.id.detail_item_maker);
            TextView item_category = viewGroup.findViewById(R.id.detail_item_category);

            item_image.setImageBitmap(bitmap);
            item_name.setText(in);
            item_value.setText(iv);
            item_mall.setText("판매처 : " + item_data[0]);
            item_link = item_data[1];
            Log.d(TAG, "ReceiveData: " + item_link);
            item_brand.setText(item_data[2]);
            item_maker.setText(item_data[3]);

            String category = item_data[4];
            if (!item_data[5].equals("")) {
                category += "/" + item_data[5];
                if (!item_data[6].equals("")) {
                    category += "/" + item_data[6];
                    if (!item_data[7].equals(""))
                        category += "/" + item_data[7];
                }
            }
            item_category.setText(category);

            item_productType = item_data[8];
            Log.d(TAG, "productType : "+ item_data[8]);
            bundle.clear();
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
                if (detail_review_fragment == null) {
                    detail_review_fragment = new search_detail_review_fragment();
                    Bundle reviewbundle = new Bundle();
                    reviewbundle.putParcelableArrayList("review", reviewList);
                    detail_review_fragment.setArguments(reviewbundle);
                    fragmentManager.beginTransaction().replace(R.id.detail_frame, detail_review_fragment, "search").addToBackStack(null).commitAllowingStateLoss();

                }
                if (detail_news_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_news_fragment).commit();
                if (detail_of_detail_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_of_detail_fragment).commit();
                if (detail_review_fragment != null)
                    fragmentManager.beginTransaction().show(detail_review_fragment).commit();

                break;
            }
            case 1: {
                if (detail_of_detail_fragment == null) {
                    detail_of_detail_fragment = new search_detail_spec_fragment();
                    Bundle specBundle = new Bundle();
                    specBundle.putParcelableArrayList("spec", specList);

                    detail_of_detail_fragment.setArguments(specBundle);
                    fragmentManager.beginTransaction().add(R.id.detail_frame, detail_of_detail_fragment, "search").addToBackStack(null).commit();
                }

                if (detail_news_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_news_fragment).commit();
                if (detail_of_detail_fragment != null)
                    fragmentManager.beginTransaction().show(detail_of_detail_fragment).commit();
                if (detail_review_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_review_fragment).commit();

                break;
            }
            case 2: {
                if (detail_news_fragment == null) {
                    detail_news_fragment = new search_detail_news_fragment();
                    Bundle newsBundle = new Bundle();
                    newsBundle.putParcelableArrayList("news", newsList);
                    detail_news_fragment.setArguments(newsBundle);
                    fragmentManager.beginTransaction().add(R.id.detail_frame, detail_news_fragment, "search").addToBackStack(null).commit();
                }
                if (detail_news_fragment != null)
                    fragmentManager.beginTransaction().show(detail_news_fragment).commit();
                if (detail_of_detail_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_of_detail_fragment).commit();
                if (detail_review_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_review_fragment).commit();

                break;
            }

        }

    }

    private void getJSon() throws UnsupportedEncodingException {
        String url = getString(R.string.detailEndpoint);
        danawaRequest detailRequest = new danawaRequest(url, in, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    reviewJson(jsonObject); //리뷰 json파싱
                    fragmentManager = getChildFragmentManager();
                    detail_review_fragment = new search_detail_review_fragment();
                    Bundle reviewbundle = new Bundle();
                    reviewbundle.putParcelableArrayList("review", reviewList);
                    detail_review_fragment.setArguments(reviewbundle);
                    fragmentManager.beginTransaction().replace(R.id.detail_frame, detail_review_fragment, "search").addToBackStack(null).commitAllowingStateLoss();

                    if (item_productType.equals("2")) {
                        specList = null;
                    } else {
                        specJson(jsonObject); //상세정보 json파싱
                    }
                    newsJson(jsonObject); //뉴스 json파싱

                    progressDialog.dismiss();
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

    private void specJson(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("spec");

        Iterator key = data.getJSONObject(0).keys();
        while (key.hasNext()) {
            String s = key.next().toString();
            keyList.add(s);
        }
        for (int j = 0; j < keyList.size(); j++) {
            keyValueList.add(data.getJSONObject(0).getString(keyList.get(j)));
        }
        for (int i = 0; i < keyList.size(); i++) {
            specList.add(new spec(keyList.get(i), keyValueList.get(i)));
        }
    }

    private void reviewJson(JSONObject jsonObject) throws JSONException {
        JSONArray review = jsonObject.getJSONArray("review");

        for (int i = 0, length = review.length(); i < length; i++) {
            String title = review.getJSONObject(i).getString("title");
            String content = review.getJSONObject(i).getString("content");
            String user = review.getJSONObject(i).getString("user");
            String score = review.getJSONObject(i).getString("score");
            String mall = review.getJSONObject(i).getString("mall");
            String date = review.getJSONObject(i).getString("date");

            Log.d(TAG, "reviewJson: " + title + " " + content + " " + user);
            review review1 = new review(title, content, user, score, mall, date);
            reviewList.add(review1);
        }
    }

    private void newsJson(JSONObject jsonObject) throws JSONException {
        JSONArray news = jsonObject.getJSONArray("news");

        for (int i = 0, length = news.length(); i < length; i++) {
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
