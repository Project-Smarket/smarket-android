package org.techtown.smarket_android.MainNavigation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.smarketClass.BookmarkAlarm;
import org.techtown.smarket_android.smarketClass.SearchedItem;
import org.techtown.smarket_android.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    private List<BookmarkAlarm> bookmarkAlarmList;
    private List<BookmarkAlarm> myBookmarkAlarmList;
    private List<SearchedItem> alarmList;

    @Override
    public void onReceive(Context context, Intent intent) {

        alarmList = new ArrayList<>();
        get_userFile(context);

        // 저장된 bookmarkAlarmList 데이터를 가져옴
        get_bookmarkAlarmList();

        // user_id가 일치하고 alarm_check:true인 bookmarkAlarm을 가져옴
        get_myBookmarkAlarmList();

        // 저장된 alarmList 데이터를 가져옴
        get_alarmList();

        Calendar calendar = Calendar.getInstance();

        // TODO : 각 시간별로 최저가 알람이 설정된 제품의 가격 정보 조회를 요청
        int hour = calendar.get(Calendar.SECOND);
        Toast.makeText(context, String.valueOf(hour), Toast.LENGTH_SHORT).show();
        int selection;
        switch (hour) {
            case 10:
                selection = 0;
                break;
            case 20:
                selection = 1;
                break;
            case 30:
                selection = 2;
                break;
            case 40:
                selection = 3;
                break;
            default:
                selection = 4;
                break;
        }
        Log.d(TAG, "selection: "+ selection);

        // 설정된 selection으로 가격 시간별 상품 분류 및 가격 조회 요청
        request_classified_bookmarkAlarmList(selection, context);

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


        // 설정된 시간으로 alarmManager 재설정
        set_alarmManager(calendar, context, intent);
    }

    // 설정된 시간으로 alarmManager 재설정
    private void set_alarmManager(Calendar calendar, Context context, Intent intent) {

        // 현재 시간
        Date date = new Date();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarm_unique_id, intent, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("알람", date.toString() + " : 알람이 " + calendar.get(Calendar.SECOND) + "분로 설정되었습니다");
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }
    }


    // bookmarkAlarm의 시간에 따라 list 분류
    // selection : 0 - 오후 12시
    // selection : 1 - 오후 3시
    // selection : 2 - 오후 6시
    // selection : 3 - 오후 9시
    // selection : 4 - 분류 안함
    private void request_classified_bookmarkAlarmList(int selection, Context context) {

        // 설정된 selection으로 가격 시간별 상품 분류
        if (selection != 4 && myBookmarkAlarmList != null) {
            List<BookmarkAlarm> classifiedList = new ArrayList<>();
            for (int i = 0; i < myBookmarkAlarmList.size(); i++) {
                if (myBookmarkAlarmList.get(i).getAlarm_time() == selection) {
                    classifiedList.add(myBookmarkAlarmList.get(i));
                }
            }

            // 임의 시간으로 분류된 bookmarkAlarmList로 상품 조회
            // 분류된 리스트가 적어도 1개는 있어야 실행
            if(classifiedList.size()>0)
                request_get_item_price(classifiedList, context);
        }
    }

    // Request - 최저가 알림 설정된 상품의 가격 재조회 요청
    private void request_get_item_price(final List<BookmarkAlarm> list, final Context context) {
        String url = context.getString(R.string.bookmarksEndpoint)+"/lprice"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                        JSONArray data_array = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject data = (JSONObject) data_array.get(i);
                            Log.d(TAG, "onResponse: " + data.toString());
                            String id = data.getString("id");
                            String item_lprice = data.getString("item_lprice");

                            // 북마크 등록된 가격
                            int past_price = Integer.parseInt(list.get(i).getBookmark_price());
                            // 새로 갱신된 가격
                            int updated_price = Integer.parseInt(item_lprice);

                            // 갱신된 가격이 다른 경우
                            if (past_price != updated_price) {

                                // 갱신된 가격이 더 낮은 경우
                                String alarm_type = "";
                                if (past_price > updated_price) {
                                    updated_price = past_price - updated_price;
                                    alarm_type = "하락";
                                }
                                // 갱신된 가격이 더 높은 경우
                                else if (past_price < updated_price) {
                                    updated_price = updated_price - past_price;
                                    alarm_type = "상승";
                                }
                                String title = data.getString("item_title");
                                String item_title = removeTag(title);
                                String item_id = data.getString("item_id");
                                String item_type = data.getString("item_type");
                                String item_image = data.getString("item_image");
                                String item_mallName = data.getString("item_link");
                                String updated_price_string = String.valueOf(updated_price);

                                SearchedItem alarm = new SearchedItem(user_id, id, item_title, item_id, item_type, item_lprice, item_image, item_mallName,
                                        alarm_type, updated_price_string);
                                // 갱신된 가격으로 북마크알람의 가격 수정
                                edit_bookmarkAlarmList(id, item_lprice);
                                Log.d(TAG, "alarm: " + alarm.toString());
                                // alarmList에 새로운 alarm을 저장 - stack 형식으로 저장
                                add_alarmList(alarm);

                                request_notification(item_title, alarm_type, item_lprice, updated_price, context);
                            }
                            // 수정된 bookmarkAlarmList 저장
                            save_bookmarkAlarmList();
                            // 수정된 alarmList 저장
                            save_alarmList();
                        }
                        for (int i = 0; i < alarmList.size(); i++) {
                            Log.d(TAG, "alarmList: " + alarmList.get(i).getItem_title());
                        }
                    } else if (!success) {
                        // ** 북마크 조회 실패시 ** //
                        Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_LONG).show();
                    }

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
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < list.size(); i++) {
                    jsonArray.put(Integer.parseInt(list.get(i).getBookmark_id()));
                }
                params.put("id", jsonArray.toString());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    // 갱신된 가격으로 북마크알람의 가격 수정
    private void edit_bookmarkAlarmList(String id, String item_price){
        for (int i = 0; i < bookmarkAlarmList.size(); i++) {
            if(bookmarkAlarmList.get(i).getBookmark_id().equals(id)){
                bookmarkAlarmList.get(i).setBookmark_price(item_price);
                break;
            }
        }
    }

    // alarmList에 동일한 id의 alarm이 있으면 삭제
    private void add_alarmList(SearchedItem alarm){
        for (int i = 0; i < alarmList.size(); i++) {
            if(alarmList.get(i).getId().equals(alarm.getId())){
                alarmList.remove(i);
                break;
            }
        }
        alarmList.add(0,alarm);
    }
    // Request - 가격 변동된 제품의 푸쉬 알림 요청
    private void request_notification(final String pushtitle, final String alarm_type, final String item_lprice, final int updated_price, final Context context) {
        String url = context.getString(R.string.fcmEndpoint)+"/send"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
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
                params.put("pushtitle", pushtitle);
                String pushbody = "가격이 " + updated_price + " 원 " + alarm_type + " 했습니다.      " + "현재가격 : " + item_lprice;
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

    // SharedPreference의 alarmList 데이터를 가져온다
    private void get_alarmList() {
        // 저장된 alarmList 있을 경우
        if (userFile.getString("alarmList", null) != null) {
            String bookmarkAlarm = userFile.getString("alarmList", null);
            Type listType = new TypeToken<ArrayList<SearchedItem>>() {
            }.getType();
            alarmList = new GsonBuilder().create().fromJson(bookmarkAlarm, listType);

        }// 저장된 alarmList 없을 경우
        else {
            alarmList = new ArrayList<>();
            save_alarmList();
        }
    }

    // SharedPreference에 alarmList 데이터 저장
    private void save_alarmList() {
        // List<SearchedItem> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<SearchedItem>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(alarmList, listType);

        // 스트링 객체로 변환된 데이터를 alarmList에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("alarmList", json);
        editor.apply();
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
    private void get_myBookmarkAlarmList() {
        myBookmarkAlarmList = new ArrayList<>();
        for (int i = 0; i < bookmarkAlarmList.size(); i++) {
            if (bookmarkAlarmList.get(i).getUser_id().equals(user_id) && bookmarkAlarmList.get(i).getAlarm_check()) {
                myBookmarkAlarmList.add(bookmarkAlarmList.get(i));
            }
        }
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
