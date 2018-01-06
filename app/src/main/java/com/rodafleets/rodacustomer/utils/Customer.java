package com.rodafleets.rodacustomer.utils;

import com.rodafleets.rodacustomer.model.VehicleRequest;

/**
 * Created by sverma4 on 12/30/17.
 */

public class Customer {
    private String customerName;
    private String atoken;
    private String currentTripId;
    private float customerRating;
    private String gender;
    private boolean isBusiness;
    private CompanyDetails compDetails;
    private String currentTrip;

    public Customer() {
        //default constructor
    }

    public Customer(String name, String gender, boolean isBusiness) {
        this.customerName = name;
        this.gender = gender;
        this.isBusiness = isBusiness;
        this.customerRating = 5;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAtoken() {
        return atoken;
    }

    public void setAtoken(String atoken) {
        this.atoken = atoken;
    }

    public String getCurrentTripId() {
        return currentTripId;
    }

    public void setCurrentTripId(String currentTripId) {
        this.currentTripId = currentTripId;
    }

    public float getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(float customerRating) {
        this.customerRating = customerRating;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isBusiness() {
        return isBusiness;
    }

    public void setBusiness(boolean business) {
        isBusiness = business;
    }

    public CompanyDetails getCompDetails() {
        return compDetails;
    }

    public void setCompDetails(CompanyDetails compDetails) {
        this.compDetails = compDetails;
    }

    public String getCurrentTrip() {
        return currentTrip;
    }

    public void setCurrentTrip(String currentTrip) {
        this.currentTrip = currentTrip;
    }
}
