package com.rodafleets.rodacustomer.utils;

/**
 * Created by sverma4 on 12/30/17.
 */


public class CompanyDetails {
    private String companyName;
    private String postalCode;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String state;

    public CompanyDetails() {
        //default constructor
    }

    public CompanyDetails(String cName, String code, String add1, String add2, String add3, String city, String state) {
        this.companyName = cName;
        this.postalCode = code;
        this.addressLine1 = add1;
        this.addressLine2 = add2;
        this.addressLine3 = add3;
        this.city = city;
        this.state = state;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
