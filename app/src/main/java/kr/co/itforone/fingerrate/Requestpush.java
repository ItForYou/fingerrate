package kr.co.itforone.fingerrate;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

class Requestpush extends StringRequest {
    private static final String URL = "http://14.48.175.176/~fingerrate/bbs/fcm_location.php";
    private Map<String, String> parameters = new HashMap();

    public Requestpush(String token, String lat, String lng, String mb_id,Response.Listener<String> paramListener)
    {
        super(Request.Method.POST, URL, paramListener, null);
        this.parameters.put("token", token);
        this.parameters.put("lat", lat);
        this.parameters.put("lng", lng);
        this.parameters.put("mb_id", mb_id);
    }
    @Override
    public Map<String, String> getParams()
    {
        return this.parameters;
    }

}
