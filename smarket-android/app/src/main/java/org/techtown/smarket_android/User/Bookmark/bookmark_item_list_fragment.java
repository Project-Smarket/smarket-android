package org.techtown.smarket_android.User.Bookmark;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import org.techtown.smarket_android.Class.Bookmark;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Class.BookmarkAlarm;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class bookmark_item_list_fragment extends Fragment {

    private String TAG = "tag";

    public static bookmark_item_list_fragment newInstance() {
        return new bookmark_item_list_fragment();
    } // 프래그먼트 생성


    private ViewGroup viewGroup;

    private Spinner bookmark_spinner; // 북마크 스피너
    private ArrayAdapter spinnerAdapter; // 스피너 어댑터
    private List<String> bookmarkFolderList = new ArrayList<>(); // SharedPreference에 저장된 bookmarkFolderList
    private List<BookmarkAlarm> bookmarkAlarmList; // SharedPreference에 저장된 bookmarkAlarmList


    private EditText bookmark_folder_name; // 추가할 북마크 이름

    private RecyclerView recyclerView;// 북마크 아이템 리스트 리사이클러뷰
    private bookmark_item_list_adapter adapter;// 북마크 아이템 리스트 어댑터
    private List<Bookmark> requestedBookmarkList; // DB에 저장된 북마크의 데이터를 수집하여 저장할 bookmarkList
    private List<Bookmark> matchedBookmarkList; // Spinner에서 선택된 folder_name과 일치하는 bookmark만 저장하는 bookmarkList
    private InputMethodManager imm; // 키보드 설정

    private static final String SETTINGS_BOOKMARK_JSON = "settings_bookmark_json"; // 북마크 폴더 리스트 가져오기 위한 SharedPreference Data Key

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;




    // 실행 시간 지연
    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.bookmark_main, container, false);
        userFile = getContext().getSharedPreferences("userFile", MODE_PRIVATE);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        requestedBookmarkList = new ArrayList<>();

        get_userFile();
        get_bookmarkAlarmList(); // SharedPreference의 bookmarkAlarmList 데이터 수집
        get_bookmarkFolderList();// SharedPreference의 bookmarkFolderList 데이터 수집

        // DB로부터 모든 Bookmark들을 가져옴 - requestedBookmarkList
        try {
            request_itemList();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        set_bookmark_spinner(); // 북마크 스피너 설정
        preset_bookmarkList(); // 북마크 리스트 설정


        set_plus_btn(); // 북마크 추가 버튼 설정
        set_trashcan_btn(); // 북마크 삭제 버튼 설정

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String callValue = mPref.getString(SETTINGS_BOOKMARK_JSON, "default value");

        return viewGroup;
    }


    // SharedPreference의 bookmarkAlarmList 데이터를 가져온다
    private void get_bookmarkAlarmList() {
        if (userFile.getString("bookmarkAlarmList", null) != null) {
            String bookmarkAlarm = userFile.getString("bookmarkAlarmList", null);
            Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
            }.getType();
            bookmarkAlarmList = new GsonBuilder().create().fromJson(bookmarkAlarm, listType);
            Log.d("Get myBookmarks", "myBookmarks: Complete Getting myBookmarks");
        } else {
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
        editor.commit();
    }

    // 북마크 폴더 스피너 설정
    private void set_bookmark_spinner() {


        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, bookmarkFolderList);
        bookmark_spinner = (Spinner) viewGroup.findViewById(R.id.bookmark_folder);
        bookmark_spinner.setAdapter(spinnerAdapter);
        bookmark_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // 현재 로그인된 user_id 와 스피너에 선택된 folder_name이 일치하는 북마크만 가져옴
                set_bookmarkList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // 북마크 폴더 리스트 데이터셋
    private void get_bookmarkFolderList() {
        if (userFile.getString("bookmarkFolderList", null) != null) {
            String bookmarkFolder = userFile.getString("bookmarkFolderList", null);
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            bookmarkFolderList = new GsonBuilder().create().fromJson(bookmarkFolder, listType);
            Log.d("Get bookmarkFolderList", "myBookmarks: Complete Getting bookmarkFolderList");
        } else {
            bookmarkFolderList = new ArrayList<>();
            save_bookmarkFolderList();
        }
    }

    // SharedPreference에 bookmarkFolderList 데이터 저장
    private void save_bookmarkFolderList() {
        // List<String> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(bookmarkFolderList, listType);

        // 스트링 객체로 변환된 데이터를 bookmarkFolderList에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkFolderList", json);
        editor.commit();
    }

    // 폴더 리스트 데이터 가져오기
    /*private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }*/

    // 북마크 폴더 추가 버튼 설정
    private void set_plus_btn() {
        ImageButton plus_btn = viewGroup.findViewById(R.id.plus_btn);
        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folder_add();  // 북마크 폴더 추가 기능
            }
        }); // 플러스 버튼
    }

    // 북마크 폴더 추가 기능
    private void folder_add() {
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.bookmark_plus_dialog, null);
        bookmark_folder_name = dialogView.findViewById(R.id.bookmark_folder_name);
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
                    bookmarkFolderList.add(folder_name); // 북마크 폴더 추가
                    spinnerAdapter.notifyDataSetChanged(); // 어댑터 갱신
                    //updateBookmarkFolderList(getContext(), SETTINGS_BOOKMARK_JSON, bookmarkFolderList);
                    bookmark_spinner.setSelection(bookmarkFolderList.size() - 1); // 새로운 북마크 생성 시 생성된 북마크 페이지
                    save_bookmarkFolderList();
                }


            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // 다이얼로그 생성시 EditText 활성화 1
        dialog.show();
        if (bookmark_folder_name.requestFocus())
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(bookmark_folder_name, 0); // 다이얼로그 생성시 EditText 활성화 2

    }

    // 북마크 폴더 삭제 버튼 설정
    private void set_trashcan_btn() {
        ImageButton trashcan_btn = viewGroup.findViewById(R.id.trashcan_btn);
        trashcan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folder_remove();
            }
        });
    }

    // 북마크 폴더 삭제 기능
    private void folder_remove() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("북마크 폴더 삭제")
                .setMessage("현재 북마크 폴더를 삭제 하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*updateBookmarkFolderList(getContext(), SETTINGS_BOOKMARK_JSON, bookmarkFolderList);*/
                        try {
                            remove_bookmarkFolder(bookmark_spinner.getSelectedItem().toString());
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

    }

    // 북마크 폴더 리스트 업데이트
    private void updateBookmarkFolderList(Context context, String key, List<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    private void remove_bookmarkFolder(String folder_name) throws UnsupportedEncodingException {
        request_remove_bookmarkFolder_to_server_by_folder_name(folder_name);
        if(bookmark_spinner.getSelectedItemPosition() == 0){
            spinnerAdapter.remove(bookmark_spinner.getSelectedItem());
            bookmark_spinner.setSelection(bookmarkFolderList.size()-1);
        }else{
            spinnerAdapter.remove(bookmark_spinner.getSelectedItem());
            bookmark_spinner.setSelection(bookmark_spinner.getSelectedItemPosition()-1);
        }
        spinnerAdapter.notifyDataSetChanged();
        set_bookmarkList();
    }

    // 서버로 folder_name과 일치하는 DB 북마크 삭제 요청
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
                        // ** 북마크 삭제 성공시 ** //
                        Toast.makeText(getContext(), folder_name + " 북마크 폴더를 삭제했습니다.", Toast.LENGTH_LONG).show();
                        remove_bookmark_in_bookmarkAlarmList_by_folder_name(folder_name);
                        remove_bookmarkFolder_in_bookmarkFolderList(folder_name);
                    } else if (!success)
                        // ** 북마크 삭제 실패시 ** //
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();

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
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    // bookmarkFolderList에서 folder_name과 일치하는 북마크 삭제
    private void remove_bookmarkFolder_in_bookmarkFolderList(String folder_name) {
        for (int i = 0; i < bookmarkFolderList.size(); i++) {
            if (bookmarkFolderList.get(i).equals(folder_name)){
                bookmarkFolderList.remove(i);
                break;
            }
        }
        save_bookmarkFolderList();
    }

    // bookmarkAlarmList에서 folder_name과 일치하는 bookmarkAlarm삭제
    private void remove_bookmark_in_bookmarkAlarmList_by_folder_name(String folder_name) {
        /*List<BookmarkAlarm> bookmarkAlarmList;
        if (userFile.getString("bookmarkAlarmList", null) != null) {
            String bookmarkAlarm = userFile.getString("bookmarkAlarmList", null);
            Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
            }.getType();
            bookmarkAlarmList = new GsonBuilder().create().fromJson(bookmarkAlarm, listType);
            Log.d("Get bookmarkAlarmList", "bookmarkAlarmList: Complete Getting bookmarkAlarmList");
        } else {
            bookmarkAlarmList = new ArrayList<>();
        }*/

        for (int i = bookmarkAlarmList.size() -1 ; i >= 0; i--) {
            if (bookmarkAlarmList.get(i).getFolder_name().equals(folder_name)) {
                Log.d(TAG, "remove_bookmark_in_bookmarkAlarmList_by_folder_name: "+ bookmarkAlarmList.get(i).getBookmark_id());
                bookmarkAlarmList.remove(i);
            }
        }

        // List<Bookmark> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(bookmarkAlarmList, listType);

        // 스트링 객체로 변환된 데이터를 myBookmarks에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkAlarmList", json);
        editor.commit();
    }

    // 북마크 아이템 리스트 설정
    private void set_bookmarkList() {


        // 현재 로그인된 user_id 와 스피너에 선택된 folder_name이 일치하는 북마크만 가져옴

        matchedBookmarkList = new ArrayList<>();
        match_to_bookmarkFolder();
        recyclerView = viewGroup.findViewById(R.id.bookmark_itemList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        Log.d(TAG, "matchedBookmarkList_size : " + matchedBookmarkList.size());
        adapter = new bookmark_item_list_adapter(getContext(), getActivity(), matchedBookmarkList);
        adapter.setOnItemClickListener(new bookmark_item_list_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, final String bookmark_id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("북마크 해제")
                        .setMessage("북마크 등록을 해제 하시겠습니까?")
                        .setPositiveButton("해제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 북마크 제거
                                remove_bookmark(bookmark_id);
                            }
                        })
                        .setNegativeButton("취소", null);
                builder.create();
                builder.show();
                /*Toast.makeText(getContext(), bookmark_id + " 북마크 아이디 입니다.", Toast.LENGTH_LONG).show();*/
            }
        });
        recyclerView.setAdapter(adapter);

    }

    // 북마크 삭제
    private void remove_bookmark(String bookmark_id) {

        // 서버로 DB 북마크 삭제 요청
        request_remove_bookmark_to_server(bookmark_id);

        // requestedBookmarkList와 matchedBookmarkList에서 bookmark_id와 일치하는 북마크 삭제
        remove_bookmark_in_bookmarkList(bookmark_id);

        //bookmarkAlarmList에서 bookmark_id와 일치하는 북마크 삭제
        remove_bookmark_in_bookmarkAlarmList(bookmark_id);

    }

    // 서버로 DB 북마크 삭제 요청
    private void request_remove_bookmark_to_server(String bookmark_id) {
        String url = getString(R.string.bookmarksEndpoint) + bookmark_id; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");


                    if (success) {
                        // ** 북마크 삭제 성공시 ** //
                        Toast.makeText(getContext(), "해당 북마크를 삭제했습니다.", Toast.LENGTH_LONG).show();
                    } else if (!success)
                        // ** 북마크 삭제 실패시 ** //
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();

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
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    // requestedBookmarkList와 matchedBookmarkList에서 bookmark_id와 일치하는 북마크 삭제
    private void remove_bookmark_in_bookmarkList(String bookmark_id) {
        for (int i = 0; i < requestedBookmarkList.size(); i++) {
            if (requestedBookmarkList.get(i).getBookmark_id().equals(bookmark_id)) {
                requestedBookmarkList.remove(i);
                break;
            }
        }
        for (int i = 0; i < matchedBookmarkList.size(); i++) {
            if (matchedBookmarkList.get(i).getBookmark_id().equals(bookmark_id)) {
                matchedBookmarkList.remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    // bookmarkAlarmList에서 bookmark_id와 일치하는 북마크 삭제
    private void remove_bookmark_in_bookmarkAlarmList(String bookmark_id) {
        List<BookmarkAlarm> bookmarkAlarmList;
        if (userFile.getString("bookmarkAlarmList", null) != null) {
            String bookmarkAlarm = userFile.getString("bookmarkAlarmList", null);
            Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
            }.getType();
            bookmarkAlarmList = new GsonBuilder().create().fromJson(bookmarkAlarm, listType);
            Log.d("Get bookmarkAlarmList", "bookmarkAlarmList: Complete Getting bookmarkAlarmList");
        } else {
            bookmarkAlarmList = null;
        }

        for (int i = 0; i < bookmarkAlarmList.size(); i++) {
            if (bookmarkAlarmList.get(i).getBookmark_id().equals(bookmark_id)) {
                bookmarkAlarmList.remove(i);
                break;
            }
        }

        // List<Bookmark> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(bookmarkAlarmList, listType);

        // 스트링 객체로 변환된 데이터를 myBookmarks에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkAlarmList", json);
        editor.commit();
    }

    ;

    private void preset_bookmarkList() {

        // 현재 로그인된 user_id 와 스피너에 선택된 folder_name이 일치하는 북마크만 가져옴
        mHandler.postDelayed(new Runnable() {
            public void run() {
                matchedBookmarkList = new ArrayList<>();
                match_to_bookmarkFolder();
                recyclerView = viewGroup.findViewById(R.id.bookmark_itemList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                Log.d(TAG, "matchedBookmarkList_size : " + matchedBookmarkList.size());
                adapter = new bookmark_item_list_adapter(getContext(), getActivity(), matchedBookmarkList);
                adapter.setOnItemClickListener(new bookmark_item_list_adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, final String bookmark_id) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                .setTitle("북마크 해제")
                                .setMessage("북마크 등록을 해제 하시겠습니까?")
                                .setPositiveButton("해제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 북마크 제거
                                        remove_bookmark(bookmark_id);
                                    }
                                })
                                .setNegativeButton("취소", null);
                        builder.create();
                        builder.show();
                        /*Toast.makeText(getContext(), bookmark_id + " 북마크 아이디 입니다.", Toast.LENGTH_LONG).show();*/
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        }, 500); // 0.5초후
    }
    // 현재 로그인된 user_id 와 스피너에 선택된 folder_name이 일치하는 북마크만 가져옴

    // requestedBookmarkList로 부터 북마크 folder_name과 일치하는 북마크만 matchedBookmarkList에 저장
    private void match_to_bookmarkFolder() {
        String folder_name;
        if (!spinnerAdapter.isEmpty()) {
            folder_name = bookmark_spinner.getSelectedItem().toString(); // 현재 스피너에 선택된 folder_name
            Log.d(TAG, "bookmarkList_size: " + requestedBookmarkList.size());
            for (int i = 0; i < requestedBookmarkList.size(); i++) {
                if (requestedBookmarkList.get(i).getFolder_name().equals(folder_name)) {
                    matchedBookmarkList.add(requestedBookmarkList.get(i));
                }
            }
        }
    }

    // DB에 저장된 item_title & item_id & item_type 리스트 조회
    private Boolean request_itemList() throws UnsupportedEncodingException {
        String url = getString(R.string.bookmarksEndpoint); // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    Log.d("SUCCESS", "onResponse: " + success);
                    JSONArray data = jsonObject.getJSONArray("data");
                    Log.d("DATA", "onResponse: " + data.toString());
                    if (success) {
                        // ** 북마크 리스트 조회 성공시 ** //

                        // 토큰에 user_id에 대한 정보가 들어 있기 때문에 별도 아이디검사를 하지 않아도됨
                        for (int i = 0; i < data.length(); i++) {
                            String id = data.getJSONObject(i).getString("id");
                            String folder_name = data.getJSONObject(i).getString("folder_name");
                            String item_title = data.getJSONObject(i).getString("item_title");
                            String item_id = data.getJSONObject(i).getString("item_id");
                            BookmarkAlarm bookmarkAlarm = null;
                            for (int j = 0; j < bookmarkAlarmList.size(); j++) {
                                if (bookmarkAlarmList.get(i).getBookmark_id().equals(id)) {
                                    bookmarkAlarm = bookmarkAlarmList.get(i);
                                    Log.d(TAG, "bookmark_alarm: " + bookmarkAlarm.toString());
                                }
                            }
                            String item_type = data.getJSONObject(i).getString("item_type");
                            Boolean item_selling = data.getJSONObject(i).getBoolean("item_selling");
                            String item_lprice = data.getJSONObject(i).getString("item_lprice");
                            String item_link = data.getJSONObject(i).getString("item_link");
                            String item_image = data.getJSONObject(i).getString("item_image");
                            Bookmark bookmark_client = new Bookmark(id, folder_name, item_title, item_id, item_type, item_selling, item_lprice, item_link, item_image, bookmarkAlarm);
                            Log.d("Volley", "onResponse: " + bookmark_client.toString());
                            requestedBookmarkList.add(bookmark_client);
                        }
                    } else if (!success)
                        // ** 북마크 리스트 조회 실패시 ** //
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();

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
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);


        return true;
    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    }

    // 키보드 입력 후 엔터 입력시 키보드 창 내림
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
    }
}
