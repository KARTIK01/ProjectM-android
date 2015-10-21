package com.mickledeals.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.text.TextUtils;
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
import com.mickledeals.datamodel.DataListModel;

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
        void onMDSuccessResponse(T object);

        void onMDNetworkErrorResponse(String errorMessage);

        void onMDErrorResponse(String errorMessage);
    }

    public static final int PAGE_SIZE = 14;
    private static RequestQueue sQueue;
    private static ImageLoader mImageLoader;
    private static Context sContext = MDApplication.sAppContext;

    public static void initVolley() {
        sQueue = Volley.newRequestQueue(sContext);
    }

    public static void sendStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return createBasicAuthHeader(null, null);
            }
        };
        sendRequest(stringRequest);
    }

    public static void sendJSONRequest(String url, JSONObject body, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        if (body != null) {
            try {
                if (MDLoginManager.mUserId != 0) body.put("userId", MDLoginManager.mUserId);
            } catch (JSONException e) {
                DLog.e(MDApiManager.class, e.toString());
            }
        }
        DLog.d(MDApiManager.class, "url = " + url + "\n" + "body = " + body);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(body == null ? Request.Method.GET : Request.Method.POST, url, body, listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return createBasicAuthHeader(null, null);
            }
        };
        sendRequest(jsonRequest);
    }


    public static void sendJSONArrayRequest(String url, JSONObject body, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        if (body != null) {
            try {
                if (MDLoginManager.mUserId != 0) body.put("userId", MDLoginManager.mUserId);
            } catch (JSONException e) {
                DLog.e(MDApiManager.class, e.toString());
            }
        }
        DLog.d(MDApiManager.class, "url = " + url + "\n" + "body = " + body);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(body == null ? Request.Method.GET : Request.Method.POST, url, body, listener, errorListener) {

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

    private static void sendRequest(Request request) {
        sQueue.add(request);
    }


    private static Map<String, String> createBasicAuthHeader(String username, String password) {
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

    // ---- Search, Browse, Feature, Top Feature, Favorite API ----- //


    public static void fetchSearchCouponList(int categoryId, String city, Location location, String searchText, int page, final MDResponseListener<List<Integer>> listener) {
        String url = "http://www.mickledeals.com/api/coupons/search";
        JSONObject body = new JSONObject();
        try {
            body.put("active", true);
            if (city != null) {
                body.put("city", city);
            }
            if (categoryId != 0) {
                body.put("primaryCategoryId", categoryId);
            }
            if (location != null) {
                body.put("latitude", location.getLatitude());
                body.put("longitude", location.getLongitude());
            }
            if (searchText != null) {
                body.put("searchText", searchText);
            }
            body.put("page", page);
            body.put("size", PAGE_SIZE);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        fetchCouponInfoList(url, body, listener);
    }

    public static void fetchNewAddedCouponList(int pageSize, final MDResponseListener<List<Integer>> listener) {
        String url = "http://www.mickledeals.com/api/coupons/search";
        JSONObject body = new JSONObject();
        try {
            body.put("active", true);
            body.put("page", 1);
            body.put("size", pageSize);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        fetchCouponInfoList(url, body, listener);
    }

    public static void fetchFeatureList(final MDResponseListener<List<Integer>> listener) {
        String url = "http://www.mickledeals.com/api/featuredCoupons/getFeaturedCoupons";
        JSONObject body = new JSONObject();
        try {
            body.put("active", true);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        fetchCouponInfoList(url, body, listener);
    }

    public static void fetchTopFeatureList(final MDResponseListener<List<Integer>> listener) {
        String url = "http://www.mickledeals.com/api/topCoupons/getTopCoupons";
        JSONObject body = new JSONObject();
        try {
            body.put("active", true);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        fetchCouponInfoList(url, body, listener);
    }

    public static void fetchSavedCoupons(final MDResponseListener<List<Integer>> listener) {
        String url = "http://www.mickledeals.com/api/userses/getSavedCoupons";
        JSONObject body = new JSONObject();
        fetchCouponInfoList(url, body, listener);
    }

    private static void fetchCouponInfoList(String request, JSONObject body, final MDResponseListener<List<Integer>> listener) {

        sendJSONArrayRequest(request, body, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                DLog.d(MDApiManager.class, response.toString());
                List<Integer> list = new ArrayList<Integer>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonobject = response.getJSONObject(i);
                        CouponInfo info = new CouponInfo(jsonobject);
                        DataListModel.getInstance().getCouponMap().put(info.mId, info);
                        list.add(info.mId);
                    } catch (JSONException e) {
                        DLog.e(MDApiManager.class, e.toString());
                    }
                }

                listener.onMDSuccessResponse(list);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onMDNetworkErrorResponse(error.getMessage());
            }
        });
    }

    //----------Login / Register API ------//

    public static void registerUserWithEmail(String email, String password, final MDResponseListener<JSONObject> listener) {
        String url = "http://www.mickledeals.com/api/userses/register";
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("password", password);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendLoginRegisterBaseRequest(url, body, listener);
    }

    public static void loginUserWithEmail(String email, String password, final MDResponseListener<JSONObject> listener) {
        String url = "http://www.mickledeals.com/api/userses/login";
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("password", password);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendLoginRegisterBaseRequest(url, body, listener);
    }

    public static void loginUserWithFb(String fbId, String email, String firstName, String lastName,
                                       String gender, String birthday, final MDResponseListener<JSONObject> listener) {
        String url = "http://www.mickledeals.com/api/userses/login";
        JSONObject body = new JSONObject();
        try {
            body.put("facebookId", fbId);
            if (email != null) body.put("email", email);
            if (firstName != null) body.put("firstName", firstName);
            if (lastName != null) body.put("lastName", lastName);
            if (gender != null) body.put("gender", gender);
            if (birthday != null) body.put("birthday", birthday);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendLoginRegisterBaseRequest(url, body, listener);
    }

    private static void sendLoginRegisterBaseRequest(String url, JSONObject body, final MDResponseListener<JSONObject> listener) {
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String errorMessage = JSONHelper.getString(response, "ERROR");
                if (!TextUtils.isEmpty(errorMessage)) {
                    listener.onMDErrorResponse(errorMessage);
                } else {
                    int id = JSONHelper.getInteger(response, "id");
                    if (id == 0) listener.onMDErrorResponse(null);
                    listener.onMDSuccessResponse(response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onMDNetworkErrorResponse(error.getMessage());
            }
        });
    }
    //----------End Login / Register API ------//


    public static void checkVersion(String versionStr, final MDResponseListener<Boolean> listener) {
        String url = "http://www.mickledeals.com/api/userses/versionCheck";
        JSONObject body = new JSONObject();
        try {
            body.put("version", versionStr);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onMDSuccessResponse(JSONHelper.getBoolean(response, "updateRequired"));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    //no response
    public static void addOrRemoveFavorite(int couponId, boolean add) {
        String addUrl = "http://www.mickledeals.com/api/userses/saveCoupon";
        String removeUrl = "http://www.mickledeals.com/api/userses/removeSaveCoupon";
        JSONObject body = new JSONObject();
        try {
            body.put("couponId", couponId);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(add ? addUrl : removeUrl, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                DLog.e(MDApiManager.class, error.getMessage());
            }
        });
    }

    public static void forgotPassword(String email, final MDResponseListener<Boolean> listener) {
        String url = "http://www.mickledeals.com/api/userses/forgotPassword";
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onMDSuccessResponse(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onMDSuccessResponse(null);
                //always return error because no response received but try to convert to JSON
//                        listener.onMDNetworkErrorResponse(error.getMessage());
//                DLog.e(MDApiManager.class, error.getMessage());
            }
        });
    }

    public static void changePassword(String email, String oldPassword, String newPassword, final MDResponseListener<Boolean> listener) {
        String url = "http://www.mickledeals.com/api/userses/resetPassword";
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("oldPassword", oldPassword);
            body.put("newPassword", newPassword);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String errorMessage = JSONHelper.getString(response, "ERROR");
                if (!TextUtils.isEmpty(errorMessage)) {
                    listener.onMDErrorResponse(errorMessage);
                } else {
                    listener.onMDSuccessResponse(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onMDSuccessResponse(null);
                //always return error because no response received but try to convert to JSON
//                        listener.onMDNetworkErrorResponse(error.getMessage());
//                DLog.e(MDApiManager.class, error.getMessage());
            }
        });
    }

    public static void reviewOrder(int couponId, final MDResponseListener<JSONObject> listener) {
        String url = "http://www.mickledeals.com/api/userses/reviewOrder";
        JSONObject body = new JSONObject();
        try {
            body.put("couponId", couponId);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String errorMessage = JSONHelper.getString(response, "ERROR");
                if (!TextUtils.isEmpty(errorMessage)) {
                    listener.onMDErrorResponse(errorMessage);
                } else {
                    listener.onMDSuccessResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DLog.e(MDApiManager.class, error.getMessage());
                listener.onMDNetworkErrorResponse(error.getMessage());
            }
        });
    }



    public static void purchaseCoupon(int couponId, int paymentId, final MDResponseListener<JSONObject> listener) {
        String url = "http://www.mickledeals.com/api/userses/purchaseCoupon";
        JSONObject body = new JSONObject();
        try {
            body.put("couponId", couponId);
            if (paymentId != 0) body.put("paymentId", couponId);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String errorMessage = JSONHelper.getString(response, "ERROR");
                if (!TextUtils.isEmpty(errorMessage)) {
                    listener.onMDErrorResponse(errorMessage);
                } else {
                    listener.onMDSuccessResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DLog.e(MDApiManager.class, error.getMessage());
//                listener.onMDNetworkErrorResponse(error.getMessage());
                //temp
                listener.onMDSuccessResponse(null);
            }
        });
    }

    //no response
    public static void redeemPromotion(String code) {
        String url = "http://www.mickledeals.com/api/userses/redeemPromotion";
        JSONObject body = new JSONObject();
        try {
            body.put("code", code);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                DLog.e(MDApiManager.class, error.getMessage());
            }
        });
    }


}
