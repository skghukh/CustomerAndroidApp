package com.rodafleets.rodacustomer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.rodafleets.rodacustomer.database.DBHelper;
import com.rodafleets.rodacustomer.database.Favourite;
import com.rodafleets.rodacustomer.database.FavouriteReceiver;
import com.rodafleets.rodacustomer.services.FirebaseReferenceService;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import java.util.List;

public class Favourites extends AppCompatActivity {
    DBHelper mydb;
    private ListView view;
    FirebaseListAdapter<FavouriteReceiver> myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        //init();
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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
                userPhone.setText("("+model.getPhoneNumber()+")");
                destAddress.setText(model.getDestAddress());
            }

        };
        view.setAdapter(myAdapter);
    }

    private void init() {
        //Favourite(String receiverName, String sourceAddress, String destAddress, Double source, Double dest, String phoneNumber)
        Favourite favourite = new Favourite("shailu", "9986291127", "Bellandur Bangalore", "Kormangala Bangalore", 12.92793603, 77.67501914);
        mydb = new DBHelper(this);
        mydb.addToFavourites(favourite);
        final List<Favourite> favourites = mydb.fetchFavourites();
        System.out.print(favourite);
    }

}
