package com.rodafleets.rodacustomer.model;

import android.util.Pair;

import java.util.List;

/**
 * Created by sverma4 on 17/09/17.
 */

public class NearByDrivers {
    private List<Pair<Double, Double>> nearBys;
    private int count;

    public List<Pair<Double, Double>> getNearBys() {
        return nearBys;
    }

    public void setNearBys(List<Pair<Double, Double>> nearBys) {
        this.nearBys = nearBys;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
