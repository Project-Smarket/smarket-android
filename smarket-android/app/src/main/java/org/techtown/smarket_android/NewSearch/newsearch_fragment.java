package org.techtown.smarket_android.NewSearch;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.smarketClass.SearchedItem;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.ClearEditText;
import org.techtown.smarket_android.searchItemList.RecyclerAdapter;
import org.techtown.smarket_android.searchItemList.RecyclerDecoration;
import org.techtown.smarket_android.searchItemList.Request.searchRequest;
import org.techtown.smarket_android.searchItemList.searchdetail_fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class newsearch_fragment extends Fragment {

    private ViewGroup viewGroup;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ProgressBar search_progressBar;
    private int start = 1;
    private int display = 10;

    private ClearEditText search_text;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    private ImageView smarket_cat;


    // 검색창을 통해 검색을 완료했는지 검사
    private boolean isUpdate = false;

    // moreLoad가 실행중인지 검사
    private boolean isMoreLoad = false;

    private String searched_item = "";


    // 검색한 데이터 가져오기
    private List<SearchedItem> itemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_newsearch_fragment, container, false);
        mAppBarLayout = viewGroup.findViewById(R.id.app_bar);
        collapsingToolbarLayout = viewGroup.findViewById(R.id.newsearch_collaps);
        toolbar = viewGroup.findViewById(R.id.toolbar);
        search_text = viewGroup.findViewById(R.id.newsearch_editText);
        search_progressBar = viewGroup.findViewById(R.id.newsearch_progressBar);
        search_progressBar.setVisibility(View.GONE);
        smarket_cat = viewGroup.findViewById(R.id.newsearch_smarket_cat);


        itemList = new ArrayList<>();

        // recyclerView 설정
        set_recyclerView();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("");


        final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);


        // search_text를 눌렀을 때 start 초기화


        // 키보드에서 "검색" 버튼 눌렀을 때 서버에 검색 요청
        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                itemList.clear();
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 검색되었던 제품명과 현재 검색한 제품명이 다를 경우, start 초기화
                    if (!searched_item.equals(search_text.getText().toString())) {
                        start = 1;
                    }
                    try {
                        // 현재 검색된 제품명 저장
                        searched_item = search_text.getText().toString();
                        // Appbar 축소
                        mAppBarLayout.setExpanded(false);
                        // AppBar 그림자 설정
                        StateListAnimator stateListAnimator = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            stateListAnimator = new StateListAnimator();
                            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(mAppBarLayout, "elevation", 16));
                            mAppBarLayout.setStateListAnimator(stateListAnimator);
                        }
                        // 프로그레스바 실행
                        search_progressBar.setVisibility(View.VISIBLE);
                        // 검색된 제품명으로 서버에 요청
                        getJson();
                    } catch (UnsupportedEncodingException e) {

                    }
                }
                return false;
            }
        });

        // 검색 완료 후 검색 창을 누르면 고양이가 내려옴
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAppBarLayout.setExpanded(true);
            }
        });


        return viewGroup;
    }

    private void set_recyclerView() {

        // 아이템 줄간격 설정
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(20);

        recyclerView = viewGroup.findViewById(R.id.newsearch_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(spaceDecoration);
        adapter = new RecyclerAdapter(getContext(), getActivity(), itemList);
        recyclerView.setAdapter(adapter);

        // 검색 상품 loadMore
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // 스크롤이 가장 위에 있을 때
                if (!recyclerView.canScrollVertically(-1)) {
                    Log.i(TAG, "Top of list");
                    // 스크롤 가장 아래로 내려왔을 때
                } else if (!recyclerView.canScrollVertically(1)) {
                    Log.i(TAG, "End of list");
                    if (!isMoreLoad) {
                        isMoreLoad = true;
                        try {
                            search_progressBar.setVisibility(View.VISIBLE);
                            getJson();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        adapter.setOnRecyclerClickListener(new RecyclerAdapter.OnRecyclerClickListener() {
            @Override
            public void OnRecyclerClickListener(View v, int position, String[] item_data) {
                searchdetail_fragment searchdetailFragment = new searchdetail_fragment();
                Bundle bundle = settingBundle(v, item_data);
                searchdetailFragment.setArguments(bundle);
                //listClear();
                adapter.notifyDataSetChanged();

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, searchdetailFragment, "search").addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }


    private void getJson() throws UnsupportedEncodingException {

        String url = getString(R.string.naverEndpoint) + "/search?query=";
        searchRequest searchRequest = new searchRequest(url, start, display, searched_item, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray key = data.getJSONArray("items");

                    for (int index = 0; index < key.length(); index++) {
                        String title = key.getJSONObject(index).getString("title");
                        String item_title = removeTag(title);
                        String item_id = key.getJSONObject(index).getString("productId");
                        String item_type = key.getJSONObject(index).getString("productType");
                        String item_lprice = key.getJSONObject(index).getString("lprice");
                        String item_image = key.getJSONObject(index).getString("image");
                        String item_mallName = key.getJSONObject(index).getString("mallName");
                        String item_link = key.getJSONObject(index).getString("link");
                        String item_brand = key.getJSONObject(index).getString("brand");
                        String item_maker = key.getJSONObject(index).getString("maker");
                        String item_category1 = key.getJSONObject(index).getString("category1");
                        String item_category2 = key.getJSONObject(index).getString("category2");
                        String item_category3 = key.getJSONObject(index).getString("category3");
                        String item_category4 = key.getJSONObject(index).getString("category4");

                        SearchedItem item = new SearchedItem(item_title, item_id, item_type, item_lprice, item_image, item_mallName, item_link, item_brand, item_maker,item_category1, item_category2, item_category3, item_category4);

                        itemList.add(item);
                    }
                    adapter.notifyDataSetChanged();

                    // 스마켓 고양이 축소
                    ViewGroup.LayoutParams cat_params = smarket_cat.getLayoutParams();
                    cat_params.width = 240;
                    smarket_cat.setLayoutParams(cat_params);

                    // 상품 검색 및 조회 완료 후 "로딩 바" 안보이게 설정
                    search_progressBar.setVisibility(View.GONE);

                    // 상품 검색 및 조회 완료 후 moreLoad 시 검색 위치 재설정
                    start += display;

                    // 상품 검색 및 조회 완료 후 isUpdate : true 설정
                    isMoreLoad = false;

                    isUpdate = true;
                    // 상품 검색 및 조회 완료 후 Appbar의 높이 재조정(가운데에서 약간 위로)
                    if (isUpdate) {
                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
                        params.height = 340;
                        mAppBarLayout.setLayoutParams(params);
                        recyclerView.setBackgroundColor(getResources().getColor(R.color.color_lite_gray));
                    }
                    hideKeyboard();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error + "", Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(searchRequest);
    }


    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    private Bundle settingBundle(View v, String[] item_data) {
        Bundle bundle = new Bundle();
        TextView item_name = v.findViewById(R.id.search_list_item_title);
        TextView item_value = v.findViewById(R.id.search_list_item_price);
        ImageView item_image = v.findViewById(R.id.search_list_item_image);

        Drawable d = item_image.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

        bundle.putString("item_name", item_name.getText().toString());
        bundle.putString("item_value", item_value.getText().toString());
        bundle.putParcelable("item_image", bitmap);
        bundle.putStringArray("item_data", item_data);

        return bundle;
    }

    // 키보드 창 내림
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
    }
}
