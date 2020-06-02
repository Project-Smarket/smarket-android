package org.techtown.smarket_android.Alarm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.DTO_Class.Alarm;
import org.techtown.smarket_android.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class alarmListAdapter extends RecyclerView.Adapter<alarmListAdapter.alViewHolder> {

    private List<Alarm> alarmList;
    private Context mContext;
    private Activity mActivity;

    alarmListAdapter(Activity activity, Context context, List<Alarm> list) {
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
        if (holder.lprice_diff < 0)
            holder.set_alarmType_down();
        else if (holder.lprice_diff > 0)
            holder.set_alarmType_up();
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class alViewHolder extends RecyclerView.ViewHolder {

        private int lprice_diff;

        private TextView alarm_title; // 상품 제목
        private TextView alarm_diff;
        private TextView alarm_productType;
        private TextView alarm_won;
        private TextView alarm_lprice;
        private TextView alarm_type;
        private ImageView direction;
        private ImageView direction2;
        private TextView alarm_date;
        //private TextView alarm_posted;

        private ImageView alarm_image;

        private Bitmap bitmap;

        alViewHolder(View itemView) {
            super(itemView);

            alarm_title = itemView.findViewById(R.id.alamr_item_title_textView);
            alarm_image = itemView.findViewById(R.id.alarm_item_imageView);
            alarm_lprice= itemView.findViewById(R.id.alarm_item_price_textView);
            alarm_diff = itemView.findViewById(R.id.alarm_diff_textView);
            alarm_date = itemView.findViewById(R.id.alarm_date_textView);

            alarm_type = itemView.findViewById(R.id.alarm_type_textView);
            alarm_productType = itemView.findViewById(R.id.alarm_productType_textView);
            alarm_won = itemView.findViewById(R.id.alarm_won_textVIew);
            direction = itemView.findViewById(R.id.direction_imageView);
            direction2 = itemView.findViewById(R.id.direction_imageView2);

            //alarm_posted = itemView.findViewById(R.id.alarm_posted_textView);
        }

        public void onBind(Alarm data) {
            alarm_title.setText(data.getItem_title());
            String item_image = data.getItem_image();
            set_alarm_image(item_image);
            int item_lprice = Integer.parseInt(data.getItem_lprice());
            alarm_lprice.setText(String.format("%,d", item_lprice));
            lprice_diff = data.getLprice_diff();
            alarm_date.setText(data.getAlarm_date());
        }

        void set_alarm_image(final String item_image) {
            //안드로이드에서 네트워크와 관련된 작업을 할 때,
            //반드시 메인 쓰레드가 아닌 별도의 작업 쓰레드를 생성하여 작업해야 한다.
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
                alarm_image.setImageBitmap(bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // direction ImageView 설정정
        void set_alarmType_down() {
            alarm_diff.setText(String.format("%,d", lprice_diff*-1)+"원");
            alarm_diff.setTextColor(mContext.getResources().getColor(R.color.blue));
            alarm_lprice.setTextColor(mContext.getResources().getColor(R.color.blue));

            alarm_type.setText("하락");
            alarm_type.setBackground(mContext.getResources().getDrawable(R.drawable.alarm_type_up));

            alarm_productType.setTextColor(mContext.getResources().getColor(R.color.blue));
            alarm_won.setTextColor(mContext.getResources().getColor(R.color.blue));

            direction.setColorFilter(itemView.getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);
            direction2.setColorFilter(itemView.getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);
            direction.setRotationX(0);
            direction2.setRotationX(0);
        }

        void set_alarmType_up() {
            alarm_diff.setText(String.format("%,d", lprice_diff)+"원");
            alarm_diff.setTextColor(mContext.getResources().getColor(R.color.red));
            alarm_lprice.setTextColor(mContext.getResources().getColor(R.color.red));

            alarm_type.setText("상승");
            alarm_type.setBackground(mContext.getResources().getDrawable(R.drawable.alarm_type_down));

            alarm_productType.setTextColor(mContext.getResources().getColor(R.color.red));
            alarm_won.setTextColor(mContext.getResources().getColor(R.color.red));

            direction.setColorFilter(itemView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            direction2.setColorFilter(itemView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            direction.setRotationX(180);
            direction2.setRotationX(180);
        }
    }
}
