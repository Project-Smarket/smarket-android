package org.techtown.smarket_android.User.Bookmark;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;

import java.util.ArrayList;
import java.util.List;

public class bookmark_folder_list_adapter extends RecyclerView.Adapter<bookmark_folder_list_adapter.itemViewHolder> {

    private List<String> bookmarkFolderList;

    public bookmark_folder_list_adapter(List<String> bookmarkFolderList){
        this.bookmarkFolderList = bookmarkFolderList;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }



    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_folder_item, parent, false);

        final itemViewHolder itemViewHolder = new itemViewHolder(view);

        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        holder.onBind(bookmarkFolderList.get(position));
    }

    @Override
    public int getItemCount() {
        return bookmarkFolderList.size();
    }

    public void add_folder(String data){
        bookmarkFolderList.add(data);
    }

    // 뷰홀더 클래스
    public class itemViewHolder extends RecyclerView.ViewHolder{
        private TextView folder_item;

        //뷰홀더 생성자
        public itemViewHolder(@NonNull  View itemView){
            super(itemView);

            folder_item = itemView.findViewById(R.id.folder_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });

        }

        void onBind(String data){
            folder_item.setText(data);
        }
    }
}
