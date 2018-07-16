package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the product data loader. */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /** Content URI for the existing product (null if it's a new product). */
    private Uri mCurrentProductUri;

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

        // Checking the intent to determine which activity should be opened - for editing or adding.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are creating new one.
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));

            // Initialize a loader to read the product data from the database and display
            // the current values in the editor.
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

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

        // Create a ContentValues object.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_SHOES_BRAND, brandString);
        values.put(ProductEntry.COLUMN_SHOES_TYPE, typeString);
        values.put(ProductEntry.COLUMN_SHOES_PRICE, price);
        values.put(ProductEntry.COLUMN_SHOES_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_SHOES_SUPPLIER_NAME, supplierNameString);
        values.put(ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER, phone);

        // Insert a new product into the provider, returning the content URI for the new product.
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        // Show toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                    Toast.LENGTH_SHORT).show();
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_SHOES_BRAND,
                ProductEntry.COLUMN_SHOES_TYPE,
                ProductEntry.COLUMN_SHOES_PRICE,
                ProductEntry.COLUMN_SHOES_QUANTITY,
                ProductEntry.COLUMN_SHOES_SUPPLIER_NAME,
                ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            int brandColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_BRAND);
            int typeColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_TYPE);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER);

            String brand = cursor.getString(brandColumnIndex);
            String type = cursor.getString(typeColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            mBrandEditText.setText(brand);
            mTypeEditText.setText(type);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(Integer.toString(supplierPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mBrandEditText.setText("");
        mTypeEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }
}
