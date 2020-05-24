package com.columnhack.fix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceRecyclerViewAdapter extends
        RecyclerView.Adapter<ServiceRecyclerViewAdapter.ServiceHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Service> mServices;

    public ServiceRecyclerViewAdapter(Context context, List<Service> services){
        mContext = context;
        mServices = services;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ServiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.service_items, parent, false);
        return new ServiceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceHolder holder, int position) {
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return mServices.size();
    }

    public class ServiceHolder extends RecyclerView.ViewHolder{
        private ImageView serviceImage;
        private TextView titleView;
        private TextView descriptionView;

        public ServiceHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.service_image);
            titleView = itemView.findViewById(R.id.service_title);
            descriptionView = itemView.findViewById(R.id.service_desc);
        }

        public void bind(int position){
            titleView.setText(mServices.get(position).getTitle());
            descriptionView.setText(mServices.get(position).getDescription());
        }
    }
}
