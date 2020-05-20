package org.techtown.smarket_android.searchItemList.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class danawaRequest extends StringRequest {
    final static private String urlRouter = "http://10.0.2.2:3000/api/item/detail?query=";
    private Response.Listener<String> responseListener;
    private Response.ErrorListener errorListener;

    public danawaRequest(String detailItem, Response.Listener<String> responseListener, Response.ErrorListener errorListener) throws UnsupportedEncodingException{
        super(Method.GET, urlRouter+ URLEncoder.encode(detailItem, "UTF-8")+"&reviewcount=5", responseListener, errorListener);
        this.responseListener = responseListener;
        this.errorListener = errorListener;
    }

    public Map<String, String> getParams(){
        Map<String,String> parameters;
        parameters = new HashMap<>();
        parameters.put("detailItem", this.getUrl());
        return parameters;
    }

}
