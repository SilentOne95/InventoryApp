package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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

    private Button mIncrementButton;
    private Button mDecrementButton;

    /** Boolean flag that keeps track of whether the product has been edited (true) or not (false) */
    private boolean mProductHasChanged = false;

    /** Helper field that help with displaying quantity. */
    private int mHelperQuantity;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are
     * modifying the view, and we change the mProductHasChanged boolean to true.
     */
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

        // Checking the intent to determine which activity should be opened - for editing or adding.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are creating new one.
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));

            invalidateOptionsMenu();
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

        mIncrementButton = findViewById(R.id.increment_button);
        mDecrementButton = findViewById(R.id.decrement_button);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user has
        // touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.

        mBrandEditText.setOnTouchListener(mTouchListener);
        mTypeEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        // Listener for button to call supplier.
        final Button callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callSupplier();
            }
        });

        // Listener for increment quantity button.
        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelperQuantity++;
                displayQuantity();
            }
        });
        mIncrementButton.setOnTouchListener(mTouchListener);

        // Listener for decrement quantity button.
        mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelperQuantity--;
                if (mHelperQuantity < 0) {
                    mHelperQuantity = 0;
                    displayQuantity();
                    displayQuantityWarning();
                } else if (mHelperQuantity == 0) {
                    displayQuantity();
                    displayQuantityWarning();
                } else {
                    displayQuantity();
                }
            }
        });
        mDecrementButton.setOnTouchListener(mTouchListener);
    }

    /**
     * Helper methods for displaying products quantity.
     */
    public void displayQuantity() {
        mQuantityEditText.setText(String.valueOf(mHelperQuantity));
    }

    public void displayQuantityWarning() {
        Toast.makeText(this, getText(R.string.editor_decrement_product),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Method opens phone app with a phone number, so user can decide if want to call or not.
     */
    public void callSupplier() {
        String phoneNumber = mSupplierPhoneEditText.getText().toString().trim();

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }

    /**
     * Get user input from editor and save new product into database.
     */
    private void saveProduct() {
        // Read from input fields.
        String brandString = mBrandEditText.getText().toString().trim();
        String typeString = mTypeEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();


        // Check if this is supposed to be a new product and check if all the fields are blank.
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(brandString) && TextUtils.isEmpty(typeString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneString)) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and to do any ContentProvider operations.
            return;
        }

        // If any field is null, pop up the toast message for the user to provide the data.
        if (TextUtils.isEmpty(brandString)) {
            Toast.makeText(this, R.string.valid_product_brand,
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(typeString)) {
            Toast.makeText(this, R.string.valid_product_type,
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, R.string.valid_product_price,
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, R.string.valid_product_quantity,
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, R.string.valid_product_supplier_info,
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, R.string.valid_product_supplier_info,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_SHOES_BRAND, brandString);
        values.put(ProductEntry.COLUMN_SHOES_TYPE, typeString);
        values.put(ProductEntry.COLUMN_SHOES_PRICE, priceString);
        values.put(ProductEntry.COLUMN_SHOES_QUANTITY, quantityString);
        values.put(ProductEntry.COLUMN_SHOES_SUPPLIER_NAME, supplierNameString);
        values.put(ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        // Determine if this is a new or existing product by checking if mCurrentPetUri is null or not.
        if (mCurrentProductUri == null) {
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
        } else {
            // If it's a existing product, update its values.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show toast message depending whether or not the update was successful.
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the menu can be
     * updated (some menu items can be hidden or made invisible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this iss a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes
                // should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes.
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press.
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes.
        showUnsavedChangesDialog(discardButtonClickListener);
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

            // Setting actual number of quantity and assign it to the helper field.
            String helper = mQuantityEditText.getText().toString().trim();
            mHelperQuantity = Integer.parseInt(helper);
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

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when the user
     *                                   confirms they want to discard their changes.
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog and continue
                // editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
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

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this an existing product.
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}
