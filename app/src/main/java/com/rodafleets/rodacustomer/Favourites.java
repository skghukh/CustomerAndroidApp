package com.rodafleets.rodacustomer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.rodafleets.rodacustomer.database.Favourite;
import com.rodafleets.rodacustomer.database.FavouriteReceiver;
import com.rodafleets.rodacustomer.services.FirebaseReferenceService;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import java.util.List;

public class Favourites extends AppCompatActivity {
    private ListView view;
    FirebaseListAdapter<FavouriteReceiver> myAdapter;
    private Favourite favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Favourites");
        }
        initViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    private void initViews() {
        view = findViewById(R.id.favourite_list);
        DatabaseReference ref = FirebaseReferenceService.getFavouriteReference(ApplicationSettings.getCustomerEid(Favourites.this));
        myAdapter = new FirebaseListAdapter<FavouriteReceiver>(Favourites.this, FavouriteReceiver.class, R.layout.favourite_list_item, ref.limitToFirst(10)) {
            @Override
            protected void populateView(View v, FavouriteReceiver model, int position) {
                TextView userName = v.findViewById(R.id.user_name);
                TextView userPhone = v.findViewById(R.id.user_phone);
                TextView destAddress = v.findViewById(R.id.dest_address);
                userName.setText(model.getReceiverName());
                userPhone.setText("(" + model.getPhoneNumber() + ")");
                destAddress.setText(model.getDestAddress());
            }

        };
        view.setAdapter(myAdapter);
    }

}
