package com.rodafleets.rodacustomer;

import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.rodafleets.rodacustomer.custom.slideview.SlideView;
import com.rodafleets.rodacustomer.model.VehicleRequest;
import com.rodafleets.rodacustomer.rest.RodaRestClient;
import com.rodafleets.rodacustomer.utils.AppConstants;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class VehicleRequestActivity extends MapActivity {

    public static final String TAG = AppConstants.APP_NAME;

    private CardView requestView;

    private TextView customerName;
    private TextView fromAddress;
    private TextView toAddress;
    private TextView distance;
    private TextView loadingUnloadingTxt;
    private TextView makeOfferTxt;
    private TextView callAdmin;

    private Button callCustomerBtn;

    private SlideView makeOfferBtn;

    private VehicleRequest vehicleRequest;
    private EditText searchSrc;
    private EditText searchDst;
    int PLACE_SOURCE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_DEST_AUTOCOMPLETE_REQUEST_CODE = 2;
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
        requestView = (CardView) findViewById(R.id.requestView);

        customerName = (TextView) findViewById(R.id.customerName);
        fromAddress = (TextView) findViewById(R.id.fromAddress);
        toAddress = (TextView) findViewById(R.id.toAddress);
        distance = (TextView) findViewById(R.id.distance);
        loadingUnloadingTxt = (TextView) findViewById(R.id.loadingUnloadingTxt);
        makeOfferTxt = (TextView) findViewById(R.id.makeOfferTxt);

        callCustomerBtn = (Button) findViewById(R.id.callCustomerBtn);
        callAdmin = (TextView) findViewById(R.id.callAdmin);
        makeOfferBtn = (SlideView) findViewById(R.id.makeOfferBtn);
        searchSrc = (EditText) findViewById(R.id.search_src);
        searchDst = (EditText) findViewById(R.id.search_dst);

        initMap();
        setFonts();
        initMakeOfferBtn();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Vehicle_Requested"));

        boolean fromNotification = getIntent().getBooleanExtra("FROM_NOTIFICATION", false);
        if (fromNotification) {
            Log.i(TAG, "opened from notification");
            showVehicleRequest();
        }

        View.OnClickListener srcSearchListener = clickListener(PLACE_SOURCE_AUTOCOMPLETE_REQUEST_CODE);
        searchSrc.setOnClickListener(srcSearchListener);
        View.OnClickListener dstSearchListener = clickListener(PLACE_DEST_AUTOCOMPLETE_REQUEST_CODE);
        searchDst.setOnClickListener(dstSearchListener);
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
                    addMarkerOnMap(0,sourceLatLang,markerSrc);
                } else if (requestCode == PLACE_DEST_AUTOCOMPLETE_REQUEST_CODE) {
                    destLatLang = place.getLatLng();
                    searchDst.setText(place.getAddress());
                    resetDropPointMarker();
                    addMarkerOnMap(1,destLatLang,markerDst);
                }
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

    private void setFonts() {
        loadFonts();
        customerName.setTypeface(poppinsMedium);
        fromAddress.setTypeface(poppinsRegular);
        toAddress.setTypeface(poppinsRegular);
        distance.setTypeface(poppinsLight);
        loadingUnloadingTxt.setTypeface(poppinsSemiBold);
//        makeOfferBtn.setTypeface(poppinsSemiBold);
        makeOfferTxt.setTypeface(poppinsRegular);
        callCustomerBtn.setTypeface(poppinsMedium);
        callAdmin.setTypeface(poppinsRegular);
    }

    private void initMakeOfferBtn() {
        makeOfferBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                v.onTouchEvent(event);

                makeOfferBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        makeOfferBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                bidRequest();
            }
        });
    }

    private void bidRequest() {
        int driverId = ApplicationSettings.getDriverId(VehicleRequestActivity.this);
        RodaRestClient.bidRequest(vehicleRequest.getId(), driverId, vehicleRequest.getApproxFareInCents(), bidRequestResponseHandler);
    }

    public void onRejectBtnClick(View view) {
        int driverId = ApplicationSettings.getDriverId(VehicleRequestActivity.this);
        RodaRestClient.rejectRequest(vehicleRequest.getId(), driverId, rejectRequestResponseHandler);
    }

    public void onCallCustomerBtnClick(View view) {

    }

    private void startNextActivity() {
        this.startActivity(new Intent(this, TripProgressActivity.class));
        finish();
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

            customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
            fromAddress.setText(vehicleRequest.getOriginAddress());
            toAddress.setText(vehicleRequest.getDestinationAddress());
            distance.setText(vehicleRequest.getDistance());

            long fare = vehicleRequest.getApproxFareInCents() / 100;

            makeOfferBtn.setText("₹" + fare);
            requestView.setVisibility(View.VISIBLE);

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
            requestView.setVisibility(View.GONE);
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//            if(errorCode == ResponseCode.INVALID_CREDENTIALS) {
//                sb = Snackbar.make(constraintLayout, getString(R.string.sign_in_invalid_credentials_error), Snackbar.LENGTH_LONG);
//            } else {
//                sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
//            }
        }
    };
}
