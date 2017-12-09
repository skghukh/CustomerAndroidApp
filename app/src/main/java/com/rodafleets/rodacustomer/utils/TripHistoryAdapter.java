package com.rodafleets.rodacustomer.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rodafleets.rodacustomer.R;
import com.rodafleets.rodacustomer.TripHistory;
import com.rodafleets.rodacustomer.model.TripHistoryDetails;

import java.util.List;

/**
 * Created by sverma4 on 23/11/17.
 */

public class TripHistoryAdapter extends BaseAdapter {

    Context context;
    List<TripHistoryDetails> historyDetails;
    private static LayoutInflater inflater = null;

    public TripHistoryAdapter(Context context, List<TripHistoryDetails> list) {
        this.context = context;
        historyDetails = list;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return historyDetails.size();
    }

    @Override
    public Object getItem(int i) {
        return historyDetails.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.trip_history_list_item, null);
        TextView time = (TextView) vi.findViewById(R.id.time);
        TextView vehicleId = (TextView) vi.findViewById(R.id.vehicleId);
        TextView srcView = (TextView) vi.findViewById(R.id.src) ;
        TextView dstView = (TextView) vi.findViewById(R.id.dst) ;
        TextView price = (TextView) vi.findViewById(R.id.price);
        TripHistoryDetails tripDetails = historyDetails.get(i);

        time.setText("Mon 21 Dec, 2017, 8:00 PM");
        vehicleId.setText(tripDetails.getVehicle());
        srcView.setText(tripDetails.getSrc());
        dstView.setText(tripDetails.getDst());
        price.setText(tripDetails.getAmount());
        return vi;
    }
}
