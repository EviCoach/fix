package com.columnhack.fix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.touchhelpers.ItemTouchHelperAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MyServicesRecyclerViewAdapter extends
        RecyclerView.Adapter<MyServicesRecyclerViewAdapter.MyServicesHolder> implements ItemTouchHelperAdapter {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Service> mNearbyServices;
    private ItemTouchHelper mTouchHelper;
    private View mItemView;

    public MyServicesRecyclerViewAdapter(Context context, List<Service> services) {
        mContext = context;
        mNearbyServices = services;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyServicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mItemView = mLayoutInflater.inflate(R.layout.nearby_service_item, parent, false);
        return new MyServicesHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyServicesHolder holder, int position) {
        holder.serviceTitleView.setText(mNearbyServices.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mNearbyServices.size();
    }

    /*ItemTouchHelper*/
    @Override
    public void onItemSwiped(final int position) {
        final Service temporyService = mNearbyServices.get(position);
        mNearbyServices.remove(position);
        Snackbar undoSnackbar = Snackbar.make(mItemView.getRootView(), R.string.removed_snackbar_message, Snackbar.LENGTH_LONG);
        undoSnackbar.setAction(R.string.undo_delete, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mNearbyServices.add(temporyService);
                notifyItemInserted(position);
            }
        });
        undoSnackbar.show();

        notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper){
        mTouchHelper = touchHelper;
    }
    public class MyServicesHolder extends RecyclerView.ViewHolder {
        private TextView serviceTitleView;
        public MyServicesHolder(@NonNull View itemView) {
            super(itemView);
            serviceTitleView = itemView.findViewById(R.id.nearby_service_desc);
        }
    }
}