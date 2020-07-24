package com.columnhack.fix.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.utility.ServiceLab;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.touchhelpers.ItemTouchHelperAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MyServicesRecyclerViewAdapter extends
        RecyclerView.Adapter<MyServicesRecyclerViewAdapter.MyServicesHolder> implements ItemTouchHelperAdapter {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Service> mMyServices;
    private ItemTouchHelper mTouchHelper;
    private View mItemView;

    public MyServicesRecyclerViewAdapter(Context context, List<Service> services) {
        mContext = context;
        mMyServices = services;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void refreshServices(){
        mMyServices = ServiceLab.getInstance(mContext).getMyServices(this);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyServicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mItemView = mLayoutInflater.inflate(R.layout.nearby_service_item, parent, false);
        return new MyServicesHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyServicesHolder holder, int position) {
        holder.serviceTitleView.setText(mMyServices.get(position).getTitle());
        // Very necessary, if you remove the following line, you'll get this exception:
        // No suitable parent found from the given view. Please provide a valid view
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mMyServices.size();
    }

    /*ItemTouchHelper*/
    @Override
    public void onItemSwiped(final int position) {
        final Service temporaryService = mMyServices.get(position);
        mMyServices.remove(position);
        Snackbar undoSnackbar = Snackbar.make(mItemView.getRootView(), R.string.removed_snackbar_message, Snackbar.LENGTH_LONG);
        undoSnackbar.setAction(R.string.undo_delete, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mMyServices.add(temporaryService);
                notifyItemInserted(position);
            }
        }).setActionTextColor(Color.RED);
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