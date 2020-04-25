package org.techtown.smarket_android.User.UserInfrom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.techtown.smarket_android.R;

public class userinform_fragment extends Fragment {

    public static userinform_fragment newInstance() {
        return new userinform_fragment();
    }

    private ViewGroup viewGroup;
    private TextView user_id_textView;
    private TextView access_token_textView;
    private TextView refresh_token_textView;


    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;
    static private String TAG = "TOKEN";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.userinform_main, container, false);

        user_id_textView = viewGroup.findViewById(R.id.user_id);
        access_token_textView = viewGroup.findViewById(R.id.access_token);
        refresh_token_textView = viewGroup.findViewById(R.id.refresh_token);

        // userFile에 저장된 user_id 와 access_token 값 가져오기
        get_userFile();

        Log.d(TAG, "access_token : " + access_token);
        Log.d(TAG, "refresh_token : " + refresh_token);

        user_id_textView.setText(user_id);
        access_token_textView.setText(access_token);
        refresh_token_textView.setText(refresh_token);

        return viewGroup;
    }

    private void get_userFile(){
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    } // userFile에 저장된 user_id 와 access_token 값 가져오기
}
