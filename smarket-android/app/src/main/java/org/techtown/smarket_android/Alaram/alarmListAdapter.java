package org.techtown.smarket_android.Alaram;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.Class.SearchedItem;
import org.techtown.smarket_android.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class alarmListAdapter extends RecyclerView.Adapter<alarmListAdapter.alViewHolder> {

    private List<SearchedItem> alarmList;
    private Context mContext;
    private Activity mActivity;

    alarmListAdapter(Activity activity, Context context, List<SearchedItem> list ){
        mActivity = activity;
        mContext = context;
        alarmList = list;
    }
    @NonNull
    @Override
    public alViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_item, parent, false);

        final alViewHolder alViewHolder = new alViewHolder(view);

        return alViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull alViewHolder holder, int position) {
        holder.onBind(alarmList.get(position));
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class alViewHolder extends RecyclerView.ViewHolder {

        private TextView item_title;
        private String item_id;
        private String item_type;
        private int item_lprice;
        private TextView alarm_message;
        private TextView item_price;
        private TextView alarm_type;
        private ImageView direction;
        //private TextView alarm_posted;

        private String item_image_url;
        private ImageView item_image;

        private Bitmap bitmap;

        alViewHolder(View itemView){
            super(itemView);

            item_title = itemView.findViewById(R.id.alamr_item_title_textView);
            item_image = itemView.findViewById(R.id.alarm_item_imageView);
            item_price = itemView.findViewById(R.id.alarm_item_price_textView);
            alarm_message = itemView.findViewById(R.id.alarm_message_textView);
            alarm_type = itemView.findViewById(R.id.alarm_type_textView);
            direction = itemView.findViewById(R.id.direction_imageView);

            //alarm_posted = itemView.findViewById(R.id.alarm_posted_textView);
        }

        public void onBind(SearchedItem data){
            item_title.setText(data.getItem_title());
            item_image_url = data.getItem_image();
            set_item_image();
            item_lprice = Integer.parseInt(data.getItem_price());
            item_price.setText(String.format("%,d", item_lprice)+"원");

            alarm_message.setText(data.getAlarm_message());
            alarm_type.setText(data.getAlarm_type());
            set_alarmType();

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

        // direction ImageView 설정정
       void set_alarmType(){
            if(alarm_type.getText().equals("하락")){
                direction.setColorFilter(itemView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            }

            else if(alarm_type.getText().equals("상승")){
                alarm_type.setBackground(mContext.getResources().getDrawable(R.drawable.alarm_type_up));
                direction.setColorFilter(itemView.getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);
                alarm_message.setTextColor(mContext.getResources().getColor(R.color.blue));
                item_price.setTextColor(mContext.getResources().getColor(R.color.blue));
                direction.setRotationX(180);
            }
        }
    }
}
