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

public class user_register_request extends StringRequest {

    private Map<String, String> parameters;
    private String userID;
    private String userNick;
    private String userPW;
    private String userName;
    private String userPhoneNumber;

    public user_register_request(Context context, String userID, String userPW, String userName, String userNick, String userPhoneNumber, Response .Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, context.getResources().getString(R.string.usersEndpoint), listener, errorListener);

        this.userID = userID;
        this.userNick = userNick;
        this.userPW = userPW;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;

    }

    public Map<String, String> getParams(){
        parameters = new HashMap<>();
        parameters.put("user_id", userID);
        parameters.put("nickname", userNick);
        parameters.put("password", userPW);
        parameters.put("name", userName);
        parameters.put("phonenum", userPhoneNumber);
        return parameters;
    }
}
