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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.MainNavigation.AlarmReceiver;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.Bookmark.bookmark_item_list_fragment;
import org.techtown.smarket_android.User.Bookmark.newbookmark_fragment;
import org.techtown.smarket_android.User.UserInfrom.userinform_fragment;
import org.techtown.smarket_android.User.recent.recent_fragment;
import org.techtown.smarket_android.smarketClass.userInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private List<userInfo> userInfoList;
    private Boolean user_alarm;

    private String userID;
    private String access_token;
    private String refresh_token;
    private String user_name = "";

    private int alarm_unique_id = 1212;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.user_login_success, container, false);


        // 현재 로그인된 아이디 가져오기
//        get_userFile();

        if(userID==null) {
            getActivity().finish();
        }

        // 현재 로그인된 아이디와 일치하는 사용자 정보(alarm_check)를 가져온다
        get_userInfoList();
        get_userAlarm();

        // 로그인된 아이디의 alarm_check = true 일 경우 알람 설정 됨
        if(user_alarm!=null){
            if (user_alarm) {
                on_alarm();
            }
        }
        // 현재 로그인된 아이디와 일치하는 사용자 이름을 가져온다
        get_userName();
        get_deviceToken();

        userId_textView = viewGroup.findViewById(R.id.user_tv);


        bookmark = viewGroup.findViewById(R.id.bookmark);

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, newbookmark_fragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        recent = viewGroup.findViewById(R.id.recent);

        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, recent_fragment.newInstance(),"login");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        userinform = viewGroup.findViewById(R.id.userinform);

        userinform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원 정보 수정 전 비밀번호 확인
                passwordconfirm();

            }
        });


        final Switch autoReboot = viewGroup.findViewById(R.id.autoreboot_switch);
        autoReboot.setChecked(user_alarm);

        autoReboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알람 On 일 경우 - 알람을 Off로 설정하고, alarm_check = false 설정(알람 제거)
                if(user_alarm) {
                    user_alarm = false;
                    off_alarm();
                    Toast.makeText(getContext(), "가격 변동 알람 : OFF", Toast.LENGTH_LONG).show();
                }
                // 알람이 Off일 경우 - 알람을 On으로 설정하고, alarm_check = true 설정(알람 설정)
                else{
                    user_alarm = true;
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
        Log.d("TOKEN", "access_token: " + access_token);
        Log.d("TOKEN", "refresh_token: " + refresh_token);
    }

    // SharedPreference의 userInfoList 데이터를 가져온다
    private void get_userInfoList() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        // 저장된 userInfoList가 있을 경우
        if (userFile.getString("userInfoList", null) != null) {
            String info = userFile.getString("userInfoList", null);
            Type listType = new TypeToken<ArrayList<userInfo>>() {
            }.getType();
            userInfoList = new GsonBuilder().create().fromJson(info, listType);

        }// 저장된 userInfoList가 없을 경우
        else {
            userInfoList = new ArrayList<>();
        }
    }

    // 현재 로그인 된 아이디와 일치하는 사용자정보(alarm_check)를 가져옴
    private void get_userAlarm(){
        for (int i = 0; i < userInfoList.size(); i++) {
            if(userInfoList.get(i).getUser_id().equals(userID)){
                user_alarm = userInfoList.get(i).getAlarm_check();
            }
        }
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

            save_userInfoList();
        }

    }

    // 알람 설정
    private void on_alarm() {

        // 알람 시간 설정
        Calendar calendar = Calendar.getInstance();

        // 알람 10분 - 오후 12시
        if (calendar.get(Calendar.SECOND) >= 0 && calendar.get(Calendar.SECOND) < 10) {
            calendar.set(Calendar.SECOND, 10);
        }
        // 알람 20분 - 오후 3시
        else if (calendar.get(Calendar.SECOND) >= 10 && calendar.get(Calendar.SECOND) < 20) {
            calendar.set(Calendar.SECOND, 20);
        }
        // 알람 30분 - 오후 6시
        else if (calendar.get(Calendar.SECOND) >= 20 && calendar.get(Calendar.SECOND) < 30) {
            calendar.set(Calendar.SECOND, 30);
        } // 알람 40분 - 오후 9시
        else if (calendar.get(Calendar.SECOND) >= 30 && calendar.get(Calendar.SECOND) < 40) {
            calendar.set(Calendar.SECOND, 40);
        } else {
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
            calendar.set(Calendar.SECOND, 10);
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
        save_userInfoList();
    }

    // 변경된 alarm_check로 userInfoList 저장
    private void save_userInfoList() {
        for (int i = 0; i < userInfoList.size(); i++) {
            if(userInfoList.get(i).getUser_id().equals(userID)){
                userInfoList.get(i).setAlarm_check(user_alarm);
            }
        }
        // List<userInfo> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<userInfo>>() {}.getType();
        String json = new GsonBuilder().create().toJson(userInfoList, listType);

        // 스트링 객체로 변환된 데이터를 userInfoList에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("userInfoList", json);
        editor.apply();

    }

    // 회원 정보 수정 전 비밀번호 확인
    private void passwordconfirm() {
        final EditText password = new EditText(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("비밀번호 확인")       // 제목 설정
                .setView(password) // EditText 삽입
                // 확인 버튼
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userPW = password.getText().toString();
                        Log.d("PASSWORD", "onClick: " + userPW);
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
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 창 띄우기
        builder.show();

    }

    // 현재 로그인된 id와 access_token, refresh_token 제거
    private void null_userFile() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("user_id", null);
        editor.putString("access_token", null);
        editor.putString("refresh_token", null);
        editor.commit();
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
                //error_handling(error);
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
