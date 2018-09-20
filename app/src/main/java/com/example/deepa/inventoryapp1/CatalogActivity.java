package com.example.deepa.inventoryapp1;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.deepa.inventoryapp1.data.ProductContract.ProductEntry;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

public class CatalogActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor> {
    /** Database helper that will provide us access to the database */

private Uri selectedItemUri;
private static final int PRODUCT_LOADER=0;
ProductCursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // Find the ListView which will be populated with the pet data
        ListView productListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);
       mCursorAdapter=new ProductCursorAdapter(this,null);
       productListView.setAdapter(mCursorAdapter);
               //Setup a Item Click Listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the selected item content uri by appending base content uri with the ListView item id from the adapter
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                selectedItemUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(selectedItemUri);

                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(PRODUCT_LOADER,null, this);
    }

       /**
     +     * Temporary helper method to display information in the onscreen TextView about the state of
     +     * the pets database.
     +     */

    private void insertProduct() {
        // Gets the database in write mode


        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
       ContentValues values = new ContentValues();
       values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Toto");
       values.put(ProductEntry.COLUMN_QUANTITY, 1);
       values.put(ProductEntry.COLUMN_PRICE,200);
       values.put(ProductEntry.COLUMN_SUPPLIERNAME,"Testre");
        values.put(ProductEntry.COLUMN_SUPPLIERPHONENO,045);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        //Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //user clicked the "Delete Button, so delete the clothes product.
                deleteAll();

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteAll() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
        if (rowsDeleted == 0) {
            //If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
        } else {
            //Otherwis, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_successful), Toast.LENGTH_SHORT).show();
        }
        getSupportLoaderManager().restartLoader(PRODUCT_LOADER,null, this);
}

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY};
        // Perform a query on the pets table
        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null);               // Don't group the rows

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

           mCursorAdapter.swapCursor(cursor);


    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


        mCursorAdapter.swapCursor(null);
    }
}

