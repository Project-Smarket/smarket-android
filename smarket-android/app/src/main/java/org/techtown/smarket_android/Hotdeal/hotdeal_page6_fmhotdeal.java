package org.techtown.smarket_android.Hotdeal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.DTO_Class.Hotdeal;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Search.RecyclerDecoration;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

public class hotdeal_page6_fmhotdeal extends Fragment {

    private ViewGroup viewGroup;
    private RecyclerView recyclerView;
    private hotdealListAdapter hotdealListAdapter;
    private ArrayList<Hotdeal> hotdealList;

    private int page_num = 1;

    private boolean isMoreLoad = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hotdealList = new ArrayList<>();
        request_fmhotdeal();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.hotdeal_list, container, false);


        String site_name = "FM핫딜";
        // 아이템 줄간격 설정
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(10);

        recyclerView = viewGroup.findViewById(R.id.hotdeal_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(spaceDecoration);
        hotdealListAdapter = new hotdealListAdapter(getActivity(), getContext(), hotdealList, site_name);
        recyclerView.setAdapter(hotdealListAdapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // 스크롤이 가장 위에 있을 때
                if (!recyclerView.canScrollVertically(-1)) {
                    // 스크롤 가장 아래로 내려왔을 때
                } else if (!recyclerView.canScrollVertically(1)) {
                    if (!isMoreLoad) {
                        isMoreLoad = true;
                        request_fmhotdeal();
                    }
                }
            }
        });

        return viewGroup;
    }

    private void request_fmhotdeal() {
        String url = getString(R.string.crawlingEndpoint) + "/fmkorea/"+page_num; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    JSONArray data = jsonObject.getJSONArray("data");

                    if (success) {
                        // ** 뽐뿌 조회 성공시 ** //
                        for (int i = 0; i < data.length(); i++) {
                            String category = data.getJSONObject(i).getString("category");
                            String title = data.getJSONObject(i).getString("title");
                            String url = data.getJSONObject(i).getString("Url");
                            String replyCount = data.getJSONObject(i).getString("replyCount");
                            if (replyCount.equals(""))
                                replyCount = "0";
                            String hit = data.getJSONObject(i).getString("hit");
                            String time = data.getJSONObject(i).getString("time");
                            Hotdeal hotdeal = new Hotdeal(category, title, url, replyCount, hit, time);
                            hotdealList.add(hotdeal);
                            hotdealListAdapter.notifyDataSetChanged();
                        }
                        isMoreLoad = false;
                        page_num += 1;
                    } else if (!success){

                        // ** 북마크 조회 실패시 ** //
                        //Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("REQUESTERROR", "onErrorResponse: " + error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}

