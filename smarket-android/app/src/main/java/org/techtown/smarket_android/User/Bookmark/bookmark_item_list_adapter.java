package org.techtown.smarket_android.User.Bookmark;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.smarketClass.BookmarkAlarm;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.smarketClass.Bookmark;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
            holder.onType(bookmarkList.get(position));
        }
        // item_selling == 0
        else{
            holder.onSoldOut(bookmarkList.get(position));
        }

        // 최저가 알람 버튼 기능 설정
        holder.alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, bookmark_price_alarm_fragment.newInstance());
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

        private String id;
        private String user_id;
        private String folder_name;
        private boolean item_selling;
        private boolean item_alarm;
        private String item_title;
        private String item_link;
        private String item_image;
        private String item_lprice;
        private String item_id;
        private String item_type;

        private TextView bookmark_title;
        private ImageView bookmark_image;
        private TextView bookmark_productype;

        private TextView bookmark_lprice;

        private ImageView bookmark_btn;
        private ImageView alarm_btn;

        private Bitmap bitmap;

        public bmViewHolder(@NonNull View itemView) {
            super(itemView);

            bookmark_title = itemView.findViewById(R.id.search_list_item_title);
            bookmark_image = itemView.findViewById(R.id.search_list_item_image);
            bookmark_lprice = itemView.findViewById(R.id.search_list_item_price);
            bookmark_productype = itemView.findViewById(R.id.search_list_item_productype);

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
        }

        // 아이템 바인드
        void onBind(Bookmark bookmark) {
            id = bookmark.getId();
            user_id = bookmark.getUser_id();
            folder_name = bookmark.getFolder_name();
            item_selling = bookmark.isItem_selling();
            item_alarm = bookmark.isItem_alarm();
            item_title = bookmark.getItem_title();
            item_link = bookmark.getItem_link();
            item_image = bookmark.getItem_image();
            int lprice = Integer.parseInt(bookmark.getItem_lprice());
            item_lprice = String.format("%,d", lprice);
            item_id = bookmark.getItem_id();
            item_type = bookmark.getItem_type();

            bookmark_title.setText(item_title);
            bookmark_lprice.setText(item_lprice);
            set_bookmark_image();

        }

        void onSoldOut(Bookmark bookmark) {
            id = bookmark.getId();
            user_id = bookmark.getUser_id();
            item_selling = bookmark.isItem_selling();
            item_alarm = false;
            bookmark_title.setText(bookmark.getItem_title());
            item_id = bookmark.getItem_id();
            item_type = bookmark.getItem_type();
            bookmark_lprice.setText("판매종료");
            item_image = bookmark.getItem_image();
            set_bookmark_image();
            alarm_btn.setVisibility(View.GONE);

        }

        void onType(Bookmark bookmark){
            if(!bookmark.getItem_type().equals("1")){
                bookmark_productype.setText("");
            }
        }

        // 최저가 알람 버튼 색상 설정
        void setPriceAlarm(bmViewHolder holder) {
            if (holder.item_alarm) {
                alarm_btn.setColorFilter(itemView.getResources().getColor(R.color.smarketyello), PorterDuff.Mode.SRC_IN);
            } else {
                alarm_btn.setColorFilter(itemView.getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);
            }
        }

        // 북마크 아이템 이미지 설정
        void set_bookmark_image() {
            Thread mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        try {
                            URL url = new URL(item_image);

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

    }

}
