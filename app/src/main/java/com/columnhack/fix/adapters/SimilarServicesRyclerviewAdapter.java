package com.columnhack.fix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.models.Service;

import java.util.List;

public class SimilarServicesRyclerviewAdapter extends
        RecyclerView.Adapter<SimilarServicesRyclerviewAdapter.SimilarServicesHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Service> mSimilarServices;

    public SimilarServicesRyclerviewAdapter(Context context, LayoutInflater layoutInflater, List<Service> similarServices) {
        mContext = context;
        mLayoutInflater = layoutInflater;
        mSimilarServices = similarServices;
    }

    @NonNull
    @Override
    public SimilarServicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.service_items, parent, false);
        return new SimilarServicesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarServicesRyclerviewAdapter.SimilarServicesHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mSimilarServices.size();
    }

    public class SimilarServicesHolder extends RecyclerView.ViewHolder {

        private ImageView serviceImage;
        private TextView titleView;
        private TextView descriptionView;

        public SimilarServicesHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.service_image);
            titleView = itemView.findViewById(R.id.nearby_service_desc);
            descriptionView = itemView.findViewById(R.id.service_desc);
        }

        public void bind(int position) {
            titleView.setText(mSimilarServices.get(position).getTitle());
            descriptionView.setText(mSimilarServices.get(position).getDescription());
        }
    }
}
