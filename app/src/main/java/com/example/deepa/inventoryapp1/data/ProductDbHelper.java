package com.example.deepa.inventoryapp1.data;

/**
 * Created by deepa on 29/07/2018.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.deepa.inventoryapp1.data.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "Product.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ProductDbHelper}.
     *
     * @param context of the app
     */
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
     //   Log.v("SQLite Entries: ", SQL_CREATE_PRODUCTS_TABLE);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ( " +
                 ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                 ProductEntry.COLUMN_PRODUCT_NAME + " TEXT , " +
                 ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                 ProductEntry.COLUMN_PRICE + " INTEGER, " +
                 ProductEntry.COLUMN_SUPPLIERNAME + " TEXT NOT NULL, " +
                 ProductEntry.COLUMN_SUPPLIERPHONENO + " TEXT NOT NULL)" +
                ";";

    // Execute the SQL statement
       db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
   }

           /**
    * This is called when the database needs to be upgraded.
    */
           @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
               // The database is still at version 1, so there's nothing to do be done here.
            }
}
