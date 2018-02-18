package com.rodafleets.rodacustomer.model;

/**
 * Created by sverma4 on 1/6/18.
 */

public class FBVehicleRequestResponse {
    private String distance;
    private String offeredFare;
    private String rating;
    private String name;
    private String vehicleId;
    private String vehicleType;
    private String driverId;
    private String tripId;

    public String getDistance() {
        return null == distance ? "NA" : distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getOfferedFare() {
        return offeredFare;
    }

    public void setOfferedFare(String offeredFare) {
        this.offeredFare = offeredFare;
    }


    public String getRating() {
        return rating == null ? "NA" : rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getName() {
        return null == name ? "NA" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVehicleId() {
        return null == vehicleId ? "NA" : vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleType() {
        return null == vehicleType ? "NA" : vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}
