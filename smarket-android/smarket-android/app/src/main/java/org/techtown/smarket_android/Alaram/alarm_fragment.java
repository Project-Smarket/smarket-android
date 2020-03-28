package org.techtown.smarket_android.Alaram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.techtown.smarket_android.R;

public class alarm_fragment extends Fragment {

    public static alarm_fragment newInstance(){
        return new alarm_fragment();
    }

    ViewGroup viewGroup;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.alarm_main, container, false);
        return viewGroup;
    }
}
