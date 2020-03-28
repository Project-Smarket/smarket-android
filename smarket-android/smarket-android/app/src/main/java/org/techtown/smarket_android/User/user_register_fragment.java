package org.techtown.smarket_android.User;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.techtown.smarket_android.R;


public class user_register_fragment extends Fragment {
    ViewGroup viewGroup;

    public static user_register_fragment newInstance(){
        return new user_register_fragment();
    }

    EditText register_id;
    EditText register_pw;
    EditText register_name;
    private AlertDialog dialog;
    private boolean validate = false;
    Button validate_btn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.user_register_main, container, false);

        register_id = viewGroup.findViewById(R.id.register_id_et);
        register_pw = viewGroup.findViewById(R.id.register_pw_et);
        register_name = viewGroup.findViewById(R.id.register_name_et);

        validate_btn = viewGroup.findViewById(R.id.validate_btn);
        validate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        Button register_btn = viewGroup.findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        return viewGroup;
    }

    private void validate(){
        String userID = register_id.getText().toString();
        if(validate){
            return;
        }
        if(userID.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("아이디는 빈 칸 일 수 없습니다.")
                    .setPositiveButton("확인",null)
                    .create();
            dialog.show();
            return;
        }
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("사용할 수 있는 아이디 입니다.")
                                .setPositiveButton("확인",null)
                                .create();
                        dialog.show();
                        register_id.setEnabled(true);
                        validate = true;
                        register_id.setBackgroundColor(getResources().getColor(R.color.colorgray));
                        validate_btn.setBackgroundColor(getResources().getColor(R.color.colorgray));
                        return;
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("사용할 수 있는 아이디 입니다.")
                                .setNegativeButton("확인",null)
                                .create();
                        dialog.show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();;
                }
            }
        };
        user_register_validate validateRequest = new user_register_validate(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(validateRequest);
    }

    private void register(){
        String userID = register_id.getText().toString();
        String userPW = register_pw.getText().toString();
        String userName = register_name.getText().toString();

        if(!validate)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                    .setPositiveButton("확인",null)
                    .create();
            dialog.show();
            return;
        }

        if(userID.equals("")|| userPW.equals("") || userName.equals(""))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                    .setPositiveButton("확인",null)
                    .create();
            dialog.show();
            return;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("회원등록에 성공했습니다.")
                                .setPositiveButton("확인",null)
                                .create();
                        dialog.show();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("회원 등록에 실패했습니다.")
                                .setNegativeButton("확인",null)
                                .create();
                        dialog.show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();;
                }
            }
        };
        user_register_request registerRequest= new user_register_request(userID, userPW, userName, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(registerRequest);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(dialog != null)
        {
            dialog.dismiss();
            dialog = null;
        }
    }
}
