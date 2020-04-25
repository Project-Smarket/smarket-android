package org.techtown.smarket_android.searchItemList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.Bookmark.bookmark_dialog;
import org.techtown.smarket_android.User.Bookmark.bookmark_recyclerview_adapater;
import org.techtown.smarket_android.User.user_login_success;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    public interface OnRecyclerClickListener {
        void OnRecyclerClickListener(View v, int position);
    }

    private OnRecyclerClickListener onRecyclerClickListener = null;

    public void setOnRecyclerClickListener(OnRecyclerClickListener listener) {
        this.onRecyclerClickListener = listener;
    }

    // adapter에 들어갈 list 입니다.
    private ArrayList<Item> listData = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;


    private bookmark_recyclerview_adapater bookmarkRecyclerviewAdapter;
    private bookmark_dialog bookmarkDialog;
    private List<String> bookmarkFolderList;
    private EditText bookmark_folder_name;
    private InputMethodManager imm;
    private static final String SETTINGS_BOOKMARK_JSON = "settings_bookmark_json"; // SharedPreference 북마크 리스트 Data Key

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;

    // ** 북마크 정보 ** //
    private String folder_name;
    private String bookmark_name;
    private String bookmark_url = "test.com";
    private Boolean alarm_check;

    private List<Bookmark> bookmarks;

    RecyclerAdapter(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        bookmarkFolderList = getStringArrayPref(mContext, SETTINGS_BOOKMARK_JSON);
        userFile = mContext.getSharedPreferences("userFile", Context.MODE_PRIVATE);
        // userFile에 myBookmarks 요소 유효성 검사 - 유효하지 않을 경우 myBookmarks 데이터 생성(한 번만 실행)
        if(userFile.getString("myBookmarks", null) == null){
            List<Bookmark> bookmarks = new ArrayList<>(); // bookmarks 리스트 생성

            // gson을 통해 타 클래스 객체를 스트링으로 변경
            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<ArrayList<Bookmark>>(){}.getType();
            String json = gson.toJson(bookmarks, listType);

            // 스트링 객체로 변환된 데이터를 myBookmarks에 저장
            SharedPreferences.Editor editor = userFile.edit();
            editor.putString("myBookmarks", json);
            editor.commit();
            Log.d("New myBookmarks", "myBookmarks: Complete setting myBookmarks");
        }else{
            String myBookmarks = userFile.getString("myBookmarks", null);
            Type listType = new TypeToken<ArrayList<Bookmark>>(){}.getType();
            bookmarks = new GsonBuilder().create().fromJson(myBookmarks, listType);
            Log.d("Get myBookmarks", "myBookmarks: Complete Getting myBookmarks");
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);

        View cashBtn = view.findViewById(R.id.cash_btn);
        cashBtn.setVisibility(View.INVISIBLE);

        final ItemViewHolder itemViewHolder = new ItemViewHolder(view);

        ImageView cash = view.findViewById(R.id.cash_btn);
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),
                        "cash", Toast.LENGTH_SHORT).show();
            }
        });

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));

        // 북마크 버튼 기능 설정
        holder.heart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View itemView) {
                if (!holder.bookmark_check) {
                    bookmarkRecyclerviewAdapter = new bookmark_recyclerview_adapater(bookmarkFolderList, mActivity);
                    bookmarkDialog = new bookmark_dialog(mActivity, "북마크 폴더 리스트", bookmarkRecyclerviewAdapter, bookmarkFolderList, mClickAddListener);

                    bookmarkRecyclerviewAdapter.setOnItemClickListener(new bookmark_recyclerview_adapater.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position, List<String> list) {
                            folder_name = list.get(position); // 북마크 폴더 이름
                            bookmark_name = String.valueOf(holder.item_name.getText()); // 북마크 아이템 이름
                            //Toast.makeText(mContext, "'" + folder_name + "'" + "폴더에 " + "'" + bookmark_name + "'" + "등록", Toast.LENGTH_LONG).show();

                            // 북마크 등록 요청
                            if(request_bookmark()){
                                save_data(holder);
                            };

                            bookmarkDialog.dismiss();
                            holder.heart_btn.setColorFilter(v.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                            holder.bookmark_check = true;
                        }
                    });
                    bookmarkDialog.show();

                } else if (holder.bookmark_check) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            .setTitle("북마크 해제")
                            .setMessage("북마크 등록을 해제 하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 북마크 삭제 요청
                                    delete_bookmark();

                                    holder.heart_btn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                                    holder.bookmark_check = false;
                                }
                            })
                            .setNegativeButton("취소", null);
                    builder.create();
                    builder.show();
                }
            }
        });
    }

    Button.OnClickListener mClickAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            bookmarkDialog.dismiss();
            folder_add();
        }
    };

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    // 북마크 폴더 추가 기능
    private void folder_add() {

        LayoutInflater inflater = mActivity.getLayoutInflater();

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
                    updateBookmarkFolderList(mContext, SETTINGS_BOOKMARK_JSON, bookmarkFolderList);
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


    void addItem(Item data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public void clear(){
        listData.clear();
    }


    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView item_name;
        private TextView item_value;
        private ImageView item_Image;
        private ImageView heart_btn;
        private TextView ItemMall;
        private Boolean bookmark_check = false;
        private Bitmap bitmap;


        ItemViewHolder(final View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.search_list_item_name);
            item_value = itemView.findViewById(R.id.search_list_item_value);
            item_Image = itemView.findViewById(R.id.search_list_item_image);
            ItemMall = itemView.findViewById(R.id.search_mallName);
            heart_btn = itemView.findViewById(R.id.heart_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (onRecyclerClickListener != null) {
                            onRecyclerClickListener.OnRecyclerClickListener(v, pos);
                        }
                    }
                }
            });
        }

        void onBind(final Item data) {
            item_name.setText(data.getItem_name());
            item_value.setText(data.getItem_value());
            ItemMall.setText(data.getItem_mall());

            //안드로이드에서 네트워크와 관련된 작업을 할 때,
            //반드시 메인 쓰레드가 아닌 별도의 작업 쓰레드를 생성하여 작업해야 한다.
            Thread mThread = new Thread(){
                @Override
                public void run() {
                    try{
                        try {
                            URL url = new URL(data.getItem_image());

                            //웹에서 이미지를 가져온 뒤
                            //이미지뷰에 지정할 비트맵을 만든다
                            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
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
                item_Image.setImageBitmap(bitmap);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
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
    }

    // 북마크 등록 요청 기능
    private boolean request_bookmark() {
        get_userFile();
        String reques_url = "http://10.0.2.2:3000/api/bookmarks"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.POST, reques_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        // ** 북마크 등록 성공 시 ** //

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
                Toast.makeText(mContext, "errorListener" + "fail to request_bookmark()", Toast.LENGTH_LONG).show();
                refresh();
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
                params.put("bookmark_name", bookmark_name);
                params.put("url", bookmark_url);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
        return true;
    }

    private void save_data(ItemViewHolder holder){
        //user_id;
        //folder_name;
        //bookmark_name;
        //bookmark_url;
        Gson gson = new GsonBuilder().create();
        String bookmark_image = gson.toJson(holder.bitmap, Bitmap.class);
        String bookmark_price = holder.item_value.getText().toString();
        Boolean bookmark_check = true;
        final Integer alarm_time = 3;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle("최저가 알람 등록")
                .setMessage("최저가 알람을 등록하시겠습니까?")
                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alarm_check = true;
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alarm_check = false;
                    }
                });
        builder.create();
        builder.show();

        Bookmark bookmark = new Bookmark(user_id, folder_name, bookmark_name, bookmark_url, bookmark_image, bookmark_price, bookmark_check, alarm_time, alarm_check);
        bookmarks.add(bookmark);

        Type listType = new TypeToken<ArrayList<Bookmark>>(){}.getType();
        String json = new GsonBuilder().create().toJson(bookmarks, listType);

        // 스트링 객체로 변환된 데이터를 myBookmarks에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("myBookmarks", json);
        editor.commit();
        Log.d("SAVE", "save_data: Set new data");

    }
    // 북마크 삭제 요청 기능
    private void delete_bookmark() {
        String request_url = "http://10.0.2.2:3000/api/bookmarks"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.GET, request_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        // ** 북마크 조회 성공 시 ** //
                        Log.d("LIST", "data: " + jsonObject);
                        //Toast.makeText(mContext, jsonObject.toString(), Toast.LENGTH_LONG).show();
                    } else if (!success)
                        // ** 북마크 조회 실패 시 ** //
                        Toast.makeText(mContext, jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "errorListener" + error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                Log.d("TOKEN", "token: " + access_token);
                params.put("x-access-token", access_token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    }

    // 액세스 토큰 갱신
    private void refresh() {
        String reques_url = "http://10.0.2.2:3000/api/auth/refresh"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
        StringRequest stringRequest = new StringRequest(Request.Method.GET, reques_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String re_access_token = jsonObject.getString("data");
                    if (success) {
                        // ** 액세스 토큰 갱신 성공 시 ** //
                        userFile = mContext.getSharedPreferences("userFile", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = userFile.edit();
                        editor.putString("access_token", re_access_token);
                    } else if (!success)
                        // ** 액세스 토큰 갱신 실패 시 ** //

                        Toast.makeText(mContext, jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // ** 리프레시 토큰 만료, 사용자 재로그인 및 새 토큰 발급 필요 ** //
                SharedPreferences.Editor editor = userFile.edit();
                editor.putString("user_id", null);
                editor.putString("access_token", null);
                editor.putString("refresh_token", null);
                editor.commit();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle("등록 실패")
                        .setMessage("로그인이 필요합니다.")
                        .setPositiveButton("확인", null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                Log.d("TOKEN", "token: " + refresh_token);
                params.put("x-refresh-token", refresh_token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    // 키보드 입력 후 엔터 입력시 키보드 창 내림
    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
    }

}