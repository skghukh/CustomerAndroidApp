package com.rodafleets.rodadriver.model;

import android.util.Log;

import com.rodafleets.rodadriver.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Driver {

    private int id;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String gender;
    private Boolean verified;
    private ArrayList<VehicleRequest> vehicleRequests;

    public Driver(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.phoneNumber = jsonObject.getString("phoneNumber");
        this.firstName = jsonObject.getString("firstName");
        this.lastName = jsonObject.getString("lastName");
        this.verified = jsonObject.getBoolean("verified");
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

    public String getFirstname() {
        return firstName;
    }

    public void setFirstname(String firstname) {
        this.firstName = firstname;
    }

    public String getLastname() {
        return lastName;
    }

    public void setLastname(String lastname) {
        this.lastName = lastname;
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

    public ArrayList<VehicleRequest> getVehicleRequests() {
        return vehicleRequests;
    }

//    public static String serializeObject(Driver driver) {
//        String serializedObject = "";
//        // serialize the object
//        try {
//            ByteArrayOutputStream bo = new ByteArrayOutputStream();
//            ObjectOutputStream so = new ObjectOutputStream(bo);
//            so.writeObject(driver);
//            so.flush();
//            serializedObject = bo.toString();
//        } catch (Exception e) {
//
//        }
//
//        return serializedObject;
//    }
//
//    public static Driver deSerializeObject(String serializedString) {
//        Driver driver = null;
//        // deserialize the object
//        try {
//            byte b[] = serializedString.getBytes();
//            ByteArrayInputStream bi = new ByteArrayInputStream(b);
//            ObjectInputStream si = new ObjectInputStream(bi);
//            driver = (Driver) si.readObject();
//        } catch (Exception e) {
//
//        }
//
//        return driver;
//    }
}
