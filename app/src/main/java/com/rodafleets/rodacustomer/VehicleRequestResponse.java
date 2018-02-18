package com.rodafleets.rodacustomer;

/**
 * Created by sverma4 on 14/10/17.
 */

public class VehicleRequestResponse {

    private String name;
    private String driverRating;
    private String driverContact;
    private String vehicleRegId;
    private String distance;
    private String fareEstimate;
    private String requestId;
    private String bidId;
    private String driverId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(String driverRating) {
        this.driverRating = driverRating;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        if(null != distance){
             double dist = Double.parseDouble(distance);
             dist = dist / 1000;
            this.distance = dist+"KM";
        }else{
            this.distance = distance+"KM";
        }
    }

    public String getFareEstimate() {
        return fareEstimate;
    }

    public void setFareEstimate(String fareEstimate) {
        this.fareEstimate = fareEstimate;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getDriverContact() {
        return driverContact;
    }

    public void setDriverContact(String driverContact) {
        this.driverContact = driverContact;
    }

    public String getVehicleRegId() {
        return vehicleRegId;
    }

    public void setVehicleRegId(String vehicleRegId) {
        this.vehicleRegId = vehicleRegId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}
