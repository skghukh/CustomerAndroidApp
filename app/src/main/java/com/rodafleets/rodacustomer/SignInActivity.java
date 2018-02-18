package com.rodafleets.rodacustomer;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodacustomer.model.Customer;
import com.rodafleets.rodacustomer.rest.ResponseCode;
import com.rodafleets.rodacustomer.services.FirebaseReferenceService;
import com.rodafleets.rodacustomer.utils.AppConstants;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;
import com.rodafleets.rodacustomer.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "RD";

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

        phoneNumber =  findViewById(R.id.phoneNumber);
        password =  findViewById(R.id.password);

        forgotPasswordText =  findViewById(R.id.forgotPasswordText);
        newDriver =  findViewById(R.id.newDriver);

        constraintLayout = findViewById(R.id.constraintLayout);
        progressBar =  findViewById(R.id.loading);

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

            signInUsingEmailAndPassword(number, pwd);

           // RodaRestClient.login(number, pwd, token, signInResponseHandler);
        }
    }

    private void signInUsingEmailAndPassword(String number, String pwd) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword((number.trim() + "@roda.com").trim(), pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "User login is successfull for " + user.getUid() + " : " + user.getDisplayName() + " : " + user.getPhoneNumber());
                            //updateUI(user);
                            // ApplicationSettings.setDriverId(SignInActivity.this,user.getUid());
                            ApplicationSettings.setCustomerUId(SignInActivity.this, user.getUid());
                            ApplicationSettings.setCustomerName(SignInActivity.this, user.getDisplayName());
                            ApplicationSettings.setCustomerEid(SignInActivity.this, user.getEmail().split("\\@")[0]);
                            ApplicationSettings.setLoggedIn(SignInActivity.this, true);

                            String token = ApplicationSettings.getRegistrationId(SignInActivity.this);
                            if (!"".equalsIgnoreCase(token)) {
                                token = FirebaseInstanceId.getInstance().getToken();
                                ApplicationSettings.setRegistrationId(SignInActivity.this, token);
                                FirebaseReferenceService.updateCustomerToken(ApplicationSettings.getCustomerEid(SignInActivity.this).split("\\@")[0],token);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            startNextActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            //TODO Instead of Toast make a snack bar here.
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignInActivity.this, "Authentication failed."+task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Utils.enableWindowActivity(getWindow(), true);
                        }

                        // ...
                    }
                });
    }

    private JsonHttpResponseHandler signInResponseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            try {
                Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
                JSONObject driverJson = jsonResponseObject.getJSONObject("customer");
                Customer driver = new Customer(driverJson);
                ApplicationSettings.setCustomerId(SignInActivity.this, driver.getId());
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
