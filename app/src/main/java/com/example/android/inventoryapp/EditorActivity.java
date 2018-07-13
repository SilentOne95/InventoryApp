package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryDbHelper;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /** EditText fields to enter the data. */
    private EditText mBrandEditText;
    private EditText mTypeEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from.
        mBrandEditText = findViewById(R.id.edit_product_brand);
        mTypeEditText = findViewById(R.id.edit_product_type);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_product_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_product_supplier_phone_number);
    }

    /**
     * Get user input from editor and save product into database.
     */
    private void insertShoes() {
        // Read from input fields.
        String brandString = mBrandEditText.getText().toString().trim();
        String typeString = mTypeEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        int phone = Integer.parseInt(supplierPhoneString);

        // Create database helper
        InventoryDbHelper mDbHelper = new InventoryDbHelper(this);

        // Gets the database in write mode.
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_SHOES_BRAND, brandString);
        values.put(ProductEntry.COLUMN_SHOES_TYPE, typeString);
        values.put(ProductEntry.COLUMN_SHOES_PRICE, price);
        values.put(ProductEntry.COLUMN_SHOES_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_SHOES_SUPPLIER_NAME, supplierNameString);
        values.put(ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER, phone);

        // Insert a new row for product in the database, returning the ID of that new row.
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);

        Log.i(LOG_TAG, "Added new row with ID: " + newRowId);

        // Show toast message depending on whether or not the insertion was successful.
        if (newRowId == -1) {
            Toast.makeText(this, "Error with saving product.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Product saved successfully!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertShoes();
                finish();
                return true;
            case R.id.action_delete:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
