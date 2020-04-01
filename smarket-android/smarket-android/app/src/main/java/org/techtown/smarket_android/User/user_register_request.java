package org.techtown.smarket_android.User;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class  user_register_request extends StringRequest {

    final static private String url = "http://192.168.0.4:3000/register";
    private Map<String, String> parameters;

    public user_register_request(String userID, String userPW, String userName, Response .Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("id", userID);
        parameters.put("password", userPW);
        parameters.put("name", userName);
    }

    public Map<String, String> getParams(){

        return parameters;
    }
}
