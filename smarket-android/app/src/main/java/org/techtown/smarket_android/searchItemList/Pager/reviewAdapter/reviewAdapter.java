package org.techtown.smarket_android.searchItemList.Pager.reviewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.smarketClass.review;
import org.techtown.smarket_android.R;

import java.util.List;

public class reviewAdapter extends RecyclerView.Adapter<reviewAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<review> reviewList;


    public reviewAdapter(List<review> reviewList){
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(reviewList.get(position));

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, content, user, score, mall, date;
        private RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reviewTitle);
            content = itemView.findViewById(R.id.reviewContent);
            user = itemView.findViewById(R.id.reviewUser);
            score = itemView.findViewById(R.id.reviewScore);
            mall = itemView.findViewById(R.id.reviewMall);
            date = itemView.findViewById(R.id.reviewDate);
            ratingBar=itemView.findViewById(R.id.reviewScore_ratingBar);
        }

        public void onBind(review data){

            title.setText(data.getTitle());
            content.setText(data.getContent());
            user.setText(data.getUser());
            String review_score_string = data.getScore().replace("점", "");
            float review_score = Float.parseFloat(review_score_string);
            review_score /= 20;
            score.setText(String.valueOf(review_score)+"점");
            mall.setText(data.getMall());
            date.setText(data.getDate());
            title.setText(data.getTitle());
            ratingBar.setRating(review_score);
        }
    }
}
