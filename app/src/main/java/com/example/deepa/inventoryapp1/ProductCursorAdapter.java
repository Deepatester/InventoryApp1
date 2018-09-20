package com.example.deepa.inventoryapp1;

/**
 * Created by deepa on 07/08/2018.
 */
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deepa.inventoryapp1.data.ProductContract;

public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param cursor       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v =  LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        ((ViewGroup) v).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        return v;
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.name);
        TextView tvPrice = (TextView) view.findViewById(R.id.price);
        TextView tvQty = (TextView) view.findViewById(R.id.quantity);
        ImageButton sellButton = (ImageButton) view.findViewById(R.id.sell_button);

        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);
        // Extract properties from cursor
        String name = "Name : " + cursor.getString(nameColumnIndex);
        String price = "Price: " + cursor.getString(priceColumnIndex);
        String quantity = "Qty: " + cursor.getString(quantityColumnIndex);
        // Populate fields with extracted properties
        tvName.setText(name);
        tvPrice.setText(String.valueOf(price));
        tvQty.setText(String.valueOf(quantity));

        //Get the current quantity and make into an integer
        final String currentQuantityString = cursor.getString(quantityColumnIndex);
        final int currentQuantity = Integer.valueOf(currentQuantityString);
        // Get the rows from the table with the ID
        final int productId = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry._ID));

        //setting up the decrement on the sell button
     sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting up an if else statement to what will be done when the current quantity is > 0
                if (currentQuantity > 0) {
                   final int newQuantity = currentQuantity - 1;

                    //Getting the URI with the append of the ID for the row
                    Uri quantityUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, productId);

                    //Getting the current Value for quantity and updating them with the new value -1
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, newQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);
                }

                //Creating a Toast message that when the quantity is 0 this will be shown
                else {
                    Toast.makeText(context, "This product is out of stock", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

