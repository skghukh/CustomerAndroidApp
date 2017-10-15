package com.rodafleets.rodacustomer;

/**
 * Created by sverma4 on 14/10/17.
 */

public class VehicleRequestResponse {

    private String name;
    private String rating;
    private String distance;
    private String fareEstimate;
    private String requestId;
    private String bidId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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
}
