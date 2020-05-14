package org.techtown.smarket_android.Hotdeal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.Class.Hotdeal;
import org.techtown.smarket_android.R;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class hotdealListAdapter extends RecyclerView.Adapter<hotdealListAdapter.hdViewHolder>{

    private Activity mActivity;
    private Context mContext;
    private ArrayList<Hotdeal> hotdealList;

    public hotdealListAdapter(Activity activity, Context context, ArrayList<Hotdeal> list){
        mActivity = activity;
        mContext = context;
        hotdealList = list;
    }

    public interface OnItemLinkSetListener {

        void OnItemLinkSet(String url);

    }


    @NonNull
    @Override
    public hotdealListAdapter.hdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotdeal_list_item, parent, false);

        final hdViewHolder hdViewHolder = new hdViewHolder(view);

        return hdViewHolder;
    }

    @Override
    public int getItemCount() {
        return hotdealList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull hotdealListAdapter.hdViewHolder holder, int position) {
        holder.onBind(hotdealList.get(position));
    }

    class hdViewHolder extends RecyclerView.ViewHolder{

        private TextView category_textView;
        private TextView title_textView;
        private TextView views_textView;
        private TextView comment_textView;
        private TextView posted_textView;
        private String id;
        private String url;

        public hdViewHolder(View itemView){
            super(itemView);
            category_textView = itemView.findViewById(R.id.category_textView);
            title_textView = itemView.findViewById(R.id.hotdeal_list_item_title);
            views_textView = itemView.findViewById(R.id.views_textView);
            comment_textView = itemView.findViewById(R.id.comment_textView);
            posted_textView = itemView.findViewById(R.id.posted_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        String url = hotdealList.get(pos).getUrl();
                        Intent intent = new Intent(mActivity, hotdeal_webView.class);
                        intent.putExtra("url", url);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        public void onBind(Hotdeal hotdeal){
            category_textView.setText(hotdeal.getCategory());
            title_textView.setText(hotdeal.getTitle());
            views_textView.setText(hotdeal.getHit());
            comment_textView.setText(hotdeal.getReplyCount());
            posted_textView.setText(hotdeal.getTime());
            id = hotdeal.getId();
            url = hotdeal.getUrl();
        }
    }



}
