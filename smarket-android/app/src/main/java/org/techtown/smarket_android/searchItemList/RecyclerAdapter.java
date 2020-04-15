package org.techtown.smarket_android.searchItemList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.util.Log;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    public interface OnRecyclerClickListener {
        void OnRecyclerClickListener(View v, int position);
    }

    private OnRecyclerClickListener onRecyclerClickListener = null;

    public void setOnRecyclerClickListener(OnRecyclerClickListener listener) {
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

        itemViewHolder.heart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemViewHolder.bookmark_check) {
                    itemViewHolder.heart_btn.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    itemViewHolder.bookmark_check = true;
                } else if (itemViewHolder.bookmark_check) {
                    itemViewHolder.heart_btn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                    itemViewHolder.bookmark_check = false;
                }
            }
        });

        ImageView cash = view.findViewById(R.id.cash_btn);
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),
                        "cash", Toast.LENGTH_SHORT).show();
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
        private ImageView item_Image;
        private ImageView heart_btn;
        private Boolean bookmark_check = false;
        private Bitmap bitmap;


        ItemViewHolder(final View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.search_list_item_name);
            item_value = itemView.findViewById(R.id.search_list_item_value);
            item_Image = itemView.findViewById(R.id.search_list_item_image);
            heart_btn = itemView.findViewById(R.id.heart_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (onRecyclerClickListener != null) {
                            onRecyclerClickListener.OnRecyclerClickListener(v, pos);
                        }
                    }
                }
            });
        }

        void onBind(final Item data) {
            item_name.setText(data.getList_item_name());
            item_value.setText(data.getList_item_value());

            //안드로이드에서 네트워크와 관련된 작업을 할 때,
            //반드시 메인 쓰레드가 아닌 별도의 작업 쓰레드를 생성하여 작업해야 한다.
            Thread mThread = new Thread(){
                @Override
                public void run() {
                    try{
                        try {
                            URL url = new URL(data.getList_item_image());

                            //웹에서 이미지를 가져온 뒤
                            //이미지뷰에 지정할 비트맵을 만든다
                            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
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
                item_Image.setImageBitmap(bitmap);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}