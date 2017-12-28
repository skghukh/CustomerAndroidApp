package com.rodafleets.rodacustomer.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rodafleets.rodacustomer.database.FavouriteReceiver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sverma4 on 12/25/17.
 */

public class FirebaseReferenceService {

    private static ExecutorService pool = Executors.newFixedThreadPool(1);

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
        pool.submit(new Runnable() {
            @Override
            public void run() {
                voidTask.getResult();
            }
        });
    }

    public static DatabaseReference firebaseCustomerFavouriteReference(String custId){
         FirebaseDatabase fbInstance = Utils.getFBInstance();
         DatabaseReference favouriteRef = fbInstance.getReference("favourites/" + custId);
         return favouriteRef;
    }

}
