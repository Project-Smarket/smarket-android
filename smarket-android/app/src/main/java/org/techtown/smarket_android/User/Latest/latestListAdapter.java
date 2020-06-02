package org.techtown.smarket_android.User.Latest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.DTO_Class.DTO;
import org.techtown.smarket_android.R;

import java.util.List;

public class latestListAdapter extends RecyclerView.Adapter<latestListAdapter.lViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private List<DTO> latestList;

    latestListAdapter(Context context, Activity activity, List<DTO> list){
        mContext = context;
        mActivity = activity;
        latestList = list;
    }

    @NonNull
    @Override
    public lViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.latest_main, parent, false);

        final lViewHolder lViewHolder = new lViewHolder(view);

        return lViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull lViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class lViewHolder extends RecyclerView.ViewHolder {

        public lViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
