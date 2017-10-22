package com.rodafleets.rodacustomer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodacustomer.model.DriverLocation;
import com.rodafleets.rodacustomer.model.NearByDrivers;
import com.rodafleets.rodacustomer.rest.RodaRestClient;
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
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scehuled_trip_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       /* setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        Intent intent = getIntent();
        driverName = intent.getStringExtra("driverName");
        driverMob = intent.getStringExtra("driverMob");
        vehicleRegNo = intent.getStringExtra("vehicleRegId");
        sourcePlace = intent.getStringExtra("sourcePlace");
        initComponents();
        /*final Bundle extras = intent.getExtras();
        LatLng src = intent.getParcelableExtra("sourceLocation");*/

    }

    protected void initComponents() {
        super.initComponents();
        initMap();
        driverNameTextView = (TextView) findViewById(R.id.driverName);
        driverNameTextView.setText(driverName);
        vehicleRegistrationNo = (TextView) findViewById(R.id.vehicleRegistrationNo);
        vehicleRegistrationNo.setText(vehicleRegNo);
        pickupLocationValue = (TextView) findViewById(R.id.pickupLocationValue);
        pickupLocationValue.setText(sourcePlace);
        driverMobNo = (TextView) findViewById(R.id.callDriverValue);
        driverMobNo.setText(driverMob);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        addMarkerForPickupLocation(ApplicationSettings.getSourceLoc());
        addMarkerForDriverCurrentLocation();
        addThreadForUpdateDriverCurrentLocation();
    }

    public void callDriver(View view) {
        Intent myIntent = new Intent(Intent.ACTION_CALL);
        String phNum = "tel:" + driverMob;
        myIntent.setData(Uri.parse(phNum));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Toast.makeText(this, "Not able to call due to permissions ", Toast.LENGTH_SHORT);
            return;
        }
        startActivity(myIntent);
    }

    private void addThreadForUpdateDriverCurrentLocation() {

    }

    private void addMarkerForDriverCurrentLocation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DriverLocationUpdaterTask mDriverLocationTask = new DriverLocationUpdaterTask(19);
                mDriverLocationTask.execute(19);
                addMarkerForDriverCurrentLocation();
            }
        }, 5000);
    }

    private void addMarkerForPickupLocation(LatLng sourceLocation) {
        if (null == sourceLocation) {
            System.out.println("Pickup location is invalid " + sourceLocation);
        }
        System.out.println("Pickup location is " + sourceLocation);
        initMarkerBitmaps();
        addMarkerOnMap(0, sourceLocation, markerSrc,false);
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

    @Override
    public void onPause() {
        if (null != handler) {
           // handler.getLooper().quit();
            handler.removeCallbacks(null);
        }
        super.onPause();
    }

    private JsonHttpResponseHandler getDriverLocationHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Toast.makeText(ScehuledTripDetailsActivity.this, "driver location updated ", Toast.LENGTH_SHORT).show();
            Gson gson = new GsonBuilder().create();
            DriverLocation driverLocation = gson.fromJson(response.toString(), DriverLocation.class);
            if (null != driverPosition) {
                driverPosition.remove();
                addMarkerOnMap(1, new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude()), carIcon,false);
            }else{
                addMarkerOnMap(1, new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude()), carIcon,false);
            }


        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Toast toast = Toast.makeText(ScehuledTripDetailsActivity.this, "Not able to fetch latest location!", Toast.LENGTH_SHORT);
        }
    };

}
