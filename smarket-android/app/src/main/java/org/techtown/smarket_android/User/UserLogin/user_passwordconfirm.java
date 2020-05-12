package org.techtown.smarket_android.User.UserLogin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.techtown.smarket_android.R;

import java.util.HashMap;
import java.util.Map;

public class user_passwordconfirm extends StringRequest {

    private Map<String, String> parameters;
    private Activity mActivity;
    private String userPW;


    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String userID;
    private String access_token;
    private String refresh_token;

    public user_passwordconfirm(String userPW, Activity activity, Response.Listener<String> listener) {
        super(Method.POST, activity.getResources().getString(R.string.usersEndpoint) + "/passwordconfirm", listener, null);
        this.userPW = userPW;
        mActivity = activity;
        get_userFile();
    }

    public Map<String, String> getHeaders(){
        parameters = new HashMap<>();
        parameters.put("x-access-token", access_token);
        return parameters;
    }

    public Map<String, String> getParams(){
        parameters = new HashMap<>();
        parameters.put("password", userPW);
        return parameters;
    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = mActivity.getSharedPreferences("userFile", Context.MODE_PRIVATE);
        userID = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
        Log.d("TOKEN", "get_userFile: " + access_token);
    }
}
