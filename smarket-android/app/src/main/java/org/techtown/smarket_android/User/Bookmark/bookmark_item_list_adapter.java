package org.techtown.smarket_android.User.Bookmark;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.DTO_Class.DTO;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Search.search_detail_fragment;
import org.techtown.smarket_android.User.UserLogin.user_login_fragment;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class bookmark_item_list_adapter extends RecyclerView.Adapter<bookmark_item_list_adapter.bmViewHolder> {


    // adapter에 들어갈 list 입니다.
    private Context mContext;
    private Activity mActivity;
    private List<DTO> bookmarkList;

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;

    // bookmark_item_list_adapter 생성자
    public bookmark_item_list_adapter(Context context, Activity activity, List<DTO> bookmarkList) {
        this.mContext = context;
        this.mActivity = activity;
        this.bookmarkList = bookmarkList;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String bookmark_id);
    }

    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public bookmark_item_list_adapter.bmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        get_userFile();

        View delete_btn = view.findViewById(R.id.bookmark_btn);
        delete_btn.setBackgroundResource(R.drawable.delete_btn);

        bmViewHolder itemViewHolder = new bookmark_item_list_adapter.bmViewHolder(view);

        return itemViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final bookmark_item_list_adapter.bmViewHolder holder, final int position) {
        // bookmarkList의 item_selling값 검사
        // item_selling == 1
        if (bookmarkList.get(position).isItem_selling()){
            holder.onBind(bookmarkList.get(position));
            holder.setPriceAlarm(holder);
            if(!holder.item_type.equals("1"))
                holder.bookmark_productype.setText("");
        }
        // item_selling == 0 : 판매 종료
        else{
            holder.onSoldOut(bookmarkList.get(position));
        }

    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = mContext.getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return bookmarkList.size();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public class bmViewHolder extends RecyclerView.ViewHolder {

        private DTO bookmark_data;

        private String id; // 북마크 삭제 및 알람 ON/OFF 시 id로 요청
        private boolean item_selling; // 판매 여부 확인

        private String item_title; // 상품 이름
        private String item_image; // 상품 이미지
        private String item_type; // "최저가" 텍스트 결정
        private String item_lprice; // 상품 가격

        private String item_alarm; // 알람 버튼 색 결정 및 ON/OFF 요청

        // 뷰홀더에 표시될 View
        private TextView bookmark_title;
        private ImageView bookmark_image;
        private TextView bookmark_productype;
        private TextView bookmark_lprice;
        private TextView bookmark_mallName;

        private ImageView bookmark_btn;
        private ImageView alarm_btn;

        private Bitmap bitmap;

        public bmViewHolder(@NonNull View itemView) {
            super(itemView);

            bookmark_title = itemView.findViewById(R.id.search_list_item_title);
            bookmark_image = itemView.findViewById(R.id.search_list_item_image);
            bookmark_productype = itemView.findViewById(R.id.search_list_item_productype);
            bookmark_lprice = itemView.findViewById(R.id.search_list_item_price);
            bookmark_mallName = itemView.findViewById(R.id.search_list_item_mallName);

            bookmark_btn = itemView.findViewById(R.id.bookmark_btn);
            alarm_btn = itemView.findViewById(R.id.alarm_btn);



            bookmark_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos, id);
                        } else {
                            Toast.makeText(mContext, "null입니다", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            });

            // 최저가 알람 버튼 기능 설정
            alarm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO : 알람 ON/OFF
                    if(item_alarm.equals("true")){
                        request_alarm("false");
                    }
                    else if(item_alarm.equals("false")){
                        request_alarm("true");
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search_detail_fragment searchdetailFragment = new search_detail_fragment();

                    // 상품 상세로 데이터 전송
                    Bundle bundle = settingBundle(bitmap, bookmark_data);
                    searchdetailFragment.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_layout, searchdetailFragment, "search").addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }

        // 아이템 바인드
        void onBind(DTO bookmark) {

            bookmark_data = bookmark;

            id = bookmark.getId();

            item_selling = bookmark.isItem_selling();
            item_alarm = bookmark.getItem_alarm();
            item_title = bookmark.getItem_title();

            item_image = bookmark.getItem_image();
            set_bookmark_image();
            int lprice = Integer.parseInt(bookmark.getItem_lprice());
            item_lprice = String.format("%,d", lprice);

            item_type = bookmark.getItem_type();

            bookmark_title.setText(item_title);
            bookmark_lprice.setText(item_lprice);
            bookmark_mallName.setText(bookmark.getItem_mallName());


        }

        void onSoldOut(DTO bookmark) {
            id = bookmark.getId();

            item_selling = bookmark.isItem_selling();
            bookmark_title.setText(bookmark.getItem_title());

            item_type = bookmark.getItem_type();
            bookmark_lprice.setText("판매종료");
            item_image = bookmark.getItem_image();
            set_bookmark_image();
            alarm_btn.setVisibility(View.GONE);
            bookmark_productype.setVisibility(View.INVISIBLE);

        }

        // 최저가 알람 버튼 색상 설정
        void setPriceAlarm(bmViewHolder holder) {
            if (holder.item_alarm.equals("true")) {
                alarm_btn.setColorFilter(itemView.getResources().getColor(R.color.smarketyello), PorterDuff.Mode.SRC_IN);
            } else {
                alarm_btn.setColorFilter(itemView.getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);
            }
        }

        // 북마크 아이템 이미지 설정
        void set_bookmark_image() {

            Glide.with(mContext).asBitmap().load(item_image).into(bookmark_image);
        }

        // 상품 상세로 전달되는 Bundle
        private Bundle settingBundle(Bitmap bitmap, DTO bookmark_data) {
            Bundle bundle = new Bundle();

            bundle.putParcelable("item_image", bitmap);
            bundle.putParcelable("item_data", bookmark_data);


            return bundle;
        }

        public void request_alarm(final String value) {
            String url = mContext.getResources().getString(R.string.bookmarksEndpoint) + "/alarm";

            StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        // 알람 ON/OFF 설정 성공시
                        if(success){
                            if(value.equals("true")){
                                item_alarm = "true";
                                alarm_btn.setColorFilter(itemView.getResources().getColor(R.color.smarketyello), PorterDuff.Mode.SRC_IN);
                                Toast.makeText(mContext, "상품 알람 : ON", Toast.LENGTH_SHORT).show();
                            }else if(value.equals("false")){
                                item_alarm = "false";
                                alarm_btn.setColorFilter(itemView.getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);
                                Toast.makeText(mContext, "상품 알람 : OFF", Toast.LENGTH_SHORT).show();
                            }
                        }
                        // 알람 ON/OFF 설정 실패 시
                        else{

                        }

                    } catch (JSONException e) {
                        Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                    error_handling(error, value);
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
                    params.put("id", id);
                    params.put("item_alarm", value);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(stringRequest);
        }

        // Error Handling - request 오류(bookmarkList 조회, bookmarkFolder 삭제, bookmark 삭제 오류) 처리 - 실패 시 access-token 갱신 요청
        private void error_handling(VolleyError error, String value) {
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
                        refresh_accessToken(value);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        // access-token 갱신 요청 후 폴더 목록 재요청 - 실패 시 logout
        private void refresh_accessToken(final String value) {
            String url = mActivity.getString(R.string.authEndpoint) + "/refresh"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
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
                            request_alarm(value);

                        } else if (!success)
                            Toast.makeText(mContext, jsonObject.toString(), Toast.LENGTH_LONG).show();

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
                    .setMessage("로그인이 필요합니다.")
                    .setCancelable(false)
                    .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            null_userFile();
                            FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
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
    }

}
