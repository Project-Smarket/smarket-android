package org.techtown.smarket_android.User;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class  user_register_request extends StringRequest {

    final static private String url = "http://10.0.2.2:3000/api/users";
    private Map<String, String> parameters;

    public user_register_request(String userID, String userPW, String userName, String userNick, String userPhoneNumber, Response .Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("user_id", userID);
        parameters.put("nickname", userNick);
        parameters.put("password", userPW);
        parameters.put("name", userName);
        parameters.put("phonenum", userPhoneNumber);

    }

    public Map<String, String> getParams(){

        return parameters;
    }
}
