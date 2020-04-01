package org.techtown.smarket_android.User.Bookmark;

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
import org.techtown.smarket_android.searchItemList.Item;
import org.techtown.smarket_android.searchItemList.itemAdapter;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class bookmark_adapter extends RecyclerView.Adapter<bookmark_adapter.bmViewHolder> {


    private ArrayList<Item> list;
    private LayoutInflater mInflate;
    private Context mContext;

    public interface OnItemClickListener{
        void onItemClickListener(View v, int position);
    }

    private itemAdapter.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(itemAdapter.OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public bookmark_adapter(Context context, ArrayList<Item> list){
        this.mContext = context;
        this.mInflate = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public bmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.search_list_item, parent, false);
        final bookmark_adapter.bmViewHolder viewHolder = new bookmark_adapter.bmViewHolder(view);

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
    public void onBindViewHolder(@NonNull final bookmark_adapter.bmViewHolder holder, final int position) {
        holder.item_name.setText(list.get(position).getItem_name());
        holder.item_value.setText(list.get(position).getItem_value());
        holder.item_image.setImageDrawable(list.get(position).getItem_image());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class bmViewHolder extends RecyclerView.ViewHolder{
        public TextView item_name;
        public TextView item_value;
        public ImageView item_image;
        public Button heart_btn;
        public final View mView;

        public bmViewHolder(View itemView){
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_value = itemView.findViewById(R.id.item_value);
            item_image = itemView.findViewById(R.id.item_image);
            mView = itemView;
        }
    }
}
