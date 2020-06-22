package com.columnhack.fix.touchhelpers;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MyServicesItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;

    public MyServicesItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // This method is responsible for setting the allowed movement flags on the viewholder, return 0
        // if you don't want to move the headers
        final int swipeFlags = ItemTouchHelper.START;
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // This method is triggered when a list item is moved to a different position,
        // we also need an adapter class here too
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // This method is called when an item is swiped, we need the help of an adapter class here
        mAdapter.onItemSwiped(viewHolder.getAdapterPosition());
    }

    /*----------------------------------------*/

    @Override
    public boolean isLongPressDragEnabled() { // for enabling draggability on the list items
        return false; // disable this feature, we will implement our own
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        // This method is called when an action is called on the list, in this case, check if it's a drag,
        // set the background color to a light grey color
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // This method is called when the list item is let go, ie, when the user removes their finger from the list,
        // set the background of the viewholder back to default, in this case, white
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
    }
}
