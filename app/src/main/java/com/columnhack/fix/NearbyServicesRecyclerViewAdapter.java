package com.columnhack.fix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NearbyServicesRecyclerViewAdapter extends
        RecyclerView.Adapter<NearbyServicesRecyclerViewAdapter.NearbyServicesHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Service> mNearbyServices;

    public NearbyServicesRecyclerViewAdapter(Context context, List<Service> services) {
        mContext = context;
        mNearbyServices = services;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NearbyServicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.nearby_service_item, parent, false);
        return new NearbyServicesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyServicesHolder holder, int position) {
        holder.serviceTitleView.setText(mNearbyServices.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mNearbyServices.size();
    }

    public class NearbyServicesHolder extends RecyclerView.ViewHolder {
        private TextView serviceTitleView;
        public NearbyServicesHolder(@NonNull View itemView) {
            super(itemView);
            serviceTitleView = itemView.findViewById(R.id.nearby_service_desc);
        }
    }
}
