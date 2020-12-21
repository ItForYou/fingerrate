package kr.co.itforone.fingerrate.volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestOfferwall extends StringRequest {
    private static final String URL = "";
    private Map<String, String> parameters = new HashMap();

    public RequestOfferwall(String google_ad_id, Response.Listener<String> paramListener)
    {
        super(Request.Method.POST, URL, paramListener, null);
        this.parameters.put("google_ad_id", google_ad_id);
    }
    @Override
    public Map<String, String> getParams()
    {
        return this.parameters;
    }


}
