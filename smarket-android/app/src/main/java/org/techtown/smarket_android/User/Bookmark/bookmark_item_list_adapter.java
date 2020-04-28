package org.techtown.smarket_android.User.Bookmark;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Bookmark;
import org.techtown.smarket_android.searchItemList.Item;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class bookmark_item_list_adapter extends RecyclerView.Adapter<bookmark_item_list_adapter.bmViewHolder> {


    // adapter에 들어갈 list 입니다.
    private Context mContext;
    private Activity mActivity;
    private List<Bookmark> bookmarkItemList;
    private InputMethodManager imm;

    private SharedPreferences userFile;

    // bookmark_item_list_adapter 생성자
    public bookmark_item_list_adapter(Context context, Activity activity, List<Bookmark> bookmarkItemList) {
        this.mContext = context;
        this.mActivity = activity;
        this.bookmarkItemList = bookmarkItemList;
        imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @NonNull
    @Override
    public bookmark_item_list_adapter.bmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);

        bmViewHolder itemViewHolder = new bookmark_item_list_adapter.bmViewHolder(view);

        return itemViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final bookmark_item_list_adapter.bmViewHolder holder, final int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(bookmarkItemList.get(position));

        // 최저가 알람 버튼 색상 설정
        holder.setPriceAlarm(holder);

        // 북마크 버튼 기능 설정
        holder.heart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View itemView) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle("북마크 해제")
                        .setMessage("북마크 등록을 해제 하시겠습니까?")
                        .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                remove_bookmark(holder.bookmark_name.getText().toString());
                            }
                        })
                        .setNegativeButton("취소", null);
                builder.create();
                builder.show();
            }
        });


            // 최저가 알람 버튼 기능 설정
        holder.cash_btn.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, bookmark_price_alarm_fragment.newInstance(holder));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            });

        }

        private void remove_bookmark (String bookmark_name){
            List<Bookmark> bookmarks;
            if (userFile.getString("myBookmarks", null) != null) {
                String myBookmarks = userFile.getString("myBookmarks", null);
                Type listType = new TypeToken<ArrayList<Bookmark>>() {
                }.getType();
                bookmarks = new GsonBuilder().create().fromJson(myBookmarks, listType);
                Log.d("Get myBookmarks", "myBookmarks: Complete Getting myBookmarks");
            } else {
                bookmarks = null;
            }

            for (int i = 0; i < bookmarks.size(); i++) {
                if (bookmarks.get(i).getBookmark_name().equals(bookmark_name)) {
                    bookmarks.remove(i);
                    break;
                }
            }

            // List<Bookmark> 클래스 객체를 String 객체로 변환
            Type listType = new TypeToken<ArrayList<Bookmark>>() {
            }.getType();
            String json = new GsonBuilder().create().toJson(bookmarks, listType);

            // 스트링 객체로 변환된 데이터를 myBookmarks에 저장
            SharedPreferences.Editor editor = userFile.edit();
            editor.putString("myBookmarks", json);
            editor.commit();

            this.notifyDataSetChanged();
        }

        @Override
        public int getItemCount () {
            // RecyclerView의 총 개수 입니다.
            return bookmarkItemList.size();
        }

        // RecyclerView의 핵심인 ViewHolder 입니다.
        // 여기서 subView를 setting 해줍니다.
        public static class bmViewHolder extends RecyclerView.ViewHolder {

            private TextView bookmark_name;
            private String bookmark_url;
            private String bookmark_image_url;
            private ImageView bookmark_image;
            private TextView bookmark_price;
            private Boolean bookmark_check;
            private int alarm_time;
            private Boolean alarm_check;

            private ImageView heart_btn;
            private ImageView cash_btn;

            private Bitmap bitmap;

            public bmViewHolder(@NonNull View itemView) {
                super(itemView);

                bookmark_name = itemView.findViewById(R.id.search_list_item_name);
                bookmark_image = itemView.findViewById(R.id.search_list_item_image);
                bookmark_price = itemView.findViewById(R.id.search_list_item_value);

                heart_btn = itemView.findViewById(R.id.heart_btn);
                heart_btn.setColorFilter(itemView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                cash_btn = itemView.findViewById(R.id.cash_btn);
            }

            // 아이템 바인드
            void onBind(Bookmark bookmark) {

                bookmark_name.setText(bookmark.getBookmark_name());
                bookmark_url = bookmark.getBookmark_url();
                bookmark_image_url = bookmark.getBookmark_image_url();
                set_bookmark_image();
                bookmark_price.setText(bookmark.getBookmark_price());
                bookmark_check = bookmark.getBookmark_check();
                alarm_time = bookmark.getAlarm_time();
                alarm_check = bookmark.getAlarm_check();

            }

            // 최저가 알람 버튼 색상 설정
            void setPriceAlarm(bmViewHolder holder) {
                if (alarm_check) {
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

            public int getAlarm_time() {
                return alarm_time;
            }

            public Boolean getAlarm_check() {
                return alarm_check;
            }

            public String getBookmark_name() {
                return bookmark_name.getText().toString();
            }
        }

    }
