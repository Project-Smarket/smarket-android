package org.techtown.smarket_android.searchItemList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;

import java.util.ArrayList;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.MyViewHolder> {

    private ArrayList<Item> list;
    private LayoutInflater mInflate;
    private Context mContext;

    public interface OnItemClickListener{
        void onItemClickListener(View v, int position);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public itemAdapter(Context context, ArrayList<Item> list){
        this.mContext = context;
        this.mInflate = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.search_list_item, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClickListener(v,pos);
                    }
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.item_name.setText(list.get(position).getItem_name());
        holder.item_value.setText(list.get(position).getItem_value());
        holder.item_image.setImageDrawable(list.get(position).getItem_image());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView item_name;
        public TextView item_value;
        public ImageView item_image;
        public Button heart_btn;
        public final View mView;

        public MyViewHolder(View itemView){
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_value = itemView.findViewById(R.id.item_value);
            item_image = itemView.findViewById(R.id.item_image);
            mView = itemView;
        }
    }
}
