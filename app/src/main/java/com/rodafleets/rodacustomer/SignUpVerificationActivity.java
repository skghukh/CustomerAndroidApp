package com.rodafleets.rodacustomer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodacustomer.custom.OtpTextWatcher;
import com.rodafleets.rodacustomer.model.Customer;
import com.rodafleets.rodacustomer.rest.ResponseCode;
import com.rodafleets.rodacustomer.rest.RodaRestClient;
import com.rodafleets.rodacustomer.services.FirebaseReferenceService;
import com.rodafleets.rodacustomer.utils.AppConstants;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class SignUpVerificationActivity extends AppCompatActivity {

    public static final String TAG = "RC";

    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText password;

    private EditText otpDigit1;
    private EditText otpDigit2;
    private EditText otpDigit3;
    private EditText otpDigit4;

    private RadioButton radioMale;
    private RadioButton radioFemale;

    private TextView otpText;
    private TextView otpMessage;
    private TextView existingDriver;

    private ProgressDialog progressDialog;

    private String mSessionId;
    private String mFirstName;
    private String mLastName;
    private String mPhoneNumber;

    private String  gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_verification);
        initComponents();
        Intent intent = getIntent();
        getValuesFromIntent(intent);
    }

    private void getValuesFromIntent(Intent intent) {
        mSessionId = intent.getStringExtra("sessionId");
        mFirstName = intent.getStringExtra("firstName");
        mLastName = intent.getStringExtra("lastName");
        mPhoneNumber = intent.getStringExtra("phoneNumber");
        firstName.setText(mFirstName);
        lastName.setText(mLastName);
        phoneNumber.setText(mPhoneNumber);
        gender = intent.getStringExtra("gender");
        if (gender.equalsIgnoreCase("male")) {
            radioMale.setSelected(true);
            radioMale.setButtonDrawable(R.drawable.radio_selected);
            radioFemale.setButtonDrawable(R.drawable.radio);
        } else {
            radioFemale.setSelected(true);
            radioFemale.setButtonDrawable(R.drawable.radio_selected);
            radioMale.setButtonDrawable(R.drawable.radio);
        }
    }

    private void initComponents() {

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);

        otpDigit1 = (EditText) findViewById(R.id.otpDigit1);
        otpDigit2 = (EditText) findViewById(R.id.otpDigit2);
        otpDigit3 = (EditText) findViewById(R.id.otpDigit3);
        otpDigit4 = (EditText) findViewById(R.id.otpDigit4);

        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);

        otpText = (TextView) findViewById(R.id.otpText);
        otpMessage = (TextView) findViewById(R.id.otpMessage);

        existingDriver = (TextView) findViewById(R.id.existingDriver);

        Typeface poppinsRegular = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface sintonyBold = Typeface.createFromAsset(getAssets(), "fonts/Sintony-Bold.otf");

        firstName.setTypeface(poppinsRegular);
        lastName.setTypeface(poppinsRegular);
        phoneNumber.setTypeface(poppinsRegular);
        password.setTypeface(poppinsRegular);

        otpText.setTypeface(poppinsRegular);
        otpMessage.setTypeface(poppinsRegular);

        existingDriver.setTypeface(sintonyBold);

        otpDigit1.setTypeface(poppinsRegular);
        otpDigit2.setTypeface(poppinsRegular);
        otpDigit3.setTypeface(poppinsRegular);
        otpDigit4.setTypeface(poppinsRegular);

        //vehicleDetailsBtn.setTypeface(sintonyBold);

        otpDigit1.addTextChangedListener(new OtpTextWatcher(otpDigit1, otpDigit2));
        otpDigit2.addTextChangedListener(new OtpTextWatcher(otpDigit2, otpDigit3));
        otpDigit3.addTextChangedListener(new OtpTextWatcher(otpDigit3, otpDigit4));
        //otpDigit4.addTextChangedListener(new OtpTextWatcher(otpDigit1, otpDigit2));

        try {
            JSONObject jsonObject = new JSONObject(ApplicationSettings.getDriver(SignUpVerificationActivity.this));
            Customer driver = new Customer(jsonObject);
            firstName.setText(driver.getFirstName());
            lastName.setText(driver.getLastName());
            phoneNumber.setText(driver.getPhoneNumber());

            String gender = driver.getGender();

            if (gender.equalsIgnoreCase("male")) {
                radioMale.setSelected(true);
                radioMale.setButtonDrawable(R.drawable.radio_selected);
                radioFemale.setButtonDrawable(R.drawable.radio);
            } else {
                radioFemale.setSelected(true);
                radioFemale.setButtonDrawable(R.drawable.radio_selected);
                radioMale.setButtonDrawable(R.drawable.radio);
            }

        } catch (Exception e) {
            //handle error
            Log.e(AppConstants.APP_NAME, "jsonException = " + e.getMessage());
        }

        //disable editing other fields
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        phoneNumber.setEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    public void signIn(View view) {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    public void onVerifyOtpClick(View view) {
        verifyOtp();
    }

    private void startNextActivity() {
        //this.startActivity(new Intent(this, VehicleDetailsActivity.class));
        this.startActivity(new Intent(this, SignInActivity.class));
        finish();
    }


    private void verifyOtp() {
        Boolean validated = true;
        String pwd = password.getText().toString();
        String otp = otpDigit1.getText().toString()
                + otpDigit2.getText().toString()
                + otpDigit3.getText().toString()
                + otpDigit4.getText().toString();

        if (pwd.equals("")) {
            validated = false;
            password.setError(getString(R.string.sign_up_password_required_error));
        }

        if (otp.length() < 4) {
            validated = false;
            //show toast?
            otpDigit1.setError(getString(R.string.sign_up_otp_required_error));
            Log.e(AppConstants.APP_NAME, "otp error");
        }

        if (validated) {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.sign_up_saving));
            progressDialog.show();
            RodaRestClient.verifyOTP(mSessionId, otp, mOtpVerificationHandler);
            //RodaRestClient.saveDriver(driverId, pwd, otp, sessionId, responseHandler);
        }
    }

    private JsonHttpResponseHandler mOtpVerificationHandler = new JsonHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            try {
                final boolean isVerified = jsonResponseObject.getBoolean("isVerified");
                if (isVerified) {
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    final String myUserId = (mPhoneNumber.trim() + "@roda.com").trim();
                    String myPassword = password.getText().toString().trim();
                    mAuth.createUserWithEmailAndPassword(myUserId, myPassword)
                            .addOnCompleteListener(SignUpVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Log.i(TAG, "createUserWithEmail:success for user Id " + myUserId + " server UId is : " + user.getUid());
                                        Toast.makeText(SignUpVerificationActivity.this, "Authentication successfull.",
                                                Toast.LENGTH_SHORT).show();
                                        //Update userProfile
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(mFirstName + " " + mLastName)
                                                .build();
                                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(SignUpVerificationActivity.this, "UserProfile updated.",
                                                        Toast.LENGTH_SHORT).show();
                                                //TODO isBusiness should be captured from Screen
                                                FirebaseReferenceService.addCustomer(mPhoneNumber, new com.rodafleets.rodacustomer.utils.Customer(mPhoneNumber, gender, false));
                                                ApplicationSettings.setVerified(SignUpVerificationActivity.this, true);
                                                progressDialog.dismiss();
                                                startNextActivity();
                                            }
                                        });
                                        progressDialog.dismiss();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                            Log.d(TAG, "This email ID already used by someone else");
                                            Toast.makeText(SignUpVerificationActivity.this, "Authentication failed. Number already used ",
                                                    Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(SignUpVerificationActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                } else {
                    //Number verification failed.
                    progressDialog.dismiss();
                    Toast.makeText(SignUpVerificationActivity.this, "Authentication failed.  ",
                            Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                Toast.makeText(SignUpVerificationActivity.this, "Authentication failed.  ",
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "Exception while signup " + e);
            }

        }

        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            //Instead of System make it snack bar
            System.out.println("Error in verification " + headers);
        }

    };

    private JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            ApplicationSettings.setVerified(SignUpVerificationActivity.this, true);
            progressDialog.dismiss();
            startNextActivity();
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            try {
                Log.i(AppConstants.APP_NAME, "errorResponse = " + errorResponse.toString());
                int errorCode = errorResponse.getInt("errorCode");
                Log.i(AppConstants.APP_NAME, "errorCode = " + errorCode);
                switch (errorCode) {

                    case ResponseCode.SESSION_ID_REQUIRED_FOR_OTP_VERIFICATION:
                        Toast.makeText(SignUpVerificationActivity.this, getString(R.string.sign_up_session_id_required_error), Toast.LENGTH_LONG).show();
                        break;

                    case ResponseCode.INVALID_OTP:
                        Toast.makeText(SignUpVerificationActivity.this, getString(R.string.sign_up_invalid_otp_error), Toast.LENGTH_LONG).show();
                        break;

                    case ResponseCode.INTERNAL_SERVER_ERROR:
                        Toast.makeText(SignUpVerificationActivity.this, getString(R.string.sign_up_default_error), Toast.LENGTH_LONG).show();
                        break;

                    default:
                        Toast.makeText(SignUpVerificationActivity.this, getString(R.string.sign_up_default_error), Toast.LENGTH_LONG).show();
                        break;
                }

            } catch (Exception e) {
                Log.e(AppConstants.APP_NAME, "api exception = " + e.getMessage());
                if (e.getCause() instanceof ConnectTimeoutException) {
                    Toast.makeText(SignUpVerificationActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpVerificationActivity.this, getString(R.string.sign_up_default_error), Toast.LENGTH_LONG).show();
                }
            }

            progressDialog.dismiss();
        }
    };


}
