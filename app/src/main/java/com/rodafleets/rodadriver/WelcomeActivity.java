package com.rodafleets.rodadriver;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initComponents();
    }

    private void initComponents(){
        Button signInBtn = (Button) findViewById(R.id.signInBtn);
        Button signUpBtn = (Button) findViewById(R.id.SignUpBtn);

        Typeface sintonyBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_SINTONY_BOLD);
        signInBtn.setTypeface(sintonyBold);
        signUpBtn.setTypeface(sintonyBold);
    }

    public void signIn(View view){
        Boolean loggedIn = ApplicationSettings.getLoggedIn(WelcomeActivity.this);
        if(loggedIn) {
//            startActivity(new Intent(this, VehicleRequestListActivity.class));
            startActivity(new Intent(this, VehicleRequestActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
    }

    public void signUp(View view){

        int driverId = ApplicationSettings.getDriverId(WelcomeActivity.this);
        Boolean verified = ApplicationSettings.getVerified(WelcomeActivity.this);
        Boolean vehicleInfoSaved = ApplicationSettings.getVehicleInfoSaved(WelcomeActivity.this);

        if(driverId == 0) {
            Log.e(AppConstants.APP_NAME, "driver id not found, starting signup activity");
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        } else if(!verified) {
            Log.e(AppConstants.APP_NAME, "driver info found but signup process not completed.");
            startActivity(new Intent(this, SignUpVerificationActivity.class));
            finish();
        } else if (!vehicleInfoSaved){
            Log.e(AppConstants.APP_NAME, "driver info found, navigating to vehicle info");
            startActivity(new Intent(this, VehicleDetailsActivity.class));
            finish();
        } else {
            Log.e(AppConstants.APP_NAME, "driver info found, navigating to vehicle info");
            startActivity(new Intent(this, DriverDocs.class));
            finish();
        }
    }
}
