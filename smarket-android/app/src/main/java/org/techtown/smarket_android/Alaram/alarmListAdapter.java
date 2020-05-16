package org.techtown.smarket_android.Alaram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.Class.SearchedItem;
import org.techtown.smarket_android.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class alarmListAdapter extends RecyclerView.Adapter<alarmListAdapter.alViewHolder> {
    @NonNull
    @Override
    public alViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull alViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class alViewHolder extends RecyclerView.ViewHolder {

        private TextView item_title;
        private String item_id;
        private String item_type;
        private int item_lprice;
        private TextView alarm_message;
        private TextView item_price;

        private String item_image_url;
        private ImageView item_image;

        private TextView item_mall_url;
        private Bitmap bitmap;

        alViewHolder(View itemView){
            super(itemView);

            item_title = itemView.findViewById(R.id.search_list_item_name);
            alarm_message = item_mall_url.findViewById(R.id.alarm_message_textView);
            item_price = itemView.findViewById(R.id.search_list_item_value);
            item_image = itemView.findViewById(R.id.search_list_item_image);
            item_mall_url = itemView.findViewById(R.id.search_mallName);
        }

        public void onBind(SearchedItem data){
            item_title.setText(data.getItem_title());
            alarm_message.setText(data.getAlarm_message());
            item_lprice = Integer.parseInt(data.getItem_price());
            item_price.setText(String.format("%,d", item_lprice)+"원");
            item_image_url = data.getItem_image();
            item_mall_url.setText(data.getItem_mall());

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
    }
}
