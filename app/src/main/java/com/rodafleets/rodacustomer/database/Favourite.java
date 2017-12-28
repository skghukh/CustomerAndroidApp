package com.rodafleets.rodacustomer.database;

/**
 * Created by sverma4 on 10/12/17.
 */

public class Favourite {
    String receiverName;
    String sourceAddress;
    String destAddress;
    Double source;
    Double dest;
    String phoneNumber;


    public Favourite(){
        //default constructor;

       /* values.put(RECIVER_NAME, favourite.getReceiverName());
        values.put(PHONE_NBR, favourite.getPhoneNumber());
        values.put(SOURCE, favourite.getSourceAddress());
        values.put(DEST, favourite.getDestAddress());
        values.put(SOURCE_LOC, favourite.getSource());
        values.put(DEST_LOC, favourite.getDest());*/
    }
    public Favourite(String receiverName, String phoneNumber, String sourceAddress, String destAddress, Double source, Double dest) {
        this.receiverName = receiverName;
        this.sourceAddress = sourceAddress;
        this.destAddress = destAddress;
        this.source = source;
        this.dest = dest;
        this.phoneNumber = phoneNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public Double getSource() {
        return source;
    }

    public void setSource(Double source) {
        this.source = source;
    }

    public Double getDest() {
        return dest;
    }

    public void setDest(Double dest) {
        this.dest = dest;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
