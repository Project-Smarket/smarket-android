package org.techtown.smarket_android.Search;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
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
import org.techtown.smarket_android.DTO_Class.DTO;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Search.Request.searchRequest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.VolleyLog.TAG;

public class search_fragment extends Fragment implements OnBackpressedListener {

    public static search_fragment newInstance(){return new search_fragment();}

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
    private List<DTO> itemList;

    private Boolean back_check = false;

    // 최근 본 상품 리스트
    private List<DTO> latestList;

    // 사용자 정보
    private SharedPreferences userFile;
    private String user_id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_newsearch_fragment, container, false);

        get_userFile();
        get_latestList();

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
                    // "검색" 버튼을 누를 경우 start 초기화
                    start = 1;
                    back_check = true;
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
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getJson();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    },300);

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
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(8);

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
            public void OnRecyclerClickListener(View v, int position, DTO item_data) {
                search_detail_fragment searchdetailFragment = new search_detail_fragment();

                // 최근 본 상품 목록 추가
                latestList.add(0,item_data);
                save_latestList();

                // 상품 상세로 데이터 전송
                Bundle bundle = settingBundle(v, item_data);
                searchdetailFragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, 0 ,0, 0);
                fragmentTransaction.add(R.id.main_layout, searchdetailFragment, "detail").addToBackStack(null);
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
                    JSONArray items = data.getJSONArray("items");

                    for (int index = 0; index < items.length(); index++) {
                        String title = items.getJSONObject(index).getString("title");
                        String item_title = removeTag(title);
                        String item_id = items.getJSONObject(index).getString("productId");
                        String item_type = items.getJSONObject(index).getString("productType");
                        String item_lprice = items.getJSONObject(index).getString("lprice");
                        String item_image = items.getJSONObject(index).getString("image");
                        String item_mallName = items.getJSONObject(index).getString("mallName");
                        String item_link = items.getJSONObject(index).getString("link");
                        String item_brand = items.getJSONObject(index).getString("brand");
                        String item_maker = items.getJSONObject(index).getString("maker");
                        String item_category1 = items.getJSONObject(index).getString("category1");
                        String item_category2 = items.getJSONObject(index).getString("category2");
                        String item_category3 = items.getJSONObject(index).getString("category3");
                        String item_category4 = items.getJSONObject(index).getString("category4");

                        DTO item = new DTO(item_title, item_link, item_image, item_lprice, item_mallName, item_id, item_type, item_brand, item_maker,item_category1, item_category2, item_category3, item_category4);

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
        userFile = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
    }

    // 키보드 창 내림
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if(back_check){
            mAppBarLayout.setExpanded(true);
            back_check = false;
        }
    }
}
