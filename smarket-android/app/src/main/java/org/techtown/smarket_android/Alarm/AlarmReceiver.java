package org.techtown.smarket_android.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.DTO_Class.DTO;
import org.techtown.smarket_android.DTO_Class.Fluctuation;
import org.techtown.smarket_android.R;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "alarmmanager";
    private int alarm_unique_id = 1212;

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;
    private String device_token;

    private List<DTO> alarmList;
    private List<Fluctuation> fluctuationList;

    private String alarm_date;
    private String fluctation_date;

    @Override
    public void onReceive(Context context, Intent intent) {

        // SharedPreference의 user 정보 가져옴
        get_userFile(context);

        // SharedPreference의 alarmList 정보 가져옴
        get_alarmList();

        Date currentTime = Calendar.getInstance().getTime();
        alarm_date = new SimpleDateFormat("yy/MM/dd", Locale.getDefault()).format(currentTime);
        fluctation_date = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(currentTime);

        Toast.makeText(context, "북마크 상품 가격을 재조회합니다", Toast.LENGTH_LONG).show();


        // 가격 조회 요청
        request_get_item_lprice(context);


        // 알람 시간과 alarmmanager를 재설정합니다.
        //set_time(context, intent);

        // 다음 알람 시간 설정
        int set_time = intent.getIntExtra("set_time", 0);

        if (set_time == 0) {
            Toast.makeText(context, "알람이 꺼졌습니다", Toast.LENGTH_LONG).show();
        } else {
            set_alarmManager(context, intent, set_time);
        }


    }

    // 설정된 시간으로 alarmManager 재설정
    private void set_alarmManager(Context context, Intent intent, int set_time) {


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("set_time", set_time);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarm_unique_id, intent, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + set_time, alarmIntent);
        }
    }

    // Request - 최저가 알림 설정된 상품의 가격 재조회 요청
    private void request_get_item_lprice(final Context context) {
        String url = context.getString(R.string.bookmarksEndpoint) + "/lprice"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        // ** 북마크 리스트 조회 성공시 ** //
                        // 토큰에 user_id에 대한 정보가 들어 있기 때문에 별도 아이디검사를 하지 않아도됨
                        // bookmark_id로 조회된 상품의 데이터 정보를 가져온다
                        // data의 null 값 검
                        if (!jsonObject.isNull("data")) {
                            JSONArray data_array = jsonObject.getJSONArray("data");
                            int count = 0;
                            String noti_title = "";
                            for (int i = 0; i < data_array.length(); i++) {
                                JSONObject data = (JSONObject) data_array.get(i);

                                int lprice_diff = data.getInt("lprice_diff");


                                // 갱신된 가격이 다른 경우만 alarm을 저장
                                if (lprice_diff != 0) {
                                    String id = data.getString("id");

                                    // 북마크 고유 id로 fluctuationList를 가져옴
                                    get_fluctuationList(id);

                                    boolean item_selling = data.getBoolean("item_selling");
                                    String item_alarm = String.valueOf(data.getBoolean("item_alarm"));
                                    String item_title = data.getString("item_title");
                                    String item_link = data.getString("item_link");
                                    String item_image = data.getString("item_image");
                                    String item_lprice = data.getString("item_lprice");
                                    String item_mallName = data.getString("item_mallname");
                                    String item_id = data.getString("item_id");
                                    String item_type = data.getString("item_type");
                                    String item_brand = data.getString("item_brand");
                                    String item_maker = data.getString("item_maker");
                                    String item_category1 = data.getString("item_category1");
                                    String item_category2 = data.getString("item_category2");
                                    String item_category3 = data.getString("item_category3");
                                    String item_category4 = data.getString("item_category4");

                                    fluctuationList.add(new Fluctuation(fluctation_date, item_lprice, lprice_diff));
                                    // fluctuationList 리스트를 저장
                                    save_fluctuationList(id, fluctuationList);

                                    DTO alarm = new DTO(id, item_selling, item_alarm, item_title, item_link, item_image, item_lprice, item_mallName
                                            , item_id, item_type, item_brand, item_maker, item_category1, item_category2, item_category3, item_category4, lprice_diff, alarm_date);

                                    // alarmList에 새로운 alarm을 저장 - stack 형식으로 저장 (한도 : 50)
                                    add_alarmList(alarm);

                                    // 가격 변동된 알람의 개수를 증가
                                    count += 1;

                                    // 변동된 상품 중 가장 마지막 상품의 title 저장
                                    noti_title = item_title;
                                }
                                // SharedPreference의 alarmList를 갱신
                                save_alarmList();
                            }

                            // 가격 변동된 알람의 개수와 title로 notification 요청
                            if (count >= 1 && !noti_title.equals(""))
                                request_notification(noti_title, count, context);
                        } else
                            Log.d(TAG, "onResponse: 데이터 없음");
                    } else if (!success) {
                        // ** 북마크 조회 실패시 ** //
                        Toast.makeText(context, "북마크 조회 실패 : " + jsonObject.toString(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "JSONexception: " + e);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "exception: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "lprice 에러 : " + error.toString(), Toast.LENGTH_SHORT).show();
                String request_type = "request_get_item_price";
                error_handling(error, request_type, context, null, 0);
            }
        }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", access_token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void add_alarmList(DTO alarm) {
        int MAX = 50; // 알람리스트 저장 한도 설정
        if (alarmList.size() == MAX) {
            alarmList.remove(alarmList.get(alarmList.size() - 1));
        }
        alarmList.add(0, alarm);
    }


    // Request - 가격 변동된 제품의 푸쉬 알림 요청
    private void request_notification(final String noti_title, final int count, final Context context) {
        String url = context.getString(R.string.fcmEndpoint) + "/send"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        // ** 알람 요청 성공시 ** //
                        Toast.makeText(context, "알람 성공", Toast.LENGTH_LONG).show();
                    } else if (!success)
                        // ** 북마크 조회 실패시 ** //
                        Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                String request_type = "request_notification";
                error_handling(error, request_type, context, noti_title, count);
            }
        }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-device-token", device_token);
                params.put("x-access-token", access_token);
                return params;
            }

            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                int mCount = count;

                String noti_title_s = "";
                // item_title이 16자 이상일 경우 글자를 자르고 "..."로 생략
                if (noti_title.length() >= 16) {
                    noti_title_s = noti_title.substring(0, 16) + "...";
                } else
                    noti_title_s = noti_title;

                String pushtitle = "";
                // 푸쉬 count가 1개일 경우
                if (mCount == 1) {
                    pushtitle = noti_title_s + "의 가격 정보가 변동되었습니다.";
                }
                // 푸쉬 count가 2개 이상일 경우
                else if (mCount > 1) {
                    mCount -= 1;
                    pushtitle = noti_title_s + "외 " + mCount + "개의 가격정보가 변동되었습니다.";
                }

                params.put("pushtitle", pushtitle);
                String pushbody = "터치하면 가격 변동 리스트로 이동합니다. 지금 확인해보세요!";
                params.put("pushbody", pushbody);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    // Error Handling - request 오류(제품 가격 정보 조회) 처리 - 실패 시 access-token 갱신 요청
    private void error_handling(VolleyError error, String request_type, Context context,
                                @Nullable String noti_title, @Nullable int count) {
        NetworkResponse response = error.networkResponse;
        if (error instanceof AuthFailureError && response != null) {
            try {
                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                Log.d(TAG, "onErrorResponse: " + res);
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(res);
                JsonObject data = element.getAsJsonObject().get("data").getAsJsonObject();
                String name = data.get("name").getAsString();
                String msg = data.get("msg").getAsString();

                // access-token 만료 시 refresh-token을 통해 토큰 갱신
                if (name.equals("TokenExpiredError") && msg.equals("jwt expired"))
                    refresh_accessToken(request_type, context, noti_title, count);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    // access-token 갱신 요청 후 폴더 목록 재요청 - 실패 시 logout
    private void refresh_accessToken(final String request_type, final Context context, @Nullable final String noti_title, @Nullable final int count) {
        Log.d(TAG, "refresh_accessToken: access-token을 갱신합니다.");
        String url = context.getString(R.string.authEndpoint) + "/refresh"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
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
                            // 북마크 삭제 재요청
                            case "request_get_item_price":
                                request_get_item_lprice(context);
                                break;
                            case "request_notification":
                                request_notification(noti_title, count, context);
                                break;
                        }

                    } else if (!success)
                        Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_LONG).show();

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
                        if (name.equals("TokenExpiredError") && msg.equals("jwt expired")) {
                            null_userFile();
                            off_alarm(context);
                        }
                        //logout(context);
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    // 만료된 access-token을 새로 갱신한 access-token으로 교체
    private void update_accessToken(String new_token) {
        access_token = new_token;
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("access_token", access_token); //Second라는 key값으로 infoSecond 데이터를 저장한다.
        editor.commit();
    }

    // 현재 로그인된 id와 access_token 제거
    private void null_userFile() {
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("user_id", null);
        editor.putString("access_token", null);
        editor.putString("refresh_token", null);
        editor.apply();
    }

    // 설정된 알람 삭제
    private void off_alarm(Context context) {

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, alarm_unique_id, intent, PendingIntent.FLAG_NO_CREATE);

        if (sender == null) {
            // TODO: 이미 설정된 알람이 없는 경우
        } else {
            // TODO: 이미 설정된 알람이 있는 경우
            sender = PendingIntent.getBroadcast(context, alarm_unique_id, intent, 0);

            am.cancel(sender);
            sender.cancel();

        }

    }

    // SharedPreference의 alarmList 데이터를 가져온다
    private void get_alarmList() {
        // 저장된 alarmList 있을 경우
        String key = user_id + "/alarmList";
        if (userFile.getString(key, null) != null) {
            String key_alarmList = userFile.getString(key, null);
            Type listType = new TypeToken<ArrayList<DTO>>() {
            }.getType();
            alarmList = new GsonBuilder().create().fromJson(key_alarmList, listType);

        }// 저장된 alarmList 없을 경우
        else {
            alarmList = new ArrayList<>();
            save_alarmList();
        }
    }

    // SharedPreference에 alarmList 데이터 저장
    private void save_alarmList() {
        String key = user_id + "/alarmList";
        // List<DTO> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<DTO>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(alarmList, listType);

        // 스트링 객체로 변환된 데이터를 alarmList에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString(key, json);
        editor.apply();
    }

    // SharedPreference의 가격 변동 리스트 데이터를 가져온다
    private void get_fluctuationList(String id) {
        // 저장된 fluctuationList 있을 경우
        String key = user_id + "/alarmList/" + id;
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

    // 가격 변동 리스트 저장
    private void save_fluctuationList(String id, List<Fluctuation> fluctuationList) {
        String key = user_id + "/alarmList/" + id;
        // List<Fluctuation> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<Fluctuation>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(fluctuationList, listType);

        // 스트링 객체로 변환된 데이터를 alarmList에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString(key, json);
        editor.apply();
    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile(Context context) {
        userFile = context.getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
        device_token = userFile.getString("device_token", null);
    }
}
