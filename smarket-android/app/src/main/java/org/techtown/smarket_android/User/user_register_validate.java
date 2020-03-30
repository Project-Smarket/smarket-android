package org.techtown.smarket_android.User;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class user_register_validate extends StringRequest {

    final static private String url = "http://10.0.2.2:3000/register";
    private Map<String, String> parameters;

    public user_register_validate(String userID, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        parameters = new HashMap<>();
        parameters.put("id", userID);
    }

    public Map<String, String> getParams() {

        return parameters;
    }
}