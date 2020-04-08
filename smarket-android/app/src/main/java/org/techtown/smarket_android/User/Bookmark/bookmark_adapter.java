package org.techtown.smarket_android.User.Bookmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Item;
import org.techtown.smarket_android.searchItemList.RecyclerAdapter;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class bookmark_adapter extends RecyclerView.Adapter<bookmark_adapter.bmViewHolder> {


    // adapter에 들어갈 list 입니다.
    private ArrayList<Item> listData = new ArrayList<>();

    @NonNull
    @Override
    public bookmark_adapter.bmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);

        bmViewHolder itemViewHolder = new bookmark_adapter.bmViewHolder(view);


        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull bookmark_adapter.bmViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
        holder.heart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "hihi", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(Item data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public static class bmViewHolder extends RecyclerView.ViewHolder {

        private TextView item_name;
        private TextView item_value;
        private ImageView itemImage;
        private ImageView heart_btn;

        public bmViewHolder(@NonNull  View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.search_list_item_name);
            item_value = itemView.findViewById(R.id.search_list_item_value);
            itemImage = itemView.findViewById(R.id.search_list_item_image);
            heart_btn = itemView.findViewById(R.id.heart_btn);
        }

        void onBind(Item data) {
            item_name.setText(data.getList_item_name());
            item_value.setText(data.getList_item_value());
            itemImage.setImageResource(data.getItem_image());
        }
    }
}
