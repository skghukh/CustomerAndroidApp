package com.rodafleets.rodadriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rodafleets.rodadriver.custom.VehicleRequestListAdapter;
import com.rodafleets.rodadriver.model.Driver;
import com.rodafleets.rodadriver.model.VehicleRequest;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONObject;

import java.util.ArrayList;

public class VehicleRequestListActivity extends ParentActivity {

    public static final String TAG = AppConstants.APP_NAME;

    private ListView vehicleRequestListView;
    private VehicleRequest selectedVehicleRequest;

    private ArrayList<VehicleRequest> vehicleRequestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_request_list);

        initComponents();
    }

    protected void initComponents() {
        super.initComponents();
        vehicleRequestListView = (ListView) findViewById(R.id.vehicleRequestListView);
        //setFonts();

//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Vehicle_Requested"));

        loadVehicleRequests();
    }

    private void setFonts() {
        loadFonts();
    }

    private void loadVehicleRequests(){

        try {

            Log.e(TAG, "jsonString = " + ApplicationSettings.getDriver(VehicleRequestListActivity.this));
            JSONObject jsonObject = new JSONObject(ApplicationSettings.getDriver(VehicleRequestListActivity.this));
            Log.e(TAG, "jsonObject created");

            Driver driver = new Driver(jsonObject);
            vehicleRequestList = driver.getVehicleRequests();

            Log.e(TAG, "%%%%%%%%%%%% = " + vehicleRequestList.size());
            VehicleRequestListAdapter adapter = new VehicleRequestListAdapter(VehicleRequestListActivity.this, R.layout.vehicle_request_list_view_item, vehicleRequestList);
            vehicleRequestListView.setAdapter(adapter);

            vehicleRequestListView.setOnItemClickListener(itemClickListener);

        } catch (Exception e) {
            //handle error
            Log.e(TAG, "loadVehicleRequests jsonException = " + e.getMessage());
        }

    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            Log.i("LIST", "items clicked");
            selectedVehicleRequest = vehicleRequestList.get(position);
//            startNext
        }

    };


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                JSONObject jsonObject = new JSONObject(ApplicationSettings.getVehicleRequest(VehicleRequestListActivity.this));
                //vehicleRequest = new VehicleRequest(jsonObject);
            } catch (Exception e) {
                //handle error
                Log.e(TAG, "vehicleRequest jsonException = " + e.getMessage());
            }
        }
    };
}
