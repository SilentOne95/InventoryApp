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

        String[] projection = {
                ProductEntry.COLUMN_SHOES_BRAND,
                ProductEntry.COLUMN_SHOES_TYPE,
                ProductEntry.COLUMN_SHOES_PRICE,
                ProductEntry.COLUMN_SHOES_QUANTITY,
                ProductEntry.COLUMN_SHOES_SUPPLIER_NAME,
                ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,    // The table to query.
                projection,                 // The columns to return.
                null,               // The columns for the WHERE clause.
                null,                  // The values for the WHERE clause.
                null,                  // Don't group the rows.
                null,                  // Don't filter by row groups.
                null);                   // The sort order.

        try {
            // Display the number of rows in the Cursor, which reflects the number of rows in the
            // shoes table in the database.
            Log.i(LOG_TAG, "Number of rows in inventory database table: " + cursor.getCount());
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
