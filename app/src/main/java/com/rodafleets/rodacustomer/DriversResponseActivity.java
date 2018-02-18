package com.rodafleets.rodacustomer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodacustomer.model.FBVehicleRequestResponse;
import com.rodafleets.rodacustomer.model.VehicleRequest;
import com.rodafleets.rodacustomer.services.FirebaseReferenceService;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DriversResponseActivity extends MapActivity {

    private ListView driverResponseView;
    private List<FBVehicleRequestResponse> vehicleResponses = new ArrayList<>();
    private DriversResponseAdapter driversResponseAdapter;
    private RadioButton selectedButton;
    private Button reqeustPickupButton;
    private FBVehicleRequestResponse selectedResponse;
    private ProgressBar progressBar;
    private DriverResponseCountDownTimer mCounter;
    private static final int waitingTime = 60000;
    private RelativeLayout driverResponseLayout;
    private RelativeLayout oopsLayout;
    private DatabaseReference tripResponseReference;
    private ChildEventListener tripResponseListener;
    private DatabaseReference tripReferece;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_response);
        initComponents();
        startTripResponseListener(ApplicationSettings.getCustomerEid(this), currentTripId);
        tripReferece = FirebaseReferenceService.getTripReference(ApplicationSettings.getCustomerEid(this), currentTripId);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addResponseToList(intent);
        }
    };

    private void startTripResponseListener(String custId, final String currentTripId) {
        tripResponseReference = FirebaseReferenceService.getTripResponseReference(custId, MapActivity.currentTripId);
        tripResponseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final FBVehicleRequestResponse response = dataSnapshot.getValue(FBVehicleRequestResponse.class);
                response.setDriverId(dataSnapshot.getKey());
                response.setTripId(currentTripId);
                addResponseChildToList(response);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tripResponseReference.addChildEventListener(tripResponseListener);
    }

    private void addResponseChildToList(FBVehicleRequestResponse response) {
        FBVehicleRequestResponse vehicleRequestResponse = new FBVehicleRequestResponse();
        /*response.setDriverId(driverId);
        response.setName("driverName");
        response.setDriverContact("driverContact");
        response.setDriverRating("driverRating");
        response.setDistance("5.2");

        //request details
        response.setRequestId("sdfsfds");
        response.setBidId("jaiShri");
        response.setFareEstimate("4000");

        //vehicle details
        response.setVehicleRegId("HR 51 BE3767");*/
        vehicleResponses.add(response);
        driversResponseAdapter.notifyDataSetChanged();

    }

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
        // vehicleResponses.add(response);
        driversResponseAdapter.notifyDataSetChanged();

    }


    protected void initComponents() {
        super.initComponents();
        initMap();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Request_Accepted"));
        driverResponseView = findViewById(R.id.driverResponseList);
        driversResponseAdapter = new DriversResponseAdapter(this, vehicleResponses);
        driverResponseView.setAdapter(driversResponseAdapter);
        reqeustPickupButton = findViewById(R.id.requestPickUp);
        progressBar = findViewById(R.id.driver_response_wait_progressbar);
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
            changeTripStatusToScheduled(selectedResponse.getDriverId(), selectedResponse.getOfferedFare());
            // RodaRestClient.acceptBid(Long.parseLong(selectedResponse.getRequestId()), Long.parseLong(selectedResponse.getBidId()), ApplicationSettings.getCustomerId(this), acceptBidResponseHandler);
        } else {
            Snackbar.make(driverResponseView, "select 1 vehicle", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void changeTripStatusToScheduled(String driverId, String offeredFare) {
        tripResponseReference.removeEventListener(tripResponseListener);
        //final Task<Void> updateCarrierId = tripReferece.child("carrierId").setValue(driverId);
        final Task<Void> updateStatus = tripReferece.child("status").setValue("scheduled_" + driverId);
        final Task<Void> updateOfferedFare = tripReferece.child("acceptedFare").setValue(offeredFare);
        updateStatus.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("trip status is updated");
                startNextActivity();

       /* updateCarrierId.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateStatus.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("trip status is updated");
                        startNextActivity();
                    }
                });
            }
        });*/
            }
        });
    }

    private void startNextActivity() {
        mCounter.cancel();
        currentVehicleRequest = null;
        Intent intent = new Intent(this, ScehuledTripDetailsActivity.class);
        final LatLng sourceLoc = ApplicationSettings.getSourceLoc();
        //Driver details
        intent.putExtra("driverId", selectedResponse.getDriverId());
        intent.putExtra("driverName", selectedResponse.getName());
        intent.putExtra("driverMob", selectedResponse.getDriverId());

        //Vehicle Details
        intent.putExtra("vehicleRegNo", selectedResponse.getVehicleId());

        //trip details
        intent.putExtra("tripRequestId", selectedResponse.getTripId());
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

