package org.techtown.smarket_android.Alarm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.techtown.smarket_android.DTO_Class.Alarm;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Search.search_detail_fragment;

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

        private Alarm item_data;

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

            // fluctuation_fragment로 데이터 전송
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fluctuation_fragment fluctuation_fragment = new fluctuation_fragment();

                    Bundle bundle = settingBundle();
                    fluctuation_fragment.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_layout, fluctuation_fragment, "search").addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }

        private Bundle settingBundle(){
            Bundle bundle = new Bundle();

            bundle.putParcelable("item_image", bitmap);
            bundle.putParcelable("item_data", item_data);

            return bundle;
        }

        public void onBind(Alarm data) {
            item_data = data;
            alarm_title.setText(data.getItem_title());
            String item_image = data.getItem_image();
            set_alarm_image(item_image);
            int item_lprice = Integer.parseInt(data.getItem_lprice());
            alarm_lprice.setText(String.format("%,d", item_lprice));
            lprice_diff = data.getLprice_diff();
            alarm_date.setText(data.getAlarm_date());
        }

        void set_alarm_image(final String item_image) {
            Glide.with(mContext).asBitmap().load(item_image).into(alarm_image);
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
