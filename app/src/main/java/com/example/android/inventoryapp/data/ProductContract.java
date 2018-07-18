package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the InventoryApp.
 */
public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventoryapp";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns {

        /** The content URI to access the inventory data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /** The MIME type of the {@link #CONTENT_URI} for a list of products (shoes). */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /** The MIME type of the {@link #CONTENT_URI} for a single product (shoes). */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /** Name of database table for inventory (shoes). */
        public final static String TABLE_NAME = "shoes";

        /**
         * Unique ID number for the shoes.
         * Type: INTEGER
         */
//        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the shoes.
         * Type: TEXT
         */
        public final static String COLUMN_SHOES_BRAND = "brand";

        /**
         * Type of the shoes.
         * Type: TEXT
         */
        public final static String COLUMN_SHOES_TYPE = "type";

        /**
         * Price of the pair of shoes.
         * Type: INTEGER
         */
        public final static String COLUMN_SHOES_PRICE = "price";

        /**
         * Quantity of the shoes.
         * Type: INTEGER
         */
        public final static String COLUMN_SHOES_QUANTITY = "quantity";

        /**
         * Supplier Name
         * Type: TEXT
         */
        public final static String COLUMN_SHOES_SUPPLIER_NAME = "name";

        /**
         * Supplier Phone Number
         * Type: INTEGER
         */
        public final static String COLUMN_SHOES_SUPPLIER_PHONE_NUMBER = "phone";
    }
}
