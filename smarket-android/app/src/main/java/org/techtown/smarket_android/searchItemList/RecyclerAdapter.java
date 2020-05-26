package org.techtown.smarket_android.searchItemList;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.smarketClass.BookmarkAlarm;
import org.techtown.smarket_android.smarketClass.SearchedItem;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.Bookmark.bookmark_dialog;
import org.techtown.smarket_android.User.Bookmark.bookmark_recyclerview_adapater;
import org.techtown.smarket_android.User.UserLogin.user_login_fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    public interface OnRecyclerClickListener {
        void OnRecyclerClickListener(View v, int position, String[] item_data);
    }

    private OnRecyclerClickListener onRecyclerClickListener = null;

    public void setOnRecyclerClickListener(OnRecyclerClickListener listener) {
        this.onRecyclerClickListener = listener;
    }

    // adapter에 들어갈 list 입니다.
    private List<SearchedItem> itemList;
    private Context mContext;
    private Activity mActivity;

    private bookmark_recyclerview_adapater bookmarkRecyclerviewAdapter;
    private bookmark_dialog bookmarkDialog;
    private List<String> bookmarkFolderList;
    private EditText bookmark_folder_name;
    private InputMethodManager imm;
    //private static final String SETTINGS_BOOKMARK_JSON = "settings_bookmark_json"; // SharedPreference 북마크 리스트 Data Key

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

    final private int alarm_time = 3;
    private Boolean alarm_check;

    private List<BookmarkAlarm> bookmarkAlarmList;

    public RecyclerAdapter(Context context, Activity activity, List<SearchedItem> itemList) {
        mContext = context;
        mActivity = activity;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        userFile = mContext.getSharedPreferences("userFile", Context.MODE_PRIVATE);
        get_userFile();
        // SharedPreference의 bookmarkFolderList 데이터를 가져옴
        get_bookmarkFolderList();

        // SharedPreference의 bookmarkAlarmList 데이터를 가져옴
        get_bookmarkAlarmList();

        View cashBtn = view.findViewById(R.id.cash_btn);
        cashBtn.setVisibility(View.GONE);

        final ItemViewHolder itemViewHolder = new ItemViewHolder(view);

        return itemViewHolder;
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

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(itemList.get(position));
        holder.onType(itemList.get(position).getItem_type());

        // 북마크 버튼 기능 설정
        holder.heart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View itemView) {
                // 비로그인 시 로그인 창으로 이동
                if (user_id == null && access_token == null) {
                    goto_login();
                } else {
                    bookmarkRecyclerviewAdapter = new bookmark_recyclerview_adapater(bookmarkFolderList, mActivity);
                    bookmarkDialog = new bookmark_dialog(mActivity, "북마크 폴더 리스트", bookmarkRecyclerviewAdapter, bookmarkFolderList, mClickAddListener);

                    bookmarkRecyclerviewAdapter.setOnItemClickListener(new bookmark_recyclerview_adapater.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position, List<String> list) {

                            folder_name = list.get(position); // 북마크 폴더 이름
                            item_title = String.valueOf(holder.item_title.getText());
                            item_id = holder.item_id;
                            item_type = holder.item_type;

                            // DB에 북마크 등록 요청
                            request_bookmark(holder);
                            bookmarkDialog.dismiss();

                        }
                    });
                    bookmarkDialog.show();
                }
            }
        });
    }

    private void goto_login() {
        FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_layout, user_login_fragment.newInstance()).commit();
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
        LayoutInflater inflater = mActivity.getLayoutInflater();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);
        builder.setTitle("북마크 폴더 추가");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folder_name = bookmark_folder_name.getText().toString();

                if (folder_name.equals("")) {
                    Toast.makeText(mContext, "폴더명을 입력해주세요", Toast.LENGTH_LONG).show();
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
            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(bookmark_folder_name, 0); // 다이얼로그 생성시 EditText 활성화 2

    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return itemList.size();
    }


    void addItem(SearchedItem data) {
        // 외부에서 item을 추가시킬 함수입니다.
        itemList.add(data);
    }

    public void clear() {
        itemList.clear();
    }


    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout item_layout;
        private TextView item_title;
        private String item_id;
        private String item_type;
        private int item_lprice;
        private TextView item_price;

        private String item_image_url;
        private ImageView item_image;
        private Bitmap bitmap;

        private ImageView heart_btn;
        private TextView item_mall;

        private String[] item_data;

        private TextView item_productype;

        ItemViewHolder(final View itemView) {
            super(itemView);

            item_layout = itemView.findViewById(R.id.search_list_item_layout);
            item_title = itemView.findViewById(R.id.search_list_item_title);
            item_price = itemView.findViewById(R.id.search_list_item_price);
            item_image = itemView.findViewById(R.id.search_list_item_image);
            item_mall = itemView.findViewById(R.id.search_list_item_mallName);
            item_productype = itemView.findViewById(R.id.search_list_item_productype);
            heart_btn = itemView.findViewById(R.id.heart_btn);
            item_data = new String[9];

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.equals(heart_btn)) {
                        Toast.makeText(mContext, "추가", Toast.LENGTH_LONG).show();
                    }
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (onRecyclerClickListener != null) {
                            onRecyclerClickListener.OnRecyclerClickListener(v, pos, item_data);
                        }
                    }
                }
            });
        }

        void onBind(final SearchedItem data) {
            item_title.setText(data.getItem_title());
            item_id = data.getItem_id();
            item_type = data.getItem_type();
            item_lprice = Integer.parseInt(data.getItem_price());
            item_price.setText(String.format("%,d", item_lprice));
            item_mall.setText(data.getItem_mallName());
            item_image_url = data.getItem_image();

            item_data[0] = data.getItem_mallName();
            item_data[1] = data.getItem_link();
            item_data[2] = data.getItem_brand();
            item_data[3] = data.getItem_maker();
            item_data[4] = data.getItem_category1();
            item_data[5] = data.getItem_category2();
            item_data[6] = data.getItem_category3();
            item_data[7] = data.getItem_category4();
            item_data[8] = data.getItem_type(); // productType


            set_item_image();

        }

        void set_item_image() {
            //안드로이드에서 네트워크와 관련된 작업을 할 때,
            //반드시 메인 쓰레드가 아닌 별도의 작업 쓰레드를 생성하여 작업해야 한다.
            Thread mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        try {
                            URL url = new URL(item_image_url);

                            //웹에서 이미지를 가져온 뒤
                            //이미지뷰에 지정할 비트맵을 만든다
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true); //서버로부터 응답 수신
                            connection.connect();

                            InputStream is = connection.getInputStream(); //inputStream 값 가져오기
                            bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 변환
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            mThread.setDaemon(true);
            mThread.start(); //쓰레드 실행

            try {
                // 메인 쓰레드는 별도의 작업 쓰레드가 작업을 완료할 때까지 대기
                // join()을 호출하여 별도의 작업 쓰레드가 종료될 때까지 메인 쓰레드가 기다리게 한다.
                mThread.join();

                // 작업 쓰레드에서 이미지를 불러오는 작업을 완료한 뒤
                // UI 작업을 할 수 있는 메인 쓰레드에서 imageView에 이미지를 지정한다.
                item_image.setImageBitmap(bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        void onType(String item_type) {
            if (!item_type.equals("1")) {
                item_productype.setText("");
            }
        }
    }

/*    // 북마크 폴더 리스트 업데이트
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

    // 폴더 리스트 데이터 가져오기
    private ArrayList<String> getStringArrayPref(Context context, String key) {
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

    // 북마크 등록 요청 기능
    private void request_bookmark(final ItemViewHolder holder) {
        String reques_url = mContext.getResources().getString(R.string.bookmarksEndpoint); // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.POST, reques_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    JSONObject data = jsonObject.getJSONObject("data");
                    String id = data.getString("id");
                    if (success) {
                        // ** 북마크 등록 성공 시 ** //
                        Toast.makeText(mContext, folder_name + " 폴더에 북마크 등록 되었습니다.", Toast.LENGTH_LONG).show();
                        // 최저가 알림 설정
                        String item_id = holder.item_id;
                        //String item_price = String.valueOf(holder.item_lprice);
                        String item_price = "1000";
                        set_lpriceAlarm(id, item_price);
                    } else if (!success)
                        // ** 북마크 등록 실패 시 ** //
                        Toast.makeText(mContext, jsonObject.toString(), Toast.LENGTH_LONG).show();

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
                error_handling(error, request_type, holder);
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

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);

    }

    // Error Handling - request 오류(bookmark 등록 오류) 처리 - 실패 시 access-token 갱신 요청
    private void error_handling(VolleyError error, String request_type,
                                final ItemViewHolder holder) {
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
                    refresh_accessToken(request_type, holder);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    // access-token 갱신 요청 후 폴더 목록 재요청 - 실패 시 logout
    private void refresh_accessToken(final String request_type, final ItemViewHolder holder) {
        Log.d(TAG, "refresh_accessToken: access-token을 갱신합니다.");
        String url = mContext.getString(R.string.authEndpoint) + "/refresh"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
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
                                request_bookmark(holder);
                                break;
                        }

                    } else if (!success)
                        Toast.makeText(mContext, jsonObject.toString(), Toast.LENGTH_LONG).show();

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

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle("로그아웃")
                .setMessage("재로그인이 필요합니다.")
                .setCancelable(false)
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        null_userFile();
                        FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
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

    // 북마크 등록 시 클라이언트에 북마크 저장
    private void set_lpriceAlarm(final String id, final String item_price) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
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