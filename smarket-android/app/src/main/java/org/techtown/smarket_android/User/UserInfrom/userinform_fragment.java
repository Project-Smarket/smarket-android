package org.techtown.smarket_android.User.UserInfrom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.UserLogin.user_login_success;
import org.techtown.smarket_android.User.UserLogin.user_validate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class userinform_fragment extends Fragment {

    public static userinform_fragment newInstance() {
        return new userinform_fragment();
    }

    private ViewGroup viewGroup;
    private TextView userinform_id;
    private TextInputEditText userinform_nick_et;
    private TextInputEditText userinform_pw_et;
    private TextInputEditText userinform_name_et;
    private Spinner userinform_phoneNumber_spinner;
    private ArrayAdapter<String> phoneNumberAdapter;
    private EditText userinform_phoneNumber1_et;
    private EditText userinform_phoneNumber2_et;
    private Button userinform_modify_btn;
    private AlertDialog dialog;
    private boolean validate_nickname = false;


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

        // userFile에 저장된 user_id 와 access_token 값 가져오기
        get_userFile();

        // ViewGroup 설정
        set_viewGroup();

        // 현재 로그인된 user_id 표시
        userinform_id.setText(user_id);

        userinform_nick_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validate_nick();
            }
        });

        // 회원정보 수정 버튼
        userinform_modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modify();
            }
        });

        return viewGroup;
    }

    // ViewGroup 설정
    private void set_viewGroup(){
        userinform_id = viewGroup.findViewById(R.id.userinform_id_et);
        userinform_nick_et = viewGroup.findViewById(R.id.userinform_nick_et);

        userinform_pw_et = viewGroup.findViewById(R.id.userinform_pw_et);
        userinform_name_et = viewGroup.findViewById(R.id.userinform_name_et);
        userinform_phoneNumber1_et = viewGroup.findViewById(R.id.userinform_phoneNumber1_et);
        userinform_phoneNumber2_et = viewGroup.findViewById(R.id.userinform_phoneNumber2_et);
        userinform_modify_btn = viewGroup.findViewById(R.id.userinform_btn);

        List<String> phoneNumber_list = new ArrayList<>();
        phoneNumber_list.add("010");
        phoneNumber_list.add("011");


        phoneNumberAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, phoneNumber_list);

        userinform_phoneNumber_spinner = (Spinner) viewGroup.findViewById(R.id.userinform_phoneNumber_spinner);
        userinform_phoneNumber_spinner.setAdapter(phoneNumberAdapter);

        String url = getString(R.string.usersEndpoint) + "/" +  user_id;
        Log.d(TAG, "set_viewGroup: " + url);
        StringRequest userinform_get_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "onResponse: " + jsonObject.toString());
                    boolean success = jsonObject.getBoolean("success");
                    JSONObject data = jsonObject.getJSONObject("data");

                    if (success) {
                        Log.d(TAG, "success ");
                        userinform_nick_et.setText(data.getString("nickname"));
                        userinform_name_et.setText(data.getString("name"));
                        String phonenumber = data.getString("phonenum");
                        String phonenum1 = phonenumber.substring(0,3);
                        switch(phonenum1){
                            case "010" : userinform_phoneNumber_spinner.setSelection(0);break;
                            case "011" : userinform_phoneNumber_spinner.setSelection(1);break;
                        }
                        userinform_phoneNumber1_et.setText(phonenumber.substring(3,7));
                        userinform_phoneNumber2_et.setText(phonenumber.substring(7,11));
                    }else{
                        Log.d(TAG, "onResponse: " + jsonObject.toString());
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onResponse: " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+ error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String,String>parameters = new HashMap<>();
                parameters.put("x-access-token", access_token);
                return parameters;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(userinform_get_request);
    }

    // nickname 중복검사
    private void validate_nick() {
        String url = getString(R.string.authEndpoint) + "/checknickname";
        String key = "nickname";
        String user_nickname = userinform_nick_et.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("사용할 수 있는 닉네임 입니다.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        userinform_nick_et.setEnabled(false);
                        validate_nickname = true;
                        userinform_nick_et.setTextColor(getResources().getColor(R.color.colorgray));

                        return;
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("사용할 수 없는 닉네임 입니다.")
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        };

        user_validate validateRequest = new user_validate(url, key, user_nickname, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if(error instanceof ServerError && response != null){
                    try{
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                        JsonParser parser = new JsonParser();
                        JsonElement element = parser.parse(res);
                        JsonObject data = null;
                        String comment = null;

                        // 닉네임은 2자리 이상입니다.
                        if(!element.getAsJsonObject().get("data").isJsonNull()){
                            data = element.getAsJsonObject().get("data").getAsJsonObject();
                            JsonArray errors = data.getAsJsonArray("errors");
                            JsonObject error_object = errors.get(0).getAsJsonObject();
                            String msg = error_object.get("msg").getAsString();
                            userinform_nick_et.setError(msg);
                            Log.d(TAG, "onErrorResponse: " + msg);
                        }// 이미 존재하는 닉네임 입니다.
                        else {
                            comment = element.getAsJsonObject().get("comment").getAsString();
                            Log.d(TAG, "onErrorResponse: "+ comment);
                            userinform_nick_et.setError(comment);
                        }

                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                }

            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(validateRequest);
    }


    private void modify() {
        String userID = userinform_id.getText().toString();
        String userPW = userinform_pw_et.getText().toString();
        String userName = userinform_name_et.getText().toString();
        String userNick = userinform_nick_et.getText().toString();
        String userPhoneNumber1 = userinform_phoneNumber1_et.getText().toString();
        String userPhoneNumber2 = userinform_phoneNumber2_et.getText().toString();
        String userPhoneNumber = userinform_phoneNumber_spinner.getSelectedItem().toString() + userPhoneNumber1 + userPhoneNumber2;

        if (!validate_nickname) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                    .setPositiveButton("확인", null)
                    .create();
            dialog.show();
            return;
        }

        if (userID.equals("") || userPW.equals("") || userName.equals("") || userNick.equals("") || userPhoneNumber1.equals("") || userPhoneNumber2.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            dialog = builder.setMessage("사용자 정보를 입력해주세요.")
                    .setPositiveButton("확인", null)
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
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("회원정보를 수정했습니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.main_layout, user_login_success.newInstance()).commit();
                                    }
                                })
                                .create();
                        dialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        dialog = builder.setMessage("회원정보를 수정할 수 없습니다.")
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        userinform_modify_request registerRequest = new userinform_modify_request(getActivity(), userID, userPW, userName, userNick, userPhoneNumber, responseListener
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                        JsonParser parser = new JsonParser();
                        JsonElement element = parser.parse(res);
                        JsonObject data = null;

                        // 비밀번호는 6자리 이상입니다. && 비밀번호를 입력해주세요
                        if (!element.getAsJsonObject().get("data").isJsonNull()) {
                            data = element.getAsJsonObject().get("data").getAsJsonObject();
                            JsonArray errors = data.getAsJsonArray("errors");
                            JsonObject error_object = errors.get(0).getAsJsonObject();
                            String msg = error_object.get("msg").getAsString();
                            userinform_pw_et.setError(msg);
                            Log.d(TAG, "onErrorResponse: " + msg);
                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(registerRequest);
    }


    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    }
}
