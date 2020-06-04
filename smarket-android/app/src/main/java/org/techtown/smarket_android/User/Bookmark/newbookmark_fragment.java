package org.techtown.smarket_android.User.Bookmark;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.Alarm.AlarmReceiver;
import org.techtown.smarket_android.DTO_Class.DTO;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.UserLogin.user_login_fragment;
import org.techtown.smarket_android.Search.RecyclerDecoration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class newbookmark_fragment extends Fragment {

    private String TAG = "tag";

    public static newbookmark_fragment newInstance() {
        return new newbookmark_fragment();
    } // 프래그먼트 생성

    private int alarm_unique_id = 1212;

    private ViewGroup viewGroup;

    private Toolbar toolbar;
    private boolean toolbar_check = true;
    private Spinner bookmark_spinner; // 북마크 스피너
    private ArrayAdapter spinnerAdapter; // 스피너 어댑터
    private List<String> bookmarkFolderList; // SharedPreference에 저장된 bookmarkFolderList

    private EditText bookmark_folder_name; // 추가할 북마크 이름

    private RecyclerView recyclerView;// 북마크 아이템 리스트 리사이클러뷰
    private bookmark_item_list_adapter adapter;// 북마크 아이템 리스트 어댑터
    private List<DTO> bookmarkList;

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;

    // 스피너 중복 실행 방지 변수
    int iCurrentSelection;

    // 북마크 폴더 중복 요청 방지
    boolean isRequested = false;

    private TextView add_bookmarkFolder;
    private TextView remove_bookmarkFolder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_newbookmark_fragment, container, false);


        get_userFile(); // SharedPreference의 user 데이터 수집

        // 북마크리스트 리사이클러 뷰 설정
        set_bookmarkList_recyclerView();

        // Request - 서버로부터 북마크 폴더 리스트를 조회
        request_bookmarkFolderList();


        set_plus_btn(); // 북마크 추가 버튼 설정
        set_trashcan_btn(); // 북마크 삭제 버튼 설정

        toolbar = viewGroup.findViewById(R.id.newbookmark_toolbar);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppBarLayout mAppbar = viewGroup.findViewById(R.id.bookmark_app_bar);
                if(toolbar_check){
                    mAppbar.setExpanded(false);
                    toolbar_check = false;
                } else{
                    mAppbar.setExpanded(true);
                    toolbar_check = true;
                }
            }
        });

        return viewGroup;
    }

    // Request - 서버로부터 북마크 폴더 리스트를 조회
    private void request_bookmarkFolderList() {
        String reques_url = getContext().getResources().getString(R.string.bookmarksEndpoint) + "/folder"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.GET, reques_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    JSONArray data = jsonObject.getJSONArray("data");
                    if (success) {
                        bookmarkFolderList = new ArrayList<>();
                        // ** 북마크 폴더 조회 성공 시 ** //
                        for (int i = 0; i < data.length(); i++) {
                            // user_id가 일치하는 북마크 폴더만 가져온다
                            if (user_id.equals(data.getJSONObject(i).getString("user_id"))) {
                                bookmarkFolderList.add(data.getJSONObject(i).getString("folder_name"));
                            }
                        }
                        // 북마크 스피너 설정
                        set_bookmark_spinner();

                    } else if (!success)
                        // ** 북마크 조회 실패 시 ** //
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // ** 북마크 등록 실패시 ** //
                // Error Handling - request 오류(토큰만료) 처리
                String request_type = "request_bookmarkFolderLIst";
                // error_handling(error, request_type, holder);
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

    // 북마크리스트 리사이클러 뷰 설정
    private void set_bookmarkList_recyclerView() {
        // 아이템 줄간격 설정
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(8);

        recyclerView = viewGroup.findViewById(R.id.bookmark_itemList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(spaceDecoration);
    }

    // 북마크 폴더 스피너 설정
    private void set_bookmark_spinner() {
        spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.bookmark_spinner_text, bookmarkFolderList) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
        };
        bookmark_spinner = (Spinner) viewGroup.findViewById(R.id.bookmarkFolder_spinner);
        iCurrentSelection = bookmark_spinner.getSelectedItemPosition();
        bookmark_spinner.setAdapter(spinnerAdapter);
        bookmark_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // 중복 실행 방지
                if (iCurrentSelection != i) {
                    // 현재 로그인된 user_id 와 스피너에 선택된 folder_name이 일치하는 북마크만 가져옴
                    set_bookmarkList(spinnerAdapter.getItem(i).toString());
                }
                iCurrentSelection = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // 북마크 폴더 추가 버튼 설정
    private void set_plus_btn() {
        add_bookmarkFolder = viewGroup.findViewById(R.id.add_bookmarkFolder);
        add_bookmarkFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();

                final View dialogView = inflater.inflate(R.layout.custom_dialog_edittext, null);
                bookmark_folder_name = dialogView.findViewById(R.id.dialog_editText);
                bookmark_folder_name.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            hideKeyboard();
                        }
                        return false;
                    }
                });


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView);
                builder.setTitle("북마크 폴더 추가");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String folder_name = bookmark_folder_name.getText().toString();

                        if (folder_name.equals("")) {
                            Toast.makeText(getContext(), "폴더명을 입력해주세요", Toast.LENGTH_LONG).show();
                        } else if (!folder_name.equals("")) {
                            char except_enter[] = folder_name.toCharArray();
                            if (except_enter[except_enter.length - 1] == '\n') {

                                char result_char[] = new char[except_enter.length - 1];
                                System.arraycopy(except_enter, 0, result_char, 0, except_enter.length - 1);
                                folder_name = String.valueOf(result_char);

                            } // 한글 입력 후 엔터시 개행문자 발생하는 오류 처리

                            // Request - 서버에 bookmarkFolder 등록 요청
                            request_add_bookmarkFolder(folder_name);
                        }

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // 다이얼로그 생성시 EditText 활성화 1
                dialog.show();
                if (bookmark_folder_name.requestFocus())
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(bookmark_folder_name, 0); // 다이얼로그 생성시 EditText 활성화 2

            }
        }); // 플러스 버튼
    }

    // Request - 서버에 bookmarkFolder 등록 요청
    private void request_add_bookmarkFolder(final String folder_name) {
        String request_url = getContext().getResources().getString(R.string.bookmarksEndpoint) + "/folder"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        // ** 북마크 폴더 등록 성공 시 ** //
                        bookmarkFolderList.add(folder_name); // 북마크 폴더 추가
                        spinnerAdapter.notifyDataSetChanged(); // 어댑터 갱신
                        //updateBookmarkFolderList(getContext(), SETTINGS_BOOKMARK_JSON, bookmarkFolderList);
                        bookmark_spinner.setSelection(bookmarkFolderList.size() - 1); // 새로운 북마크 생성 시 생성된 북마크 페이지
                    } else if (!success)
                        // ** 북마크 폴더 등록 실패 시 ** //
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // ** 북마크 등록 실패시 ** //
                // Error Handling - request 오류(토큰만료) 처리
                String request_type = "request_bookmarkFolderLIst";
                // error_handling(error, request_type, holder);
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
                params.put("user_id", user_id);
                params.put("folder_name", folder_name);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    // 북마크 폴더 삭제 버튼 설정
    private void set_trashcan_btn() {
        remove_bookmarkFolder = viewGroup.findViewById(R.id.remove_bookmarkFolder);
        remove_bookmarkFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spinnerAdapter.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("북마크 폴더 삭제")
                            .setMessage("현재 북마크 폴더를 삭제 하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /*updateBookmarkFolderList(getContext(), SETTINGS_BOOKMARK_JSON, bookmarkFolderList);*/
                                    try {
                                        if (!spinnerAdapter.isEmpty())
                                            request_remove_bookmarkFolder_to_server_by_folder_name(bookmark_spinner.getSelectedItem().toString());
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.create();
                    builder.show();
                } else {
                    Toast.makeText(getContext(), "삭제할 폴더가 없습니다", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // spinner에 선택된 folder_name과 일치하는 북마크 리스트 조회
    private void set_bookmarkList(String folder_name) {
        try {
            bookmarkList = new ArrayList<>();
            adapter = new bookmark_item_list_adapter(getContext(), getActivity(), bookmarkList);
            adapter.setOnItemClickListener(new bookmark_item_list_adapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, final String id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setTitle("북마크 해제")
                            .setMessage("북마크 등록을 해제 하시겠습니까?")
                            .setPositiveButton("해제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 단일 북마크 제거
                                    request_remove_bookmark_to_server(id);
                                }
                            })
                            .setNegativeButton("취소", null);
                    builder.create();
                    builder.show();
                }
            });
            recyclerView.setAdapter(adapter);
            // Request - 서버로 부터 folder_name과 일치하는 북마크 리스트를 가져온다
            request_bookmarkList_by_folder_name(folder_name);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // Request - 서버로 bookmark_id와 일치하는 DB 북마크 삭제 요청 - 실패 시 request 오류(토큰만료) 처리
    private void request_remove_bookmark_to_server(final String id) {
        String url = getString(R.string.bookmarksEndpoint) + "/" + id; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");


                    if (success) {
                        // ** 북마크 삭제 성공시 ** //

                        // bookmarkList에서 bookmark_id와 일치하는 bookmark 삭제
                        remove_bookmark_in_bookmarkList(id);
                        remove_fluctuationList(id);
                        Toast.makeText(getContext(), "해당 북마크를 삭제했습니다.", Toast.LENGTH_LONG).show();
                    } else if (!success)
                        // ** 북마크 삭제 실패시 ** //
                        Toast.makeText(getContext(), "북마크 삭제 - false", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // ** 북마크 삭제 실패시 ** //
                // Error Handling - request 오류(토큰만료) 처리
                String request_type = "request_remove_bookmark";
                error_handling(error, request_type, id);
            }
        }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", access_token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    // bookmarkList에서 bookmark_id와 일치하는 bookmark 삭제
    private void remove_bookmark_in_bookmarkList(String bookmark_id) {
        for (int i = 0; i < bookmarkList.size(); i++) {
            if (bookmarkList.get(i).getId().equals(bookmark_id)) {
                bookmarkList.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void remove_fluctuationList(String id){
        String key = user_id + "/alarmList/" + id;
        SharedPreferences.Editor editor = userFile.edit();
        editor.remove(key);
        editor.apply();
    }

    // Request - 서버로 folder_name과 일치하는 DB 북마크 삭제 요청 - 실패 시 request 오류(토큰만료) 처리
    private void request_remove_bookmarkFolder_to_server_by_folder_name(final String folder_name) throws UnsupportedEncodingException {
        String url = getString(R.string.bookmarksEndpoint) + "?foldername=" + URLEncoder.encode(folder_name, "UTF-8"); // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");


                    if (success) {
                        // ** 북마크 폴더 삭제 성공시 ** //
                        Toast.makeText(getContext(), folder_name + " 북마크 폴더를 삭제했습니다.", Toast.LENGTH_LONG).show();
                        remove_bookmarkFolder_in_bookmarkFolderList(folder_name);
                    } else if (!success)
                        // ** 북마크 폴더 삭제 실패시 ** //
                        Toast.makeText(getContext(), "북마크 폴더 삭제 - false", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // ** 북마크 폴더 삭제 실패시 ** //
                // Error Handling - request 오류(토큰만료) 처리
                String request_type = "request_remove_bookmarkFolder";
                error_handling(error, request_type, folder_name);
            }
        }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", access_token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    // bookmarkFolderList에서 folder_name과 일치하는 북마크 삭제
    private void remove_bookmarkFolder_in_bookmarkFolderList(String folder_name) {
// 북마크 폴더가 1개일 경우
        if (bookmarkFolderList.size() == 1) {
            bookmarkFolderList.clear();
            spinnerAdapter.notifyDataSetChanged();
            bookmarkList.clear();
            adapter.notifyDataSetChanged();
        }
        // 북마크 폴더가 여러개인 경우
        else {
            // 첫 번째 북마크 폴더 삭제한 경우
            if (bookmark_spinner.getSelectedItemPosition() == 0) {
                spinnerAdapter.remove(bookmark_spinner.getSelectedItem());
                spinnerAdapter.notifyDataSetChanged();
                bookmarkList.clear();
                set_bookmarkList(bookmark_spinner.getSelectedItem().toString());
            } else {
                spinnerAdapter.remove(bookmark_spinner.getSelectedItem());
                spinnerAdapter.notifyDataSetChanged();
                bookmark_spinner.setSelection(bookmark_spinner.getSelectedItemPosition() - 1);
            }
        }

        for (int i = 0; i < bookmarkFolderList.size(); i++) {
            if (bookmarkFolderList.get(i).equals(folder_name)) {
                bookmarkFolderList.remove(i);
                break;
            }
        }
    }


    // Request - 서버로 folder_name과 일치하는 DB 북마크 조회 요청 - 실패 시 request 오류(토큰만료) 처리
    private void request_bookmarkList_by_folder_name(final String folder_name) throws UnsupportedEncodingException {

        String url = getString(R.string.bookmarksEndpoint) + "?foldername=" + URLEncoder.encode(folder_name, "UTF-8"); // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        // ** 북마크 리스트 조회 성공시 ** //
                        if (!jsonObject.isNull("data")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            // 토큰에 user_id에 대한 정보가 들어 있기 때문에 별도 아이디검사를 하지 않아도됨
                            for (int i = 0; i < data.length(); i++) {
                                String id = data.getJSONObject(i).getString("id");
                                Boolean item_selling = data.getJSONObject(i).getBoolean("item_selling");
                                String item_alarm = String.valueOf(data.getJSONObject(i).getBoolean("item_alarm"));
                                String item_title = data.getJSONObject(i).getString("item_title");
                                String item_link = data.getJSONObject(i).getString("item_link");
                                String item_image = data.getJSONObject(i).getString("item_image");
                                String item_lprice = data.getJSONObject(i).getString("item_lprice");
                                String item_mallName = data.getJSONObject(i).getString("item_mallname");
                                String item_id = data.getJSONObject(i).getString("item_id");
                                String item_type = data.getJSONObject(i).getString("item_type");
                                String item_brand = data.getJSONObject(i).getString("item_brand");
                                String item_maker = data.getJSONObject(i).getString("item_maker");
                                String item_category1 = data.getJSONObject(i).getString("item_category1");
                                String item_category2 = data.getJSONObject(i).getString("item_category2");
                                String item_category3 = data.getJSONObject(i).getString("item_category3");
                                String item_category4 = data.getJSONObject(i).getString("item_category4");
                                DTO bookmark = new DTO(id, item_selling, item_alarm, item_title, item_link, item_image, item_lprice, item_mallName
                                        , item_id, item_type, item_brand, item_maker, item_category1, item_category2, item_category3, item_category4);
                                bookmarkList.add(bookmark);
                            }
                            adapter.notifyDataSetChanged();
                        }else
                            Log.d(TAG, "onResponse: 북마크가 비엇습니다");
                    } else if (!success)
                        // ** 북마크 조회 실패시 ** //
                        Toast.makeText(getContext(), "북마크 조회 - false", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // ** 북마크 조회 실패시 ** //
                // Error Handling - request 오류(토큰만료) 처리
                String request_type = "request_bookmarkList";
                error_handling(error, request_type, folder_name);
            }
        }
        ) {
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
    private void error_handling(VolleyError error, String request_type, String folder_name) {
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
                    refresh_accessToken(request_type, folder_name);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    // access-token 갱신 요청 후 폴더 목록 재요청 - 실패 시 logout
    private void refresh_accessToken(final String request_type, final String bookmark_data) {
        Log.d(TAG, "refresh_accessToken: access-token을 갱신합니다.");
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
                            // 북마크 삭제 재요청
                            case "request_remove_bookmark":
                                request_remove_bookmark_to_server(bookmark_data);
                                break;
                            // 북마크 폴더 삭제 재요청
                            case "request_remove_bookmarkFolder":
                                request_remove_bookmarkFolder_to_server_by_folder_name(bookmark_data);
                                break;
                            // 폴더 목록 조회 재요청
                            case "request_bookmarkList":
                                request_bookmarkList_by_folder_name(bookmark_data);
                                break;
                        }

                    } else if (!success)
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException | UnsupportedEncodingException e) {
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
                        // 알람끔
                        off_alarm();
                        FragmentManager fragmentManager = getFragmentManager();
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

        }

    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    }

    // 키보드 입력 후 엔터 입력시 키보드 창 내림
    private void hideKeyboard() {
        InputMethodManager imm; // 키보드 설정
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
    }

}
