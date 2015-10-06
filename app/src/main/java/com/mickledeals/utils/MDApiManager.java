package com.mickledeals.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mickledeals.activities.MDApplication;
import com.mickledeals.datamodel.CouponInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nicky on 7/15/2015.
 */
public class MDApiManager {
    public interface MDResponseListener<T> {
        public void onMDSuccessResponse(T object);
        public void onMDNetworkErrorResponse(String errorMessage);
        public void onMDErrorResponse(String errorMessage);
    }

    private static RequestQueue sQueue;
    private static ImageLoader mImageLoader;
    private static Context sContext = MDApplication.sAppContext;

    public static void initVolley() {
        sQueue = Volley.newRequestQueue(sContext);
    }

    //another solution is implement interface for activity and fragment, pass in this and the interface to this method
    public static void sendStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return createBasicAuthHeader(null, null);
            }
        };
        sendRequest(stringRequest);
    }

    public static void sendJSONRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return createBasicAuthHeader(null, null);
            }
        };
        sendRequest(jsonRequest);
    }



    public static void sendJSONArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return createBasicAuthHeader(null, null);
            }
        };
        sendRequest(jsonRequest);
    }

    public static void sendImageRequest(String url, int maxWidth, int maxHeight, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener) {
        ImageRequest imageRequest = new ImageRequest(url, listener, maxWidth, maxHeight, ImageView.ScaleType.CENTER, null, errorListener);
        sendRequest(imageRequest);
    }

    public static void sendStringRequestWithPOST(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, listener, errorListener);
        sendRequest(stringRequest);
    }

    private static void sendRequest(Request request) {
        sQueue.add(request);
    }





    static Map<String, String> createBasicAuthHeader(String username, String password) {
        Map<String, String> headerMap = new HashMap<String, String>();
        username = "nickyfantasy@gmail.com";
        password = "123321";
        String credentials = username + ":" + password;
        //"YWRtaW46dGVzdB=="
        //"bmlja3lmYW50YXN5OjEyMzMyMQ=="
        String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);
        headerMap.put("Authorization", "Basic " + "YWRtaW46dGVzdA==");

        return headerMap;
    }



    public static void fetchCouponInfoList(String request, final MDResponseListener<List<CouponInfo>> listener) {
        sendJSONArrayRequest(request, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<CouponInfo> list = new ArrayList<CouponInfo>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        DLog.d(MDApiManager.class, "parsing pos = " + i);
                        JSONObject jsonobject = response.getJSONObject(i);
                        CouponInfo info = new CouponInfo(jsonobject);
                        list.add(info);
                    } catch (JSONException e) {
                        DLog.e(MDApiManager.class, e.toString());
                    }
                }

                if (listener != null) {
                    listener.onMDSuccessResponse(list);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) {
                    listener.onMDNetworkErrorResponse(error.getMessage());
                }
            }
        });
    }

}
