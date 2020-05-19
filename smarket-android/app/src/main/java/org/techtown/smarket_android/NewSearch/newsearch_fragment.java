package org.techtown.smarket_android.NewSearch;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.Class.SearchedItem;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.UserLogin.user_login_fragment;
import org.techtown.smarket_android.searchItemList.ClearEditText;
import org.techtown.smarket_android.searchItemList.RecyclerAdapter;
import org.techtown.smarket_android.searchItemList.RecyclerDecoration;
import org.techtown.smarket_android.searchItemList.Request.searchRequest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;
import static org.techtown.smarket_android.R.color.red;

public class newsearch_fragment extends Fragment {

    private ViewGroup viewGroup;
    private RecyclerView recyclerView;
    private String txt;
    private RecyclerAdapter adapter;
    private ProgressBar search_progressBar;
    private int start = 1;
    private int display = 10;

    private ClearEditText search_text;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    private boolean isUpdate = false;
    private boolean isItemList = false;

    // 검색한 데이터 가져오기
    private List<SearchedItem> itemList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_newsearch_fragment, container, false);
        search_progressBar = viewGroup.findViewById(R.id.newsearch_progressBar);
        search_progressBar.setVisibility(View.GONE);
        itemList = new ArrayList<>();

        // 검색 데이터 가져오기
        CreateList();

        mAppBarLayout = viewGroup.findViewById(R.id.app_bar);
        collapsingToolbarLayout = viewGroup.findViewById(R.id.newsearch_collaps);

        toolbar = viewGroup.findViewById(R.id.toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("");

        search_text = viewGroup.findViewById(R.id.newsearch_editText);
        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                itemList.clear();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    try {
                        txt = search_text.getText().toString();
                        search_text.setText("");
                        mAppBarLayout.setExpanded(false);
                        search_progressBar.setVisibility(View.VISIBLE);
                        //collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorNone));
                        collapsingToolbarLayout.setTitle(search_text.getText().toString());
                        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorBlack));
                        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorNone));
                        getJson();
                    } catch (UnsupportedEncodingException e) {


                    }
                    return true;
                }
                return false;
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAppBarLayout.setExpanded(true);
            }
        });







        // 이미 데이터를 가져왔으면 moreLoad하지 않음
        /*if (!isItemList) {
            try {
                search_progressBar.setVisibility(View.VISIBLE);
                getJson();
                isItemList = true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }*/

        /*FloatingActionButton fab = (FloatingActionButton) viewGroup.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        return viewGroup;
    }

    private void CreateList() {

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
                    //Log.i(TAG, "End of list");
                    if (!isUpdate) {
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
    }


    private void getJson() throws UnsupportedEncodingException {

        searchRequest searchRequest = new searchRequest(start, display, txt, new Response.Listener<String>() {
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

                        SearchedItem item = new SearchedItem(item_title, item_id, item_type, item_lprice, item_image, item_mallName);

                        itemList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    isUpdate = false;
                    search_progressBar.setVisibility(View.GONE);
                    start += display;


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
        isUpdate = true;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(searchRequest);
    }


    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    // 키보드 창 내림
    private void hideKeyboard(){
        InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
    }
}
