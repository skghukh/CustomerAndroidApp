package com.rodafleets.rodacustomer.services;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sverma4 on 12/28/17.
 */

public class Utils {

    private static FirebaseDatabase fbInstance;

    public static FirebaseDatabase getFBInstance() {
        if (fbInstance == null) {
            fbInstance = FirebaseDatabase.getInstance();
            fbInstance.setPersistenceEnabled(true);
        }
        return fbInstance;
    }
}
