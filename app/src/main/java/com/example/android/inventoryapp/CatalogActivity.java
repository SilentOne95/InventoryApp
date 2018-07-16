package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class CatalogActivity extends AppCompatActivity {

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

        // Find the ListView which will be populated with the products data.
        ListView productListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);
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

        // Perform a query on the provider using the ContentResolver.
        Cursor cursor = getContentResolver().query(
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        ListView productListView = findViewById(R.id.list);

        // Setup an Adapter to create a list item for each row of product data in the Cursor.
        ProductCursorAdapter adapter = new ProductCursorAdapter(this, cursor);

        // Attach the adapter to the ListView.
        productListView.setAdapter(adapter);
    }

    /**
     * Helper method to insert hardcoded shoes data into the database. For debugging purposes only.
     */
    private void insertShoes() {
        // Create a ContentValues object.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_SHOES_BRAND, "Test Brand");
        values.put(ProductEntry.COLUMN_SHOES_TYPE, "Test Type");
        values.put(ProductEntry.COLUMN_SHOES_PRICE, 99);
        values.put(ProductEntry.COLUMN_SHOES_QUANTITY, 0);
        values.put(ProductEntry.COLUMN_SHOES_SUPPLIER_NAME, "Test User");
        values.put(ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER, "123456789");

        // Insert a new row for TestValues above into the provider using the ContentResolver.
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
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
