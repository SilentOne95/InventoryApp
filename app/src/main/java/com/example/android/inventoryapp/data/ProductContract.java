package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * API Contract for the InventoryApp.
 */
public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {}

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns {

        /** Name of database table for inventory (shoes). */
        public final static String TABLE_NAME = "shoes";

        /**
         * Unique ID number for the shoes.
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

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
