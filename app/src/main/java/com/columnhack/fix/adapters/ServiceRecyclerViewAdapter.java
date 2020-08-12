package com.columnhack.fix.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.columnhack.fix.R;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.activities.ServiceDetailActivity;
import com.columnhack.fix.utility.ServiceLab;

import java.util.List;

public class ServiceRecyclerViewAdapter extends
        RecyclerView.Adapter<ServiceRecyclerViewAdapter.ServiceHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Service> mServices;
    private int mCurrentPosition;

    public ServiceRecyclerViewAdapter(Context context, List<Service> services) {
        mContext = context;
        mServices = services;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void refreshServices() {
        mServices = ServiceLab.getInstance(mContext).getServices(this);
        notifyDataSetChanged();
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
        mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mServices.size();
    }

    public class ServiceHolder extends RecyclerView.ViewHolder {
        private ImageView serviceImage;
        private TextView titleView;
        private TextView descriptionView;

        public ServiceHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.service_image);
            titleView = itemView.findViewById(R.id.service_title);
            descriptionView = itemView.findViewById(R.id.service_desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: display the selected service, not just a blank service
                    Intent intent = new Intent(mContext, ServiceDetailActivity.class);
                    Service service = mServices.get(getAdapterPosition());
                    intent.putExtra(ServiceDetailActivity.SELECTED_SERVICE, service);
                    mContext.startActivity(intent);
                }
            });
        }

        public void bind(int position) {
            List<String> serviceImgUrls = mServices.get(position).getService_img_urls();
            if (!serviceImgUrls.isEmpty()) {
                Glide.with(mContext)
                        .load(serviceImgUrls.get(0))
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(serviceImage);
            }
            titleView.setText(mServices.get(position).getTitle());
            descriptionView.setText(mServices.get(position).getDescription());
        }
    }
}
