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

import static com.android.volley.VolleyLog.TAG;


public class searchRequest extends StringRequest {
    final static private String urlRouter = "http://10.0.2.2:3000/api/naver/search?query=";
    private Response.Listener<String> responseListener;
    private Response.ErrorListener errorListener;

    public searchRequest(int start, int display, String searchItemName, Response.Listener<String> responseListener, Response.ErrorListener errorListener) throws UnsupportedEncodingException {
        super(Method.GET, urlRouter+URLEncoder.encode(searchItemName, "UTF-8") + "&start=" + start + "&display=" + display
                , responseListener, errorListener);
        this.responseListener = responseListener;
        this.errorListener = errorListener;
    }

    public Map<String, String> getParams(){
        Map<String,String> parameters;
        parameters = new HashMap<>();
        parameters.put("searchItemName", this.getUrl());
        return parameters;
    }
}
