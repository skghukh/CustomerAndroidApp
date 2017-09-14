package com.rodafleets.rodadriver;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodadriver.model.Driver;
import com.rodafleets.rodadriver.rest.ResponseCode;
import com.rodafleets.rodadriver.rest.RodaRestClient;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;
import com.rodafleets.rodadriver.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class SignInActivity extends AppCompatActivity {

    TextView welcomeText;
    Button signInBtn;

    EditText phoneNumber;
    EditText password;

    TextView forgotPasswordText;
    TextView newDriver;

    ConstraintLayout constraintLayout;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initComponents();
    }

    private void initComponents(){
        welcomeText = (TextView) findViewById(R.id.welcomeText);
        signInBtn = (Button) findViewById(R.id.signInBtn);

        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);

        forgotPasswordText = (TextView) findViewById(R.id.forgotPasswordText);
        newDriver = (TextView) findViewById(R.id.newDriver);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        progressBar = (ProgressBar) findViewById(R.id.loading);

        Typeface poppinsSemibold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface poppinsRegular = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface sintonyBold = Typeface.createFromAsset(getAssets(), "fonts/Sintony-Bold.otf");

        welcomeText.setTypeface(poppinsSemibold);

        signInBtn.setTypeface(sintonyBold);

        phoneNumber.setTypeface(poppinsRegular);
        password.setTypeface(poppinsRegular);
        forgotPasswordText.setTypeface(poppinsRegular);

        newDriver.setTypeface(sintonyBold);
    }

    public void onSignUpClick(View view){
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    public void onSignInBtnClick(View view) {
        signIn();
    }

    private void startNextActivity() {
        startActivity(new Intent(this, VehicleRequestActivity.class));
        finish();
    }

    private void signIn() {

        Boolean validated = true;
        String number = phoneNumber.getText().toString();
        String pwd = password.getText().toString();

        if (number.equals("")) {
            validated = false;
            phoneNumber.setError(getString(R.string.sign_in_phone_number_required_error));
        }

        if (pwd.equals("")) {
            validated = false;
            password.setError(getString(R.string.sign_in_password_required_error));
        }

        if (validated) {
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            Utils.enableWindowActivity(getWindow(), false);
            String token = ApplicationSettings.getRegistrationId(this);
            if(token.equals("")) {
                token = FirebaseInstanceId.getInstance().getToken();
                ApplicationSettings.setRegistrationId(this, token);
            }

            RodaRestClient.login(number, pwd, token, signInResponseHandler);
        }
    }

    private JsonHttpResponseHandler signInResponseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            try {
                Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
                JSONObject driverJson = jsonResponseObject.getJSONObject("driver");
                Driver driver = new Driver(driverJson);
                ApplicationSettings.setDriverId(SignInActivity.this, driver.getId());
                ApplicationSettings.setDriver(SignInActivity.this, driverJson);
                ApplicationSettings.setLoggedIn(SignInActivity.this, true);
                startNextActivity();
            } catch (JSONException e) {
                //handle error
                Log.e(AppConstants.APP_NAME, "jsonException = " + e.getMessage());
            }
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Snackbar sb;
            try {
                Log.i(AppConstants.APP_NAME, "errorResponse = " + errorResponse.toString());
                int errorCode = errorResponse.getInt("errorCode");
                Log.i(AppConstants.APP_NAME, "errorCode = " + errorCode);
                if(errorCode == ResponseCode.INVALID_CREDENTIALS) {
                    sb = Snackbar.make(constraintLayout, getString(R.string.sign_in_invalid_credentials_error), Snackbar.LENGTH_LONG);
                } else {
                    sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
                }
            } catch (Exception e) {
                Log.e(AppConstants.APP_NAME, "api exception = " + e.getMessage());
                if ( e.getCause() instanceof ConnectTimeoutException) {
                    sb = Snackbar.make(constraintLayout, getString(R.string.internet_error), Snackbar.LENGTH_LONG);
                } else {
                    sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
                }
            }
            sb.show();
            progressBar.setVisibility(View.GONE);
            Utils.enableWindowActivity(getWindow(), true);
        }
    };
}
