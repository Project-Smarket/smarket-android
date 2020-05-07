package org.techtown.smarket_android.User.UserLogin;

import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class user_validate extends StringRequest {

    private String key;
    private String value;
    private Map<String, String> parameters;

    public user_validate(String url, String key, String value, Response.Listener<String> listener, ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        this.key = key;
        this.value = value;
    }

    public Map<String, String> getParams() {
        parameters = new HashMap<>();
        parameters.put(key, value);
        return parameters;
    }


}