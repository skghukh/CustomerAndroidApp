package com.rodafleets.rodacustomer.rest;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rodafleets.rodacustomer.utils.AppConstants;

public class RodaRestClient {

    private static final String API_VERSION = "0.1";

    //    private static final String API_BASE_URL = "https://api.rodafleets.com/" + API_VERSION;
    private static final String API_BASE_URL = "http://192.168.0.12:8080/" + API_VERSION;
    //http://192.168.0.12:8080/
    //http://104.198.208.172:8080/"
    //private static final String API_BASE_URL = "http://10.64.13.254:8080/" + API_VERSION;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void GET(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        if (params != null) {
            Log.i(AppConstants.APP_NAME, "Api params = " + params.toString());
        }
        client.setMaxRetriesAndTimeout(AppConstants.HTTP_CONNECTION_RETRIES, AsyncHttpClient.DEFAULT_RETRY_SLEEP_TIME_MILLIS);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void POST(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setMaxRetriesAndTimeout(AppConstants.HTTP_CONNECTION_RETRIES, AsyncHttpClient.DEFAULT_RETRY_SLEEP_TIME_MILLIS);
        client.setTimeout(20 * 1000);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        String apiUrl = API_BASE_URL + relativeUrl;
        Log.i(AppConstants.APP_NAME, "Api Url = " + apiUrl);
        return apiUrl;
    }

    //eventually change to this
    public static void GET(String url, RequestParams params, RestResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void POST(String url, RequestParams params, RestResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    /* //============example usage ============

    private void fucntionName() {
        RodaRestClient.GET("/relative/path/to/api", params, responseHandler);
    }

    private JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            // If the response is JSONObject instead of expected JSONArray
            Log.i(AppConstants.APP_NAME, "response = " + response.toString());
        }

        public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponseArray) {
            Log.i(AppConstants.APP_NAME, "response2 = " + responseArray.toString());

        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            // super.onFailure(statusCode,headers, throwable, errorResponse);

            switch (statusCode) {

        }
    };*/

    public static void updateDeviceRegistrationId(int driverId, String token, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("registrationid", token);
        params.put("device_os", "android");
        RodaRestClient.POST("/drivers/" + driverId + "/deviceregistration", params, responseHandler);
    }

    public static void signUp(String phoneNumber, String firstName, String lastName, String gender, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("phonenumber", phoneNumber);
        params.put("firstname", firstName);
        params.put("lastname", lastName);
        params.put("gender", gender);
        RodaRestClient.POST("/customers", params, responseHandler);
    }

    public static void saveDriver(int driverId, String password, String otp, String sessionId, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("password", password);
        params.put("otp", otp);
        params.put("otp", otp);
        params.put("session_id", sessionId);
        RodaRestClient.POST("/customers/" + driverId, params, responseHandler);
    }

    public static void getVehicleTypes(JsonHttpResponseHandler responseHandler) {
        RodaRestClient.GET("/vehicle/types", null, responseHandler);
    }

    public static void saveVehicleInfo(int driverId, String vehicleNumber, int vehicleTypeId, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("number", vehicleNumber);
        params.put("vehicletype_id", vehicleTypeId);
        RodaRestClient.POST("/drivers/" + driverId + "/vehicles", params, responseHandler);
    }

    public static void saveVehicleInfo(int driverId, String vehicleNumber, int vehicleTypeId, String ownerFirstName, String ownerLastName, String ownerPhoneNumber, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("number", vehicleNumber);
        params.put("vehicletype_id", vehicleTypeId);
        params.put("owner_firstname", ownerFirstName);
        params.put("owner_lastname", ownerLastName);
        params.put("owner_phonenumber", ownerPhoneNumber);

        RodaRestClient.POST("/drivers/" + driverId + "/vehicles", params, responseHandler);
    }

    public static void uploadDriverDocuments(int driverId, RequestParams params, JsonHttpResponseHandler responseHandler) {
        RodaRestClient.POST("/drivers/" + driverId + "/uploaddocuments", params, responseHandler);
    }

    public static void login(String phoneNumber, String password, String token, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("phonenumber", phoneNumber);
        params.put("password", password);
        params.put("android_registrationid", token);
        RodaRestClient.POST("/customers/login", params, responseHandler);
    }

    public static void rejectRequest(int requestId, int driverId, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("driver_id", driverId);
        RodaRestClient.POST("/requests/" + requestId + "/reject", params, responseHandler);
    }

    public static void bidRequest(int requestId, int driverId, int fareInCents, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("driver_id", driverId);
        params.put("bid_amount_in_cents", fareInCents);
        RodaRestClient.POST("/requests/" + requestId + "/bids", params, responseHandler);
    }

    public static void getNearByDriverLocations(Double lat, Double lan, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("lat", lat);
        params.put("lan", lan);
        RodaRestClient.GET("/customers/nearybys", params, responseHandler);

    }

    public static void requestVehicle(String custId, int vehicleTypeId, Double sourceLat, Double sourceLan, Double destLat, Double destLan,String source, String dest, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("customer_id", custId);
        params.put("vehicletype_id", vehicleTypeId);
        params.put("origin_lat", sourceLat);
        params.put("origin_lng", sourceLan);
        params.put("destination_lat", destLat);
        params.put("destination_lng", destLan);
        params.put("approx_fare_in_cents", 3000);
        params.put("source", source);
        params.put("dest", dest);
        params.put("recName", "shailu");
        params.put("recNum","9986291127");
        RodaRestClient.POST("/requests/add/bids",params,responseHandler);
       // RodaRestClient.POST("/requests", params, responseHandler);
    }

    public static void acceptBid(long requestId, long bidId, int custId, JsonHttpResponseHandler responseHandler) {
        String url = "/requests/" + requestId + "/bids/" + bidId + "/accept";
        RequestParams params = new RequestParams();
        params.put("customer_id", custId);
        RodaRestClient.POST(url, params, responseHandler);
    }

    public static void getDriverLocation(int driverId, JsonHttpResponseHandler responseHandler) {
        String url = "/location/" + driverId;
        RequestParams params = new RequestParams();
        RodaRestClient.GET(url, params, responseHandler);
    }

    public static void getTripsHistory(String custId, JsonHttpResponseHandler responseHandler){
        String url = "/trip/history/custid/"+custId;
        RequestParams params = new RequestParams();
        RodaRestClient.GET(url,params,responseHandler);
    }

    public static void sendNumberVerificationRequest(String phoneNumber, JsonHttpResponseHandler responseHandler){
        String url = "/verification";
        RequestParams param = new RequestParams();
        param.put("phoneNumber", phoneNumber);
        RodaRestClient.GET(url,param,responseHandler);
    }

    public static void verifyOTP(String sessionId, String otp, JsonHttpResponseHandler responseHandler){
        String url = "/verification";
        RequestParams requestParams = new RequestParams();
        requestParams.put("sessionId",sessionId);
        requestParams.put("otp",otp);
        RodaRestClient.GET(url,requestParams,responseHandler);
    }
}
