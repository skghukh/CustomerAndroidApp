package com.rodafleets.rodadriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rodafleets.rodadriver.model.Driver;
import com.rodafleets.rodadriver.rest.ResponseCode;
import com.rodafleets.rodadriver.rest.RodaRestClient;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpBtn;

    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;

    private RadioGroup radioGroup;

    private TextView existingDriver;

    private String gender;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initComponents();
    }

    private void initComponents() {
        signUpBtn = (Button) findViewById(R.id.signUpBtn);

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        radioGroup = (RadioGroup) findViewById(R.id.genderGroup);

        existingDriver = (TextView) findViewById(R.id.existingDriver);

        radioGroup.setOnCheckedChangeListener(onGenderChange);
        gender = "Male"; //default

        Typeface poppinsRegular = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_REGULAR);
        Typeface sintonyBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_SINTONY_BOLD);

        signUpBtn.setTypeface(sintonyBold);
        firstName.setTypeface(poppinsRegular);
        lastName.setTypeface(poppinsRegular);
        phoneNumber.setTypeface(poppinsRegular);

        existingDriver.setTypeface(sintonyBold);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    public void signIn(View view) {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    private void startNextActivity(){
        this.startActivity(new Intent(this, SignUpVerificationActivity.class));
        finish();
    }

    public void onSignUpCLick(View view) {
        signUp();
    }

    private void signUp() {

        Boolean validated = true;
        String number = phoneNumber.getText().toString();
        String fName = firstName.getText().toString();
        String lName = lastName.getText().toString();

        if (number.equals("")) {
            validated = false;
            phoneNumber.setError(getString(R.string.sign_up_phone_number_required_error));
        }

        if (fName.equals("")) {
            validated = false;
            firstName.setError(getString(R.string.sign_up_first_name_required_error));
        }

        if (lName.equals("")) {
            validated = false;
            lastName.setError(getString(R.string.sign_up_last_name_required_error));
        }

        if (validated) {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.sign_up_saving));
            progressDialog.show();

            RodaRestClient.signUp(number, fName, lName, gender, responseHandler);
        }
    }

    private OnCheckedChangeListener onGenderChange = new OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
        // This will get the radiobutton that has changed in its check state
        RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
        // If the radiobutton that has changed in check state is now checked...
        if (checkedRadioButton.isChecked()) {
            gender = checkedRadioButton.getText().toString();
            Log.i(AppConstants.APP_NAME, "onCheckedChanged: " + gender);
        }
        }
    };

    private JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            try {
                Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
                Driver newDriver = new Driver(jsonResponseObject.getJSONObject("driver"));
                ApplicationSettings.setDriverId(SignUpActivity.this, newDriver.getId());
                ApplicationSettings.setOtpSessionId(SignUpActivity.this, jsonResponseObject.getString("sessionId"));
                ApplicationSettings.setDriver(SignUpActivity.this, jsonResponseObject.getJSONObject("driver"));
                progressDialog.dismiss();
                startNextActivity();
            } catch (JSONException e) {
                //handle error
                Log.e(AppConstants.APP_NAME, "jsonException = " + e.getMessage());
                Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_default_error), Toast.LENGTH_LONG).show();
            }

        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            try {
                Log.i(AppConstants.APP_NAME, "errorResponse = " + errorResponse.toString());
                int errorCode = errorResponse.getInt("errorCode");
                Log.i(AppConstants.APP_NAME, "errorCode = " + errorCode);
                switch (errorCode) {

                    case ResponseCode.DUPLICATE_ENTRY:
                        Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_phone_number_exists_error), Toast.LENGTH_LONG).show();
                        break;

                    case ResponseCode.SENDING_OTP_FAILED:
                        Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_sending_otp_error), Toast.LENGTH_LONG).show();
                        break;

                    case ResponseCode.INTERNAL_SERVER_ERROR:
                        Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_default_error), Toast.LENGTH_LONG).show();
                        break;

                    default:
                        Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_default_error), Toast.LENGTH_LONG).show();
                        break;
                }

            } catch (Exception e) {
                Log.e(AppConstants.APP_NAME, "api exception = " + e.getMessage());
                if ( e.getCause() instanceof ConnectTimeoutException ) {
                    Toast.makeText(SignUpActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_default_error), Toast.LENGTH_LONG).show();
                }
            }

            progressDialog.dismiss();
        }
    };

}
