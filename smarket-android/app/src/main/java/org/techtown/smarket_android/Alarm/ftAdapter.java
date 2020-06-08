package org.techtown.smarket_android.Alarm;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.DTO_Class.Fluctuation;
import org.techtown.smarket_android.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

class ftAdapter extends RecyclerView.Adapter<ftAdapter.ftViewHolder> {

    private Activity mActivity;
    private Context mContext;
    private List<Fluctuation> fluctuationList;

    ftAdapter(Context context, Activity activity, List<Fluctuation> list){
        mContext = context;
        mActivity = activity;
        fluctuationList = list;
    }

    @NonNull
    @Override
    public ftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_fluctuation_item, parent, false);

        final ftViewHolder ftViewHolder = new ftViewHolder(view);

        return ftViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ftViewHolder holder, int position) {
        holder.onBind(fluctuationList.get(position));
        if(fluctuationList.get(position).getLprice_diff() > 0){
            holder.set_price_up();
        }else{
            holder.set_price_down();
        }
    }

    @Override
    public int getItemCount() {
        return fluctuationList.size();
    }

    public class ftViewHolder extends RecyclerView.ViewHolder {

        private TextView fluctuation_date;
        private TextView fluctuation_diff;
        private TextView fluctuation_lprice;
        private ImageView fluctuation_direction;

        public ftViewHolder(@NonNull View itemView) {
            super(itemView);

            fluctuation_date = itemView.findViewById(R.id.fluctuation_date);
            fluctuation_diff = itemView.findViewById(R.id.fluctuation_diff);
            fluctuation_lprice = itemView.findViewById(R.id.fluctuation_lprice);
            fluctuation_direction = itemView.findViewById(R.id.fluctuation_direction);

        }

        public void onBind(Fluctuation data){

            String date = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(data.getDate());
            fluctuation_date.setText(date);
            fluctuation_diff.setText(String.format("%,d", data.getLprice_diff())+"원");
            int item_lprice = Integer.parseInt(data.getLprice());
            fluctuation_lprice.setText(String.format("%,d", item_lprice)+"원");
        }

        public void set_price_down() {
            fluctuation_diff.setTextColor(mContext.getResources().getColor(R.color.blue));
            fluctuation_direction.setColorFilter(itemView.getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);
            fluctuation_direction.setRotationX(0);
            fluctuation_lprice.setTextColor(mContext.getResources().getColor(R.color.blue));

        }

        public void set_price_up() {
            fluctuation_diff.setTextColor(mContext.getResources().getColor(R.color.red));
            fluctuation_direction.setColorFilter(itemView.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            fluctuation_direction.setRotationX(180);
            fluctuation_lprice.setTextColor(mContext.getResources().getColor(R.color.red));

        }
    }
}
