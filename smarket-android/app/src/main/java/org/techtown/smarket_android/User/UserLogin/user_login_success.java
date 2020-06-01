package org.techtown.smarket_android.User.UserLogin;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.Alarm.AlarmReceiver;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.Bookmark.newbookmark_fragment;
import org.techtown.smarket_android.User.UserInfrom.userinform_fragment;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class user_login_success extends Fragment {

    public static user_login_success newInstance() {
        return new user_login_success();
    }

    private ViewGroup viewGroup;

    private TextView userId_textView;

    private ConstraintLayout bookmark;
    private ConstraintLayout recent;
    private ConstraintLayout userinform;
    private ConstraintLayout logout;

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private Boolean user_alarm;

    private String userID;
    private String access_token;
    private String refresh_token;
    private String user_name = "";

    private int alarm_unique_id = 1212;
    private Boolean alarm_check = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.user_login_success, container, false);


        // 현재 로그인된 아이디 가져오기
        get_userFile();

        if(userID==null) {
            getActivity().finish();
        }


        // 현재 로그인된 아이디와 일치하는 사용자 이름을 가져온다
        get_userName();
        // notification 요청에 사용되는 deviceToken 가져옴
        get_deviceToken();

        userId_textView = viewGroup.findViewById(R.id.user_tv);


        bookmark = viewGroup.findViewById(R.id.bookmark);

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, newbookmark_fragment.newInstance(),"login");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        recent = viewGroup.findViewById(R.id.recent);

        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, retrofit_fragment.newInstance(),"login");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
            }
        });

        userinform = viewGroup.findViewById(R.id.userinform);

        userinform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원 정보 수정 전 비밀번호 확인
                password_confirm();

            }
        });


        // alarm_check : true로 설정되어 있을경우 알람 시작
        if(alarm_check){
            on_alarm();
        }

        final Switch priceAlarm = viewGroup.findViewById(R.id.priceAlarm_switch);
        priceAlarm.setChecked(alarm_check);

        priceAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알람 On 일 경우 - 알람을 Off로 설정하고, alarm_check = false 설정(알람 제거)
                if(alarm_check) {
                    alarm_check = false;
                    off_alarm();
                    Toast.makeText(getContext(), "가격 변동 알람 : OFF", Toast.LENGTH_LONG).show();
                }
                // 알람이 Off일 경우 - 알람을 On으로 설정하고, alarm_check = true 설정(알람 설정)
                else{
                    alarm_check = true;
                    on_alarm();
                    Toast.makeText(getContext(), "가격 변동 알람 : ON", Toast.LENGTH_LONG).show();
                }
            }
        });

        logout = viewGroup.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 로그인된 id와 access_token 제거
                null_userFile();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_layout, user_login_fragment.newInstance(),"logout").addToBackStack(null).commit();
            }
        });

        return viewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        userID = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
        alarm_check = userFile.getBoolean("alarm_check", false);
        Log.d("TOKEN", "access_token: " + access_token);
        Log.d("TOKEN", "refresh_token: " + refresh_token);
    }

    // 설정된 알람 삭제
    private void off_alarm() {

        AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getContext(), alarm_unique_id, intent, PendingIntent.FLAG_NO_CREATE);

        if (sender == null) {
            // TODO: 이미 설정된 알람이 없는 경우
        } else {
            // TODO: 이미 설정된 알람이 있는 경우
            sender = PendingIntent.getBroadcast(getContext(), alarm_unique_id, intent, 0);

            am.cancel(sender);
            sender.cancel();

            //save_userInfoList();
            save_alarmCheck();
        }

    }

    // 알람 설정
    private void on_alarm() {

        // 알람 시간 설정
        Calendar calendar = Calendar.getInstance();

        // 알람 10분 - 오후 12시
        if (calendar.get(Calendar.SECOND) >= 0 && calendar.get(Calendar.SECOND) < 15) {
            calendar.set(Calendar.SECOND, 15);
        }
        // 알람 20분 - 오후 3시
        else if (calendar.get(Calendar.SECOND) >= 15 && calendar.get(Calendar.SECOND) < 30) {
            calendar.set(Calendar.SECOND, 30);
        }
        // 알람 30분 - 오후 6시
        else if (calendar.get(Calendar.SECOND) >= 30 && calendar.get(Calendar.SECOND) < 45) {
            calendar.set(Calendar.SECOND, 45);
        } // 알람 40분 - 오후 9시
        else if (calendar.get(Calendar.SECOND) >= 45 && calendar.get(Calendar.SECOND) < 60) {
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
            calendar.set(Calendar.SECOND, 0);
        }
        set_alarmManager(calendar);
    }

    // alarmManager 설정
    private void set_alarmManager(Calendar calendar) {
        // 현재 시간
        Date date = new Date();

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), alarm_unique_id, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("알람", date.toString() + " : 알람이 " + calendar.get(Calendar.SECOND) + "분로 설정되었습니다");
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

        }
        save_alarmCheck();
    }


    private void save_alarmCheck(){
        SharedPreferences.Editor editor = userFile.edit();
        editor.putBoolean("alarm_check", alarm_check);
        editor.apply();
    }

    // 회원 정보 수정 전 비밀번호 확인
    private void password_confirm() {

        // custom_dialog_editText
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_edittext, null);
        final EditText password = dialogView.findViewById(R.id.dialog_editText);
        password.setHint("비밀번호를 입력해주세요");
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // 비밀번호 입력 다이얼로그
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setTitle("비밀번호 확인");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userPW = password.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.main_layout, userinform_fragment.newInstance(),"login")
                                        .addToBackStack(null).commit();
                            } else {
                                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                        .setMessage("비밀번호가 일치하지 않습니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                alertDialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                user_passwordconfirm passwordconfirm_request = new user_passwordconfirm(userPW, getActivity(), responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(passwordconfirm_request);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // 다이얼로그 생성시 EditText 활성화 1
        dialog.show();
        if (password.requestFocus())
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(password, 0); // 다이얼로그 생성시 EditText 활성화 2

    }

    // 서버로부터 device_token 가져옴
    public void get_deviceToken() {
        String url = getContext().getResources().getString(R.string.fcmEndpoint) + "/select";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    JSONObject data = jsonObject.getJSONObject("data");
                    String device_token = data.getString("deviceToken");
                    Log.d("TOKEN", "device_token: " + device_token);
                    save_deviceToken(device_token);
                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                params.put("x-access-token", access_token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    // SharedPreference device_token 저장
    private void save_deviceToken(String device_token) {
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("device_token", device_token);
        editor.apply();
    }

    private void get_userName() {
        String loginUrl = getString(R.string.usersEndpoint) + "/" + userID;  // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.GET, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (success) {
                        // ** 로그인 성공시 ** //
                        user_name = data.getString("name");
                        userId_textView.setText(user_name);
                    } else if (!success) {
                        // ** 로그인 실패 시 ** //
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String request_type = "request_get_userName";
                error_handling(error, request_type);
            }
        }
        ) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userID);
                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", access_token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    // Error Handling - request 오류(bookmarkList 조회, bookmarkFolder 삭제, bookmark 삭제 오류) 처리 - 실패 시 access-token 갱신 요청
    private void error_handling(VolleyError error, String request_type ) {
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
                    refresh_accessToken(request_type);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    // access-token 갱신 요청 후 폴더 목록 재요청 - 실패 시 logout
    private void refresh_accessToken(final String request_type) {
        String url = getString(R.string.authEndpoint) + "/refresh"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
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
                        switch (request_type) {
                            // 사용자 이름 가져오기 요청
                            case "request_get_userName":
                                get_userName();
                                break;
                        }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("로그아웃")
                .setMessage("재로그인이 필요합니다.")
                .setCancelable(false)
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        null_userFile();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_layout, user_login_fragment.newInstance(),"login").commit();
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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        get_userFile();
        Fragment logout = getFragmentManager().findFragmentByTag("logout");
        if(logout!=null && userID==null){
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        null_userFile();
    }
}
