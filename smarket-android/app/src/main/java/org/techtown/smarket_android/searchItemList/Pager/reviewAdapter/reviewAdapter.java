package org.techtown.smarket_android.searchItemList.Pager.reviewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.BookmarkClass.review;
import org.techtown.smarket_android.R;

import java.util.List;

public class reviewAdapter extends RecyclerView.Adapter<reviewAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<review> reviewList;

    public reviewAdapter(Context context, List<review> reviewList){
        this.layoutInflater = LayoutInflater.from(context);
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.review_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String Title = reviewList.get(position).getTitle();
        String Content = reviewList.get(position).getContent();
        String Score = reviewList.get(position).getScore();
        String User = reviewList.get(position).getUser();
        String Mall = reviewList.get(position).getMall();
        String Date = reviewList.get(position).getDate();

        holder.title.setText(Title);
        holder.content.setText(Content);
        holder.score.setText(Score);
        holder.user.setText(User);
        holder.mall.setText(Mall);
        holder.date.setText(Date);

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, content, user, score, mall, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reviewTitle);
            content = itemView.findViewById(R.id.reviewContent);
            user = itemView.findViewById(R.id.reviewUser);
            score = itemView.findViewById(R.id.reviewScore);
            mall = itemView.findViewById(R.id.reviewMall);
            date = itemView.findViewById(R.id.reviewDate);
        }
    }
}
