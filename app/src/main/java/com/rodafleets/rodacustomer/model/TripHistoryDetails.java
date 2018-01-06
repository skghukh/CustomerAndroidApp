package com.rodafleets.rodacustomer.model;

/**
 * Created by sverma4 on 23/11/17.
 */

public class TripHistoryDetails {

    private Long timestamp;
    private int vehicleTypeId;
    private String originAddress;
    private String destinationAddress;
    private String acceptedFare;
    private String status;
    private String recName;

    public String getRecName() {
        return recName;
    }

    public void setRecName(String recName) {
        this.recName = recName;
    }

    public String getRecNum() {
        return recNum;
    }

    public void setRecNum(String recNum) {
        this.recNum = recNum;
    }

    private String recNum;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(int vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getAcceptedFare() {
        return acceptedFare;
    }

    public void setAcceptedFare(String acceptedFare) {
        this.acceptedFare = acceptedFare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
