package com.rodafleets.rodadriver;

import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import com.rodafleets.rodadriver.custom.slideview.SlideView;
import com.rodafleets.rodadriver.model.Driver;
import com.rodafleets.rodadriver.model.VehicleRequest;
import com.rodafleets.rodadriver.rest.RodaRestClient;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class VehicleRequestActivity extends MapActivity {

    public static final String TAG = AppConstants.APP_NAME;

    private CardView requestView;

    private TextView customerName;
    private TextView fromAddress;
    private TextView toAddress;
    private TextView distance;
    private TextView loadingUnloadingTxt;
    private TextView makeOfferTxt;
    private TextView callAdmin;

    private Button callCustomerBtn;

    private SlideView makeOfferBtn;

    private VehicleRequest vehicleRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_request);
        initComponents();
    }

    protected void initComponents() {
        super.initComponents();
        requestView = (CardView) findViewById(R.id.requestView);

        customerName = (TextView) findViewById(R.id.customerName);
        fromAddress = (TextView) findViewById(R.id.fromAddress);
        toAddress = (TextView) findViewById(R.id.toAddress);
        distance = (TextView) findViewById(R.id.distance);
        loadingUnloadingTxt = (TextView) findViewById(R.id.loadingUnloadingTxt);
        makeOfferTxt = (TextView) findViewById(R.id.makeOfferTxt);

        callCustomerBtn = (Button) findViewById(R.id.callCustomerBtn);
        callAdmin = (TextView) findViewById(R.id.callAdmin);
        makeOfferBtn = (SlideView) findViewById(R.id.makeOfferBtn);

        initMap();
        setFonts();
        initMakeOfferBtn();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Vehicle_Requested"));

        boolean fromNotification = getIntent().getBooleanExtra("FROM_NOTIFICATION", false);
        if(fromNotification) {
            Log.i(TAG, "opened from notification");
            showVehicleRequest();
        }
    }

    private void setFonts() {
        loadFonts();
        customerName.setTypeface(poppinsMedium);
        fromAddress.setTypeface(poppinsRegular);
        toAddress.setTypeface(poppinsRegular);
        distance.setTypeface(poppinsLight);
        loadingUnloadingTxt.setTypeface(poppinsSemiBold);
//        makeOfferBtn.setTypeface(poppinsSemiBold);
        makeOfferTxt.setTypeface(poppinsRegular);
        callCustomerBtn.setTypeface(poppinsMedium);
        callAdmin.setTypeface(poppinsRegular);
    }

    private void initMakeOfferBtn() {
        makeOfferBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                v.onTouchEvent(event);

                makeOfferBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        makeOfferBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                bidRequest();
            }
        });
    }

    private void bidRequest() {
        int driverId = ApplicationSettings.getDriverId(VehicleRequestActivity.this);
        RodaRestClient.bidRequest(vehicleRequest.getId(), driverId, vehicleRequest.getApproxFareInCents(), bidRequestResponseHandler);
    }

    public void onRejectBtnClick(View view) {
        int driverId = ApplicationSettings.getDriverId(VehicleRequestActivity.this);
        RodaRestClient.rejectRequest(vehicleRequest.getId(), driverId, rejectRequestResponseHandler);
    }

    public void onCallCustomerBtnClick(View view) {

    }

    private void startNextActivity() {
        this.startActivity(new Intent(this, TripProgressActivity.class));
        finish();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showVehicleRequest();
        }
    };

    private void showVehicleRequest() {
        try {

            JSONObject jsonObject = new JSONObject(ApplicationSettings.getVehicleRequest(VehicleRequestActivity.this));

            vehicleRequest = new VehicleRequest(jsonObject);

            RodaDriverApplication.vehicleRequests.add(vehicleRequest);

            customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
            fromAddress.setText(vehicleRequest.getOriginAddress());
            toAddress.setText(vehicleRequest.getDestinationAddress());
            distance.setText(vehicleRequest.getDistance());

            long fare = vehicleRequest.getApproxFareInCents()/100;

            makeOfferBtn.setText("₹" + fare);
            requestView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            //handle error
            Log.e(TAG, "vehicleRequest jsonException = " + e.getMessage());
        }
    }

    private JsonHttpResponseHandler bidRequestResponseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
            startNextActivity();
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//            if(errorCode == ResponseCode.INVALID_CREDENTIALS) {
//                sb = Snackbar.make(constraintLayout, getString(R.string.sign_in_invalid_credentials_error), Snackbar.LENGTH_LONG);
//            } else {
//                sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
//            }
        }
    };

    private JsonHttpResponseHandler rejectRequestResponseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
            ApplicationSettings.setVehicleRequest(VehicleRequestActivity.this, null);
            clearMap();
            requestView.setVisibility(View.GONE);
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//            if(errorCode == ResponseCode.INVALID_CREDENTIALS) {
//                sb = Snackbar.make(constraintLayout, getString(R.string.sign_in_invalid_credentials_error), Snackbar.LENGTH_LONG);
//            } else {
//                sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
//            }
        }
    };
}
