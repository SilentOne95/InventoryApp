package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryDbHelper;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class CatalogActivity extends AppCompatActivity {

    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new InventoryDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_SHOES_BRAND,
                ProductEntry.COLUMN_SHOES_TYPE,
                ProductEntry.COLUMN_SHOES_PRICE,
                ProductEntry.COLUMN_SHOES_QUANTITY,
                ProductEntry.COLUMN_SHOES_SUPPLIER_NAME,
                ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER
        };

        // Perform a query.
        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,    // The table to query.
                projection,                 // The columns to return.
                null,               // The columns for the WHERE clause.
                null,                  // The values for the WHERE clause.
                null,                  // Don't group the rows.
                null,                  // Don't filter by row groups.
                null);                   // The sort order.

        TextView displayView = findViewById(R.id.text_view_product);

        try {
            // Create a header which looks like this:
            // _i | brand | type | price | quantity | (supplier) name | phone
            displayView.setText("This table contains " + cursor.getCount() + " rows.\n\n");
            displayView.append(ProductEntry._ID + " | "
                    + ProductEntry.COLUMN_SHOES_BRAND + " | "
                    + ProductEntry.COLUMN_SHOES_TYPE + " | "
                    + ProductEntry.COLUMN_SHOES_PRICE + " | "
                    + ProductEntry.COLUMN_SHOES_QUANTITY + " | "
                    + ProductEntry.COLUMN_SHOES_SUPPLIER_NAME + " | "
                    + ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER);

            // Figure out the index of each column.
            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int brandColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_BRAND);
            int typeColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_TYPE);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_QUANTITY);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentId = cursor.getInt(idColumnIndex);
                String currentBrand = cursor.getString(brandColumnIndex);
                String currentType = cursor.getString(typeColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPhone = cursor.getInt(phoneColumnIndex);

                // Display the values to the user in the TextView.
                displayView.append(("\n" + currentId + " | "
                        + currentBrand + " | "
                        + currentType + " | "
                        + currentPrice + " | "
                        + currentQuantity + " | "
                        + currentName + " | "
                        + currentPhone));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded shoes data into the database. For debugging purposes only.
     */
    private void insertShoes() {
        // Gets the database in write mode.
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_SHOES_BRAND, "Test Brand");
        values.put(ProductEntry.COLUMN_SHOES_TYPE, "Test Type");
        values.put(ProductEntry.COLUMN_SHOES_PRICE, 99);
        values.put(ProductEntry.COLUMN_SHOES_QUANTITY, 0);
        values.put(ProductEntry.COLUMN_SHOES_SUPPLIER_NAME, "Test User");
        values.put(ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER, "123456789");

        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);

        Log.i(LOG_TAG, "Added new row with ID: " + newRowId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertShoes();
                displayDatabaseInfo();
                return true;
            case R.id.action_delete_all_entries:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
