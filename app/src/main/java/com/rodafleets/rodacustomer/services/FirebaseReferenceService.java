package com.rodafleets.rodacustomer.services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rodafleets.rodacustomer.database.FavouriteReceiver;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;
import com.rodafleets.rodacustomer.utils.Customer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sverma4 on 12/25/17.
 */

public class FirebaseReferenceService {

    public static final String TAG = "RC";

    private static ExecutorService firebaseOperationThreadPool = Executors.newFixedThreadPool(3);


    final static String customerPath = "customers/";

    public static void updateCustomerToken(String customerId, String token) {
        DatabaseReference driverTokenReference = Utils.getFBInstance().getReference(customerPath + "/" + customerId + "/atoken");
        driverTokenReference.setValue(token);
    }

    public static void addCustomer(String phoneNumber, Customer customer) {
        DatabaseReference customerReference = Utils.getFBInstance().getReference(customerPath + "/" + phoneNumber);
        final Task<Void> addCustomer = customerReference.setValue(customer);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                addCustomer.getResult();
            }
        });

    }

    public static DatabaseReference getCustomerReference(String custId) {
        return Utils.getFBInstance().getReference(customerPath + "/" + custId);
    }

    public static void addCustomerCurrentTrip(String custId, String tripId) {
        final Task<Void> updateCurrentTrip = Utils.getFBInstance().getReference(customerPath + "/" + custId).child("currentTrip").setValue(tripId);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                updateCurrentTrip.getResult();
            }
        });
    }

    public static DatabaseReference getTripReference(String custId, String tripId) {
        DatabaseReference tripRef = Utils.getFBInstance().getReference("vehicleRequests/" + custId + "/" + tripId);
        return tripRef;
    }

    public static DatabaseReference getTripResponseReference(String custId, String tripId) {
        DatabaseReference tripResponseRef = Utils.getFBInstance().getReference("vehicleRequests/" + custId + "/" + tripId + "/responses");
        return tripResponseRef;
    }

    public static DatabaseReference getFavouriteReference(String custId) {
        return Utils.getFBInstance().getReference("favourites/" + custId);
    }

    public static void addFavourite(String custId, FavouriteReceiver favouriteReceiver) {
        final FirebaseDatabase fbInstance = Utils.getFBInstance();
        final DatabaseReference favouriteRef = fbInstance.getReference("favourites/" + custId);
        final DatabaseReference pushRef = favouriteRef.push();
        final Task<Void> voidTask = pushRef.setValue(favouriteReceiver);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                voidTask.getResult();
            }
        });
    }

    public static DatabaseReference firebaseCustomerFavouriteReference(String custId) {
        FirebaseDatabase fbInstance = Utils.getFBInstance();
        DatabaseReference favouriteRef = fbInstance.getReference("favourites/" + custId);
        return favouriteRef;
    }

    public static DatabaseReference getRequestHistoryReference(String custId) {
        return Utils.getFBInstance().getReference("vehicleRequests/" + custId);
    }

    public static Query getLastTripReferece(String custId) {
        final Query lastTripQuery = Utils.getFBInstance().getReference("vehicleRequests/" + custId).orderByChild("timestamp").limitToLast(1);
        lastTripQuery.keepSynced(true);
        return lastTripQuery;
    }

    public static DatabaseReference getCustomerCurrentTripIdReference(String custId) {
        return Utils.getFBInstance().getReference("customers/" + custId).child("currentTrip");
    }

    public static void expireCustomerCurrentTrip(final String custId, final String tripId) {
        final Task<Void> removeCurrentTrip = Utils.getFBInstance().getReference("customers/" + custId).child("currentTrip").removeValue();
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Removed current trip " + tripId);
                removeCurrentTrip.getResult();
            }
        });
        final Task<Void> expireTrip = getTripReference(custId, tripId).child("status").setValue("expired");
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                expireTrip.getResult();
                Log.d(TAG, "Expired trip : " + tripId);
            }
        });
    }


    public static DatabaseReference getLocationReference() {
        return Utils.getFBInstance().getReference("locations");
    }

    public static DatabaseReference getLocationUpdatesForDriver(String driverId) {
        return Utils.getFBInstance().getReference("locations" + "/" + driverId);
    }

    public void signOut() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

    }


    public static void cancelCurrentTrip(final String custId, final String tripId, final String driverId) {
        final Task<Void> removeCurrentTrip = Utils.getFBInstance().getReference("customers/" + custId).child("currentTrip").removeValue();
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Removed current trip " + tripId);
                removeCurrentTrip.getResult();
            }
        });
        final Task<Void> cancelTrip = getTripReference(custId, tripId).child("status").setValue("cancelled_" + driverId);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                cancelTrip.getResult();
                Log.d(TAG, "Expired trip : " + tripId);
            }
        });
    }

    public static void logout(Context context){
        ApplicationSettings.setLoggedIn(context,false);
        FirebaseAuth.getInstance().signOut();

    }
}
