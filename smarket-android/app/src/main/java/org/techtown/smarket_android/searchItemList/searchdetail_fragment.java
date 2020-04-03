package org.techtown.smarket_android.searchItemList;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.techtown.smarket_android.MainActivity;
import org.techtown.smarket_android.R;


public class searchdetail_fragment extends Fragment {
    ViewGroup viewGroup;
    Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_item_detail, container, false);

        ReceiveData();
        setHasOptionsMenu(true);

        return viewGroup;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
    }


    private void ReceiveData(){
        bundle = getArguments();

        if(bundle != null){
            String in = bundle.getString("item_name");
            String iv = bundle.getString("item_value");
            Bitmap bitmap = bundle.getParcelable("item_image");

            TextView item_name = viewGroup.findViewById(R.id.detail_item_name);
            TextView item_value = viewGroup.findViewById(R.id.detail_item_value);
            ImageView item_image = viewGroup.findViewById(R.id.item_detail_image);

            item_image.setImageBitmap(bitmap);
            item_name.setText(in);
            item_value.setText(iv);
        }

    }
}

