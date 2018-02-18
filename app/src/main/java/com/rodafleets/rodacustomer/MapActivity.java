package com.rodafleets.rodacustomer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rodafleets.rodacustomer.model.VehicleRequest;
import com.rodafleets.rodacustomer.services.FirebaseReferenceService;
import com.rodafleets.rodacustomer.services.Utils;
import com.rodafleets.rodacustomer.utils.AppConstants;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;
import com.rodafleets.rodacustomer.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapActivity extends ParentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = AppConstants.APP_NAME;

    protected GoogleMap mGoogleMap;
    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected Marker mCurrLocationMarker;
    protected Marker pickupPointMarker;
    protected Marker dropPointMarker;
    protected Marker driverPosition;
    //TODO: login need to be done using phone number
    private static FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    protected BitmapDescriptor markerDst;
    protected BitmapDescriptor markerSrc;
    protected BitmapDescriptor vehicleIcon;
    protected BitmapDescriptor currentLocationIcon;
    private boolean firstTime = true;
    private GeoQuery geoQueryForGetNearByDriversQuery;

    private HashMap<String, Marker> mMarkers = new HashMap<>();


    protected static String currentTripId;
    protected static VehicleRequest currentVehicleRequest;

    @Override
    public void onPause() {
        super.onPause();
    }

    public void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initializeMapMarkers();
    }

    private void initializeMapMarkers() {
        markerSrc = getBitmapDescriptor(R.drawable.ic_pickup_marker);
        markerDst = getBitmapDescriptor(R.drawable.ic_drop_marker);
        vehicleIcon = getBitmapDescriptor(R.drawable.ic_truck);
        currentLocationIcon = BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE);//getBitmapDescriptor(R.drawable.marker_green);
    }

    public void clearMap() {
        mGoogleMap.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        checkLocationPermissionAndInitMap();
    }

    public void checkLocationPermissionAndInitMap() {
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                //Request Location Permission
                Utils.checkLocationPermission(MapActivity.this);
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected void subscribeForSurroundingDriversLocationUpdates() {
        GeoFire geoFire = new GeoFire(FirebaseReferenceService.getLocationReference());
        final Location currentLocation = getLocation();
        addMarkerOnMap(2, new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), currentLocationIcon, true);
        Log.i(TAG, "Subscribing for updates from Drivers running around my current location " + currentLocation.getLatitude() + " : " + currentLocation.getLongitude());
        geoQueryForGetNearByDriversQuery = geoFire.queryAtLocation(new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()), 5);
        geoQueryForGetNearByDriversQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                setOrUpdateDriverMarkerOnMap(key, location.latitude, location.longitude);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                setOrUpdateDriverMarkerOnMap(key, location.latitude, location.longitude);
            }

            @Override
            public void onGeoQueryReady() {
                Toast.makeText(MapActivity.this, "Geoquery is ready ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(MapActivity.this, "Error in GeoQuery", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void clearAllMarkers() {
        for (Marker marker : mMarkers.values()) {
            marker.remove();
        }
    }

    protected void subscribeForSpecificDriverLocationUpdates(String driverId) {
        DatabaseReference ref = FirebaseReferenceService.getLocationUpdatesForDriver(driverId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot.getValue()) {
                    setOrUpdateDriverMarker(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setOrUpdateDriverMarker(DataSnapshot dataSnapshot) {
        // Functionality coming next step
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        ArrayList<Double> driverLoc = (ArrayList<Double>) value.get("l");
        double lat = driverLoc.get(0);
        double lng = driverLoc.get(1);
        setOrUpdateDriverMarkerOnMap(key, lat, lng);
        // mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
    }

    private void setOrUpdateDriverMarkerOnMap(String key, double lat, double lng) {
        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mGoogleMap.addMarker(new MarkerOptions().title(key).position(location).icon(vehicleIcon)));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
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
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void resetCurrentMarkers() {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
    }

    protected void resetPickupPointMarker() {
        if (pickupPointMarker != null) {
            pickupPointMarker.remove();
        }
    }

    protected void resetDropPointMarker() {
        if (dropPointMarker != null) {
            dropPointMarker.remove();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
       /* mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        setCurrentPositionMarker(latLng);*/
       /* if (!ApplicationSettings.getVehicleRequest(this).equals("")) {
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
                pickUpMarkerOptions.icon(markerSrc);

                pickupPointMarker = mGoogleMap.addMarker(pickUpMarkerOptions);
                builder.include(pickupPointMarker.getPosition());

                //Place drop location marker
                LatLng dropPointLatlng = new LatLng(vehicleRequest.getDestinationLat(), vehicleRequest.getDestinationLng());
                MarkerOptions dropMarkerOptions = new MarkerOptions();

                dropMarkerOptions.position(dropPointLatlng);
                dropMarkerOptions.icon(markerDst);

                dropPointMarker = mGoogleMap.addMarker(dropMarkerOptions);
                builder.include(dropPointMarker.getPosition());

            } catch (Exception e) {
                //handle error
                Log.e(TAG, "vehicleRequest jsonException in MapActivity = " + e.getMessage());
            }

            LatLngBounds bounds = builder.build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        } */
        //else {
       /* if (firstTime) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
            firstTime = false;
        }
        // }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20);
        mGoogleMap.animateCamera(cameraUpdate);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);*/

    }

    protected void addMarkerOnMap(int source, LatLng pos, BitmapDescriptor icon, boolean isCameraMoveRequired) {
        if (source == 0) {
            if (null != pickupPointMarker) {
                pickupPointMarker.setPosition(pos);
            } else {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(pos);
                markerOptions.icon(icon);
                pickupPointMarker = mGoogleMap.addMarker(markerOptions);
            }
        } else if (source == 1) {
            if (null != dropPointMarker) {
                dropPointMarker.setPosition(pos);
            } else {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(pos);
                markerOptions.icon(icon);
                dropPointMarker = mGoogleMap.addMarker(markerOptions);
            }
            //driverPosition = mGoogleMap.addMarker(markerOptions);
        } else if (source == 2) {
            if (null != mCurrLocationMarker) {
                mCurrLocationMarker.setPosition(pos);
            } else {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(pos);
                markerOptions.icon(icon);
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
            }

        }
        if (isCameraMoveRequired) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 16f));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_LOCATION: {
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
                            mGoogleMap.setMyLocationEnabled(true);
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public Location getLocation() {
        Location location = null;
        if (ContextCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapActivity.this, "PLZ open up GPS To Access", Toast.LENGTH_SHORT).show();
        }
        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean isPassiveEnabled = locationManager
                .isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled || isNetworkEnabled || isPassiveEnabled) {

            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (isPassiveEnabled && location == null) {
            Log.d("Network", "Network Enabled");
            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }

        if (isNetworkEnabled && location == null) {
            Log.d("Network", "Network Enabled");
            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        return location;
    }

    private Location getMyLocation() {
        LocationManager lm = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        final Location myLocation;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSION_ACCESS_FINE_LOCATION);
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
       /* if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }
            myLocation = lm.getLastKnownLocation(provider);
        }*/

        if (myLocation == null) {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
        return myLocation;
    }
}
