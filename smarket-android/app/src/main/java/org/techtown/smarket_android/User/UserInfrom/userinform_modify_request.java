package org.techtown.smarket_android.User.UserInfrom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class userinform_modify_request extends StringRequest {

    final static private String url = "http://10.0.2.2:3000/api/users";
    private String userID;
    private String userNick;
    private String userPW;
    private String userName;
    private String userPhoneNumber;
    private Activity mActivity;

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String access_token;


    public userinform_modify_request(Activity activity, String userID, String userPW, String userName, String userNick, String userPhoneNumber,
                                     Response .Listener<String> listener) {
        super(Method.PUT, url + "/" + userID, listener, null);

        mActivity = activity;
        this.userID = userID;
        this.userNick = userNick;
        this.userPW = userPW;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        get_userFile();
    }

    public Map<String, String> getParams(){
        Map<String,String> parameters = new HashMap<>();
        parameters.put("user_id", userID);
        parameters.put("nickname", userNick);
        parameters.put("password", userPW);
        parameters.put("name", userName);
        parameters.put("phonenum", userPhoneNumber);
        return parameters;
    }

    public Map<String, String> getHeaders(){
        Map<String,String> parameters = new HashMap<>();
        parameters.put("x-access-token", access_token);
        return parameters;
    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = mActivity.getSharedPreferences("userFile", Context.MODE_PRIVATE);
        access_token = userFile.getString("access_token", null);
    }
}
