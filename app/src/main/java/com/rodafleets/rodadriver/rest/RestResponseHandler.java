package com.rodafleets.rodadriver.rest;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by divya on 18/05/17.
 */

public class RestResponseHandler extends JsonHttpResponseHandler {

    private String failureMessage;

    public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
        // super.onFailure(statusCode,headers, throwable, errorResponse);

        switch (statusCode) {
            case ResponseCode.NOT_FOUND:
                this.setFailureMessage("Api Not Found");
                break;

            case ResponseCode.BAD_REQUEST:
                break;

            case ResponseCode.UNAUTHORIZED:
                break;

            default:
                break;
        }
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public String getFailureMessage() {
        return failureMessage;
    }
}
