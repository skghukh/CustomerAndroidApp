package com.rodafleets.rodacustomer.database;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sverma4 on 12/27/17.
 */

public class FavouriteReceiver {

    private String receiverName;
    private String phoneNumber;
    private String destAddress;
    private Double destLat;
    private Double destLng;


    public FavouriteReceiver() {

        //defaulut constructor
    }

    public FavouriteReceiver(String name, String phoneNumber, String destAddress, Double destLat, Double destLan) {
        this.receiverName = name;
        this.phoneNumber = phoneNumber;
        this.destAddress = destAddress;
        this.destLat = destLat;
        this.destLng = destLan;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public Double getDestLat() {
        return destLat;
    }

    public void setDestLat(Double destLat) {
        this.destLat = destLat;
    }

    public Double getDestLng() {
        return destLng;
    }

    public void setDestLng(Double destLng) {
        this.destLng = destLng;
    }


}
