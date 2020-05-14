package org.techtown.smarket_android.Alaram;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.Class.Bookmark;
import org.techtown.smarket_android.Class.BookmarkAlarm;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.UserLogin.user_login_fragment;
import org.techtown.smarket_android.searchItemList.search_list_fragment;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class alarm_fragment extends Fragment {

    private ViewGroup viewGroup;
    private List<BookmarkAlarm> bookmarkAlarmList;
    private List<BookmarkAlarm> myBookmarkAlarmList = new ArrayList<>();
    private int time = 15;

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_main, container, false);
        get_userFile();
        get_bookmarkAlarmList();
        // 현재 로그인한 user_id와 일치하고 alarm_check이 True인 bookmarkAlarm만 조회
        get_myBookmarkAlarmList();

        // bookmarkAlarm의 시간에 따라 list 분류
        sort_bookmarkAlarmList();

        return viewGroup;
    }

    private void sort_bookmarkAlarmList(){
        if(time==15){
            // 15시로 선택된 bookmarkAlarmList만 조회
            List<BookmarkAlarm> sortedBookmarkAlarmList = new ArrayList<>();
            for (int i = 0; i < myBookmarkAlarmList.size(); i++) {
                if(myBookmarkAlarmList.get(i).getAlarm_time() == 3){
                    sortedBookmarkAlarmList.add(myBookmarkAlarmList.get(i));
                }
            }
            // 임의 시간으로 분류된 bookmarkAlarmList로 상품 조회
            for (int i = 0; i < sortedBookmarkAlarmList.size(); i++) {
                request_getting_item_price(sortedBookmarkAlarmList.get(i));
            }

        }
    }

    private void request_getting_item_price(final BookmarkAlarm bookmarkAlarm){
        String url = "http://10.0.2.2:3000/api/bookmarks/lprice"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    JSONArray data = jsonObject.getJSONArray("data");
                    if (success) {
                        // ** 북마크 리스트 조회 성공시 ** //
                        // 토큰에 user_id에 대한 정보가 들어 있기 때문에 별도 아이디검사를 하지 않아도됨
                        // bookmark_id로 조회된 상품의 데이터 정보를 가져온다
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
        }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", access_token);
                return params;
            }

            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bookmark_id", bookmarkAlarm.getBookmark_id());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    // SharedPreference의 bookmarkAlarmList 데이터를 가져온다
    private void get_bookmarkAlarmList() {
        // 저장된 bookmarkAlarmList가 있을 경우
        if (userFile.getString("bookmarkAlarmList", null) != null) {
            String bookmarkAlarm = userFile.getString("bookmarkAlarmList", null);
            Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
            }.getType();
            bookmarkAlarmList = new GsonBuilder().create().fromJson(bookmarkAlarm, listType);

            Log.d("Get bookmarkAlarmList", "bookmarkAlarmList: Complete Getting bookmarkAlarmList");
        }// 저장된 bookmarkAlarmList가 없을 경우
        else {
            bookmarkAlarmList = new ArrayList<>();
            save_bookmarkAlarmList();
        }
    }

    // SharedPreference에 bookmarkAlarmList 데이터 저장
    private void save_bookmarkAlarmList() {
        // List<BookmarkAlarm> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(bookmarkAlarmList, listType);

        // 스트링 객체로 변환된 데이터를 bookmarkFolderList에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkAlarmList", json);
        editor.apply();
    }

    // 현재 로그인한 user_id와 일치하고 alarm_check이 True인 bookmarkAlarm만 조회
    private void get_myBookmarkAlarmList(){
        for (int i = 0; i < bookmarkAlarmList.size(); i++) {
            if(bookmarkAlarmList.get(i).getUser_id().equals(user_id) && bookmarkAlarmList.get(i).getAlarm_check()){
                myBookmarkAlarmList.add(bookmarkAlarmList.get(i));
            }
        }
    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    }
}
