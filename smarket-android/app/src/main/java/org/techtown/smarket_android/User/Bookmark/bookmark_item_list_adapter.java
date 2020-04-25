package org.techtown.smarket_android.User.Bookmark;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Item;

import java.util.ArrayList;
import java.util.List;

public class bookmark_item_list_adapter extends RecyclerView.Adapter<bookmark_item_list_adapter.bmViewHolder> {


    // adapter에 들어갈 list 입니다.
    private ArrayList<Item> listData = new ArrayList<>();
    private Context mContext;
    private OnItemClick mCallback;
    private List<Item> bookmarkItemList;

    public bookmark_item_list_adapter(Context context, List<Item> bookmarkItemList , OnItemClick listener){
        this.mContext = context;
        this.mCallback = listener;
        this.bookmarkItemList = bookmarkItemList;
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
    public void onBindViewHolder(@NonNull final bookmark_item_list_adapter.bmViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(bookmarkItemList.get(position));

        holder.heart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.bookmark_check) {
                    Intent intent = new Intent(mContext, bookmark_folder_list_activity.class);
                    mContext.startActivity(intent);
                    holder.heart_btn.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    holder.bookmark_check = true;
                } else if (holder.bookmark_check) {
                    holder.heart_btn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                    holder.bookmark_check = false;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return bookmarkItemList.size();
    }

    void addItem(Item data) {
        // 외부에서 item을 추가시킬 함수입니다.
        bookmarkItemList.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public static class bmViewHolder extends RecyclerView.ViewHolder {

        private TextView item_name;
        private TextView item_value;
        private ImageView itemImage;
        private ImageView heart_btn;
        private Boolean bookmark_check;

        public bmViewHolder(@NonNull  View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.search_list_item_name);
            item_value = itemView.findViewById(R.id.search_list_item_value);
            itemImage = itemView.findViewById(R.id.search_list_item_image);
            heart_btn = itemView.findViewById(R.id.heart_btn);
            bookmark_check = false;
        }

        void onBind(Item data) {
//            Log.d(TAG, "onBind: "+data.getList_item_name());
            item_name.setText(data.getList_item_name());
            item_value.setText(data.getList_item_value());
        }
    }

    private void bookmark_add(View view){
        Toast.makeText(mContext, "bookmark_add가 실행되었습니다", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(view.getContext(), bookmark_folder_list_activity.class);
    }

    public interface OnItemClick{
        void onClick(String value);
    }
}
