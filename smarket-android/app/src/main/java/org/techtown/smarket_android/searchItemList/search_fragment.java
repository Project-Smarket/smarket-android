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
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.user_login_fragment;


public class search_fragment extends Fragment {
    private ViewGroup viewGroup;
    private Button search_btn;
    InputMethodManager imm;
    EditText search_text;
    Button lockBtn;
    JSONArray key;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.search_main, container, false);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        search_text = viewGroup.findViewById(R.id.search_value);
        search_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(); // 키보드 입력 후 엔터 입력시 키보드 창 내림
                    return true;
                }
                return false;
            }
        });

        search_btn = (Button) viewGroup.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search_list_fragment slf = new search_list_fragment();
                Bundle bundle = setBundle();
                slf.setArguments(bundle);
                assert getFragmentManager() != null;
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, slf).addToBackStack(null).commitAllowingStateLoss();
            }
        });

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


//    private void getJson() throws UnsupportedEncodingException {
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    key = jsonObject.getJSONArray("items"); // json으로 검색한 결과 객체
//
//                    JSONObject item = key.getJSONObject(0); //원하는 json 결과 인덱스 접근
//                    itemTitle = item.getString("title"); // 0번 인덱스 객체의 결과값 중 title 선택
//
//                    Bundle bundle = setBundle();
//                    search_list_fragment slf = new search_list_fragment();
//                    slf.setArguments(bundle);
//                    try {
//                        Toast.makeText(getContext(), key.getJSONObject(0).getString("title"), Toast.LENGTH_SHORT );
//                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.main_layout, slf).addToBackStack(null).commit();
//                        Log.d(TAG, "onResponse: 성공");
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        searchRequest searchRequest = new searchRequest(search_text.getText().toString(), responseListener, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), error + "", Toast.LENGTH_LONG).show();
//            }
//        });
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        queue.add(searchRequest);
//    }
// getJson 양식 참고