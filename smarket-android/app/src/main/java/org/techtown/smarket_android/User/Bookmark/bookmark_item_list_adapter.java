package org.techtown.smarket_android.User.Bookmark;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.Class.BookmarkAlarm;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Class.Bookmark;

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

public class bookmark_item_list_adapter extends RecyclerView.Adapter<bookmark_item_list_adapter.bmViewHolder> {


    // adapter에 들어갈 list 입니다.
    private Context mContext;
    private Activity mActivity;
    private List<Bookmark> bookmarkList;
    private InputMethodManager imm;

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;

    // bookmark_item_list_adapter 생성자
    public bookmark_item_list_adapter(Context context, Activity activity, List<Bookmark> bookmarkList) {
        this.mContext = context;
        this.mActivity = activity;
        this.bookmarkList = bookmarkList;
        imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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

        View delete_btn = view.findViewById(R.id.heart_btn);
        delete_btn.setBackgroundResource(R.drawable.delete_btn);

        bmViewHolder itemViewHolder = new bookmark_item_list_adapter.bmViewHolder(view);

        return itemViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final bookmark_item_list_adapter.bmViewHolder holder, final int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        if (bookmarkList.get(position).getBookmark_selling()){
            holder.onBind(bookmarkList.get(position));
            holder.setPriceAlarm(holder);
        }
        else{
            holder.onSoldOut(bookmarkList.get(position));
        }


        // 최저가 알람 버튼 색상 설정


        // 최저가 알람 버튼 기능 설정
        holder.cash_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, bookmark_price_alarm_fragment.newInstance(holder));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

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

        private String bookmark_id;
        private TextView bookmark_title;
        private String bookmark_itemId;
        private String bookmark_type;
        private Boolean bookmark_selling;
        private String bookmark_link;
        private String bookmark_image_url;
        private ImageView bookmark_image;
        private String bookmark_lprice;
        private TextView bookmark_price;
        private BookmarkAlarm bookmark_alarm;

        private ImageView heart_btn;
        private ImageView cash_btn;

        private Bitmap bitmap;

        public bmViewHolder(@NonNull View itemView) {
            super(itemView);

            bookmark_title = itemView.findViewById(R.id.search_list_item_name);
            bookmark_image = itemView.findViewById(R.id.search_list_item_image);
            bookmark_price = itemView.findViewById(R.id.search_list_item_value);

            heart_btn = itemView.findViewById(R.id.heart_btn);
            cash_btn = itemView.findViewById(R.id.cash_btn);

            heart_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos, bookmark_id);
                        } else {
                            Toast.makeText(mContext, "null입니다", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            });
        }

        // 아이템 바인드
        void onBind(Bookmark bookmark) {
            bookmark_id = bookmark.getBookmark_id();
            bookmark_title.setText(bookmark.getBookmark_title());
            bookmark_itemId = bookmark.getBookmark_itemId();
            bookmark_type = bookmark.getBookmark_type();
            bookmark_selling = bookmark.getBookmark_selling();
            bookmark_image_url = bookmark.getBookmark_image_url();
            bookmark_lprice = bookmark.getBookmark_lprice();
            bookmark_price.setText(bookmark.getBookmark_lprice());
            bookmark_image_url = bookmark.getBookmark_image_url();
            set_bookmark_image();

            bookmark_link = bookmark.getBookmark_link();
            bookmark_alarm = bookmark.getBookmarkAlarm();

        }

        void onSoldOut(Bookmark bookmark) {
            bookmark_id = bookmark.getBookmark_id();
            bookmark_title.setText(bookmark.getBookmark_title());
            bookmark_itemId = bookmark.getBookmark_itemId();
            bookmark_type = bookmark.getBookmark_type();
            bookmark_selling = bookmark.getBookmark_selling();
            bookmark_image_url = bookmark.getBookmark_image_url();
            bookmark_price.setText("판매종료");
            bookmark_image.setBackgroundResource(R.drawable.soldout);

            cash_btn.setVisibility(View.GONE);

        }

        // 최저가 알람 버튼 색상 설정
        void setPriceAlarm(bmViewHolder holder) {
            if (bookmark_alarm.getAlarm_check()) {
                cash_btn.setColorFilter(itemView.getResources().getColor(R.color.smarketyello), PorterDuff.Mode.SRC_IN);
            } else {
                cash_btn.setColorFilter(itemView.getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);
            }
        }

        // 북마크 아이템 이미지 설정
        void set_bookmark_image() {
            Thread mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        try {
                            URL url = new URL(bookmark_image_url);

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
                bookmark_image.setImageBitmap(bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // GET - bookmarkAlarm_time
        public int getAlarm_time() {
            return bookmark_alarm.getAlarm_time();
        }

        // GET - bookmarkAlarm_check
        public Boolean getAlarm_check() {
            return bookmark_alarm.getAlarm_check();
        }

        // GET - bookmark_id
        public String getBookmark_id() {
            return bookmark_id;
        }

    }

}
