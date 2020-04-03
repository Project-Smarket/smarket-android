package org.techtown.smarket_android.User;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class user_register_validate_nick extends StringRequest {

    final static private String url = "http://10.0.2.2:3000/api/auth/checknickname";
    private Map<String, String> parameters;



    public user_register_validate_nick(String userID, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        parameters = new HashMap<>();
        parameters.put("user_id", userID);

    }

    public Map<String, String> getParams() {

        return parameters;
    }


}
