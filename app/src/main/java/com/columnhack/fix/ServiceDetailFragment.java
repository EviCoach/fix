package com.columnhack.fix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceDetailFragment extends Fragment {
    private List<Service> mSimilarServices;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: change to real similar and related services later
        mSimilarServices = ServicesLab.getInstance(getActivity()).getNearbyServices();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_detail, container, false);

        RecyclerView similarServicesView = view.findViewById(R.id.similar_services_recycler_view);
        ServiceRecyclerViewAdapter similarServicesAdapter = new ServiceRecyclerViewAdapter(getActivity(), mSimilarServices);
        similarServicesView.setAdapter(similarServicesAdapter);
        similarServicesView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        return view;
    }
}
