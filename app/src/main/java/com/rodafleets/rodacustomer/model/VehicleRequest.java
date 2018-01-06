package com.rodafleets.rodacustomer.model;

import com.google.firebase.database.ServerValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VehicleRequest {
    private String distance;
    private String acceptedFare;
    private int approxFareInCents;
    private String carrierId;
    private int customerId;
    private String destinationAddress;
    private String customerName;
    private double destinationLat;
    private double destinationLng;
    private int id;
    private int loadingRequired;
    private int unloadingRequired;
    private String originAddress;
    private double originLat;
    private double originLng;
    private String recName;
    private String recNum;
    private String status;
    private Long timeStamp;
    private int vehicleTypeId;

    public VehicleRequest() {
        //Default Constructor
    }


    public static VehicleRequest getVehicleRequest(HashMap<String, Object> vehicleRequestMap) {
        Gson gson = new Gson();
        final JsonElement jsonElement = gson.toJsonTree(vehicleRequestMap);
        return gson.fromJson(jsonElement, VehicleRequest.class);
    }

    public VehicleRequest(JSONObject jsonObject) throws JSONException {
        final Map<String, String> timestamp = ServerValue.TIMESTAMP;
        this.id = jsonObject.getInt("id");
        this.originLat = ((jsonObject.isNull("originLat")) ? 0 : jsonObject.getDouble("originLat"));
        this.originLng = ((jsonObject.isNull("originLng")) ? 0 : jsonObject.getDouble("originLng"));
        this.destinationLat = ((jsonObject.isNull("destinationLat")) ? 0 : jsonObject.getDouble("destinationLat"));
        this.destinationLng = ((jsonObject.isNull("destinationLng")) ? 0 : jsonObject.getDouble("destinationLng"));
        this.approxFareInCents = ((jsonObject.isNull("approxFareInCents")) ? 0 : jsonObject.getInt("approxFareInCents"));
        this.originAddress = ((jsonObject.isNull("originAddress")) ? "" : jsonObject.getString("originAddress"));
        this.destinationAddress = ((jsonObject.isNull("destinationAddress")) ? "" : jsonObject.getString("destinationAddress"));
        this.distance = ((jsonObject.isNull("distance")) ? "" : jsonObject.getString("distance"));
        this.customerName = ((jsonObject.isNull("customerName")) ? "" : jsonObject.getString("customerName"));
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

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcceptedFare() {
        return acceptedFare;
    }

    public void setAcceptedFare(String acceptedFare) {
        this.acceptedFare = acceptedFare;
    }

    public String getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(String carrierId) {
        this.carrierId = carrierId;
    }

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
}
