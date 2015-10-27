package com.mickledeals.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
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
import com.mickledeals.datamodel.MyCouponInfo;
import com.mickledeals.datamodel.PaymentInfo;

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
    public static ImageLoader sImageLoader;
    private static Context sContext = MDApplication.sAppContext;

    public static void initVolley() {
        sQueue = Volley.newRequestQueue(sContext);
        sImageLoader = new ImageLoader(sQueue, new LruBitmapCache(
                LruBitmapCache.getCacheSize(sContext)));
    }

    public static class LruBitmapCache extends LruCache<String, Bitmap>
            implements ImageLoader.ImageCache {

        public LruBitmapCache(int maxSize) {
            super(maxSize);
        }

        public LruBitmapCache(Context ctx) {
            this(getCacheSize(ctx));
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }

        // Returns a cache size equal to approximately three screens worth of images.
        public static int getCacheSize(Context ctx) {
            final DisplayMetrics displayMetrics = ctx.getResources().
                    getDisplayMetrics();
            final int screenWidth = displayMetrics.widthPixels;
            final int screenHeight = displayMetrics.heightPixels;
            // 4 bytes per pixel
            final int screenBytes = screenWidth * screenHeight * 4;

            return screenBytes * 5;
        }
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

    private static void handleErrorResponse(VolleyError error, final MDResponseListener<JSONObject> listener) {
        if (listener == null) return;

        if (error.getMessage() == null) {
            listener.onMDNetworkErrorResponse("timeout");
            return;
        }
        DLog.e(MDApiManager.class, error.getMessage());
        if (error.getMessage().contains("JSONException")) {
            listener.onMDErrorResponse(error.getMessage());
        } else {
            listener.onMDNetworkErrorResponse(error.getMessage());
        }
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

    public static void fetchMyCoupons(String sortMethod, boolean expired, boolean redeemed, int page, final MDResponseListener<List<MyCouponInfo>> listener) {
        String url = "http://www.mickledeals.com/api/purchases/search";
        JSONObject body = new JSONObject();
        try {
            if (redeemed == false) body.put("expiredPurchase", expired);
            body.put("redeemedPurchase", redeemed);
            body.put("sortBy", sortMethod);
            if (page > 0) {
                body.put("page", page);
                body.put("size", PAGE_SIZE);
            }
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONArrayRequest(url, body, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                DLog.d(MDApiManager.class, response.toString());
                List<MyCouponInfo> list = new ArrayList<MyCouponInfo>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonobject = response.getJSONObject(i);
                        CouponInfo info = new CouponInfo(jsonobject);
                        DataListModel.getInstance().getCouponMap().put(info.mId, info);
                        MyCouponInfo myCouponInfo = new MyCouponInfo();
                        myCouponInfo.mId = info.mId;
                        myCouponInfo.mRedemptionDate = JSONHelper.getLong(jsonobject, "redemptionDate");
                        Log.e("ZZZ", "myCouponInfo.name = " + info.mDescription  + "myCouponInfo.mRedemptionDate = " + myCouponInfo.mRedemptionDate);
                        list.add(myCouponInfo);
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
                if (response.has("ERROR")) {
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
                handleErrorResponse(error, listener);
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
                //check what error maybe....
                listener.onMDSuccessResponse(null);
                //always return error because no response received but try to convert to JSON
//                        listener.onMDNetworkErrorResponse(error.getMessage());
//                DLog.e(MDApiManager.class, error.getMessage());
            }
        });
    }

    //no response
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
                if (response.has("ERROR")) {
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
                if (response.has("ERROR")) {
                    listener.onMDErrorResponse(errorMessage);
                } else {
                    listener.onMDSuccessResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorResponse(error, listener);
            }
        });
    }



    public static void purchaseCoupon(int couponId, int paymentId, String clientMetadataId, final MDResponseListener<JSONObject> listener) {
        String url = "http://www.mickledeals.com/api/userses/purchaseCoupon";
        JSONObject body = new JSONObject();
        try {
            body.put("couponId", couponId);
            if (paymentId != 0) body.put("paymentId", paymentId);
            if (clientMetadataId != null) body.put("clientMetadataId", clientMetadataId);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String errorMessage = JSONHelper.getString(response, "ERROR");
                if (response.has("ERROR")) {
                    listener.onMDErrorResponse(errorMessage);
                } else {
                    listener.onMDSuccessResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorResponse(error, listener);
            }
        });
    }


    public static void redeemCoupon(String purchaseId, final MDResponseListener<JSONObject> listener) {
        String url = "http://www.mickledeals.com/api/userses/redeemCoupon";
        JSONObject body = new JSONObject();
        try {
            body.put("purchaseId", purchaseId);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                DLog.d(MDApiManager.class, response.toString());
//                String errorMessage = JSONHelper.getString(response, "ERROR");
//                if (!TextUtils.isEmpty(errorMessage)) {
//                    listener.onMDErrorResponse(errorMessage);
//                } else {
                    listener.onMDSuccessResponse(response);
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DLog.e(MDApiManager.class, error.getMessage());
////                listener.onMDNetworkErrorResponse(error.getMessage());
//                //temp
//                listener.onMDSuccessResponse(null);
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

    public static void getPayments(final MDResponseListener<Void> listener) {
        String url = "http://www.mickledeals.com/api/userses/getPayments";
        JSONObject body = new JSONObject();
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                double credit = JSONHelper.getDouble(response, "mickleCredit");
                DataListModel.getInstance().setMickleCredit(credit);
                JSONArray paymentArray = JSONHelper.getJSONArray(response, "payment");
                List<PaymentInfo> list = DataListModel.getInstance().getPaymentList();
                list.clear();
                for (int i = 0; i < paymentArray.length(); i++) {
                    try {
                        JSONObject jsonobject = paymentArray.getJSONObject(i);
                        PaymentInfo info = new PaymentInfo(jsonobject);
                        list.add(info);
                    } catch (JSONException e) {
                        DLog.e(MDApiManager.class, e.toString());
                    }
                }
                DataListModel.getInstance().mUpdatedPayment = true;
                if (listener != null) listener.onMDSuccessResponse(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DLog.e(MDApiManager.class, error.getMessage());
                if (listener != null) listener.onMDNetworkErrorResponse(error.getMessage());
            }
        });
    }


    public static void addPayments(String cardNumber, String cardType, String expireMonth, String expireYear, final MDResponseListener<JSONObject> listener) {
        String url = "http://www.mickledeals.com/api/userses/addPayment";
        JSONObject body = new JSONObject();
        try {
            JSONObject cardObject = new JSONObject();
            cardObject.put("number", cardNumber);
            cardObject.put("type", cardType);
            cardObject.put("expireMonth", expireMonth);
            cardObject.put("expireYear", expireYear);
            body.put("creditCard", cardObject);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String errorMessage = JSONHelper.getString(response, "ERROR");
                if (response.has("ERROR")) {
                    listener.onMDErrorResponse(errorMessage);
                } else {
                    List<PaymentInfo> list = DataListModel.getInstance().getPaymentList();
                    PaymentInfo info = new PaymentInfo(response);
                    if (info.mPaymentId == 0) {
                        listener.onMDErrorResponse(null);
                    } else {
                        list.add(0, info);
                        listener.onMDSuccessResponse(response);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorResponse(error, listener);
            }
        });
    }

    public static void addPayPalPayments(String correlationId, String authorizationCode, final MDResponseListener<JSONObject> listener) {
        String url = "http://www.mickledeals.com/api/userses/addPayment";
        JSONObject body = new JSONObject();
        try {
            body.put("payPalAccount", "");
            body.put("correlationId", correlationId);
            body.put("authorizationCode", authorizationCode);
        } catch (JSONException e) {
            DLog.e(MDApiManager.class, e.toString());
        }
        sendJSONRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                DLog.e(MDApiManager.class, "response = " + response.toString());
                String errorMessage = JSONHelper.getString(response, "ERROR");
                if (response.has("ERROR")) {
                    DLog.e(MDApiManager.class, "error Message = " + errorMessage);
                    listener.onMDErrorResponse(errorMessage);
                } else {
                    List<PaymentInfo> list = DataListModel.getInstance().getPaymentList();
                    PaymentInfo info = new PaymentInfo(response);
                    if (info.mPaymentId == 0) {
                        listener.onMDErrorResponse(null);
                    } else {
                        list.add(0, info);
                        listener.onMDSuccessResponse(response);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorResponse(error, listener);
            }
        });
    }

    //no response
    public static void setPrimaryPayment(int paymentId) {
        String url = "http://www.mickledeals.com/api/userses/setPrimaryPayment";
        JSONObject body = new JSONObject();
        try {
            body.put("paymentId", paymentId);
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
