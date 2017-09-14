package com.rodafleets.rodadriver;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Spinner languageSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initComponents();
    }

    private void initComponents(){
        // Welcome Text Font
        welcomeText = (TextView) findViewById(R.id.welcomeText);
        Typeface sintonyBold = Typeface.createFromAsset(getAssets(), "fonts/Sintony-Bold.otf");
        welcomeText.setTypeface(sintonyBold);

        // Language Spinner
        languageSpinner = (Spinner)findViewById(R.id.languageSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_dropdown, R.layout.language_spinner_view);
        adapter.setDropDownViewResource(R.layout.language_spinner_dropdown_view);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(languageSelectedListener);

        String language = ApplicationSettings.getAppLanguage(this);
        if(!language.equals("")) {
            checkAppLocale();
        }
    }

    private AdapterView.OnItemSelectedListener languageSelectedListener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            Log.i(AppConstants.APP_NAME, "on change called");
            String appLanguage;

            switch (position){
                case 1:
                    appLanguage = "en";
                    break;
                case 2:
                    appLanguage = "hi";
                    break;
                default:
                    appLanguage = "";
            }

            ApplicationSettings.setAppLanguage(SplashScreenActivity.this, appLanguage);
            if(!appLanguage.equals("")) {
                setLocale(appLanguage);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    };

    private void startNextActivity(){
        Boolean loggedIn = ApplicationSettings.getLoggedIn(SplashScreenActivity.this);
        if(loggedIn) {
//            startActivity(new Intent(this, VehicleRequestListActivity.class));
            startActivity(new Intent(this, VehicleRequestActivity.class));
            finish();
        } else {
            this.startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
    }

    public void setLocale(String languageCode) {
        setLocale(new Locale(languageCode));
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void setLocale(Locale myLocale) {
        Configuration config = new Configuration();
        //TODO: fix deprecation issue for locale
        config.locale = myLocale;
//        config.setLocale(myLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    public Locale getAppLocale() {
        if(ApplicationSettings.getAppLanguage(this).equals("")) {
            return Locale.getDefault();
        } else {
            return new Locale(ApplicationSettings.getAppLanguage(this));
        }
    }

    public void checkAppLocale() {
        setLocale(getAppLocale());
        startNextActivity();
    }
}