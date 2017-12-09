package com.rodafleets.rodacustomer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodacustomer.rest.RodaRestClient;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DriversResponseActivity extends MapActivity {

    private ListView driverResponseView;
    private List<VehicleRequestResponse> vehicleResponses = new ArrayList<>();
    private DriversResponseAdapter driversResponseAdapter;
    private RadioButton selectedButton;
    private Button reqeustPickupButton;
    private VehicleRequestResponse selectedResponse;
    private ProgressBar progressBar;
    private DriverResponseCountDownTimer mCounter;
    private static final int waitingTime = 45000;
    private RelativeLayout driverResponseLayout;
    private RelativeLayout oopsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_response);
        initComponents();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addResponseToList(intent);
        }
    };

    private void addResponseToList(Intent intent) {
        VehicleRequestResponse response = new VehicleRequestResponse();
        //driver details
        response.setDriverId(intent.getStringExtra("driverId"));
        response.setName(intent.getStringExtra("driverName"));
        response.setDriverContact(intent.getStringExtra("driverContact"));
        response.setDriverRating(intent.getStringExtra("driverRating"));
        response.setDistance(intent.getStringExtra("driverDistance"));

        //request details
        response.setRequestId(intent.getStringExtra("requestId"));
        response.setBidId(intent.getStringExtra("bid"));
        response.setFareEstimate(intent.getStringExtra("Amount"));

        //vehicle details
        response.setVehicleRegId(intent.getStringExtra("vehicleRegId"));
        vehicleResponses.add(response);
        driversResponseAdapter.notifyDataSetChanged();

    }

    protected void initComponents() {
        super.initComponents();
        initMap();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Request_Accepted"));
        driverResponseView = (ListView) findViewById(R.id.driverResponseList);
        driversResponseAdapter = new DriversResponseAdapter(this, vehicleResponses);
        driverResponseView.setAdapter(driversResponseAdapter);
        reqeustPickupButton = (Button) findViewById(R.id.requestPickUp);
        progressBar = (ProgressBar) findViewById(R.id.driver_response_wait_progressbar);
        mCounter = new DriverResponseCountDownTimer(waitingTime, 1000);
        mCounter.start();
    }

    public void selectDriverResponse(View view) {
        if (null != selectedButton) {
            selectedButton.setChecked(false);
        }
        selectedButton = (RadioButton) view;
        reqeustPickupButton.setBackgroundColor(getResources().getColor(R.color.pickup_button_enabled_background));
    }

    public void acceptBid(View view) {
        if (null != selectedButton) {
            selectedResponse = vehicleResponses.get((int) selectedButton.getTag());
            RodaRestClient.acceptBid(Long.parseLong(selectedResponse.getRequestId()), Long.parseLong(selectedResponse.getBidId()), ApplicationSettings.getCustomerId(this), acceptBidResponseHandler);
        } else {
            Toast toast = Toast.makeText(this, "Please select 1 bid", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void startNextActivity() {
        mCounter.cancel();
        Intent intent = new Intent(this, ScehuledTripDetailsActivity.class);
        final LatLng sourceLoc = ApplicationSettings.getSourceLoc();
        //Driver details
        intent.putExtra("driverId", selectedResponse.getDriverId());
        intent.putExtra("driverName", selectedResponse.getName());
        intent.putExtra("driverMob", selectedResponse.getDriverContact());

        //Vehicle Details
        intent.putExtra("vehicleRegNo", selectedResponse.getVehicleRegId());

        //trip details
        intent.putExtra("tripRequestId", selectedResponse.getRequestId());
        intent.putExtra("sourcePlace", ApplicationSettings.getSourcePlace());
        intent.putExtra("destPlace", ApplicationSettings.getDestPlace());
        Bundle b = new Bundle();
        b.putParcelable("sourceLocation", sourceLoc);
        intent.putExtras(b);
        startActivityForResult(intent, 15);
        finish();
    }

    private JsonHttpResponseHandler acceptBidResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Toast toast = Toast.makeText(DriversResponseActivity.this, "response sent successfully", Toast.LENGTH_SHORT);
            startNextActivity();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Toast toast = Toast.makeText(DriversResponseActivity.this, "Oops!", Toast.LENGTH_SHORT);
        }
    };

    public void tryAgain(View view) {
        finish();
    }

    public class DriverResponseCountDownTimer extends CountDownTimer {

        public DriverResponseCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished / 1000);

            progressBar.setProgress(progressBar.getMax() - progress + 1);
        }

        @Override
        public void onFinish() {
            if (vehicleResponses.isEmpty()) {
                //If no response need to start oops here.
                // finish();
                Toast.makeText(DriversResponseActivity.this, "Oops! Try Again", Toast.LENGTH_LONG);
                driverResponseLayout = (RelativeLayout) findViewById(R.id.responseLayout);
                oopsLayout = (RelativeLayout) findViewById(R.id.oops_layout);

                if (null != driverResponseLayout && null != oopsLayout) {
                    driverResponseLayout.setVisibility(View.INVISIBLE);
                    oopsLayout.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(DriversResponseActivity.this, "Select 1 from available responses", Toast.LENGTH_SHORT);
            }
        }
    }

}

