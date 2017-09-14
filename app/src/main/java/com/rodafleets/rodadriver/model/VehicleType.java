package com.rodafleets.rodadriver.model;


import org.json.JSONException;
import org.json.JSONObject;

public class VehicleType {
    private long id;

    private String name;
    private int capacityInKgs;
    private String dimensions;
    private int baseFareInCents;
    private int baseFareKms;
    private int distanceFareInCents;
    private int distanceFareKms;
    private int rideTimeFareInCents;
    private int rideTimeFareMins;

    public VehicleType(JSONObject jsonObject) throws JSONException{
        try {
            this.id = jsonObject.getInt("id");
            this.name = jsonObject.getString("name");
            this.capacityInKgs = jsonObject.getInt("capacityInKgs");
            this.dimensions = jsonObject.getString("dimensions");
            this.capacityInKgs = jsonObject.getInt("baseFareInCents");
            this.baseFareKms = jsonObject.getInt("baseFareKms");
            this.distanceFareInCents = jsonObject.getInt("distanceFareInCents");
            this.distanceFareKms = jsonObject.getInt("distanceFareKms");
            this.rideTimeFareInCents = jsonObject.getInt("rideTimeFareInCents");
            this.rideTimeFareMins = jsonObject.getInt("rideTimeFareMins");
        } catch (JSONException e) {

        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacityInKgs() {
        return capacityInKgs;
    }

    public void setCapacityInKgs(int capacityInKgs) {
        this.capacityInKgs = capacityInKgs;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public int getBaseFareInCents() {
        return baseFareInCents;
    }

    public void setBaseFareInCents(int baseFareInCents) {
        this.baseFareInCents = baseFareInCents;
    }

    public int getBaseFareKms() {
        return baseFareKms;
    }

    public void setBaseFareKms(int baseFareKms) {
        this.baseFareKms = baseFareKms;
    }

    public int getDistanceFareInCents() {
        return distanceFareInCents;
    }

    public void setDistanceFareInCents(int distanceFareInCents) {
        this.distanceFareInCents = distanceFareInCents;
    }

    public int getDistanceFareKms() {
        return distanceFareKms;
    }

    public void setDistanceFareKms(int distanceFareKms) {
        this.distanceFareKms = distanceFareKms;
    }

    public int getRideTimeFareInCents() {
        return rideTimeFareInCents;
    }

    public void setRideTimeFareInCents(int rideTimeFareInCents) {
        this.rideTimeFareInCents = rideTimeFareInCents;
    }

    public int getRideTimeFareMins() {
        return rideTimeFareMins;
    }

    public void setRideTimeFareMins(int rideTimeFareMins) {
        this.rideTimeFareMins = rideTimeFareMins;
    }
}
