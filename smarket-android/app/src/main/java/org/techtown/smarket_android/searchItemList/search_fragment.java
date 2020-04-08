package org.techtown.smarket_android.searchItemList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.user_login_success;
import org.techtown.smarket_android.User.user_register_request;
import org.techtown.smarket_android.searchItemList.Request.searchRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;


public class search_fragment extends Fragment {
    private ViewGroup viewGroup;
    private Button search_btn;
    InputMethodManager imm;
    EditText search_text;
    JSONArray key;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_main, container, false);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        search_text = viewGroup.findViewById(R.id.search_value);
        search_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(); // 키보드 입력 후 엔터 입력시 키보드 창 내림
                    return true;
                }
                return false;
            }
        });

        search_btn = (Button) viewGroup.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    getJson();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                search_list_fragment slf = new search_list_fragment();
                Bundle bundle = setBundle();
                slf.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, slf).addToBackStack(null).commit();

            }
        });

        return viewGroup;
    }

    private Bundle setBundle() {
        Bundle bundle = new Bundle();

        String textView = search_text.getText().toString();
        bundle.putString("searchName", textView);

        return bundle;
    }

    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
    }// 키보드 입력 후 엔터 입력시 키보드 창 내림

//    private void getJson() throws UnsupportedEncodingException {
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    key = jsonObject.getJSONArray("items"); // json으로 검색한 결과 객체
//
//                    JSONObject item = key.getJSONObject(0); //원하는 json 결과 인덱스 접근
//                    itemTitle = item.getString("title"); // 0번 인덱스 객체의 결과값 중 title 선택
//
//                    Bundle bundle = setBundle();
//                    search_list_fragment slf = new search_list_fragment();
//                    slf.setArguments(bundle);
//                    try {
//                        Toast.makeText(getContext(), key.getJSONObject(0).getString("title"), Toast.LENGTH_SHORT );
//                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.main_layout, slf).addToBackStack(null).commit();
//                        Log.d(TAG, "onResponse: 성공");
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        searchRequest searchRequest = new searchRequest(search_text.getText().toString(), responseListener, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), error + "", Toast.LENGTH_LONG).show();
//            }
//        });
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        queue.add(searchRequest);
//    }
}