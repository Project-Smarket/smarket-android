package org.techtown.smarket_android.searchItemList;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.user_login_success;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class search_fragment extends Fragment {
    private ViewGroup viewGroup;
    private Button search_btn;
    private search_list_fragment sf;
    private AlertDialog dialog;
    InputMethodManager imm;
    EditText search_text;

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
                sf = new search_list_fragment();

                Bundle bundle = setBundle();
                sf.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, sf).addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();

//                search();
            }
        });

        return viewGroup;
    }

    private Bundle setBundle(){
        Bundle bundle = new Bundle(1);
        TextView textView = viewGroup.findViewById(R.id.search_value);

        bundle.putString("search", textView.getText().toString());

        return bundle;
    }

    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
    }// 키보드 입력 후 엔터 입력시 키보드 창 내림

//    private void search(){
//        String url = "https://openapi.naver.com/v1/search/shop.json?"; // 10.0.2.2 안드로이드에서 localhost 주소 접속 방법
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                sf = new search_list_fragment();
//
// //               Bundle bundle = setBundle();
// //               sf.setArguments(bundle);
//
//                Response.Listener<String> responseListener = new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try{
//                            JSONObject jsonObject = new JSONObject();
//                            boolean success = jsonObject.getBoolean("success");
//                            if(success){
//                                AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
//                                dialog = builder.setMessage("성공").
//                                        setNegativeButton("확인",null).create();
//                                dialog.show();
//                                return;
//
//                            }else{
//                                AlertDialog.Builder builder = new AlertDialog.Builder(viewGroup.getContext());
//                                dialog = builder.setMessage("실패").
//                                        setNegativeButton("확인",null).create();
//                                dialog.show();
//                            }
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                };
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), error+"", Toast.LENGTH_LONG).show();
//
//            }
//        }
//        ){
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                String text = search_text.getText().toString();
//                params.put("text", text);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        requestQueue.add(stringRequest);
//    }

}