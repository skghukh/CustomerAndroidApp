package com.rodafleets.rodacustomer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_response);
        initComponents();
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addResponseToList(intent);
        }
    };

    private void addResponseToList(Intent intent) {
        final String driverName = intent.getStringExtra("driverName");
        final String amount = intent.getStringExtra("Amount");
        final String requestId = intent.getStringExtra("requestId");
        final String bid = intent.getStringExtra("bid");
        VehicleRequestResponse response = new VehicleRequestResponse();
        response.setName(driverName);
        response.setFareEstimate(amount);
        response.setRequestId(requestId);
        response.setBidId(bid);
        response.setDistance("6 KM");
        response.setRating("3.2");
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
            final VehicleRequestResponse vehicleRequestResponse = vehicleResponses.get((int) selectedButton.getTag());
            RodaRestClient.acceptBid(Long.parseLong(vehicleRequestResponse.getRequestId()), Long.parseLong(vehicleRequestResponse.getBidId()), ApplicationSettings.getCustomerId(this), acceptBidResponseHandler);
        } else {
            Toast toast = Toast.makeText(this, "Please select 1 bid", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private JsonHttpResponseHandler acceptBidResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Toast toast = Toast.makeText(DriversResponseActivity.this, "response sent successfully", Toast.LENGTH_SHORT);
            // startNextActivity();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Toast toast = Toast.makeText(DriversResponseActivity.this, "Oops!", Toast.LENGTH_SHORT);
        }
    };
}

