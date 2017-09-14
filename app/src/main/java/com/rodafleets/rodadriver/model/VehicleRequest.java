package com.rodafleets.rodadriver.model;

import org.json.JSONException;
import org.json.JSONObject;

public class VehicleRequest {

    private int id;
    private int customerId;
    private int vehicleTypeId;
    private double originLat;
    private double originLng;
    private double destinationLat;
    private double destinationLng;
    private int loadingRequired;
    private int unloadingRequired;
    private int approxFareInCents;
    private String originAddress;
    private String destinationAddress;
    private String distance;
    private String customerName;

    public VehicleRequest(JSONObject jsonObject) throws JSONException {

        this.id = jsonObject.getInt("id");
        this.originLat = ((jsonObject.isNull("originLat")) ? 0 : jsonObject.getDouble("originLat"));
        this.originLng = ((jsonObject.isNull("originLng")) ? 0 : jsonObject.getDouble("originLng"));
        this.destinationLat =((jsonObject.isNull("destinationLat")) ? 0 :  jsonObject.getDouble("destinationLat"));
        this.destinationLng = ((jsonObject.isNull("destinationLng")) ? 0 : jsonObject.getDouble("destinationLng"));
        this.approxFareInCents = ((jsonObject.isNull("approxFareInCents")) ? 0 : jsonObject.getInt("approxFareInCents"));
        this.originAddress = ((jsonObject.isNull("originAddress")) ? "" : jsonObject.getString("originAddress"));
        this.destinationAddress = ((jsonObject.isNull("destinationAddress")) ? "" : jsonObject.getString("destinationAddress"));
        this.distance = ((jsonObject.isNull("distance")) ? "" : jsonObject.getString("distance"));
        this.customerName = ((jsonObject.isNull("customerName")) ? "" : jsonObject.getString("customerName"));

//        "id": 77,
//                "customerId": 1,
//                "vehicleTypeId": 1,
//                "originLat": 12.96488,
//                "originLng": 77.638572,
//                "destinationLat": 12.961033,
//                "destinationLng": 77.65628,
//                "loadingRequired": 0,
//                "unloadingRequired": 0,
//                "approxFareInCents": 35000,
//                "originAddress": "650, 100 Feet Rd, 6th Block, Koramangala, Bengaluru, Karnataka 560047, India",
//                "destinationAddress": "Diamond District, H A L Old Airport Rd, ISRO Colony, Domlur, Bengaluru, Karnataka 560008, India",
//                "expiration": 1500575400000
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(int vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double pickupPointLat) {
        this.originLat = pickupPointLat;
    }

    public double getOriginLng() {
        return originLng;
    }

    public void setOriginLng(double pickupPointLng) {
        this.originLng = pickupPointLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double dropPointLat) {
        this.destinationLat = dropPointLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double dropPointLng) {
        this.destinationLng = dropPointLng;
    }

    public int getLoadingRequired() {
        return loadingRequired;
    }

    public void setLoadingRequired(int loadingRequired) {
        this.loadingRequired = loadingRequired;
    }

    public int getUnloadingRequired() {
        return unloadingRequired;
    }

    public void setUnloadingRequired(int unloadingRequired) {
        this.unloadingRequired = unloadingRequired;
    }

    public int getApproxFareInCents() {
        return approxFareInCents;
    }

    public void setApproxFareInCents(int approxFareInCents) {
        this.approxFareInCents = approxFareInCents;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
