package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of shoes data as its data source. This adapter knows
 * how to create list items for each row of shoes data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    private int test;

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context.
     * @param cursor  The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the shoes data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the brand name for the current shoes can be set on the name
     * TextView in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify on the list item layout.
        TextView brandTextView = view.findViewById(R.id.brand_name);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView quantityTextView = view.findViewById(R.id.quantity);

        Button saleButton = view.findViewById(R.id.sale_button);

        // Find the columns of product attributes that we are interested in.
        int brandColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_BRAND);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_QUANTITY);

        // Read the product attributes from the Cursor for the current product.
        final String shoesBrand = cursor.getString(brandColumnIndex);
        String shoesPrice = cursor.getString(priceColumnIndex);
        String shoesQuantity = cursor.getString(quantityColumnIndex);

        final int quantity = cursor.getInt(quantityColumnIndex);
        final String id = cursor.getString(cursor.getColumnIndex(ProductEntry._ID));

        // TODO:
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri mCurrentProductUri = Uri.withAppendedPath(ProductEntry.CONTENT_URI, id);
                ContentValues values = new ContentValues();

                if (quantity > 1) {
                    values.put(ProductEntry.COLUMN_SHOES_QUANTITY, quantity - 1);

                    int rowsAffected = context.getContentResolver().update(
                            mCurrentProductUri, values, null, null);
                } else if (quantity == 1) {
                    values.put(ProductEntry.COLUMN_SHOES_QUANTITY, quantity - 1);

                    int rowsAffected = context.getContentResolver().update(
                            mCurrentProductUri, values, null, null);

                    Toast.makeText(context,
                            context.getApplicationContext().getResources().getString(R.string.editor_decrement_product),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,
                            context.getApplicationContext().getResources().getString(R.string.editor_decrement_product),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Update the TextViews with the attributes for the current product.
        brandTextView.setText(shoesBrand);
        priceTextView.setText(shoesPrice);
        quantityTextView.setText(shoesQuantity);
    }
}
