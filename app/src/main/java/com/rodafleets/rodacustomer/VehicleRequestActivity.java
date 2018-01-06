package com.rodafleets.rodacustomer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodacustomer.database.FavouriteReceiver;
import com.rodafleets.rodacustomer.model.VehicleRequest;
import com.rodafleets.rodacustomer.rest.RodaRestClient;
import com.rodafleets.rodacustomer.services.FirebaseReferenceService;
import com.rodafleets.rodacustomer.services.Utils;
import com.rodafleets.rodacustomer.utils.AppConstants;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class VehicleRequestActivity extends MapActivity {

    public static final String TAG = AppConstants.APP_NAME;

    private CardView receiverDetailsCardView;
    private RelativeLayout selectedVehicle;
    private VehicleRequest vehicleRequest;
    private RelativeLayout selectVehicleType;
    private RelativeLayout receiverDetails;
    private Place sourcePlace;
    private Place destPlace;
    private EditText searchSrc;
    private EditText searchDst;
    private TextView driverName;
    private TextView driverContact;
    int PLACE_SOURCE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_DEST_AUTOCOMPLETE_REQUEST_CODE = 2;
    int viewType = 1;
    LatLng sourceLatLang;
    LatLng destLatLang;
    private Handler handler;
    private EditText receiverName;
    private EditText receiverPhone;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        // RodaRestClient.getNearByDriverLocations(10.1, 10.8, getNearByDriverLocationResponseHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_request);
        initComponents();
    }


    protected void checkIfTripInProgress() {
        final String customerEid = ApplicationSettings.getCustomerEid(VehicleRequestActivity.this);
        final DatabaseReference currentTripReference = FirebaseReferenceService.getCustomerCurrentTripIdReference(customerEid);
        currentTripReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot && null != dataSnapshot.getValue()) {
                    final String lastTripId = (String) dataSnapshot.getValue();
                    final DatabaseReference lastTripReference = FirebaseReferenceService.getTripReference(customerEid, lastTripId);
                    lastTripReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (null != dataSnapshot.getValue()) {
                                final HashMap<String, Object> tripDetails = (HashMap<String, Object>) dataSnapshot.getValue();
                                String status = (String) (tripDetails.get("status"));
                                Long timeStamp = (Long) (tripDetails.get("timestamp"));
                                final long l = Utils.getCurrentTime().longValue();
                                //TODO awaiting time right now is 3 mins, should be configurable.
                                if ((l - timeStamp < 180000) || !((status == null) || status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("expired")))
                                {
                                    currentTripId = lastTripId;
                                    currentVehicleRequest = VehicleRequest.getVehicleRequest(tripDetails);
                                    startNextActivityForCurrentTrip(status);
                                } else{
                                    //trip is expired, remove current trip reference.
                                    FirebaseReferenceService.expireCustomerCurrentTrip(customerEid, lastTripId);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void checkIfAnyTripInProgressSession() {
        //TODO this is depricated for now, as only 1 trip could be in progress for a user.
        final Query lastTripReferece = FirebaseReferenceService.getLastTripReferece(ApplicationSettings.getCustomerEid(VehicleRequestActivity.this));
        lastTripReferece.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot.getValue()) {
                    final Map.Entry<String, Object> tripMapEntry = ((Map<String, Object>) dataSnapshot.getValue()).entrySet().iterator().next();
                    String tripId = tripMapEntry.getKey();
                    final HashMap<String, Object> tripDetails = (HashMap<String, Object>) tripMapEntry.getValue();
                    String status = (String) (tripDetails.get("status"));
                    Long timeStamp = (Long) (tripDetails.get("timestamp"));
                    final long l = Utils.getCurrentTime().longValue();
                    if (l - timeStamp < 180000 || !(status.equalsIgnoreCase("awaiting") || status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("expired"))) {
                        currentTripId = tripId;
                        currentVehicleRequest = VehicleRequest.getVehicleRequest(tripDetails);
                        startNextActivityForCurrentTrip(status);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

/*        lastTripReferece.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    String tripId = snapShot.getKey();
                    final HashMap<String, Object> tripDetails = (HashMap<String, Object>) snapShot.getValue();
                    String status = (String) (tripDetails.get("status"));
                    Long timeStamp = (Long) (tripDetails.get("timestamp"));
                    final long l = Utils.getCurrentTime().longValue();
                    if (l - timeStamp < 180000 || !(status.equalsIgnoreCase("awaiting") || status.equalsIgnoreCase("completed"))) {
                        currentTripId = tripId;
                        currentVehicleRequest = VehicleRequest.getVehicleRequest(tripDetails);
                        startNextActivityForCurrentTrip(status);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        Snackbar.make(receiverDetailsCardView, "Checking Active Bookings", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private void startNextActivityForCurrentTrip(String status) {
        Intent intent = new Intent(this, ScehuledTripDetailsActivity.class);
        startActivity(intent);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetViews();
            }
        }, 1000);
    }

    private void initViews() {
        searchSrc = findViewById(R.id.search_src);
        searchDst = findViewById(R.id.search_dst);
        receiverDetailsCardView = findViewById(R.id.receiverDetailsCardView);
        selectVehicleType = findViewById(R.id.selectVehicleType);
        driverName = findViewById(R.id.driverName);
        receiverName = findViewById(R.id.receiverName);
        receiverPhone = findViewById(R.id.receiverPhone);
    }

    protected void initComponents() {
        super.initComponents();
        initViews();
        initMap();
        setFonts();
        //checkIfAnyTripInProgressSession();
        checkIfTripInProgress();
        subscribeForSurroundingDriversLocationUpdates();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Vehicle_Requested"));
        boolean fromNotification = getIntent().getBooleanExtra("FROM_NOTIFICATION", false);
        if (fromNotification) {
            Log.i(TAG, "opened from notification");
            showVehicleRequest();
        }

        if (searchSrc != null) {
            View.OnClickListener srcSearchListener = clickListener(PLACE_SOURCE_AUTOCOMPLETE_REQUEST_CODE);
            searchSrc.setOnClickListener(srcSearchListener);
        }
        if (searchDst != null) {
            View.OnClickListener dstSearchListener = clickListener(PLACE_DEST_AUTOCOMPLETE_REQUEST_CODE);
            searchDst.setOnClickListener(dstSearchListener);

        }
        handler = new Handler();
    }

    private View.OnClickListener clickListener(final int requestCode) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(VehicleRequestActivity.this);
                    startActivityForResult(intent, requestCode);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                    System.out.println("OOPS Error" + e);
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                    System.out.println("OOPS Error" + e);
                }

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_SOURCE_AUTOCOMPLETE_REQUEST_CODE || requestCode == PLACE_DEST_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                if (requestCode == PLACE_SOURCE_AUTOCOMPLETE_REQUEST_CODE) {
                    sourcePlace = place;
                    sourceLatLang = place.getLatLng();
                    ApplicationSettings.setSourceLoc(sourceLatLang);
                    ApplicationSettings.setSourcePlace(place.getAddress().toString());
                    searchSrc.setText(place.getAddress());
                    resetPickupPointMarker();
                    resetCurrentMarkers();
                    addMarkerOnMap(0, sourceLatLang, markerSrc, true);
                } else if (requestCode == PLACE_DEST_AUTOCOMPLETE_REQUEST_CODE) {
                    destPlace = place;
                    destLatLang = place.getLatLng();
                    searchDst.setText(place.getAddress());
                    ApplicationSettings.setDestPlace(place.getAddress().toString());
                    resetDropPointMarker();
                    addMarkerOnMap(1, destLatLang, markerDst, true);
                }
                checkIfSourceDestinationAvailable();
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    private void checkIfSourceDestinationAvailable() {
        showSourceDestRoute();
        showReceiversDetails();
    }

    private void showSourceDestRoute() {

    }

    private void setFonts() {
        loadFonts();
//        makeOfferBtn.setTypeface(poppinsSemiBold);
    }

    private void startNextActivity() {
        //this.startActivity(new Intent(this, RequestConfirmationDetails.class));
        //Intent intent = new Intent(this, RequestConfirmationDetails.class);
        Intent intent = new Intent(this, DriversResponseActivity.class);
        startActivity(intent);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetViews();
            }
        }, 1000);
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


            long fare = vehicleRequest.getApproxFareInCents() / 100;


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


    private JsonHttpResponseHandler acceptBidResponseHandler = new JsonHttpResponseHandler() {
        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            System.out.println("Bid accept conveyed successfully");
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            System.out.println("Bid accept not able to convey");
        }
    };

    private JsonHttpResponseHandler rejectRequestResponseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
            ApplicationSettings.setVehicleRequest(VehicleRequestActivity.this, null);
            clearMap();
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//            if(errorCode == ResponseCode.INVALID_CREDENTIALS) {
//                sb = Snackbar.make(constraintLayout, getString(R.string.sign_in_invalid_credentials_error), Snackbar.LENGTH_LONG);
//            } else {
//                sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
//            }
        }
    };

    private void resetSelected(View view) {
        if (null != selectedVehicle) {
            selectedVehicle.setBackground(getResources().getDrawable(R.drawable.vehicle_request_list_item_unselect_background));
        }
    }

    private void setSelected(View view) {
        selectedVehicle = (RelativeLayout) view;
        selectedVehicle.setBackground(getResources().getDrawable(R.drawable.vehicle_request_list_item_selected_background));
    }

    public void selectVehicleType(View view) {
        if (null != view) {
            resetSelected(view);
            setSelected(view);
            viewType = 1;
        }
    }

    public void makeVehicleRequest(View view) {
        if (sourceLatLang != null && destLatLang != null)
            RodaRestClient.requestVehicle(ApplicationSettings.getCustomerEid(VehicleRequestActivity.this), 1, sourceLatLang.latitude, sourceLatLang.longitude, destLatLang.latitude, destLatLang.longitude, sourcePlace.getAddress().toString(), destPlace.getAddress().toString(), vehicleReqestHandler);
    }

    private JsonHttpResponseHandler vehicleReqestHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                currentTripId = response.getString("ref");
                FirebaseReferenceService.addCustomerCurrentTrip(ApplicationSettings.getCustomerEid(VehicleRequestActivity.this), currentTripId);
            } catch (JSONException e) {
                Snackbar.make(receiverDetailsCardView, "Something went wrong : Try Again", Snackbar.LENGTH_LONG).show();
            }
            startNextActivity();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            //TODO Actually errors should be handled, and proper UI message should be displayed
            Snackbar.make(receiverDetailsCardView, "Something went wrong : Try Again", Snackbar.LENGTH_LONG).show();
        }
    };

    // Show view for vehicle types.
    public void showVehicleTypes(View view) {

        receiverDetails = (RelativeLayout) findViewById(R.id.locationViewSmall);
        selectVehicleType = (RelativeLayout) findViewById(R.id.selectVehicleType);
        ToggleButton favouriteToggle = (ToggleButton) findViewById(R.id.favourite_toggle);

        if (null != favouriteToggle && favouriteToggle.isChecked()) {
            //create here new thread to put on favourites.
            String receiverName = ((EditText) findViewById(R.id.receiverName)).getText().toString();
            String receiverPhone = ((EditText) findViewById(R.id.receiverPhone)).getText().toString();
            addToFavourites(this, receiverName, receiverPhone, destPlace.getAddress().toString(), destPlace.getLatLng());
        }
        if (null != receiverDetails && null != selectVehicleType) {
            startGoneAnimation(receiverDetailsCardView);
            receiverDetails.setVisibility(View.GONE);
            selectVehicleType.setVisibility(View.VISIBLE);
            startInAnimation(receiverDetailsCardView, 750);
        }
    }


    private void addToFavourites(Context context, String receiverName, String receiverPhoneNumber, String destAddress, LatLng dest) {
        FavouriteReceiver favRec = new FavouriteReceiver(receiverName, receiverPhoneNumber, destAddress, dest.latitude, dest.longitude);
        FirebaseReferenceService.addFavourite(ApplicationSettings.getCustomerEid(VehicleRequestActivity.this), favRec);
       /* Favourite favourite = new Favourite(receiverName, receiverPhoneNumber, sourceAddress, destAddress, source, dest);
        DBHelper mydb = new DBHelper(context);
        mydb.addToFavourites(favourite);*/
    }

    //Animation to show view coming from bottom, intended for card view.
    private void startInAnimation(View v, int position) {
        Display mDisplay = this.getWindowManager().getDefaultDisplay();
        final int height = mDisplay.getHeight();
        final float yPosition = v.getY();
        v.setY(height);
        v.setVisibility(View.VISIBLE);
        //v.animate().y(position > receiverDetailsCardView.getBottom()-v.getHeight() ? position : 0).setDuration(500).start();
        v.animate().y(receiverDetailsCardView.getBottom() - v.getHeight()).setDuration(500).start();
    }

    //Animation to hide view down, intended for card view.
    private void startGoneAnimation(View v) {
        Display mDisplay = this.getWindowManager().getDefaultDisplay();
        final int height = mDisplay.getHeight();
        v.animate().y(height).setDuration(500).start();
        v.setVisibility(View.GONE);
    }

    //This is to show bottom CardView to fill vehicle type & receiver details.
    private void showReceiversDetails() {
        if (null != sourceLatLang && null != destLatLang) {
            this.startInAnimation(receiverDetailsCardView, 0);
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    //This is to reset the views once next activity is started.
    private void resetViews() {
        if (null != searchSrc) {
            searchSrc.getText().clear();
            sourceLatLang = null;
        }
        if (null != searchDst) {
            searchDst.getText().clear();
            destLatLang = null;
        }
        if (null != receiverPhone) {
            receiverPhone.getText().clear();
        }
        if (null != receiverName) {
            receiverName.getText().clear();
        }

        if (null != receiverDetails) {
            receiverDetails.setVisibility(View.VISIBLE);
        }
        if (null != selectVehicleType) {
            selectVehicleType.setVisibility(View.INVISIBLE);
        }
        if (null != receiverDetailsCardView) {
            receiverDetailsCardView.setVisibility(View.INVISIBLE);
        }
    }

}
