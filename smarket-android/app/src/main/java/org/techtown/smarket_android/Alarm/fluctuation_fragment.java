package org.techtown.smarket_android.Alarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.DTO_Class.Alarm;
import org.techtown.smarket_android.DTO_Class.DTO;
import org.techtown.smarket_android.DTO_Class.Fluctuation;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Search.RecyclerDecoration;
import org.techtown.smarket_android.Search.search_detail_fragment;
import org.techtown.smarket_android.User.Latest.latest_fragment;
import org.techtown.smarket_android.User.UserLogin.user_login_fragment;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class fluctuation_fragment extends Fragment {


    private ViewGroup viewGroup;

    private LineChart lineChart;
    private RecyclerView ftRecyclerView;
    private ftAdapter ftAdapter;
    private List<Fluctuation> fluctuationList;


    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;

    private ImageView fluctuation_alarm;
    private boolean item_alarm;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.price_fluctuation, container, false);

        // 툴바 설정
        set_toolbar();

        // Toolbar의 메뉴 버튼 활성화
        setHasOptionsMenu(true);

        // 현재 로그인된 아이디 - userFile에 저장된 user_id 가져오기
        get_userFile();

        // 번들로 데이터 가져옴
        receive_bundle(savedInstanceState);

        // lineChart 설정
        set_lineChart();

        // 리사이클러뷰 설정
        set_recyclerView();


        return viewGroup;
    }

    private void set_lineChart() {
        lineChart = viewGroup.findViewById(R.id.fluctuation_lineChart);

        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < fluctuationList.size(); i++) {
            values.add(new Entry(i + 1, Integer.parseInt(fluctuationList.get(i).getLprice())));
        }

        LineDataSet set = new LineDataSet(values, "fluctuation");
        set.setColor(getResources().getColor(R.color.colorBlack));
        set.setCircleColor(getResources().getColor(R.color.colorBlack));
        set.setValueTextSize(12);
        set.setLineWidth(3);
        set.setCircleRadius(6);
        set.setCircleHoleRadius(3);
        set.setCircleHoleColor(getResources().getColor(R.color.colorWhite));
        set.setLabel("");


        LineData data = new LineData(set);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setLabelCount(0, false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        //lineChart.setDrawGridBackground(true);
        lineChart.setNoDataText("가겨 변동 내역이 없습니다");
        lineChart.setNoDataTextColor(getResources().getColor(R.color.colorBlack));
        lineChart.getLegend().setEnabled(false);
        lineChart.setDescription(null);
        lineChart.setExtraOffsets(30, 30, 30, 10);
        lineChart.setScaleEnabled(false);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(getContext(), "눌림" + e.getX(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });


        lineChart.setData(data);
    }

    // alarmList 데이터 가져옴
    private void set_recyclerView() {
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(8);

        ftRecyclerView = viewGroup.findViewById(R.id.fluctuation_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        ftRecyclerView.setLayoutManager(linearLayoutManager);
        ftRecyclerView.addItemDecoration(spaceDecoration);
        ftAdapter = new ftAdapter(getActivity(), getActivity(), fluctuationList);
        ftRecyclerView.setAdapter(ftAdapter);
    }


    private void set_toolbar() {
        Toolbar toolbar = viewGroup.findViewById(R.id.fluctuation_toolbar);
        toolbar.setTitle("가격 변동 내역");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // 뒤로가기 버튼 활성화
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void receive_bundle(Bundle bundle) {
        bundle = getArguments();
        final DTO item_data = bundle.getParcelable("item_data");
        bundle.clear();
        final String id = item_data.getId();
        String item_title = item_data.getItem_title();
        String item_image = item_data.getItem_image();
        item_alarm = Boolean.parseBoolean(item_data.getItem_alarm());

        // id를 통해 SharedPreference fluctuationList 데이터 가져옴
        get_fluctuation(id);

        // 제품명 설정
        TextView fluctuation_textView = viewGroup.findViewById(R.id.fluctuation_textView);
        fluctuation_textView.setText(item_title);

        // 이미지 설정
        ImageView fluctuation_image = viewGroup.findViewById(R.id.fluctuation_ImageView);
        Glide.with(getContext()).asBitmap().load(item_image).into(fluctuation_image);

        fluctuation_alarm = viewGroup.findViewById(R.id.fluctuation_alarm);
        if (item_alarm) {
            fluctuation_alarm.setColorFilter(getResources().getColor(R.color.smarketyello), PorterDuff.Mode.SRC_IN);
        } else {
            fluctuation_alarm.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);
        }
        fluctuation_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : 알람 ON/OFF
                if(item_alarm){
                    request_alarm(id,"false");
                }
                else {
                    request_alarm(id,"true");
                }
            }
        });

        Button fluctuation_button = viewGroup.findViewById(R.id.goto_detail);
        fluctuation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search_detail_fragment search_detail_fragment = new search_detail_fragment();

                Bundle bundle = set_bundle(item_data);
                search_detail_fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, search_detail_fragment, "fluctuation").addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private Bundle set_bundle(DTO item_data) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("item_data", item_data);
        return bundle;
    }

    public void request_alarm(final String id, final String value) {
        String url = getActivity().getResources().getString(R.string.bookmarksEndpoint) + "/alarm";

        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    // 알람 ON/OFF 설정 성공시
                    if(success){
                        if(value.equals("true")){
                            item_alarm = true;
                            fluctuation_alarm.setColorFilter(getResources().getColor(R.color.smarketyello), PorterDuff.Mode.SRC_IN);
                            Toast.makeText(getContext(), "상품 알람 : ON", Toast.LENGTH_SHORT).show();
                        }else if(value.equals("false")){
                            item_alarm = false;
                            fluctuation_alarm.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);
                            Toast.makeText(getContext(), "상품 알람 : OFF", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // 알람 ON/OFF 설정 실패 시
                    else{

                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                error_handling(error, id, value);
            }
        }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                params.put("x-access-token", access_token);
                return params;
            }

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("item_alarm", value);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    // Error Handling - request 오류(bookmarkList 조회, bookmarkFolder 삭제, bookmark 삭제 오류) 처리 - 실패 시 access-token 갱신 요청
    private void error_handling(VolleyError error, String id, String value) {
        NetworkResponse response = error.networkResponse;
        if (error instanceof AuthFailureError && response != null) {
            try {
                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(res);
                JsonObject data = element.getAsJsonObject().get("data").getAsJsonObject();
                String name = data.get("name").getAsString();
                String msg = data.get("msg").getAsString();

                // access-token 만료 시 refresh-token을 통해 토큰 갱신
                if (name.equals("TokenExpiredError") && msg.equals("jwt expired"))
                    refresh_accessToken(id, value);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    // access-token 갱신 요청 후 폴더 목록 재요청 - 실패 시 logout
    private void refresh_accessToken(final String id, final String value) {
        String url = getActivity().getString(R.string.authEndpoint) + "/refresh"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        // ** access-token 갱신 성공 시 ** // access-token 업데이트
                        String data = jsonObject.getString("data");
                        // SharedPreference 의 access-token 갱신
                        update_accessToken(data);
                        request_alarm(id ,value);

                    } else if (!success)
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // ** access-token 갱신 실패 시 ** // refresh-token 만료로 인해 logout
                Log.d("REQUESTERROR", "onErrorResponse: refresh-toke이 만료되었습니다");
                NetworkResponse response = error.networkResponse;
                if (error instanceof AuthFailureError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        JsonParser parser = new JsonParser();
                        JsonElement element = parser.parse(res);
                        JsonObject data = element.getAsJsonObject().get("data").getAsJsonObject();
                        String name = data.get("name").getAsString();
                        String msg = data.get("msg").getAsString();

                        // refresh-token 만료되어 logout
                        if (name.equals("TokenExpiredError") && msg.equals("jwt expired"))
                            logout();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-refresh-token", refresh_token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    // 만료된 access-token을 새로 갱신한 access-token으로 교체
    private void update_accessToken(String new_token) {
        access_token = new_token;
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("access_token", access_token); //Second라는 key값으로 infoSecond 데이터를 저장한다.
        editor.commit();
    }

    // 사용자 정보를 지우고 로그인 화면으로 이동
    private void logout() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("로그아웃")
                .setMessage("로그인이 필요합니다.")
                .setCancelable(false)
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        null_userFile();
                        FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_layout, user_login_fragment.newInstance()).commit();
                    }
                });
        builder.create();
        builder.show();
    }

    // 현재 로그인된 id와 access_token 제거
    private void null_userFile() {
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("user_id", null);
        editor.putString("access_token", null);
        editor.putString("refresh_token", null);
        editor.apply();
    }

    // SharedPreference의 alarmList 데이터를 가져온다
    private void get_fluctuation(String id) {
        String key = user_id + "/alarmList/" + id;
        // 저장된 alarmList 있을 경우
        if (userFile.getString(key, null) != null) {
            String key_fluctuationList = userFile.getString(key, null);
            Type listType = new TypeToken<ArrayList<Fluctuation>>() {
            }.getType();
            fluctuationList = new GsonBuilder().create().fromJson(key_fluctuationList, listType);

        }// 저장된 alarmList 없을 경우
        else {
            fluctuationList = new ArrayList<>();
        }
    }

    // userFile에 저장된 user_id 가져오기
    private void get_userFile() {
        userFile = getContext().getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    }

    // toolbar 뒤로가기 메소드
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().remove(fluctuation_fragment.this).commit();
                fm.popBackStack();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
