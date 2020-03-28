package org.techtown.smarket_android.searchItemList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.techtown.smarket_android.R;


public class search_fragment extends Fragment {
    ViewGroup viewGroup;
    Button search_btn;
    search_list_fragment sf;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_main, container, false);

        sf = new search_list_fragment();

        search_btn = (Button) viewGroup.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView textView = viewGroup.findViewById(R.id.search_value);
                Bundle bundle = new Bundle(1);
                bundle.putString("search", textView.getText().toString());
                sf.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, sf).addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        return viewGroup;
    }

}