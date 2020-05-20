package org.techtown.smarket_android.searchItemList.Pager.newsAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.Class.news;
import org.techtown.smarket_android.R;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class newsAdapter extends RecyclerView.Adapter<newsAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<news> newsList;

    public newsAdapter(Context context, List<news> newsList){
        this.layoutInflater = LayoutInflater.from(context);
        this.newsList = newsList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.news_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.onBind(newsList.get(position));

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView newsImg;
        private TextView newsTitle;
        private TextView newsUrl;
        private TextView newsHit;
        private TextView newsUser;
        private TextView newsDate;
        private String newsImageUrl;
        private Bitmap bitmap;

        ViewHolder(final View itemView){
            super(itemView);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsUser = itemView.findViewById(R.id.newsUser);
            newsHit = itemView.findViewById(R.id.newsHit);
            newsDate = itemView.findViewById(R.id.newsDate);
            newsUrl = itemView.findViewById(R.id.newsUrl);
            newsImg =itemView.findViewById(R.id.newsImage);
        }

        public void onBind(news data){
            newsTitle.setText(data.getNewsTitle());
            newsUser.setText(data.getNewsUser());
            newsHit.setText(data.getNewsHit());
            newsDate.setText(data.getNewsDate());
            newsUrl.setText(data.getNewsUrl());
            newsImageUrl = data.getNewsImg();

            set_item_image();
        }

        void set_item_image() {
            //안드로이드에서 네트워크와 관련된 작업을 할 때,
            //반드시 메인 쓰레드가 아닌 별도의 작업 쓰레드를 생성하여 작업해야 한다.
            Thread mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        try {
                            URL url = new URL(newsImageUrl);

                            //웹에서 이미지를 가져온 뒤
                            //이미지뷰에 지정할 비트맵을 만든다
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true); //서버로부터 응답 수신
                            connection.connect();

                            InputStream is = connection.getInputStream(); //inputStream 값 가져오기
                            bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 변환
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            mThread.setDaemon(true);
            mThread.start(); //쓰레드 실행

            try {
                // 메인 쓰레드는 별도의 작업 쓰레드가 작업을 완료할 때까지 대기
                // join()을 호출하여 별도의 작업 쓰레드가 종료될 때까지 메인 쓰레드가 기다리게 한다.
                mThread.join();

                // 작업 쓰레드에서 이미지를 불러오는 작업을 완료한 뒤
                // UI 작업을 할 수 있는 메인 쓰레드에서 imageView에 이미지를 지정한다.
                newsImg.setImageBitmap(bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }



}


