package com.rodafleets.rodacustomer;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.rodafleets.rodacustomer.utils.AppConstants;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

public class ParentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Typeface poppinsRegular;
    Typeface poppinsMedium;
    Typeface poppinsLight;
    Typeface poppinsBold;
    Typeface poppinsSemiBold;

    Typeface sintonyRegular;
    Typeface sintonyBold;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar toolbar;
    private Switch paySwitch;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    protected void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeSideMenu();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(Gravity.LEFT);
        if (!isDrawerOpen) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    public void initializeSideMenu() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            Log.i("MENU", "DrawerLayout is null");
        }
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.open,  /* "open drawer" description */
                R.string.close  /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();

        ActionBar actionBar = this.getSupportActionBar();

        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionBarPurple)));
        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        checkPaymentListener();
    }

    private void checkPaymentListener() {
        paySwitch = findViewById(R.id.paySwitch);
        if (null != paySwitch) {
            paySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(ParentActivity.this, "Cache pay option ", Toast.LENGTH_SHORT).show();
                        ApplicationSettings.setPayOption(isChecked, ParentActivity.this);
                    } else {
                        Toast.makeText(ParentActivity.this, "Wallet pay option ", Toast.LENGTH_SHORT).show();
                        ApplicationSettings.setPayOption(isChecked, ParentActivity.this);
                    }
                }
            });
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.menu_trip: {
                Log.i("Menu", "trip clicked");
                break;
            }

            case R.id.menu_payment: {
                //do somthing
                break;
            }

            case R.id.menu_settings: {
                //do somthing
                break;
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    public void loadFonts() {
        poppinsRegular = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_REGULAR);
        poppinsMedium = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_MEDIUM);
        poppinsLight = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_LIGHT);
        poppinsBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_BOLD);
        poppinsSemiBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_SEMI_BOLD);

        sintonyRegular = Typeface.createFromAsset(getAssets(), AppConstants.FONT_SINTONY_REGULAR);
        sintonyBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_SINTONY_BOLD);
    }

    public void showTrips(View view) {
        Intent intent = new Intent(this, TripHistory.class);
        startActivity(intent);
    }

    public void showVehicleRates(View view) {
        Intent intent = new Intent(this, VehicleAndRates.class);
        startActivity(intent);
    }

    public void inviteFriends(View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey There, I am using Roda App. Best application for Logistic, You can download from https://play.google.com/store/apps/details?id=com.awem.cradleofempires.andr";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Roda subject");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void showSettings(View view) {
        //TODO
        //Show Settings
        Toast.makeText(this, "Settings: Not implmented yet ", Toast.LENGTH_SHORT).show();
    }

    public void showFavourites(View view) {
        Intent intent = new Intent(this, Favourites.class);
        startActivity(intent);
    }
}
