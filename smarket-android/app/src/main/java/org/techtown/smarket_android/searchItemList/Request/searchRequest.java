package org.techtown.smarket_android.searchItemList.Request;

import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import static com.android.volley.VolleyLog.TAG;

public class searchRequest extends StringRequest {
    final static private String urlRouter = "http://10.0.2.2:3000/api/naver/search?query=";
    private Map<String, String> parameters;
    String text = null;

    public searchRequest(String searchItemName, Response.Listener<String> listener, Response.ErrorListener errorListener) throws UnsupportedEncodingException {
        super(Method.GET, urlRouter+URLEncoder.encode(searchItemName, "UTF-8"), listener, errorListener);

        parameters = new HashMap<>();
        parameters.put("searchItemName", this.getUrl());
        Log.d(TAG, "searchRequest: "+this.getUrl());
    }

    public Map<String, String> getParams(){
        return parameters;
    }
}
