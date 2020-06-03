package org.techtown.smarket_android.Search.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class danawaRequest extends StringRequest {
    private Response.Listener<String> responseListener;
    private Response.ErrorListener errorListener;

    public danawaRequest(String url, String detailItem, Response.Listener<String> responseListener, Response.ErrorListener errorListener) throws UnsupportedEncodingException{
        super(Method.GET, url+ URLEncoder.encode(detailItem, "UTF-8")+"&reviewcount=5", responseListener, errorListener);
        this.responseListener = responseListener;
        this.errorListener = errorListener;
    }

    public Map<String, String> getParams() throws AuthFailureError {
        Map<String,String> parameters;
        parameters = new HashMap<>();
        parameters.put("detailItem", this.getUrl());
        return parameters;
    }

}
