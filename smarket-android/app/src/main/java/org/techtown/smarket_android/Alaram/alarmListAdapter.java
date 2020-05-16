package org.techtown.smarket_android.Alaram;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;

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
        private TextView item_price;

        private String item_image_url;
        private ImageView item_image;

        private TextView item_mall_url;
        private Bitmap bitmap;

        alViewHolder(View itemView){
            super(itemView);

            item_title = itemView.findViewById(R.id.search_list_item_name);
            item_price = itemView.findViewById(R.id.search_list_item_value);
            item_image = itemView.findViewById(R.id.search_list_item_image);
            item_mall_url = itemView.findViewById(R.id.search_mallName);
        }
    }
}
