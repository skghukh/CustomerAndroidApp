package com.rodafleets.rodacustomer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodacustomer.model.VehicleRequest;
import com.rodafleets.rodacustomer.rest.RodaRestClient;
import com.rodafleets.rodacustomer.utils.AppConstants;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class VehicleRequestActivity extends MapActivity {

    public static final String TAG = AppConstants.APP_NAME;

    private CardView receiverDetailsCardView;
    private RelativeLayout selectedVehicle;
    private VehicleRequest vehicleRequest;
    private RelativeLayout selectVehicleType;
    private RelativeLayout receiverDetails;
    // private RelativeLayout driverDetailsView;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        RodaRestClient.getNearByDriverLocations(10.1, 10.8, getNearByDriverLocationResponseHandler);
    }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_request);
        initComponents();
    }


    protected void initComponents() {
        super.initComponents();
        searchSrc = (EditText) findViewById(R.id.search_src);
        searchDst = (EditText) findViewById(R.id.search_dst);
        receiverDetailsCardView = (CardView) findViewById(R.id.receiverDetailsCardView);
        selectVehicleType = (RelativeLayout) findViewById(R.id.selectVehicleType);
        // driverDetailsView = (RelativeLayout) findViewById(R.id.driverDetails);
        driverName = (TextView) findViewById(R.id.driverName);
        // driverContact = (TextView) findViewById(R.id.driverContact);
        receiverName = (EditText) findViewById(R.id.receiverName);
        receiverPhone = (EditText) findViewById(R.id.receiverPhone);
        initMap();
        setFonts();
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
                    sourceLatLang = place.getLatLng();
                    ApplicationSettings.setSourceLoc(sourceLatLang);
                    ApplicationSettings.setSourcePlace(place.getAddress().toString());
                    searchSrc.setText(place.getAddress());
                    resetPickupPointMarker();
                    resetCurrentMarkers();
                    addMarkerOnMap(0, sourceLatLang, markerSrc, true);
                } else if (requestCode == PLACE_DEST_AUTOCOMPLETE_REQUEST_CODE) {
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
        showReceiversDetails();
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
            RodaRestClient.requestVehicle(ApplicationSettings.getCustomerId(VehicleRequestActivity.this), 1, sourceLatLang.latitude, sourceLatLang.longitude, destLatLang.latitude, destLatLang.longitude, vehicleReqestHandler);
    }

    private JsonHttpResponseHandler vehicleReqestHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            System.out.println("Response code is " + statusCode + " Response is " + response);
            startNextActivity();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            System.out.println("Oops ! " + errorResponse);
        }
    };

    // Show view for vehicle types.
    public void showVehicleTypes(View view) {
        receiverDetails = (RelativeLayout) findViewById(R.id.locationViewSmall);
        selectVehicleType = (RelativeLayout) findViewById(R.id.selectVehicleType);
        if (null != receiverDetails && null != selectVehicleType) {
            startGoneAnimation(receiverDetailsCardView);
            receiverDetails.setVisibility(View.GONE);
            selectVehicleType.setVisibility(View.VISIBLE);
            startInAnimation(receiverDetailsCardView, 750);
        }
    }

    //Animation to show view coming from bottom, intended for card view.
    private void startInAnimation(View v, int position) {
        Display mDisplay = this.getWindowManager().getDefaultDisplay();
        final int height = mDisplay.getHeight();
        final float yPosition = v.getY();
        v.setY(height);
        v.setVisibility(View.VISIBLE);
        v.animate().y(position > 0 ? position : yPosition).setDuration(500).start();
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
