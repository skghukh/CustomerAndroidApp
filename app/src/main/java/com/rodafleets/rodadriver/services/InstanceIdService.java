package com.rodafleets.rodadriver.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodadriver.rest.RodaRestClient;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class InstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(AppConstants.APP_NAME, "Refreshed token: " + refreshedToken);
        ApplicationSettings.setRegistrationId(this, refreshedToken);

        int driverId = ApplicationSettings.getDriverId(this);
        if(driverId != 0) {
            RodaRestClient.updateDeviceRegistrationId(driverId, refreshedToken, responseHandler);
        }
    }

    private JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            try {
                Log.i(AppConstants.APP_NAME, "updateDeviceRegistrationId response = " + jsonResponseObject.toString());
            } catch (Exception e) {
                //handle error
                Log.e(AppConstants.APP_NAME, "updateDeviceRegistrationId jsonException = " + e.getMessage());
            }
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            try {
                Log.i(AppConstants.APP_NAME, "updateDeviceRegistrationId errorResponse = " + errorResponse.toString());
            } catch (Exception e) {
                Log.e(AppConstants.APP_NAME, "updateDeviceRegistrationId api exception = " + e.getMessage());
            }
        }
    };
}
