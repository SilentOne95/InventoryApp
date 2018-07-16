package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of shoes data as its data source. This adapter knows
 * how to create list items for each row of shoes data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify on the list item layout.
        TextView brandTextView = view.findViewById(R.id.brand_name);
        TextView typeTextView = view.findViewById(R.id.type);

        // Find the columns of product attributes that we are interested in.
        int brandColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_BRAND);
        int typeColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SHOES_TYPE);

        // Read the product attributes from the Cursor for the current product.
        String shoesBrand = cursor.getString(brandColumnIndex);
        String shoesType = cursor.getString(typeColumnIndex);

        // Update the TextViews with the attributes for the current product.
        brandTextView.setText(shoesBrand);
        typeTextView.setText(shoesType);
    }
}
