package com.example.deepa.inventoryapp1.data;

/**
 * Created by deepa on 29/07/2018.
 */
import android.content.ContentResolver;
import android.provider.BaseColumns;
import android.net.Uri;

public final class ProductContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.deepa.inventoryapp1";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PETS = "Products";
    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns {
        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /** Name of database table for products */
        public final static String TABLE_NAME = "Products";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="Name";
        /**
         *Qty of the product.
         *
         * Type: INTEGER
         */


        public final static String COLUMN_QUANTITY = "Quantity";


        /**
         *Price of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRICE = "Price";

        /**
         *Supplier NAme of the product.
         *
         * Type: TEXT
         */

        public final static String COLUMN_SUPPLIERNAME = "Supplier_Name";
        /**
         *Supplier PhoneNO of the product.
         *
         * Type: INTEGER
         */

        public final static String COLUMN_SUPPLIERPHONENO= "Supplier_No";

        /**
         * Possible values for the Qty of the product.
         *
         */




    }


}


