package com.rodafleets.rodacustomer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
    private   RelativeLayout selectedVehicle;
    private VehicleRequest vehicleRequest;
    private EditText searchSrc;
    private EditText searchDst;
    int PLACE_SOURCE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_DEST_AUTOCOMPLETE_REQUEST_CODE = 2;
    int viewType = 1;
    LatLng sourceLatLang;
    LatLng destLatLang;


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
        /*if(receiverDetailsView !=null){
            receiverDetailsView.setVisibility(View.INVISIBLE);
        }*/
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
                    searchSrc.setText(place.getAddress());
                    resetPickupPointMarker();
                    resetCurrentMarkers();
                    addMarkerOnMap(0, sourceLatLang, markerSrc);
                } else if (requestCode == PLACE_DEST_AUTOCOMPLETE_REQUEST_CODE) {
                    destLatLang = place.getLatLng();
                    searchDst.setText(place.getAddress());
                    resetDropPointMarker();
                    addMarkerOnMap(1, destLatLang, markerDst);

                    // receiverDetailsView.invalidate();
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
        if (null != sourceLatLang && null != destLatLang)
            receiverDetailsCardView.setVisibility(View.VISIBLE);
    }

    private void setFonts() {
        loadFonts();
//        makeOfferBtn.setTypeface(poppinsSemiBold);
    }


    private void bidRequest() {
        int driverId = ApplicationSettings.getCustomerId(VehicleRequestActivity.this);
        RodaRestClient.bidRequest(vehicleRequest.getId(), driverId, vehicleRequest.getApproxFareInCents(), bidRequestResponseHandler);
    }

    public void onRejectBtnClick(View view) {
        int driverId = ApplicationSettings.getCustomerId(VehicleRequestActivity.this);
        RodaRestClient.rejectRequest(vehicleRequest.getId(), driverId, rejectRequestResponseHandler);
    }

    public void onCallCustomerBtnClick(View view) {

    }

    private void startNextActivity() {
         this.startActivity(new Intent(this, RequestConfirmationDetails.class));
        // finish();
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


            // requestView.setVisibility(View.VISIBLE);

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

    private JsonHttpResponseHandler rejectRequestResponseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
            ApplicationSettings.setVehicleRequest(VehicleRequestActivity.this, null);
            clearMap();
            // requestView.setVisibility(View.GONE);
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//            if(errorCode == ResponseCode.INVALID_CREDENTIALS) {
//                sb = Snackbar.make(constraintLayout, getString(R.string.sign_in_invalid_credentials_error), Snackbar.LENGTH_LONG);
//            } else {
//                sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
//            }
        }
    };

    public void showVehicleTypes(View view) {
        RelativeLayout receiverDetails = (RelativeLayout) findViewById(R.id.receiverDetailsView);
        RelativeLayout selectVehicleType = (RelativeLayout) findViewById(R.id.selectVehicleType);
        if (null != receiverDetails && null != selectVehicleType) {
            receiverDetails.setVisibility(View.GONE);
            selectVehicleType.setVisibility(View.VISIBLE);
        }
    }

    private void resetSelected(View view) {
        if (null != selectedVehicle) {
            selectedVehicle.setBackground(getResources().getDrawable(R.drawable.vehicle_request_list_item_unselect_background));
        }
    }
    private void setSelected(View view){
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
}
