package org.techtown.smarket_android.searchItemList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.techtown.smarket_android.R;


public class search_fragment extends Fragment {
    private ViewGroup viewGroup;
    private Button search_btn;
    private search_list_fragment sf;
    int check_Num=0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_main, container, false);

        search_btn = (Button) viewGroup.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sf = new search_list_fragment();

                Bundle bundle = setBundle(v);
                sf.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, sf).addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        return viewGroup;
    }

    private Bundle setBundle(View v){
        Bundle bundle = new Bundle();
        TextView textView = viewGroup.findViewById(R.id.search_value);

        if(getArguments() != null){
            bundle.putString("search", textView.getText().toString());
        }

        return bundle;
    }

}