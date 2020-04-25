package org.techtown.smarket_android.User.Bookmark;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Bookmark;
import org.techtown.smarket_android.searchItemList.Item;

import java.util.ArrayList;
import java.util.List;

public class bookmark_item_list_adapter extends RecyclerView.Adapter<bookmark_item_list_adapter.bmViewHolder> {


    // adapter에 들어갈 list 입니다.
    private Context mContext;
    private Activity mActivity;
    private List<Bookmark> bookmarkItemList;
    private List<String> timeList;
    private List<String> booleanValueList;
    private InputMethodManager imm;

    private static final String SETTINGS_BOOKMARK_JSON = "settings_bookmark_json"; // SharedPreference 북마크 리스트 Data Key
    private static final String SETTINGS_TIMELIST_JSON = "settings_timelist_json"; // SharedPreference 타임 리스트 Data Key
    private static final String SETTINGS_BOOLEANVALUE_JSON = "settings_booleanvalue_json"; // SharedPreference 최저가 알람 설정 값 Data Key


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


        timeList = getStringArrayPref(mContext, SETTINGS_TIMELIST_JSON);// 최저가 알람 시간 데이터 가져오기
        booleanValueList = getStringArrayPref(mContext, SETTINGS_BOOLEANVALUE_JSON); // 최저가 알람 설정 데이터 가져오기
        holder.setPriceAlarm(booleanValueList.get(position)); // 최저가 알람 버튼 색상 설정
        holder.setBookmark();
        //set_timeList();
        //set_booleanValueList();

        holder.heart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View itemView) {
                if (!holder.bookmark_check) {
                    holder.heart_btn.setColorFilter(itemView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                    holder.bookmark_check = true;

                } else if (holder.bookmark_check) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            .setTitle("북마크 해제")
                            .setMessage("북마크 등록을 해제 하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.heart_btn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                                    holder.bookmark_check = false;
                                }
                            })
                            .setNegativeButton("취소", null);
                    builder.create();
                    builder.show();
                }
            }
        }); // 북마크 버튼 기능 설정

        holder.cash_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    int itemPosition = holder.getAdapterPosition();
                    FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_layout, bookmark_price_alarm_fragment.newInstance(itemPosition));
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
            }
        }); // 최저가 알람 버튼 기능 설정

    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return bookmarkItemList.size();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public static class bmViewHolder extends RecyclerView.ViewHolder {

        private TextView bookmark_name;
        private String bookmark_url;
        private TextView bookmark_price;
        private ImageView bookmark_image;
        private Boolean bookmark_check;
        private Boolean alarm_check;

        private ImageView heart_btn;
        private ImageView cash_btn;

        public bmViewHolder(@NonNull View itemView) {
            super(itemView);

            bookmark_name = itemView.findViewById(R.id.search_list_item_name);
            bookmark_url = "";
            bookmark_price = itemView.findViewById(R.id.search_list_item_value);
            bookmark_image = itemView.findViewById(R.id.search_list_item_image);
            bookmark_check = false;
            alarm_check = false;

            heart_btn = itemView.findViewById(R.id.heart_btn);
            cash_btn = itemView.findViewById(R.id.cash_btn);
        }

        void onBind(Bookmark bookmark) {

            bookmark_name.setText(bookmark.getBookmark_name());
            bookmark_url = bookmark.getBookmark_url();
            bookmark_price.setText(bookmark.getBookmark_price());
            Bitmap image = new GsonBuilder().create().fromJson(bookmark.getBookmark_image(), Bitmap.class);
            //bookmark_image.setImageBitmap(image);
            bookmark_image.setImageDrawable(itemView.getResources().getDrawable(R.drawable.premierball));
            bookmark_check = bookmark.getBookmark_check();

            Log.d("bookmark", bookmark.toString());
        } // 아이템 바인드

        void setPriceAlarm(String alarm_checked){
            if(alarm_checked.equals("true")){
                cash_btn.setColorFilter(itemView.getResources().getColor(R.color.smarketyello), PorterDuff.Mode.SRC_IN);
            }else{
                cash_btn.setColorFilter(itemView.getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_IN);
            }
        } // 최적가 알람 버튼 색상 설정

        void setBookmark(){
            bookmark_check = true;
            heart_btn.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
    }



    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> timeList = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String item = a.optString(i);
                    timeList.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return timeList;
    }// 북마크 폴더 리스트 & 타임 리스트 데이터 가져오기

    private void set_booleanValueList(){

        ArrayList<String> booleanValueList = new ArrayList<>();

        for (int i = 0; i < getItemCount(); i++) {
            booleanValueList.add("true");
        }
        setStringArrayPref(mContext, SETTINGS_BOOLEANVALUE_JSON, booleanValueList);
    } // 디폴트 최저가 알람 데이터리스트 생성 :: 한번만 실행

    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
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
    } // SharedPreference Data 생성

}
