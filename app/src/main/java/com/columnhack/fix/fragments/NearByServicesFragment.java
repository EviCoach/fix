package com.columnhack.fix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.ServicesLab;
import com.columnhack.fix.adapters.NearbyServicesRecyclerViewAdapter;
import com.columnhack.fix.models.Service;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

public class NearByServicesFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mMap;

    List<Service> mNearbyServices;
    private RecyclerView mNearbyServicesRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNearbyServices = ServicesLab.getInstance(getActivity()).getNearbyServices();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_services, container, false);
        mNearbyServicesRecyclerView = view.findViewById(R.id.nearby_services_recyclerview);
        mNearbyServicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNearbyServicesRecyclerView.setAdapter(new NearbyServicesRecyclerViewAdapter(getActivity(), mNearbyServices));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.nearby_services_map);
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mMap = googleMap;
    }
}
