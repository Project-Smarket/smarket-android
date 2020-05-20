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
    private Response.Listener<String> responseListener;
    private Response.ErrorListener errorListener;

    public searchRequest(String url, int start, int display, String searchItemName, Response.Listener<String> responseListener, Response.ErrorListener errorListener) throws UnsupportedEncodingException {
        super(Method.GET, url+URLEncoder.encode(searchItemName, "UTF-8") + "&start=" + start + "&display=" + display
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
