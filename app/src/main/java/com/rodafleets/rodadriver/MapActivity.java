package com.rodafleets.rodadriver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rodafleets.rodadriver.model.VehicleRequest;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONObject;

public class MapActivity extends ParentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = AppConstants.APP_NAME;

    GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker pickupPointMarker;
    Marker dropPointMarker;

    private Bitmap redIcon;
    private Bitmap greenIcon;
    private Bitmap carIcon;

    @Override
    public void onPause() {
        super.onPause();
//        //stop location updates when Activity is no longer active
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        }
    }

    public void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void clearMap() {
        mGoogleMap.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

        initMarkerBitmaps();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapActivity.this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    private void initMarkerBitmaps(){

        int marker_height = 45;
        int marker_width = 32;

        int car_height = 30;
        int car_width = 37;

        greenIcon = BitmapFactory.decodeResource(getResources(), R.drawable.marker_green);
        redIcon = BitmapFactory.decodeResource(getResources(), R.drawable.marker_red);
        carIcon = BitmapFactory.decodeResource(getResources(), R.drawable.car_icon);

        greenIcon = Bitmap.createScaledBitmap(greenIcon, marker_width, marker_height, false);
        redIcon = Bitmap.createScaledBitmap(redIcon, marker_width, marker_height, false);
        carIcon = Bitmap.createScaledBitmap(carIcon, car_width, car_height, false);

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(carIcon));

        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);


        if(!ApplicationSettings.getVehicleRequest(this).equals("")) {

            VehicleRequest vehicleRequest;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            int padding = 30; // offset from edges of the map in pixels

            builder.include(mCurrLocationMarker.getPosition());
            try {

                JSONObject jsonObject = new JSONObject(ApplicationSettings.getVehicleRequest(MapActivity.this));

                vehicleRequest = new VehicleRequest(jsonObject);

                //Place pickup location marker
                LatLng pickupPointLatLng = new LatLng(vehicleRequest.getOriginLat(), vehicleRequest.getOriginLng());

                MarkerOptions pickUpMarkerOptions = new MarkerOptions();
                pickUpMarkerOptions.position(pickupPointLatLng);
                pickUpMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(greenIcon));

                pickupPointMarker = mGoogleMap.addMarker(pickUpMarkerOptions);
                builder.include(pickupPointMarker.getPosition());

                //Place drop location marker
                LatLng dropPointLatlng = new LatLng(vehicleRequest.getDestinationLat(), vehicleRequest.getDestinationLng());
                MarkerOptions dropMarkerOptions = new MarkerOptions();

                dropMarkerOptions.position(dropPointLatlng);
                dropMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(redIcon));

                dropPointMarker = mGoogleMap.addMarker(dropMarkerOptions);
                builder.include(dropPointMarker.getPosition());

            } catch (Exception e) {
                //handle error
                Log.e(TAG, "vehicleRequest jsonException in MapActivity = " + e.getMessage());
            }

            LatLngBounds bounds = builder.build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        } else {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        }

        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please tick_green to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
