package com.rodafleets.rodacustomer;

import android.app.Application;

import com.rodafleets.rodacustomer.model.VehicleRequest;
import com.rodafleets.rodacustomer.services.NotificationService;

import java.util.ArrayList;

public class RodaDriverApplication extends Application {

    public static NotificationService vehicleRequestService;

    public static ArrayList<VehicleRequest>  vehicleRequests = new ArrayList();
}
