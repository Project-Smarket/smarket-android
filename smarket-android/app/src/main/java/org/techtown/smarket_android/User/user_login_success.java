package org.techtown.smarket_android.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.Bookmark.bookmark_item_list_fragment;
import org.techtown.smarket_android.User.UserInfrom.userinform_fragment;
import org.techtown.smarket_android.User.recent.recent_fragment;


public class user_login_success extends Fragment {

    public static user_login_success newInstance() {
        return new user_login_success();
    }

    ViewGroup viewGroup;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.user_login_success, container, false);

        ConstraintLayout bookmark = viewGroup.findViewById(R.id.bookmark);

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_layout, bookmark_item_list_fragment.newInstance()).commit();
            }
        });

        ConstraintLayout recent = viewGroup.findViewById(R.id.recent);

        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_layout, recent_fragment.newInstance()).commit();
            }
        });

        ConstraintLayout userinfrom = viewGroup.findViewById(R.id.userinform);

        userinfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_layout, userinform_fragment.newInstance()).commit();
            }
        });
        return viewGroup;
    }
}
