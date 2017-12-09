package com.rodafleets.rodacustomer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sverma4 on 14/10/17.
 */

public class DriversResponseAdapter extends BaseAdapter {
    Context context;
    List<VehicleRequestResponse> driverResponseList;
    RadioGroup radioGroup;
    private static LayoutInflater inflater = null;

    public DriversResponseAdapter(Context context, List<VehicleRequestResponse> data) {
        this.context = context;
        this.driverResponseList = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return driverResponseList.size();
    }

    @Override
    public Object getItem(int position) {
        return driverResponseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_item, null);
        //get all views
        TextView driverName = (TextView) vi.findViewById(R.id.driverName);
        TextView driverRating = (TextView) vi.findViewById(R.id.driverRating);
        TextView distance = (TextView) vi.findViewById(R.id.distance);
        TextView fare = (TextView) vi.findViewById(R.id.fare);
        RadioButton radioButton = (RadioButton) vi.findViewById(R.id.radioButton);


        //check response
        VehicleRequestResponse vehicleRequestResponse = driverResponseList.get(position);

        //set views values based on response
        driverName.setText(vehicleRequestResponse.getName());
        driverRating.setText(vehicleRequestResponse.getDriverRating());
        distance.setText(vehicleRequestResponse.getDistance());
        fare.setText(vehicleRequestResponse.getFareEstimate());
        radioButton.setTag(position);
        return vi;
    }
}
