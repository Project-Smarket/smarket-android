package org.techtown.smarket_android.searchItemList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.Hotdeal.hotdeal_webView;
import org.techtown.smarket_android.User.Bookmark.bookmark_dialog;
import org.techtown.smarket_android.User.Bookmark.bookmark_recyclerview_adapater;
import org.techtown.smarket_android.User.UserLogin.user_login_fragment;
import org.techtown.smarket_android.smarketClass.BookmarkAlarm;
import org.techtown.smarket_android.smarketClass.news;
import org.techtown.smarket_android.smarketClass.review;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.smarketClass.spec;
import org.techtown.smarket_android.searchItemList.Pager.news.search_detail_news_fragment;
import org.techtown.smarket_android.searchItemList.Pager.spec.search_detail_spec_fragment;
import org.techtown.smarket_android.searchItemList.Pager.review.search_detail_review_fragment;
import org.techtown.smarket_android.searchItemList.Request.danawaRequest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class searchdetail_fragment extends Fragment {
    private ViewGroup viewGroup;
    private Bundle bundle;
    private Toolbar toolbar;
    private search_detail_news_fragment detail_news_fragment;
    private search_detail_spec_fragment detail_of_detail_fragment;
    private search_detail_review_fragment detail_review_fragment;
    private FragmentManager fragmentManager;
    private ArrayList<spec> specList;
    private ArrayList<String> keyList;
    private ArrayList<String> keyValueList;
    private ArrayList<review> reviewList;
    private ArrayList<news> newsList;
    private String item_link = "";

    private String item_productType;

    private ProgressDialog progressDialog;

    // 북마크
    private bookmark_recyclerview_adapater bookmarkRecyclerviewAdapter;
    private bookmark_dialog bookmarkDialog;
    private List<String> bookmarkFolderList;
    private EditText bookmark_folder_name;

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;

    // ** 북마크 정보 ** //
    private String folder_name;
    private String item_title;
    private String item_id;
    private String item_type;
    private String item_price;

    final private int alarm_time = 3;
    private Boolean alarm_check;

    private List<BookmarkAlarm> bookmarkAlarmList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_item_detail, container, false);
        progressDialog = ProgressDialog.show(getContext(), "", "로딩");
        userFile = getContext().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        get_userFile();

        // SharedPreference의 bookmarkFolderList 데이터를 가져옴
        get_bookmarkFolderList();

        // SharedPreference의 bookmarkAlarmList 데이터를 가져옴
        get_bookmarkAlarmList();

        // getbundle
        ReceiveData();


        try {
            specList = new ArrayList<>();
            keyList = new ArrayList<>();
            keyValueList = new ArrayList<>();
            reviewList = new ArrayList<>();
            newsList = new ArrayList<>();
            getJSon();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Button gotoMall = viewGroup.findViewById(R.id.detail_gotoMall);
        gotoMall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item_link.equals("")) {
                    Intent intent = new Intent(getActivity(), hotdeal_webView.class);
                    intent.putExtra("url", item_link);
                    getContext().startActivity(intent);
                }
            }
        });

        settingToolbar();
        setHasOptionsMenu(true);


        Tab();


        return viewGroup;
    }

    private void add_bookmark(){
        // 비로그인 시 로그인 창으로 이동
        if (user_id == null && access_token == null) {
            goto_login();
        } else {
            bookmarkRecyclerviewAdapter = new bookmark_recyclerview_adapater(bookmarkFolderList, getActivity());
            bookmarkDialog = new bookmark_dialog(getActivity(), "북마크 폴더 리스트", bookmarkRecyclerviewAdapter, bookmarkFolderList, mClickAddListener);

            bookmarkRecyclerviewAdapter.setOnItemClickListener(new bookmark_recyclerview_adapater.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position, List<String> list) {

                    folder_name = list.get(position); // 북마크 폴더 이름

                    // DB에 북마크 등록 요청
                    request_bookmark();
                    bookmarkDialog.dismiss();

                }
            });
            bookmarkDialog.show();
        }
    }

    Button.OnClickListener mClickAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            bookmarkDialog.dismiss();
            folder_add();
        }
    };

    // 북마크 폴더 추가 기능
    private void folder_add() {

        // 북마크 폴더 리스트 다이얼로그
        LayoutInflater inflater = getActivity().getLayoutInflater();
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


        // 새 폴더 이름 입력 다이얼로그
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
                    bookmarkRecyclerviewAdapter.notifyDataSetChanged(); // 어댑터 갱신
                    save_bookmarkFolderList();
                    //updateBookmarkFolderList(mContext, SETTINGS_BOOKMARK_JSON, bookmarkFolderList);
                }

                bookmarkDialog.show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // 다이얼로그 생성시 EditText 활성화 1
        dialog.show();
        if (bookmark_folder_name.requestFocus())
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(bookmark_folder_name, 0); // 다이얼로그 생성시 EditText 활성화 2

    }

    // SharedPreference의 bookmarkFolderList 데이터를 가져옴
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

    // 수정된 bookmarkFolderList를 저장
    private void save_bookmarkFolderList() {
        // List<String> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(bookmarkFolderList, listType);

        // 스트링 객체로 변환된 데이터를 bookmarkFolderList에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkFolderList", json);
        editor.apply();
    }

    // SharedPreference의 bookmarkAlarmList 데이터를 가져옴
    private void get_bookmarkAlarmList() {
        // userFile에 myBookmarks 요소 유효성 검사 - 유효하지 않을 경우 myBookmarks 데이터 생성(한 번만 실행)
        if (userFile.getString("bookmarkAlarmList", null) == null) {
            bookmarkAlarmList = new ArrayList<>(); // bookmarks 리스트 생성

            // gson을 통해 타 클래스 객체를 스트링으로 변경
            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
            }.getType();
            String json = gson.toJson(bookmarkAlarmList, listType);

            // 스트링 객체로 변환된 데이터를 myBookmarks에 저장
            SharedPreferences.Editor editor = userFile.edit();
            editor.putString("bookmarkAlarmList", json);
            editor.apply();
            Log.d("New bookmarkAlarmList", "bookmarkAlarmList: Complete setting bookmarkAlarmList");
        } else {
            String bookmarkAlarm = userFile.getString("bookmarkAlarmList", null);
            Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
            }.getType();
            bookmarkAlarmList = new GsonBuilder().create().fromJson(bookmarkAlarm, listType);
            Log.d("Get bookmarkAlarmList", "bookmarkAlarmList: Complete Getting bookmarkAlarmList");
        }
    }

    private void save_bookmarkAlarmList(String id, String item_price) {
        BookmarkAlarm bookmarkAlarm = new BookmarkAlarm(user_id, folder_name, id, item_price, alarm_time, alarm_check);
        bookmarkAlarmList.add(bookmarkAlarm);

        // List<BookmarkAlarm> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(bookmarkAlarmList, listType);

        // 스트링 객체로 변환된 데이터를 bookmarkAlarmList(SharedPreference) 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkAlarmList", json);
        editor.commit();
        Log.d("SAVE", "save_data: saved bookmarkAlarmList");
    }

    private void goto_login() {
        FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_layout, user_login_fragment.newInstance()).commit();
    }

    // 북마크 등록 요청 기능
    private void request_bookmark() {
        String request_url = getContext().getResources().getString(R.string.bookmarksEndpoint); // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    JSONObject data = jsonObject.getJSONObject("data");
                    String id = data.getString("id");
                    if (success) {
                        // ** 북마크 등록 성공 시 ** //
                        Toast.makeText(getContext(), folder_name + " 폴더에 북마크 등록 되었습니다.", Toast.LENGTH_LONG).show();
                        // 최저가 알림 설정
                        //String item_price = String.valueOf(holder.item_lprice);
                        //String item_price = "1000";
                        set_lpriceAlarm(id, item_price);
                    } else if (!success)
                        // ** 북마크 등록 실패 시 ** //
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
                String request_type = "request_bookmark";
                error_handling(error, request_type);
            }
        }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                Log.d("TOKEN", "token: " + access_token);
                params.put("x-access-token", access_token);
                return params;
            }

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("folder_name", folder_name);
                params.put("item_title", item_title);
                params.put("item_id", item_id);
                params.put("item_type", item_type);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    // 북마크 등록 시 클라이언트에 북마크 저장
    private void set_lpriceAlarm(final String id, final String item_price) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("최저가 알람 등록")
                .setMessage("최저가 알람을 등록하시겠습니까?")
                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alarm_check = true;
                        save_bookmarkAlarmList(id, item_price);
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alarm_check = false;
                        save_bookmarkAlarmList(id, item_price);

                    }
                })
                .setCancelable(false);
        builder.create();
        builder.show();

    }

    // Error Handling - request 오류(bookmark 등록 오류) 처리 - 실패 시 access-token 갱신 요청
    private void error_handling(VolleyError error, String request_type) {
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
                    refresh_accessToken(request_type);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    // access-token 갱신 요청 후 폴더 목록 재요청 - 실패 시 logout
    private void refresh_accessToken(final String request_type) {
        Log.d(TAG, "refresh_accessToken: access-token을 갱신합니다.");
        String url = getContext().getString(R.string.authEndpoint) + "/refresh"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
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
                            // 북마크 등록 재요청
                            case "request_bookmark":
                                request_bookmark();
                                break;
                        }

                    } else if (!success)
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (
                        JSONException e) {
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
                        FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
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


    private void Tab() {
        TabLayout tabLayout = viewGroup.findViewById(R.id.detail_TabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                changeView(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void ReceiveData() {
        bundle = getArguments();

        if (bundle != null) {
            item_title = bundle.getString("item_title");
            item_id = bundle.getString("item_id");
            item_type = bundle.getString("item_type");
            item_price = bundle.getString("item_price");
            Bitmap bitmap = bundle.getParcelable("item_image");
            String[] item_data = bundle.getStringArray("item_data");

            ImageView item_image = viewGroup.findViewById(R.id.detail_item_image);
            TextView item_title_textView = viewGroup.findViewById(R.id.detail_item_name);
            TextView item_price_textView = viewGroup.findViewById(R.id.detail_item_value);
            TextView item_mall_textView = viewGroup.findViewById(R.id.detail_firm_name);
            TextView item_brand_textView = viewGroup.findViewById(R.id.detail_item_brand);
            TextView item_maker_textView = viewGroup.findViewById(R.id.detail_item_maker);
            TextView item_category_textView = viewGroup.findViewById(R.id.detail_item_category);

            item_image.setImageBitmap(bitmap);
            item_title_textView.setText(item_title);
            item_price_textView.setText(item_price);
            item_mall_textView.setText("판매처 : " + item_data[0]);
            item_link = item_data[1];
            Log.d(TAG, "ReceiveData: " + item_link);
            item_brand_textView.setText(item_data[2]);
            item_maker_textView.setText(item_data[3]);

            String category = item_data[4];
            if (!item_data[5].equals("")) {
                category += "/" + item_data[5];
                if (!item_data[6].equals("")) {
                    category += "/" + item_data[6];
                    if (!item_data[7].equals(""))
                        category += "/" + item_data[7];
                }
            }
            item_category_textView.setText(category);

            item_productType = item_data[8];
            Log.d(TAG, "productType : " + item_data[8]);
            bundle.clear();
        }

    }

    private void settingToolbar() {
        toolbar = viewGroup.findViewById(R.id.detailToolbar);
        toolbar.setTitle("제품 상세");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().remove(searchdetail_fragment.this).commit();
                fm.popBackStack();
                return true;
            }
            case R.id.menu_bookmark:
                add_bookmark();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.detailmenu, menu);
    }


    private void changeView(int index) {

        switch (index) {
            case 0: {
                if (detail_review_fragment == null) {
                    detail_review_fragment = new search_detail_review_fragment();
                    Bundle reviewbundle = new Bundle();
                    reviewbundle.putParcelableArrayList("review", reviewList);
                    detail_review_fragment.setArguments(reviewbundle);
                    fragmentManager.beginTransaction().replace(R.id.detail_frame, detail_review_fragment, "search").addToBackStack(null).commitAllowingStateLoss();

                }
                if (detail_news_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_news_fragment).commit();
                if (detail_of_detail_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_of_detail_fragment).commit();
                if (detail_review_fragment != null)
                    fragmentManager.beginTransaction().show(detail_review_fragment).commit();

                break;
            }
            case 1: {
                if (detail_of_detail_fragment == null) {
                    detail_of_detail_fragment = new search_detail_spec_fragment();
                    Bundle specBundle = new Bundle();
                    specBundle.putParcelableArrayList("spec", specList);

                    detail_of_detail_fragment.setArguments(specBundle);
                    fragmentManager.beginTransaction().add(R.id.detail_frame, detail_of_detail_fragment, "search").addToBackStack(null).commit();
                }

                if (detail_news_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_news_fragment).commit();
                if (detail_of_detail_fragment != null)
                    fragmentManager.beginTransaction().show(detail_of_detail_fragment).commit();
                if (detail_review_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_review_fragment).commit();

                break;
            }
            case 2: {
                if (detail_news_fragment == null) {
                    detail_news_fragment = new search_detail_news_fragment();
                    Bundle newsBundle = new Bundle();
                    newsBundle.putParcelableArrayList("news", newsList);
                    detail_news_fragment.setArguments(newsBundle);
                    fragmentManager.beginTransaction().add(R.id.detail_frame, detail_news_fragment, "search").addToBackStack(null).commit();
                }
                if (detail_news_fragment != null)
                    fragmentManager.beginTransaction().show(detail_news_fragment).commit();
                if (detail_of_detail_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_of_detail_fragment).commit();
                if (detail_review_fragment != null)
                    fragmentManager.beginTransaction().hide(detail_review_fragment).commit();

                break;
            }

        }

    }

    private void getJSon() throws UnsupportedEncodingException {
        String url = getString(R.string.detailEndpoint);
        danawaRequest detailRequest = new danawaRequest(url, item_title, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    reviewJson(jsonObject); //리뷰 json파싱
                    fragmentManager = getChildFragmentManager();
                    detail_review_fragment = new search_detail_review_fragment();
                    Bundle reviewbundle = new Bundle();
                    reviewbundle.putParcelableArrayList("review", reviewList);
                    detail_review_fragment.setArguments(reviewbundle);
                    fragmentManager.beginTransaction().replace(R.id.detail_frame, detail_review_fragment, "search").addToBackStack(null).commitAllowingStateLoss();

                    if (item_productType.equals("2")) {
                        specList = null;
                    } else {
                        specJson(jsonObject); //상세정보 json파싱
                    }
                    newsJson(jsonObject); //뉴스 json파싱

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    Log.d(TAG, "getJson: " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error + "", Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(detailRequest);
    }

    private void specJson(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("spec");

        Iterator key = data.getJSONObject(0).keys();
        while (key.hasNext()) {
            String s = key.next().toString();
            keyList.add(s);
        }
        for (int j = 0; j < keyList.size(); j++) {
            keyValueList.add(data.getJSONObject(0).getString(keyList.get(j)));
        }
        for (int i = 0; i < keyList.size(); i++) {
            specList.add(new spec(keyList.get(i), keyValueList.get(i)));
        }
    }

    private void reviewJson(JSONObject jsonObject) throws JSONException {
        JSONArray review = jsonObject.getJSONArray("review");

        for (int i = 0, length = review.length(); i < length; i++) {
            String title = review.getJSONObject(i).getString("title");
            String content = review.getJSONObject(i).getString("content");
            String user = review.getJSONObject(i).getString("user");
            String score = review.getJSONObject(i).getString("score");
            String mall = review.getJSONObject(i).getString("mall");
            String date = review.getJSONObject(i).getString("date");

            Log.d(TAG, "reviewJson: " + title + " " + content + " " + user);
            review review1 = new review(title, content, user, score, mall, date);
            reviewList.add(review1);
        }
    }

    private void newsJson(JSONObject jsonObject) throws JSONException {
        JSONArray news = jsonObject.getJSONArray("news");

        for (int i = 0, length = news.length(); i < length; i++) {
            String img = news.getJSONObject(i).getString("img");
            String title = news.getJSONObject(i).getString("title");
            String url = news.getJSONObject(i).getString("url");
            String user = news.getJSONObject(i).getString("user");
            String hit = news.getJSONObject(i).getString("hit");
            String date = news.getJSONObject(i).getString("date");

            newsList.add(new news(img, title, url, user, hit, date));
        }
    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    }

    // 키보드 입력 후 엔터 입력시 키보드 창 내림
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
    }
}
