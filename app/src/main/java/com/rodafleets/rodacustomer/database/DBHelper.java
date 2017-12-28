package com.rodafleets.rodacustomer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sverma4 on 10/12/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "RODAFLEET";
    private static final String RECIVER_NAME = "receiver_name";
    private static final String PHONE_NBR = "receiver_phone_number";
    private static final String SOURCE = "source";
    private static final String DEST = "dest";
    private static final String SOURCE_LOC = "source_loc";
    private static final String DEST_LOC = "dest_loc";
    private static final String FAVAOURITE_TABLE = "Favourites";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, "RODAFLEET", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE Favourites(receiver_name text NOT NULL, receiver_phone_number text NOT NULL, source text NOT NULL, dest text NOT NULL , source_loc double NOT NULL , dest_loc double NOT NULL , PRIMARY KEY (receiver_name,receiver_phone_number,source_loc,dest_loc))";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addToFavourites(Favourite favourite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RECIVER_NAME, favourite.getReceiverName());
        values.put(PHONE_NBR, favourite.getPhoneNumber());
        values.put(SOURCE, favourite.getSourceAddress());
        values.put(DEST, favourite.getDestAddress());
        values.put(SOURCE_LOC, favourite.getSource());
        values.put(DEST_LOC, favourite.getDest());
        db.insert(FAVAOURITE_TABLE, null, values);
        db.close();
    }

    public List<Favourite> fetchFavourites() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Favourite> favourites = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from Favourites", null);
        if (cursor.moveToFirst()) {
            do {
                Favourite fav = new Favourite(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5));
                favourites.add(fav);
            } while (cursor.moveToNext());
        }
        return favourites;
    }


}
