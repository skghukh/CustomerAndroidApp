package com.rodafleets.rodadriver.custom;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.graphics.Typeface;
import android.widget.TextView;

import com.rodafleets.rodadriver.model.VehicleType;

import java.util.ArrayList;

public class VehicleTypeSpinnerAdapter extends ArrayAdapter {

    // Initialise custom font, for example:
    Typeface font;
    private ArrayList<VehicleType> vehicleTypes;


    public VehicleTypeSpinnerAdapter(Context context, int resource, ArrayList<VehicleType> items, Typeface font) {
        super(context, resource, items);
        this.font = font;
        this.vehicleTypes = items;
    }

    // Affects default (closed) state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(font);

        VehicleType vType = vehicleTypes.get(position);
        view.setText(vType.getName());
        return view;
    }

    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(font);

        VehicleType vType = vehicleTypes.get(position);
        view.setText(vType.getName());
        return view;
    }

}