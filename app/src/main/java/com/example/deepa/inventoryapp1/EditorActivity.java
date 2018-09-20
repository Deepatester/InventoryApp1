package com.example.deepa.inventoryapp1;

import android.app.AlertDialog;

import android.content.ContentUris;
import android.content.ContentValues;

import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import com.example.deepa.inventoryapp1.data.ProductContract;
import com.example.deepa.inventoryapp1.data.ProductContract.ProductEntry;
import com.example.deepa.inventoryapp1.data.ProductDbHelper;

public class EditorActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText mNameEditText;

    /**
     * EditText field to enter the price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the suppliersname and supplierphoneno
     */
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNoEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private EditText mQuantityEditText;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mQuantity = 0;
    private Uri mCurrentItemUri;
    private int mCurrentID;
    public static final int EDITOR_LOADER_ID = 1;
    private boolean mProductHasChanged = false;

    /**
     * int for given quantity
     */
    private int currentQuantity;

    //OnTouchListener that listens for any user touches on a View, implying that they are modyfying
    // the view, and we change the mClothesHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
       mCurrentItemUri = intent.getData();

       //Change between Edit Mode and Add Mode depending on which intent started the activity
        //if the activity was started through the Add button the selectedItemUri will be null
        //if the activity was started from clicking on the item start the activity in Edit Mode
        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.editor_activity_add_mode_label));
            //redraw the menu
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_edit_mode_label));
            //initialise the Loader when the Activity is created in the Edit Mode
            getLoaderManager().initLoader(EDITOR_LOADER_ID, null, this);
        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mQuantityEditText = (EditText) findViewById(R.id.edit_clothes_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_Supplier_name);
        mSupplierPhoneNoEditText = (EditText) findViewById(R.id.edit_Supplier_PhoneNumber);
        ImageButton mIncrease = (ImageButton) findViewById(R.id.edit_quantity_increase);
        ImageButton mDecrease = (ImageButton) findViewById(R.id.edit_quantity_decrease);
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNoEditText.setOnTouchListener(mTouchListener);
        mIncrease.setOnTouchListener(mTouchListener);
        mDecrease.setOnTouchListener(mTouchListener);

        //increase quantity
        mIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(EditorActivity.this, R.string.editor_quantity_field_cant_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    currentQuantity = Integer.parseInt(quantity);
                    mQuantityEditText.setText(String.valueOf(currentQuantity + 1));
                }
            }
        });

        //decrease quantity with button

        mDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(EditorActivity.this, R.string.editor_quantity_field_cant_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    currentQuantity = Integer.parseInt(quantity);
                    // to validate if quantity is greater than =
                    if ((currentQuantity - 1) >= 0) {
                        mQuantityEditText.setText(String.valueOf(currentQuantity - 1));
                    } else {
                        Toast.makeText(EditorActivity.this, R.string.editor_quantity_cant_be_less_then_0, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        final ImageButton mPhoneCallSupplierButton = (ImageButton) findViewById(R.id.call_supplier_phone_button);

        mPhoneCallSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mSupplierPhoneNoEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);

                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        // If the clothes editing hasnt changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        //Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User clicked "Discard" button, close the current activity-
                        finish();

                    }
                };
        //Show the dialog that there are unsaved changes
        showUnsavedChangedDialog(discardButtonClickListener);
    }


  /*
    /**
     * Get user input from editor and save pet into database.
     */
    private void saveProduct() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String selectedQty = mQuantityEditText.getText().toString().trim();
        String priceEntered = mPriceEditText.getText().toString().trim();
        String supplierNameEntered = mSupplierNameEditText.getText().toString().trim();
        String supplierNumberEntered = mSupplierPhoneNoEditText.getText().toString().trim();



        if (mCurrentItemUri == null &&
                 TextUtils.isEmpty(nameString) &&
                 TextUtils.isEmpty(selectedQty) &&
                 TextUtils.isEmpty(priceEntered) &&
                 TextUtils.isEmpty(supplierNameEntered) &&
                 TextUtils.isEmpty(supplierNumberEntered))
        {
            Toast.makeText(this, getString(R.string.editor_fill_in), Toast.LENGTH_LONG).show();
            //Since no fields were modified, we can return early without creating a new clothes product.
            // No need to create ContentValues and no need to do any ContenProvider operations.
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_QUANTITY, selectedQty);
        values.put(ProductEntry.COLUMN_PRICE, priceEntered);
        values.put(ProductEntry.COLUMN_SUPPLIERNAME, supplierNameEntered);
        values.put(ProductEntry.COLUMN_SUPPLIERPHONENO,  supplierNumberEntered);

        if (TextUtils.isEmpty(nameString)) {
            mNameEditText.setError(getString(R.string.error_for_empty_field_name));
            return;
        }
        int finalQty=0;
        if (!TextUtils.isEmpty(selectedQty)) {
            finalQty = Integer.parseInt(selectedQty);
        }else{
            mQuantityEditText.setError(getString(R.string.editor_quantity_field_cant_be_empty));
            return;
        }
        values.put(ProductEntry.COLUMN_QUANTITY, finalQty);

        int finalPrice=0;
        if (!TextUtils.isEmpty(priceEntered)) {
            finalPrice = Integer.parseInt(priceEntered);
        }else{
            mPriceEditText.setError(getString(R.string.error_for_empty_field_price));
            return;
        }
        values.put(ProductEntry.COLUMN_PRICE, finalPrice);

        if (TextUtils.isEmpty(supplierNameEntered)) {
            mSupplierNameEditText.setError(getString(R.string.error_for_empty_field_supplier_name));
            return;
        }

        if (TextUtils.isEmpty(supplierNumberEntered)) {
            mSupplierPhoneNoEditText.setError(getString(R.string.error_for_empty_field_supplier_number));
            return;
        }


        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.



        if (mCurrentItemUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
    private void deleteProduct() {

        //Only perform the delte if this is an existing product.
        if (mCurrentItemUri != null) {
            //Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentClothesUri
            // content Uri already identifies the product that we want.

            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                //If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                //Otherwis, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_successful), Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                //Otherwise if there are unsaved changes, setuo a dialog to warn the user.
                //Create a clikc listener to handle the user confirming that
                // chnages should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //User clicked "Disacard" button, navigate to parent activity-
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }

                        };
                //Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangedDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangedDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //User clicked the "Keep editing" button, so dismiss the dialog
                //and continue editing the clothes.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });

        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                deleteProduct();

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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_SUPPLIERNAME,
                ProductEntry.COLUMN_SUPPLIERPHONENO};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(EditorActivity.this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Bail early if the cursor is null or there is less that 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        //if cursor is null / no rows then just return
       if (cursor.moveToFirst()) {
// Figure out the index of each column

            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int qtyColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIERNAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIERPHONENO);


            String Name = cursor.getString(nameColumnIndex);
            int Quantity = cursor.getInt(qtyColumnIndex);
            int Price = cursor.getInt(priceColumnIndex);
            String Supplier_Name = cursor.getString(supplierNameColumnIndex);
            String supplier_No = cursor.getString(supplierNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(Name);
            mQuantityEditText.setText(String.valueOf(Quantity));
            mPriceEditText.setText(String.valueOf(Price));
            mSupplierNameEditText.setText(Supplier_Name);
            mSupplierPhoneNoEditText.setText(supplier_No);

        }

        }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNoEditText.setText("");

    }


}
