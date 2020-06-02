package org.techtown.smarket_android.Search.Pager.spec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.DTO_Class.Deatail_spec;
import org.techtown.smarket_android.R;

import java.util.List;

public class specAdapter extends RecyclerView.Adapter<specAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Deatail_spec> data;

    public specAdapter(Context context, List<Deatail_spec> data){
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.spec_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String Key = data.get(position).getKey();
        String Spec = data.get(position).getSpec();
        holder.key.setText(Key);
        holder.spec.setText(Spec);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView key, spec;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            key = itemView.findViewById(R.id.specTitle);
            spec = itemView.findViewById(R.id.specContent);
        }
    }
}
