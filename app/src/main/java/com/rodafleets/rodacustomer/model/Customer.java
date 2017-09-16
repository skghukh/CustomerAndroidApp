package com.rodafleets.rodacustomer.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sverma4 on 17/09/17.
 */

public class Customer {

    private int id;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String gender;
    private Boolean verified;

    public Customer(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.phoneNumber = jsonObject.getString("phoneNumber");
        this.firstName = jsonObject.getString("firstName");
        this.lastName = jsonObject.getString("lastName");
        this.verified = !jsonObject.getString("verified").equalsIgnoreCase("0");
        this.gender = jsonObject.getString("gender");
        //  this.vehicleRequests = Utils.toVehicleRequestArrayList(jsonObject.getJSONArray("vehicleRequests"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
