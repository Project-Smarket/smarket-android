package org.techtown.smarket_android.User.UserLogin;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.Alarm.AlarmReceiver;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.Bookmark.bookmark_fragment;
import org.techtown.smarket_android.User.Latest.latest_fragment;
import org.techtown.smarket_android.User.UserInfrom.userinform_fragment;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;


public class user_login_success extends Fragment {

    public static user_login_success newInstance() {
        return new user_login_success();
    }

    private ViewGroup viewGroup;


    private ConstraintLayout bookmark;
    private ConstraintLayout latest;
    private ConstraintLayout userinform;
    private ConstraintLayout clock;
    private ConstraintLayout logout;


    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private Boolean user_alarm;

    private String userID;
    private String user_nickname;
    private String access_token;
    private String refresh_token;

    private int alarm_unique_id = 1212;
    private Boolean alarm_check;
    private String[] timeList_s;
    private int alarm_time;
    private String alarm_time_s;
    private TextView set_time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.user_login_success, container, false);


        // 현재 로그인된 아이디 가져오기
        get_userFile();

        if (userID == null) {
            getActivity().finish();
        }

        // notification 요청에 사용되는 deviceToken 가져옴
        get_deviceToken();

        TextView user_nickname_textView = viewGroup.findViewById(R.id.user_tv);
        user_nickname_textView.setText(user_nickname);


        bookmark = viewGroup.findViewById(R.id.bookmark);

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmark_fragment newbookmark_fragment = new bookmark_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, newbookmark_fragment, "login").addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        latest = viewGroup.findViewById(R.id.latest);

        latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latest_fragment latest_fragment = new latest_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, latest_fragment, "login").addToBackStack(null);
                fragmentTransaction.commit();
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
        if (alarm_check) {
            on_alarm();
        }

        final Switch priceAlarm = viewGroup.findViewById(R.id.alarm_switch);
        clock = viewGroup.findViewById(R.id.clock);
        priceAlarm.setChecked(alarm_check);
        set_clock_layout(String.valueOf(alarm_check));

        priceAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알람 On 일 경우 - 알람을 Off로 설정하고, alarm_check = false 설정(알람 제거)
                if (alarm_check) {
                    alarm_check = false;
                    off_alarm();
                    set_clock_layout("false");
                    Toast.makeText(getContext(), "가격 변동 알람 : OFF", Toast.LENGTH_LONG).show();
                }
                // 알람이 Off일 경우 - 알람을 On으로 설정하고, alarm_check = true 설정(알람 설정)
                else {
                    alarm_check = true;
                    on_alarm();
                    set_clock_layout("true");
                    Toast.makeText(getContext(), "가격 변동 알람 : ON", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 시간 리스트
        timeList_s = new String[]{"15초 마다", "1분 마다", "5분 마다", "10분 마다", "30분 마다", "1시간 마다", "3시간 마다", "6시간 마다"};

        // 설정된 사용자 시간으로 String 설정
        alarm_time_s = timeList_s[alarm_time];

        // 설정된 시간 표시
        set_time = viewGroup.findViewById(R.id.set_time);
        set_time.setText(alarm_time_s);

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_time();
            }
        });


        logout = viewGroup.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 로그인된 id와 access_token 제거
                null_userFile();
                // 알람끔
                off_alarm();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_layout, user_login_fragment.newInstance(), "logout").addToBackStack(null).commit();
            }
        });

        return viewGroup;
    }

    // 알람 시간 설정 레이아웃 설정
    private void set_clock_layout(String state) {

        ImageView clock_img = viewGroup.findViewById(R.id.clock_img);
        TextView clock_tv = viewGroup.findViewById(R.id.clock_tv);
        TextView set_time = viewGroup.findViewById(R.id.set_time);

        switch (state) {
            case "true": {
                // 버튼 클릭 효과 설정
                clock.setEnabled(true);
                TypedValue ouvalue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, ouvalue, true);
                clock.setBackgroundResource(ouvalue.resourceId);

                // 레이아웃 활성화
                clock_img.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);
                clock_tv.setTextColor(getResources().getColor(R.color.colorBlack));
                set_time.setTextColor(getResources().getColor(R.color.colorgray));
                break;
            }
            case "false": {
                // 버튼 클릭 효과 삭제
                clock.setEnabled(false);

                // 레이아웃 비활성화
                clock_img.setColorFilter(getResources().getColor(R.color.color_lite_gray), PorterDuff.Mode.SRC_IN);
                clock_tv.setTextColor(getResources().getColor(R.color.color_lite_gray));
                set_time.setTextColor(getResources().getColor(R.color.color_lite_gray));
                break;
            }
        }
    }

    // 알람 시간 설정
    private void select_time() {


        // 선택된 시간 임시 저장 - "설정" 버튼 누를 시 적용
        final int[] selectedTime = new int[1];
        selectedTime[0] = -1;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("알람 시간 설정")
                .setSingleChoiceItems(timeList_s, alarm_time, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedTime[0] = which;
                    }
                })
                .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 선택된 시간이 있을 경우
                        if (selectedTime[0] >= 0) {

                            // 알람 시간 설정
                            alarm_time = selectedTime[0];
                            alarm_time_s = timeList_s[alarm_time];

                            // 설정된 시간 토스트 알람
                            Snackbar snackbar = Snackbar.make(viewGroup.findViewById(R.id.placeSnackBar), alarm_time_s + " 알람이 울립니다", 3000)
                                    .setActionTextColor(getResources().getColor(R.color.smarketyello));

                            // 스낵바 배경 색 설정
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(getResources().getColor(R.color.smarketyello));

                            // 스낵바 글씨 색 설정
                            TextView svTextView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
                            svTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack));

                            snackbar.show();
                            // 설정된 시간 표시
                            set_time.setText(alarm_time_s);

                            // 설정된 시간 저장
                            save_alarmTime();

                            // 설정된 시간으로 알람 설정
                            on_alarm();
                        }
                    }
                })
                .setNegativeButton("취소", null);

        builder.create().show();
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

    private void on_alarm() {
        // 초, 분, 시
        int SECOND = 1000;
        int MINUTE = 60 * SECOND;
        int HOUR = 60 * MINUTE;

        // 타임 리스트 : 15초, 1분, 5분, 10분, 30분, 1시간, 3시간, 6시간
        int[] timeList = new int[]{(15*SECOND), (1*MINUTE), (5*MINUTE), (10*MINUTE), (30*MINUTE), (1*HOUR), (3*HOUR), (6*HOUR)};
        int set_time = timeList[alarm_time];

        // 설정된 시간으로 alarmManager 설정
        set_alarmManager(set_time);
    }

    // alarmManager 설정
    private void set_alarmManager(int set_time) {
        // 현재 시간

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        // 설정된 시간 AlarmReceiver로 전달
        intent.putExtra("set_time", set_time);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), alarm_unique_id, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + set_time, alarmIntent);

        }
        save_alarmCheck();
    }


    private void save_alarmCheck() {
        String key = userID + "/alarm/check";
        SharedPreferences.Editor editor = userFile.edit();
        editor.putBoolean(key, alarm_check);
        editor.apply();
    }

    private void save_alarmTime() {
        String key = userID + "/alarm/time";
        SharedPreferences.Editor editor = userFile.edit();
        editor.putInt(key, alarm_time);
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
                                fragmentTransaction.replace(R.id.main_layout, userinform_fragment.newInstance(), "login").commit();
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
                //Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                error_handling(error);
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

    // Error Handling - request 오류(bookmarkList 조회, bookmarkFolder 삭제, bookmark 삭제 오류) 처리 - 실패 시 access-token 갱신 요청
    private void error_handling(VolleyError error) {
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
                    refresh_accessToken();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    // access-token 갱신 요청 후 폴더 목록 재요청 - 실패 시 logout
    private void refresh_accessToken() {
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
                        get_deviceToken();

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
                        fragmentManager.beginTransaction().replace(R.id.main_layout, user_login_fragment.newInstance(), "login").commit();
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
        Log.d(TAG, "onCreate: sadfasdfsa");
        get_userFile();
        Fragment logout = getFragmentManager().findFragmentByTag("logout");
        if (logout != null && userID == null) {
            Log.d(TAG, "onCreate: logout");
            getActivity().finish();
        }
    }


    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        userID = userFile.getString("user_id", null);
        user_nickname = userFile.getString("user_nickname", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
        alarm_check = userFile.getBoolean(userID + "/alarm/check", false);
        alarm_time = userFile.getInt(userID + "/alarm/time", 1);
        Log.d("TOKEN", "access_token: " + access_token);
        Log.d("TOKEN", "refresh_token: " + refresh_token);
    }
}
