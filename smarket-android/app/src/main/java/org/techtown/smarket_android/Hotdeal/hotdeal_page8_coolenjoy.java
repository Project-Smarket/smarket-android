package org.techtown.smarket_android.Hotdeal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import org.techtown.smarket_android.Class.Hotdeal;
import org.techtown.smarket_android.R;

import java.util.ArrayList;

public class hotdeal_page8_coolenjoy extends Fragment {
    private ViewGroup viewGroup;
    private RecyclerView recyclerView;
    private hotdealListAdapter hotdealListAdapter;
    private ArrayList<Hotdeal> hotdealList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.hotdeal_list, container, false);

        hotdealList = new ArrayList<>();

        recyclerView = viewGroup.findViewById(R.id.hotdeal_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        hotdealListAdapter = new hotdealListAdapter(getActivity(), getContext(), hotdealList);
        recyclerView.setAdapter(hotdealListAdapter);

        request_ruliweb();

        return viewGroup;
    }

    private void request_ruliweb() {
        String url = "http://10.0.2.2:3000/api/crawling/coolenjoy/1"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
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
                    } else if (!success)
                        // ** 북마크 조회 실패시 ** //
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
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