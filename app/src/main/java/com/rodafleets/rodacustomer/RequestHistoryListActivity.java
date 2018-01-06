package com.rodafleets.rodacustomer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rodafleets.rodacustomer.custom.VehicleRequestListAdapter;
import com.rodafleets.rodacustomer.model.VehicleRequest;
import com.rodafleets.rodacustomer.utils.AppConstants;

import java.util.ArrayList;

public class RequestHistoryListActivity extends ParentActivity {
    //TODO This activity need to be deleted.
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
        setFonts();

        loadVehicleRequests();
    }

    private void setFonts() {
        loadFonts();
    }

    private void loadVehicleRequests(){

        try {

            vehicleRequestList = RodaDriverApplication.vehicleRequests;

            Log.e(TAG, "%%%%%%%%%%%% = " + vehicleRequestList.size());
            VehicleRequestListAdapter adapter = new VehicleRequestListAdapter(RequestHistoryListActivity.this, R.layout.vehicle_request_list_view_item, vehicleRequestList);
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
}
