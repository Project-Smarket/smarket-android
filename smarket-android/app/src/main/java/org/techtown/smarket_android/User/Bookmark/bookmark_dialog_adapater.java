package org.techtown.smarket_android.User.Bookmark;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;

import java.util.List;

public class bookmark_dialog_adapater extends RecyclerView.Adapter<bookmark_dialog_adapater.itemViewHolder> {

    private List<String> bookmarkFolderList;
    private Activity mActivity;

    public bookmark_dialog_adapater(List<String> list, Activity activity){
        bookmarkFolderList = list;
        mActivity = activity;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position, List<String> bookmarkFolderList) ;
    }
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public bookmark_dialog_adapater.itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) mActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bookmark_folder_list_item, parent, false);

        final bookmark_dialog_adapater.itemViewHolder itemViewHolder = new bookmark_dialog_adapater.itemViewHolder(view);

        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookmark_dialog_adapater.itemViewHolder holder, int position) {
        holder.onBind(this.bookmarkFolderList.get(position));
    }

    public void add_folder(String data){
        this.bookmarkFolderList.add(data);
    }

    @Override
    public int getItemCount() {
        return this.bookmarkFolderList.size();
    }



    public class itemViewHolder extends RecyclerView.ViewHolder{
        private TextView folder_item;
        private itemViewHolder(View itemView){
            super(itemView);
            folder_item = itemView.findViewById(R.id.folder_list_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    List<String>list = bookmarkFolderList;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos, list) ;
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
