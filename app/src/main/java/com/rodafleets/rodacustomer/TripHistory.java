package com.rodafleets.rodacustomer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodacustomer.database.FavouriteReceiver;
import com.rodafleets.rodacustomer.model.NearByDrivers;
import com.rodafleets.rodacustomer.model.TripHistoryDetails;
import com.rodafleets.rodacustomer.model.VehicleRequest;
import com.rodafleets.rodacustomer.rest.RodaRestClient;
import com.rodafleets.rodacustomer.services.FirebaseReferenceService;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;
import com.rodafleets.rodacustomer.utils.TripHistoryAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TripHistory extends AppCompatActivity {

    List<TripHistoryDetails> tripsList = new ArrayList<>();
    TripHistoryAdapter tripAdapter;
    FirebaseListAdapter<TripHistoryDetails> tripHistoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("TRIPS");
        }
        initComponents();

    }

    private void initComponents() {
        getHistoryRequestsFromFirebase();
       /* tripAdapter = new TripHistoryAdapter(this, tripsList);
        ListView historyList = (ListView) findViewById(R.id.trip_history_list);
        if (null != historyList) {
            historyList.setAdapter(tripAdapter);
        }
        getHistoryRequests();*/
    }


    private void getHistoryRequestsFromFirebase() {
        final DatabaseReference requestHistoryReference = FirebaseReferenceService.getRequestHistoryReference(ApplicationSettings.getCustomerEid(TripHistory.this));
        tripHistoryAdapter = new FirebaseListAdapter<TripHistoryDetails>(TripHistory.this, TripHistoryDetails.class, R.layout.trip_history_list_item, requestHistoryReference.limitToFirst(10)) {
            @Override
            protected void populateView(View v, TripHistoryDetails model, int position) {
                TextView time = v.findViewById(R.id.time);
                time.setText(String.valueOf(model.getTimestamp()));
                TextView vehicleId = v.findViewById(R.id.vehicleId);
                vehicleId.setText("HR 51 BE3767");
                TextView src = v.findViewById(R.id.src);
                src.setText(model.getOriginAddress());
                TextView dst = v.findViewById(R.id.dst);
                dst.setText(model.getDestinationAddress());
                TextView price = v.findViewById(R.id.price);
                price.setText(model.getAcceptedFare());
            }
        };
        ListView historyList = (ListView) findViewById(R.id.trip_history_list);
        historyList.setAdapter(tripHistoryAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getHistoryRequests() {

        final JsonHttpResponseHandler tripHistoryRestResponse = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                TripHistoryDetails[] historyDetails = new Gson().fromJson(response.toString(), TripHistoryDetails[].class);
                tripsList.addAll(Arrays.asList(historyDetails));
                tripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
                System.out.println(jsonResponseObject);
                TripHistoryDetails[] historyDetails = new Gson().fromJson(jsonResponseObject.toString(), TripHistoryDetails[].class);
                tripsList = Arrays.asList(historyDetails);
                tripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                System.out.println(statusCode + " " + errorResponse);
                Toast.makeText(TripHistory.this, "Oops!", Toast.LENGTH_SHORT).show();
            }
        };

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                RodaRestClient.getTripsHistory(ApplicationSettings.getCustomerEid(TripHistory.this), tripHistoryRestResponse);
            }
        });


    }
}
