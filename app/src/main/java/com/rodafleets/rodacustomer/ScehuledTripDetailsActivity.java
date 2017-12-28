package com.rodafleets.rodacustomer;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodacustomer.model.DriverLocation;
import com.rodafleets.rodacustomer.model.NearByDrivers;
import com.rodafleets.rodacustomer.rest.RodaRestClient;
import com.rodafleets.rodacustomer.services.FirebaseReferenceService;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.logging.Logger;

import cz.msebera.android.httpclient.Header;

public class ScehuledTripDetailsActivity extends MapActivity {

    private String driverName;
    private String driverMob;
    private String vehicleRegNo;
    private String sourcePlace;
    private TextView driverNameTextView;
    private TextView vehicleRegistrationNo;
    private TextView pickupLocationValue;
    private TextView driverMobNo;
    private String tripRequestId;
    private Marker driverPosition;
    private String driverId;
    private String destPlace;
    private RelativeLayout src_dst_long;
    private RelativeLayout pickupLocationDetails;
    private RelativeLayout priceDistanceView;
    private RelativeLayout payment_mode_cancel_layout;
    private TextView dest_loc_val;
    private TextView pick_loc_val;
    final Handler handler = new Handler();
    private final int REQUEST_CALL_PHONE = 1;
    private LinearLayout statusLayout;
    private TextView loading;
    private TextView unloading;
    private TextView inprogress;
    private TextView arrivedAtLocation;
    private CardView receiverDetailsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scehuled_trip_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        driverId = intent.getStringExtra("driverId");
        driverName = intent.getStringExtra("driverName");
        driverMob = intent.getStringExtra("driverMob");
        vehicleRegNo = intent.getStringExtra("vehicleRegNo");
        sourcePlace = intent.getStringExtra("sourcePlace");
        tripRequestId = intent.getStringExtra("tripRequestId");
        destPlace = intent.getStringExtra("destPlace");
        initComponents();
    }

    protected void initComponents() {
        super.initComponents();
        initMap();
        clearAllMarkers();
        subscribeForSpecifiDriver(driverId);
        driverNameTextView = (TextView) findViewById(R.id.driverName);
        driverNameTextView.setText(driverName);
        vehicleRegistrationNo = (TextView) findViewById(R.id.vehicleRegistrationNo);
        vehicleRegistrationNo.setText(vehicleRegNo);
        pickupLocationValue = (TextView) findViewById(R.id.pickupLocationValue);
        pickupLocationValue.setText(sourcePlace);
        driverMobNo = (TextView) findViewById(R.id.callDriverValue);
        driverMobNo.setText(driverMob);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Location_Updated_" + tripRequestId));
        src_dst_long = (RelativeLayout) findViewById(R.id.src_dst_long);
        pickupLocationDetails = (RelativeLayout) findViewById(R.id.pickupLocationDetails);
        priceDistanceView = (RelativeLayout) findViewById(R.id.price_dist_view);
        payment_mode_cancel_layout = (RelativeLayout) findViewById(R.id.payment_mode_cancel_layout);
        pick_loc_val = (TextView) findViewById(R.id.pick_loc_val);
        dest_loc_val = (TextView) findViewById(R.id.dest_loc_val);
        pick_loc_val.setText(sourcePlace);
        dest_loc_val.setText(destPlace);
        statusLayout = findViewById(R.id.statusLayout);
        loading = findViewById(R.id.status_loading);
        inprogress = findViewById(R.id.status_inprogress);
        unloading = findViewById(R.id.status_unloading);
        arrivedAtLocation = findViewById(R.id.status_arrived);
        receiverDetailsView = findViewById(R.id.receiverDetailsCardView);
        subscribeForTripStatus(String.valueOf(ApplicationSettings.getCustomerId(ScehuledTripDetailsActivity.this)),tripId);

    }


    private void subscribeForTripStatus(String custId, String tripId){
        DatabaseReference tripReference = FirebaseReferenceService.getTripReference(custId,tripId);
        final DatabaseReference statusRef = tripReference.child("status");
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setStatusLayoutForStatus(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Location has been updated to " + intent.getStringExtra("lat") + " , " + intent.getStringExtra("lan"));
            addMarkerForDriverCurrentLocation(Double.parseDouble(intent.getStringExtra("lat")), Double.parseDouble(intent.getStringExtra("lan")));
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        addMarkerForPickupLocation(ApplicationSettings.getSourceLoc());
        /*if (null != driverId) {
            new DriverLocationUpdaterTask(Integer.parseInt(driverId)).execute();
        }*/

    }

    public void callDriver(View view) {
        Intent myIntent = new Intent(Intent.ACTION_CALL);
        String phNum = "tel:" + driverMob;
        myIntent.setData(Uri.parse(phNum));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        }
        startActivity(myIntent);
    }


    private void addMarkerForDriverCurrentLocation(final double lat, final double lan) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //addMarkerOnMap(1, new LatLng(lat, lan), carIcon, false);
                if (null != driverPosition) {
                    driverPosition.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, lan));
                markerOptions.icon(vehicleIcon);
                driverPosition = mGoogleMap.addMarker(markerOptions);
            }
        });
    }

    private void addMarkerForPickupLocation(LatLng sourceLocation) {
        if (null == sourceLocation) {
            System.out.println("Pickup location is invalid " + sourceLocation);
        }
        System.out.println("Pickup location is " + sourceLocation);
        addMarkerOnMap(0, sourceLocation, markerSrc, false);
    }

    public void cancelTrip(View view) {

    }

    public void updownSelectionOnClick(View view) {
        ImageButton imageButtonUpDown = (ImageButton) view;
        System.out.println("image level is " + imageButtonUpDown.getDrawable().getLevel());
        imageButtonUpDown.setImageLevel((imageButtonUpDown.getDrawable().getLevel() + 1) % 2);
        System.out.println("image level is " + imageButtonUpDown.getDrawable().getLevel());

        if (null != src_dst_long && null != pickupLocationDetails && null != priceDistanceView) {
            if (imageButtonUpDown.getDrawable().getLevel() == 1) {
                src_dst_long.setVisibility(View.VISIBLE);
                pickupLocationDetails.setVisibility(View.GONE);
                priceDistanceView.setVisibility(View.VISIBLE);
                payment_mode_cancel_layout.setVisibility(View.VISIBLE);
            } else {
                src_dst_long.setVisibility(View.GONE);
                pickupLocationDetails.setVisibility(View.VISIBLE);
                priceDistanceView.setVisibility(View.GONE);
                payment_mode_cancel_layout.setVisibility(View.GONE);
            }
        }
    }


    private class DriverLocationUpdaterTask extends AsyncTask<Integer, Void, Void> {

        private LatLng currentLocation;

        int driverId;

        DriverLocationUpdaterTask(int driverId) {
            this.driverId = driverId;
        }

        @Override
        protected Void doInBackground(Integer... input) {
            RodaRestClient.getDriverLocation(driverId, getDriverLocationHandler);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void setStatusLayoutForStatus(String status) {
        switch (status) {
            case "Awaiting":
                statusLayout.setVisibility(View.INVISIBLE);
                break;
            case "Scheduled":
                statusLayout.setVisibility(View.VISIBLE);
                statusLayout.setBackgroundColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, R.color.separator_color));
                loading.setVisibility(View.GONE);
                inprogress.setVisibility(View.GONE);
                unloading.setVisibility(View.GONE);
                arrivedAtLocation.setVisibility(View.VISIBLE);
                break;
            case "arrived":
                break;
            case "Loading":
                statusLayout.setVisibility(View.VISIBLE);
                arrivedAtLocation.setVisibility(View.GONE);
                statusLayout.setBackgroundColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, android.R.color.black));
                loading.setVisibility(View.VISIBLE);
                inprogress.setVisibility(View.VISIBLE);
                unloading.setVisibility(View.VISIBLE);
                loading.setTextColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, android.R.color.white));
                inprogress.setTextColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, R.color.status_message_color));
                unloading.setTextColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, R.color.status_message_color));
                break;
            case "Inprogress":
                statusLayout.setVisibility(View.VISIBLE);
                arrivedAtLocation.setVisibility(View.GONE);
                statusLayout.setBackgroundColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, android.R.color.black));
                loading.setVisibility(View.VISIBLE);
                inprogress.setVisibility(View.VISIBLE);
                unloading.setVisibility(View.VISIBLE);
                loading.setTextColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, R.color.status_message_color));
                inprogress.setTextColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, android.R.color.white));
                unloading.setTextColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, R.color.status_message_color));
                break;
            case "UnLoading":
                statusLayout.setVisibility(View.VISIBLE);
                arrivedAtLocation.setVisibility(View.GONE);
                statusLayout.setBackgroundColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, android.R.color.black));
                loading.setVisibility(View.VISIBLE);
                inprogress.setVisibility(View.VISIBLE);
                unloading.setVisibility(View.VISIBLE);
                loading.setTextColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, R.color.status_message_color));
                inprogress.setTextColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, R.color.status_message_color));
                unloading.setTextColor(ContextCompat.getColor(ScehuledTripDetailsActivity.this, android.R.color.white));
                break;
            case "Completed":
                receiverDetailsView.setVisibility(View.GONE);
                finish();
            default:
                statusLayout.setVisibility(View.INVISIBLE);
                break;

        }
    }

    private JsonHttpResponseHandler getDriverLocationHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Toast.makeText(ScehuledTripDetailsActivity.this, "driver location updated ", Toast.LENGTH_SHORT).show();
            Gson gson = new GsonBuilder().create();
            DriverLocation driverLocation = gson.fromJson(response.toString(), DriverLocation.class);
            if (null != driverPosition) {
                driverPosition.remove();
                addMarkerOnMap(1, new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude()), vehicleIcon, false);
            } else {
                addMarkerOnMap(1, new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude()), vehicleIcon, false);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Toast toast = Toast.makeText(ScehuledTripDetailsActivity.this, "Not able to fetch latest location!", Toast.LENGTH_SHORT);
        }
    };

}
