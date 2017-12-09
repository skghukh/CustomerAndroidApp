package com.rodafleets.rodacustomer;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

public class VehicleAndRates extends AppCompatActivity {

    String vehicleTypes[] = new String[]{"Small Load", "Medium Load", "Heavy Load"};
    LayoutInflater inflater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_and_rates);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("VEHICLE AND RATES");
        }

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        initView();
    }

    private void initView() {
        ListView rateList = (ListView) findViewById(R.id.rate_list);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rateList.setAdapter(new ListAdapter() {
            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int i) {
                return false;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public int getCount() {
                return vehicleTypes.length;
            }

            @Override
            public Object getItem(int i) {
                return vehicleTypes[i];
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View vi = view;
                if (vi == null)
                    vi = inflater.inflate(R.layout.vehicle_and_rates_list_view_item, null);
                TextView vehicleType = (TextView) vi.findViewById(R.id.vehicle_type);
                AppCompatImageView vehicleTypeImage = (AppCompatImageView) vi.findViewById(R.id.vehicle_type_image);
                if (null != vehicleType) {
                    vehicleType.setText(vehicleTypes[i]);
                }
                if (null != vehicleTypeImage) {
                    switch (i) {
                        case 0:
                            vehicleTypeImage.setImageResource(R.drawable.ic_small_load);
                            break;
                        case 1:
                            vehicleTypeImage.setImageResource(R.drawable.ic_medium_load);
                            break;
                        case 2:
                            vehicleTypeImage.setImageResource(R.drawable.ic_small_load);
                            break;
                        default:
                            vehicleTypeImage.setImageResource(R.drawable.ic_small_load);

                    }
                }
                return vi;
            }

            @Override
            public int getItemViewType(int i) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });
    }

}
