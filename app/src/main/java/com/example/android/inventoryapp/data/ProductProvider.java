package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * {@link ContentProvider} for InventoryApp.
 */
public class ProductProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the shoes table */
    private static final int SHOES = 100;

    /** URI matcher code for the content URI for a single item in the shoes table */
    private static final int SHOES_ID = 101;

    /** UriMatcher object to match a content URI to a corresponding code */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_INVENTORY, SHOES);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_INVENTORY + "/#", SHOES_ID);
    }

    /** Database helper object */
    private InventoryDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query.
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code.
        int match = sUriMatcher.match(uri);
        switch (match){
            case SHOES:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case SHOES_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHOES:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check if the brand name, type, supplier name and his phone is not null.
        // Also check if price and quantity are grater or equal to 0.
        String brand = values.getAsString(ProductEntry.COLUMN_SHOES_BRAND);
        if (brand == null) {
            throw new IllegalArgumentException("Product requires a brand name");
        }

        String type = values.getAsString(ProductEntry.COLUMN_SHOES_BRAND);
        if (type == null) {
            throw new IllegalArgumentException("Product requires a type");
        }

        Integer price = values.getAsInteger(ProductEntry.COLUMN_SHOES_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_SHOES_PRICE);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }

        String supplierName = values.getAsString(ProductEntry.COLUMN_SHOES_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Product requires a supplier name");
        }

        Integer supplierPhone = values.getAsInteger(ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER);
        if (supplierPhone != null && supplierPhone < 0) {
            throw new IllegalArgumentException("Product requires a supplier phone number");
        }

        // Get writeable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new product with the given values.
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the shoes content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end.
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHOES:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case SHOES_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ProductEntry#COLUMN_SHOES_BRAND} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_SHOES_BRAND)) {
            String brand = values.getAsString(ProductEntry.COLUMN_SHOES_BRAND);
            if (brand == null) {
                throw new IllegalArgumentException("Product requires a brand name");
            }
        }

        // If the {@link ProductEntry#COLUMN_SHOES_TYPE} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_SHOES_TYPE)) {
            String type = values.getAsString(ProductEntry.COLUMN_SHOES_TYPE);
            if (type == null) {
                throw new IllegalArgumentException("Product requires a type");
            }
        }

        // If the {@link ProductEntry#COLUMN_SHOES_PRICE} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_SHOES_PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.COLUMN_SHOES_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        // If the {@link ProductEntry#COLUMN_SHOES_QUANTITY} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_SHOES_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_SHOES_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        // If the {@link ProductEntry#COLUMN_SHOES_SUPPLIER_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_SHOES_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(ProductEntry.COLUMN_SHOES_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // If the {@link ProductEntry#COLUMN_SHOES_SUPPLIER_PHONE_NUMBER} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER)) {
            Integer supplierPhone = values.getAsInteger(ProductEntry.COLUMN_SHOES_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone != null && supplierPhone < 0) {
                throw new IllegalArgumentException("Product requires a valid phone number");
            }
        }

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed.
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected.
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows are updated, then notify all listeners that the data at
        // the given URI has changed.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted.
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHOES:
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SHOES_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at
        // the given URI has changed.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHOES:
                return ProductEntry.CONTENT_LIST_TYPE;
            case SHOES_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
