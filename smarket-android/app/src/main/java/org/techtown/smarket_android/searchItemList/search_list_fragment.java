package org.techtown.smarket_android.searchItemList;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.Class.SearchedItem;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Request.searchRequest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class search_list_fragment extends Fragment {

    private ViewGroup viewGroup;
    private RecyclerView recyclerView;
    private String txt;
    private Toolbar toolbar;
    private RecyclerAdapter adapter;

    // 검색한 데이터 가져오기
    private List<SearchedItem> itemList = new ArrayList<>();


    @Nullable
    @Override
    public ViewGroup onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_list, container, false);

        getBundle();

        // 검색 데이터 가져오기
        CreateList();

        try {
            getJson();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "CreateList: " + itemList.isEmpty());

        settingToolbar();

        setHasOptionsMenu(true);

        adapter.setOnRecyclerClickListener(new RecyclerAdapter.OnRecyclerClickListener() {
            @Override
            public void OnRecyclerClickListener(View v, int position) {
                searchdetail_fragment searchdetailFragment = new searchdetail_fragment();
                Bundle bundle = settingBundle(v);
                searchdetailFragment.setArguments(bundle);
                //listClear();
                adapter.notifyDataSetChanged();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, searchdetailFragment).addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });


        return viewGroup;
    }



    private void CreateList() {
        recyclerView = viewGroup.findViewById(R.id.search_item_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter(getContext(), getActivity(), itemList);
        recyclerView.setAdapter(adapter);
    }


    private void getJson() throws UnsupportedEncodingException {

        searchRequest searchRequest = new searchRequest(txt, new Response.Listener<String>() {
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
                        String price = key.getJSONObject(index).getString("lprice");
                        int item_price = Integer.parseInt(price);
                        String item_lprice = String.format("%,d", item_price);

                        String item_image = key.getJSONObject(index).getString("image");

                        String item_mallName = key.getJSONObject(index).getString("mallName");

                        SearchedItem item = new SearchedItem(item_title, item_id, item_type, item_lprice, item_image, item_mallName);

                        itemList.add(item);
                    }
                    adapter.notifyDataSetChanged();

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

    /**
     * 모든 HTML 태그를 제거하고 반환한다.
     *
     * @param html
     * @throws Exception
     */
    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchBtn = menu.findItem(R.id.search_list_btn);
        SearchView searchView = (SearchView) searchBtn.getActionView();
        searchView.setQueryHint(txt);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_list_fragment slf = new search_list_fragment();
                Bundle bundle = setBundle(query);
                slf.setArguments(bundle);
                assert getFragmentManager() != null;
                listClear();
                adapter.notifyDataSetChanged();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, slf).addToBackStack(null).commitAllowingStateLoss();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private Bundle setBundle(String query) {
        Bundle bundle = new Bundle();

        String text = query;
        if (text.equals("")) {
            Toast.makeText(getContext(), "폴더명을 입력해주세요", Toast.LENGTH_LONG).show();
        } else if (!text.equals("")) {
            char except_enter[] = text.toCharArray();
            if (except_enter[except_enter.length - 1] == '\n') {

                char result_char[] = new char[except_enter.length - 1];
                System.arraycopy(except_enter, 0, result_char, 0, except_enter.length - 1);
                text = String.valueOf(result_char);

            } // 한글 입력 후 엔터시 개행문자 발생하는 오류 처리
            bundle.putString("searchName", text);

        }
        return bundle;
    }

    public void settingToolbar() {
        toolbar = viewGroup.findViewById(R.id.searchToolbar);
        toolbar.setTitle(txt);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
//              프래그먼트 모든 stack 한번에 지우기 fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().remove(search_list_fragment.this).commit();
                fm.popBackStack();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void listClear(){
        adapter.clear();
    }

    private void getBundle() {
        if (getArguments() != null) {
            txt = getArguments().getString("searchName");
        }

    }
    private Bundle settingBundle(View v) {
        Bundle bundle = new Bundle();
        TextView item_name = v.findViewById(R.id.search_list_item_name);
        TextView item_value = v.findViewById(R.id.search_list_item_value);
        ImageView item_image = v.findViewById(R.id.search_list_item_image);
        TextView item_mall = v.findViewById(R.id.search_mallName);

        Drawable d = item_image.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

        bundle.putString("item_name", item_name.getText().toString());
        bundle.putString("item_value", item_value.getText().toString());
        bundle.putParcelable("item_image", bitmap);
        bundle.putString("item_mallName", item_mall.getText().toString());
        bundle.putString("txt",txt);

        return bundle;
    }

}
