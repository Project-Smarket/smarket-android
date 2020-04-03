package org.techtown.smarket_android.User;

import android.content.Context;
import android.os.Bundle;
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

import org.techtown.smarket_android.R;

import java.util.HashMap;
import java.util.Map;

public class user_login_fragment extends Fragment {

    ViewGroup viewGroup;

    EditText login_id;
    EditText login_pw;
    Button login_btn;

    InputMethodManager imm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.user_login_main, container, false);

        login_id = viewGroup.findViewById(R.id.login_id_et);
        login_pw = viewGroup.findViewById(R.id.login_pw_et);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        login_pw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Enter키눌렀을떄 처리
                    hideKeyboard(); // 키보드 입력 후 엔터 입력시 키보드 창 내림
                    login(); // 로그인 실행
                    return true;
                }
                return false;
            }
        });

        login_btn = viewGroup.findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        final TextView goto_register_tv = viewGroup.findViewById(R.id.goto_register_tv);
        goto_register_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_register();
            }
        });
        return viewGroup;
    }

    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(login_id.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(login_pw.getWindowToken(), 0);
    }// 키보드 입력 후 엔터 입력시 키보드 창 내림

    private void login(){
        String url = "http://10.0.2.2:3000/login"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, user_login_success.newInstance()).commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error+"", Toast.LENGTH_LONG).show();

            }
        }
        ){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String id = login_id.getText().toString();
                String pw = login_pw.getText().toString();
                params.put("id", id);
                params.put("password", pw);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    private void goto_register(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_layout, user_register_fragment.newInstance()).commit();
    }
}

