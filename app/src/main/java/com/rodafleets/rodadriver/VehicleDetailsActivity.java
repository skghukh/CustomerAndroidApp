package com.rodafleets.rodadriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rodafleets.rodadriver.custom.VehicleTypeSpinnerAdapter;
import com.rodafleets.rodadriver.model.Driver;
import com.rodafleets.rodadriver.model.VehicleType;
import com.rodafleets.rodadriver.rest.ResponseCode;
import com.rodafleets.rodadriver.rest.RodaRestClient;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class VehicleDetailsActivity extends AppCompatActivity {

    private EditText vehicleNumber;
    private EditText ownerFirstName;
    private EditText ownerLastName;
    private EditText ownerPhoneNumber;

    private CheckBox isOwner;

    private TextView ownersDetailsText;
    private TextView existingDriver;

    private Button saveVehicleDetailsBtn;

    private Spinner vehicleTypesSpinner;

    private ArrayList<VehicleType> vehicleTypes;

    Typeface poppinsRegular;
    Typeface sintonyBold;

    private int selectedVehicleTypeId = 0;

    private ProgressBar progressBar;

    private TextInputLayout vehicleNumberLayout;
    private TextInputLayout ownerFirstNameLayout;
    private TextInputLayout ownerLastNameLayout;
    private TextInputLayout ownerPhoneNumberLayout;

    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        initComponents();
    }

    private void initComponents() {

        poppinsRegular = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        sintonyBold = Typeface.createFromAsset(getAssets(), "fonts/Sintony-Bold.otf");

        vehicleTypesSpinner = (Spinner) findViewById(R.id.vehicleTypeSpinner);

        vehicleNumber = (EditText) findViewById(R.id.vehicleNumber);
        ownerFirstName = (EditText) findViewById(R.id.ownerFirstName);
        ownerLastName = (EditText) findViewById(R.id.ownerLastName);
        ownerPhoneNumber = (EditText) findViewById(R.id.ownerPhoneNumber);

        isOwner = (CheckBox) findViewById(R.id.isOwner);
        ownersDetailsText = (TextView) findViewById(R.id.ownersDetailsText);
        existingDriver = (TextView) findViewById(R.id.existingDriver);

        saveVehicleDetailsBtn = (Button) findViewById(R.id.saveVehicleDetailsBtn);

        vehicleNumberLayout = (TextInputLayout) findViewById(R.id.vehicleNumberLayout);
        ownerFirstNameLayout = (TextInputLayout) findViewById(R.id.ownerFirstNameLayout);
        ownerLastNameLayout = (TextInputLayout) findViewById(R.id.ownerLastNameLayout);
        ownerPhoneNumberLayout = (TextInputLayout) findViewById(R.id.ownerPhoneNumberLayout);

        saveVehicleDetailsBtn.setTypeface(sintonyBold);

        vehicleNumber.setTypeface(poppinsRegular);
        ownerFirstName.setTypeface(poppinsRegular);
        ownerLastName.setTypeface(poppinsRegular);
        ownerPhoneNumber.setTypeface(poppinsRegular);

        isOwner.setTypeface(poppinsRegular);

        ownersDetailsText.setTypeface(sintonyBold);
        existingDriver.setTypeface(sintonyBold);

        // vehicle type Spinner
        vehicleTypesSpinner = (Spinner) findViewById(R.id.vehicleTypeSpinner);
        vehicleTypesSpinner.setOnItemSelectedListener(vehicleTypeSelectedListener);

        isOwner.setOnCheckedChangeListener(onIsOwnerClicked);

        progressBar = (ProgressBar) findViewById(R.id.loading);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        getVehicleTypes();
    }

    public void onSaveVehicleDetailsBtn(View view) {
        Boolean validated = true;
        String vNumber = vehicleNumber.getText().toString();
        String fName = ownerFirstName.getText().toString();
        String lName = ownerLastName.getText().toString();
        String pNumber = ownerPhoneNumber.getText().toString();

        if (vNumber.equals("")) {
            validated = false;
            vehicleNumberLayout.setError(getString(R.string.sign_up_vehicle_number_required_error));
        } else {
            vehicleNumberLayout.setErrorEnabled(false);
        }

        if(!isOwner.isChecked()) {
            if (fName.equals("")) {
                validated = false;
                ownerFirstNameLayout.setError(getString(R.string.sign_up_owner_first_name_required_error));
            } else {
                ownerFirstNameLayout.setErrorEnabled(false);
            }

            if (lName.equals("")) {
                validated = false;
                ownerLastNameLayout.setError(getString(R.string.sign_up_owner_last_name_required_error));
            } else {
                ownerLastNameLayout.setErrorEnabled(false);
            }

            if (pNumber.equals("")) {
                validated = false;
                ownerPhoneNumberLayout.setError(getString(R.string.sign_up_owner_phone_number_required_error));
            } else {
                ownerPhoneNumberLayout.setErrorEnabled(false);
            }
        }


        if (validated) {
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            int driverId = ApplicationSettings.getDriverId(VehicleDetailsActivity.this);
            if(isOwner.isChecked()) {
                RodaRestClient.saveVehicleInfo(driverId, vNumber, selectedVehicleTypeId, saveVehicleInfoResponseHandler);
            } else{
                RodaRestClient.saveVehicleInfo(driverId, vNumber, selectedVehicleTypeId, fName, lName, pNumber, saveVehicleInfoResponseHandler);
            }
        }
    }

    public void signIn(View view) {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    private void startNextActivity() {
        this.startActivity(new Intent(this, DriverDocs.class));
        finish();
    }

    private void getVehicleTypes() {
        RodaRestClient.getVehicleTypes(getVehicleTypesResponseHanlder);
    }

    private AdapterView.OnItemSelectedListener vehicleTypeSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            VehicleType selected = (VehicleType) parentView.getItemAtPosition(position);
            selectedVehicleTypeId = (int) selected.getId();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    };

    private OnCheckedChangeListener onIsOwnerClicked = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            //clear info
            ownerFirstName.setEnabled(true);
            ownerLastName.setEnabled(true);
            ownerPhoneNumber.setEnabled(true);
            try {
                JSONObject jsonObject = new JSONObject(ApplicationSettings.getDriver(VehicleDetailsActivity.this));
                Driver driver = new Driver(jsonObject);

                if (isChecked){
                    ownerFirstName.setText(driver.getFirstname());
                    ownerLastName.setText(driver.getLastname());
                    ownerPhoneNumber.setText(driver.getPhoneNumber());

                    ownerFirstName.setEnabled(false);
                    ownerLastName.setEnabled(false);
                    ownerPhoneNumber.setEnabled(false);

                    ownerFirstNameLayout.setErrorEnabled(false);
                    ownerLastNameLayout.setErrorEnabled(false);
                    ownerPhoneNumberLayout.setErrorEnabled(false);
                } else {
                    //clear info
                    ownerFirstName.setText("");
                    ownerLastName.setText("");
                    ownerPhoneNumber.setText("");

                    ownerFirstNameLayout.setErrorEnabled(true);
                    ownerLastNameLayout.setErrorEnabled(true);
                    ownerPhoneNumberLayout.setErrorEnabled(true);
                }

            } catch (Exception e) {
                //handle error
                Log.e(AppConstants.APP_NAME, "jsonException = " + e.getMessage());
            }
        }
    };

    private JsonHttpResponseHandler getVehicleTypesResponseHanlder = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
            Log.i(AppConstants.APP_NAME, "response = " + responseArray.toString());

            vehicleTypes = new ArrayList<VehicleType>();
            for (int i=0; i<responseArray.length(); i++) {
                try {
                    VehicleType vehicleType = new VehicleType(responseArray.getJSONObject(i));
                    vehicleTypes.add(vehicleType);
                } catch (JSONException e) {

                }
            }

            VehicleTypeSpinnerAdapter adapter = new VehicleTypeSpinnerAdapter(VehicleDetailsActivity.this, R.layout.vehicle_type_spinner_view, vehicleTypes, poppinsRegular);
            adapter.setDropDownViewResource(R.layout.vehicle_type_spinner_dropdown_view);
            vehicleTypesSpinner.setAdapter(adapter);
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

        }
    };

    private JsonHttpResponseHandler saveVehicleInfoResponseHandler = new JsonHttpResponseHandler() {
        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
            ApplicationSettings.setVehicleInfoSaved(VehicleDetailsActivity.this, true);
            startNextActivity();
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Snackbar sb;
            try {
                Log.i(AppConstants.APP_NAME, "errorResponse = " + errorResponse.toString());
                int errorCode = errorResponse.getInt("errorCode");
                Log.i(AppConstants.APP_NAME, "errorCode = " + errorCode);

                if(errorCode == ResponseCode.DUPLICATE_ENTRY) {
                    sb = Snackbar.make(constraintLayout, getString(R.string.sign_up_vehicle_number_already_added_error), Snackbar.LENGTH_LONG);
                } else {
                    sb = Snackbar.make(constraintLayout, getString(R.string.sign_up_default_error), Snackbar.LENGTH_LONG);
                }
            } catch (Exception e) {
                Log.e(AppConstants.APP_NAME, "api exception = " + e.getMessage());
                if ( e.getCause() instanceof ConnectTimeoutException) {
                    sb = Snackbar.make(constraintLayout, getString(R.string.internet_error), Snackbar.LENGTH_LONG);
                } else {
                    sb = Snackbar.make(constraintLayout, getString(R.string.sign_up_default_error), Snackbar.LENGTH_LONG);
                }
            }
            sb.show();
            progressBar.setVisibility(View.GONE);
        }
    };
}

