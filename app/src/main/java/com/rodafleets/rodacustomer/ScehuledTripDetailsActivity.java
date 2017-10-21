package com.rodafleets.rodacustomer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

public class ScehuledTripDetailsActivity extends MapActivity {

    private String driverName;
    private String driverMob;
    private String vehicleRegNo;
    private String sourcePlace;
    private TextView driverNameTextView;
    private TextView vehicleRegistrationNo;
    private TextView pickupLocationValue;
    private TextView driverMobNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scehuled_trip_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       /* setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        Intent intent = getIntent();
        driverName = intent.getStringExtra("driverName");
        driverMob = intent.getStringExtra("driverMob");
        vehicleRegNo = intent.getStringExtra("vehicleRegId");
        sourcePlace = intent.getStringExtra("sourcePlace");
        initComponents();
        /*final Bundle extras = intent.getExtras();
        LatLng src = intent.getParcelableExtra("sourceLocation");*/

    }

    protected void initComponents() {
        super.initComponents();
        initMap();
        driverNameTextView = (TextView) findViewById(R.id.driverName);
        driverNameTextView.setText(driverName);
        vehicleRegistrationNo = (TextView) findViewById(R.id.vehicleRegistrationNo);
        vehicleRegistrationNo.setText(vehicleRegNo);
        pickupLocationValue = (TextView) findViewById(R.id.pickupLocationValue);
        pickupLocationValue.setText(sourcePlace);
        driverMobNo  = (TextView) findViewById(R.id.callDriverValue);
        driverMobNo.setText(driverMob);
    }

    public void callDriver(View view) {
        Intent myIntent = new Intent(Intent.ACTION_CALL);
        String phNum = "tel:" + driverMob;
        myIntent.setData(Uri.parse(phNum));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Not able to call due to permissions ",Toast.LENGTH_SHORT);
            return;
        }
        startActivity(myIntent);
    }
}
