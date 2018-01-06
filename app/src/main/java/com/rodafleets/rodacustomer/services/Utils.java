package com.rodafleets.rodacustomer.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rodafleets.rodacustomer.MapActivity;
import com.rodafleets.rodacustomer.utils.Constants;

/**
 * Created by sverma4 on 12/28/17.
 */

public class Utils {

    private static FirebaseDatabase fbInstance;
    private static double offset;

    public static FirebaseDatabase getFBInstance() {
        if (fbInstance == null) {
            fbInstance = FirebaseDatabase.getInstance();
            fbInstance.setPersistenceEnabled(true);

        }
        return fbInstance;
    }

    public static Double getCurrentTime() {
        return System.currentTimeMillis() + offset;
    }

    private void test() {
        DatabaseReference offsetRef = Utils.getFBInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double offset = snapshot.getValue(Double.class);
                double estimatedServerTimeMs = System.currentTimeMillis() + offset;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

    }


    public static void checkLocationPermission(final Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(activity)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please tick_green to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        Constants.MY_PERMISSIONS_REQUEST_LOCATION);

                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }
}
