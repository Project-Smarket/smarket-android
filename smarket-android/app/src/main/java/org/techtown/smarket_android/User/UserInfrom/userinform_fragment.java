package org.techtown.smarket_android.User.UserInfrom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.techtown.smarket_android.R;

public class userinform_fragment extends Fragment {

    public static userinform_fragment newInstance() {
        return new userinform_fragment();
    }

    ViewGroup viewGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.userinform_main, container, false);

        return viewGroup;
    }
}
