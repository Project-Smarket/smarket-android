package org.techtown.smarket_android.User.UserLogin;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.android.volley.ClientError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.NewSearch.newsearch_fragment;
import org.techtown.smarket_android.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class user_login_fragment extends Fragment {

    ViewGroup viewGroup;

    private EditText login_id;
    private EditText login_pw;
    private Button login_btn;

    InputMethodManager imm;

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;
    public String device_token = "";

    public RequestQueue queue;


    public static user_login_fragment newInstance() {
        return new user_login_fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.user_login_main, container, false);

        /* user 정보가 유효한지 검사
         * validate_user()
         * 유효하면 로그인 성공화면으로 이동
         * 유효하지 않으면 로그인 해야함
         */
        validate_user();

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

    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(login_id.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(login_pw.getWindowToken(), 0);
    }// 키보드 입력 후 엔터 입력시 키보드 창 내림


    private void login() {
        String loginUrl = getString(R.string.authEndpoint) + "/login";  // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        // ** 로그인 성공시 ** //
                        String user_id = login_id.getText().toString(); // userFile에 저장할 user_id
                        JSONObject data = jsonObject.getJSONObject("data");
                        String access_token = data.getString("accessToken"); // userFile에 저장할 access_token
                        String refresh_token = data.getString("refreshToken");
                        set_userFile(user_id, access_token, refresh_token); // userFile에 user_id와 access_token을 저장

                        createToken(access_token, getContext());

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_layout, user_login_success.newInstance()).commit(); // 로그인 성공화면으로 이동
                    } else if(!success)
                        // ** 로그인 실패 시 ** //
                        Toast.makeText(getContext(), jsonObject.toString() , Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();

                }
                }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error_handling(error);
            }
        }
        ) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String user_id = login_id.getText().toString();
                String password = login_pw.getText().toString();
                params.put("user_id", user_id);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void createToken(String a_token, final Context mContext) {
        //파이어베이스 API에서 현재 토큰을 검색하는 메소드
        final String access = a_token;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(),

                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        //instanceIdResult.getToken(); 토큰 조회
                        sendRegistrationToServer(access, instanceIdResult.getToken(), mContext);
                    }
                }
        );
    }

    public void sendRegistrationToServer(String a_token, String d_token, Context mContext) { // 토큰을 서버로 보내는 메소드

        final String access = a_token;
        final String device = d_token;
        String url = mContext.getResources().getString(R.string.fcmEndpoint) + "/receive"; // getString 하면 오류남 ㅠㅠ
        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("OK", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //에러 추가
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                params.put("x-access-token", access);
                params.put("x-device-token", device);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public static void getDeviceToken(String a_token, Context mContext) {
        final String access = a_token;
        String url = mContext.getResources().getString(R.string.fcmEndpoint) + "/select";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    JSONObject data = jsonObject.getJSONObject("data");
                    String token = data.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                params.put("x-access-token", access);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void error_handling(VolleyError error){
        NetworkResponse response = error.networkResponse;
        if (error instanceof ClientError && response != null) {
            try {
                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                Log.d("error", "onErrorResponse: " + res);
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(res);
                JsonElement comment_element = element.getAsJsonObject().get("comment");
                String comment = " ";
                if(!comment_element.isJsonNull())
                    comment = comment_element.getAsString();
                Toast.makeText(getContext(), comment,  Toast.LENGTH_LONG).show();
                //String name = data.get("name").getAsString();
               // String msg = data.get("msg").getAsString();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void generate_userFile(){
        userFile = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
    } // user의 로그인 정보를 젖아하는 userFile 생성

    private void set_userFile(String user_id, String access_token, String refresh_token){
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("user_id", user_id); //First라는 key값으로 infoFirst 데이터를 저장한다.
        editor.putString("access_token", access_token); //Second라는 key값으로 infoSecond 데이터를 저장한다.
        editor.putString("refresh_token", refresh_token);
        editor.commit(); //완료한다.
    } // 로그인 시 user의 아이디와 토큰 정보를 저장

    private void validate_user(){
        generate_userFile();
        String user_id = userFile.getString("user_id", null);
        String access_token = userFile.getString("access_token", null);

        if(user_id != null && access_token != null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_layout, user_login_success.newInstance()).commit(); // 로그인 성공화면으로 이동

        }
    }

    private void goto_register() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_layout, user_register_fragment.newInstance(getContext())).commit();
    }
}

