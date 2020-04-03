package org.techtown.smarket_android.searchItemList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    public interface OnRecyclerClickListener{
        void OnRecyclerClickListener(View v, int position);
    }

    private OnRecyclerClickListener onRecyclerClickListener = null;

    public void setOnRecyclerClickListener(OnRecyclerClickListener listener){
        this.onRecyclerClickListener = listener;
    }

    // adapter에 들어갈 list 입니다.
    private ArrayList<Item> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);

        final ItemViewHolder itemViewHolder = new ItemViewHolder(view);

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int pos = itemViewHolder.getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    if(onRecyclerClickListener!=null){
                        onRecyclerClickListener.OnRecyclerClickListener(v,pos);
                    }
                }
            }
        });

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
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
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView item_name;
        private TextView item_value;
        private ImageView itemImage;
        private ImageButton heart;

        ItemViewHolder(View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.search_list_item_name);
            item_value = itemView.findViewById(R.id.search_list_item_value);
            itemImage = itemView.findViewById(R.id.search_list_item_image);
            heart = itemView.findViewById(R.id.heart_btn);
        }

        void onBind(Item data) {
            item_name.setText(data.getItem_name());
            item_value.setText(data.getItem_value());
            itemImage.setImageResource(data.getItem_image());
        }
    }


//    private ArrayList<Item> listData = new ArrayList<>();
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
////        MyViewHolder viewHolder = new MyViewHolder(view);
//        return new MyViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.onBind(listData.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return listData.size();
//    }
//
//    void addItem(Item item) {
//        // 외부에서 item을 추가시킬 함수입니다.
//        listData.add(item);
//    }
//
//     static class MyViewHolder extends RecyclerView.ViewHolder{
//        private TextView item_name;
//        private TextView item_value;
//        private ImageView item_image;
//
//        MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            item_name = itemView.findViewById(R.id.search_list_item_name);
//            item_value = itemView.findViewById(R.id.item_value);
//            item_image = itemView.findViewById(R.id.item_image);
//        }
//
//        void onBind(Item item){
//            item_name.setText(item.getItem_name());
//            item_value.setText(item.getItem_value());
//            item_image.setImageResource(item.getItem_image());
//        }
//    }
}


//    private ArrayList<Item> list;
//    private LayoutInflater mInflate;
//    private Context mContext;
//    private ImageButton heart_btn;
//
//    public interface OnItemClickListener{
//        void onItemClickListener(View v, int position);
//    }
//
//    private OnItemClickListener onItemClickListener = null;
//
//    public void setOnItemClickListener(OnItemClickListener listener){
//        this.onItemClickListener = listener;
//    }
//
//    public itemAdapter(Context context, ArrayList<Item> list){
//        this.mContext = context;
//        this.mInflate = LayoutInflater.from(context);
//        this.list = list;
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        final View view = mInflate.inflate(R.layout.search_list_item, parent, false);
//        final MyViewHolder viewHolder = new MyViewHolder(view);
//
//        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int pos = viewHolder.getAdapterPosition();
//                if(pos != RecyclerView.NO_POSITION){
//                    if(onItemClickListener!=null){
//                        onItemClickListener.onItemClickListener(v,pos);
//                    }
//                }
//            }
//        });
//
//
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
//        holder.item_name.setText(list.get(position).getItem_name());
//        holder.item_value.setText(list.get(position).getItem_value());
//        holder.item_image.setImageDrawable(list.get(position).getItem_image());
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    static class MyViewHolder extends RecyclerView.ViewHolder{
//        TextView item_name;
//        TextView item_value;
//        ImageView item_image;
//        ImageButton heart_btn1;
//
//        final View mView;
//
//        MyViewHolder(View itemView){
//            super(itemView);
//            item_name = itemView.findViewById(R.id.item_name);
//            item_value = itemView.findViewById(R.id.item_value);
//            item_image = itemView.findViewById(R.id.item_image);
//            heart_btn1 = itemView.findViewById(R.id.heart_btn);
//            mView = itemView;
//        }
//    }

