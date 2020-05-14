package org.techtown.smarket_android.searchItemList;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.UserLogin.user_login_fragment;


public class search_fragment extends Fragment {
    private ViewGroup viewGroup;
    private Button search_btn;
    InputMethodManager imm;
    ClearEditText search_text;
    Button lockBtn;
    JSONArray key;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_main, container, false);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        /*search_text = viewGroup.findViewById(R.id.search_value);
        search_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(); // 키보드 입력 후 엔터 입력시 키보드 창 내림
                    return true;
                }
                return false;
            }
        });*/

        search_text = viewGroup.findViewById(R.id.search_value);


        lockBtn = (Button) viewGroup.findViewById(R.id.login_lock);
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_login_fragment ulf = new user_login_fragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, ulf).addToBackStack(null).commitAllowingStateLoss();

            }
        });

        return viewGroup;
    }

    private Bundle setBundle() {
        Bundle bundle = new Bundle();

        String text = search_text.getText().toString();
        if (text.equals("")) {
            Toast.makeText(getContext(), "폴더명을 입력해주세요", Toast.LENGTH_LONG).show();
        } else if (!text.equals("")) {
            char except_enter[] = text.toCharArray();
            if (except_enter[except_enter.length - 1] == '\n') {

                char result_char[] = new char[except_enter.length - 1];
                System.arraycopy(except_enter, 0, result_char, 0, except_enter.length - 1);
                text = String.valueOf(result_char);

            } // 한글 입력 후 엔터시 개행문자 발생하는 오류 처리
            bundle.putString("searchName", text);


        }
        return bundle;
    }




    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
    }// 키보드 입력 후 엔터 입력시 키보드 창 내림
}
