package com.rodafleets.rodacustomer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.rodafleets.rodacustomer.custom.slideview.SlideView;
import com.rodafleets.rodacustomer.model.VehicleRequest;
import com.rodafleets.rodacustomer.utils.AppConstants;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import org.json.JSONObject;

public class TripProgressActivity extends MapActivity {

    public static final String TAG = AppConstants.APP_NAME;

    // Customer View
    private CardView customerView;
    private TextView customerName;
    private TextView acceptanceStatus;

    // Address View
    private CardView addressView;
    private TextView navigate;
    private TextView fromAddress;

    //Start Loading View
    private CardView startLoadingView;
    private TextView arrivedAtOriginTxt;
    private SlideView startLoadingBtn;
    private TextView startLoadingTxt;

    // Start Trip View
    private CardView startTripView;
    private SlideView startTripBtn;
    private TextView startTripTxt;

    //Start Unloading View
    private CardView startUnloadingView;
    private TextView arrivedAtDestinationTxt;
    private SlideView startUnloadingBtn;
    private TextView startUnloadingTxt;

    // End Trip View
    private CardView endTripView;
    private SlideView endTripBtn;
    private TextView endTripTxt;

    // Fare Summary View
    private CardView fareSummaryView;
    private TextView fareSummaryTxt;
    private TextView paidByTxt;
    private TextView fareTxt;
    private TextView rateCustomerTxt;
    private SlideView goOnlineBtn;

    private VehicleRequest vehicleRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_progress);

        initComponents();
    }

    protected void initComponents() {

        super.initComponents();

        initMap();

        customerView = findViewById(R.id.customerView);
        addressView = findViewById(R.id.addressView);
        startLoadingView = findViewById(R.id.startLoadingView);
        startTripView = findViewById(R.id.startTripView);
        startUnloadingView = findViewById(R.id.startUnloadingView);
        endTripView = findViewById(R.id.endTripView);
        fareSummaryView = findViewById(R.id.fareSummaryView);

        customerName = findViewById(R.id.customerName);
        acceptanceStatus = findViewById(R.id.acceptanceStatus);

        navigate = findViewById(R.id.navigate);
        fromAddress = findViewById(R.id.fromAddress);

        arrivedAtOriginTxt = findViewById(R.id.arrivedAtOriginTxt);
        startLoadingTxt = findViewById(R.id.startLoadingTxt);

        startTripTxt = findViewById(R.id.startTripTxt);

        arrivedAtDestinationTxt = findViewById(R.id.arrivedAtDestinationTxt);
        startUnloadingTxt = findViewById(R.id.startUnloadingTxt);

        endTripTxt = findViewById(R.id.endTripTxt);

        fareSummaryTxt = findViewById(R.id.fareSummaryTxt);
        paidByTxt = findViewById(R.id.paidByTxt);
        fareTxt = findViewById(R.id.fareTxt);
        rateCustomerTxt = findViewById(R.id.rateCustomerTxt);

        startLoadingBtn = findViewById(R.id.startLoadingBtn);
        startTripBtn = findViewById(R.id.startTripBtn);
        startUnloadingBtn = findViewById(R.id.startUnloadingBtn);
        endTripBtn = findViewById(R.id.endTripBtn);
        goOnlineBtn = findViewById(R.id.goOnlineBtn);

        setFonts();
        initSwipeButtonEvents();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Bid_Accepted"));

        try {
            JSONObject jsonObject = new JSONObject(ApplicationSettings.getVehicleRequest(TripProgressActivity.this));
            VehicleRequest vehicleRequest = new VehicleRequest(jsonObject);
            Log.e(TAG, vehicleRequest.toString());
            //₹
            customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
            fromAddress.setText(vehicleRequest.getOriginAddress());

        } catch (Exception e) {
            //handle error
            Log.e(TAG, "vehicleRequest jsonException = " + e.getMessage());
        }
    }

    private void setFonts() {
        loadFonts();

        customerName.setTypeface(poppinsMedium);
        acceptanceStatus.setTypeface(poppinsSemiBold);
        navigate.setTypeface(poppinsRegular);
        fromAddress.setTypeface(poppinsRegular);

        arrivedAtOriginTxt.setTypeface(poppinsSemiBold);
        startLoadingTxt.setTypeface(poppinsRegular);

        startTripTxt.setTypeface(poppinsRegular);

        arrivedAtDestinationTxt.setTypeface(poppinsSemiBold);
        startUnloadingTxt.setTypeface(poppinsRegular);

        endTripTxt.setTypeface(poppinsRegular);

        fareSummaryTxt.setTypeface(poppinsRegular);
        paidByTxt.setTypeface(poppinsSemiBold);
        fareTxt.setTypeface(poppinsLight);
        rateCustomerTxt.setTypeface(poppinsSemiBold);

//        startLoadingBtn.setTypeface(poppinsSemiBold);
//        startTripBtn.setTypeface(poppinsSemiBold);
//        startUnloadingBtn.setTypeface(poppinsSemiBold);
//        endTripBtn.setTypeface(poppinsSemiBold);
//        goOnlineBtn.setTypeface(poppinsSemiBold);
    }

    private void initSwipeButtonEvents() {

        initStartLoadingBtn();
        initStartTripBtn();
        initStartUnloadingBtn();
        initEndTripBtn();
        initGoOnlineBtn();

    }

    private void initStartLoadingBtn() {

        startLoadingBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
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

                startLoadingBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        startLoadingBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                hideAllViews();
                startTripView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initStartTripBtn() {

        startTripBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
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

                startTripBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        startTripBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                hideAllViews();
                startUnloadingView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initStartUnloadingBtn() {

        startUnloadingBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
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

                startUnloadingBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        startUnloadingBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                hideAllViews();
                endTripView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initEndTripBtn() {

        endTripBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
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

                endTripBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        endTripBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                hideAllViews();
                rateCustomerTxt.setText("Rate " + vehicleRequest.getCustomerName());
                paidByTxt.setText("Payment made by e-wallet");
                long fare = vehicleRequest.getApproxFareInCents();
                fareTxt.setText("₹" + fare);
                fareSummaryView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initGoOnlineBtn() {
        goOnlineBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
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

                goOnlineBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        goOnlineBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                startNextActivity();
            }
        });
    }

    private void hideAllViews() {
        customerView.setVisibility(View.GONE);
        addressView.setVisibility(View.GONE);
        startLoadingView.setVisibility(View.GONE);
        startTripView.setVisibility(View.GONE);
        startUnloadingView.setVisibility(View.GONE);
        endTripView.setVisibility(View.GONE);
    }

    private void startNextActivity() {
        ApplicationSettings.setVehicleRequest(this, null);
        this.startActivity(new Intent(this, VehicleRequestActivity.class));
        finish();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.i(TAG, "-------AAAAAA------");

                JSONObject jsonObject = new JSONObject(ApplicationSettings.getVehicleRequest(TripProgressActivity.this));

                vehicleRequest = new VehicleRequest(jsonObject);

                customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
                fromAddress.setText(vehicleRequest.getOriginAddress());
                acceptanceStatus.setText("ACCEPTED");
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    public void run() {
                        customerView.setVisibility(View.GONE);
                        addressView.setVisibility(View.VISIBLE);
                        startLoadingView.setVisibility(View.VISIBLE);
                    }
                }, 2000);


            } catch (Exception e) {
                //handle error
                Log.e(TAG, "vehicleRequest jsonException = " + e.getMessage());
            }
        }
    };
}
