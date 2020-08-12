package com.columnhack.fix.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.adapters.NearbyServicesRecyclerViewAdapter;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.utility.ServiceLab;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class NearByServicesFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = NearByServicesFragment.class.getSimpleName();
    public static final int LOCATION_REQUEST_CODE = 12;
    private MapView mMapView;
    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private Location mUserLocation;
    private NearbyLocationCallback mLocationCallback;

    List<Service> mNearbyServices = new ArrayList<>();
    private RecyclerView mNearbyServicesRecyclerView;
    private NearbyServicesRecyclerViewAdapter mAdapter;
    private TextView mDisabledLocationText;
    private Button mEnableLocationBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NearbyServicesRecyclerViewAdapter(getActivity(), mNearbyServices);
        requestLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        if (ServiceLab.getInstance(getActivity()).isLocationEnabled()) {
            requestLocationUpdates();
            updateUI();
        }
    }

    public NearbyServicesRecyclerViewAdapter getAdapter() {
        return mAdapter;
    }

    public GoogleMap getGoogleMap() {
        return mMap;
    }

    private void requestLocationUpdates() {
        if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mUserLocation == null) {
                mFusedLocationClient = new FusedLocationProviderClient(getActivity());
                mLocationRequest = new LocationRequest();
                mLocationCallback = new NearbyLocationCallback();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setFastestInterval(2000);
                mLocationRequest.setInterval(2000);
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, getMainLooper());
            }
        } else {
            // Request for permission here
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissions, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length < 1) {
                throw new RuntimeException("no permissions on request result");
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                requestLocationUpdates();
            } else {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // User declined but I can still ask for more and show rationale
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_REQUEST_CODE);
                } else {
                    // User declined and I can't ask more
                    Toast.makeText(getActivity(), "You need file access permissions to upload service images",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_services, container, false);


        mNearbyServicesRecyclerView = view.findViewById(R.id.nearby_services_recyclerview);
        mEnableLocationBtn = view.findViewById(R.id.enable_location_btn);
        if (mEnableLocationBtn != null)
            mEnableLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
        mDisabledLocationText = view.findViewById(R.id.disabled_location_text);
        updateUI();
        return view;
    }

    private void updateUI() {
        if (!ServiceLab.getInstance(getActivity()).isLocationEnabled()) {
            mDisabledLocationText.setVisibility(View.VISIBLE);
            mEnableLocationBtn.setVisibility(View.VISIBLE);
            mNearbyServicesRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            if (mDisabledLocationText != null)
                mDisabledLocationText.setVisibility(View.INVISIBLE);
            if (mEnableLocationBtn != null )
                mEnableLocationBtn.setVisibility(View.INVISIBLE);
            mNearbyServicesRecyclerView.setVisibility(View.VISIBLE);
            mNearbyServicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mAdapter = new NearbyServicesRecyclerViewAdapter(getActivity(), mNearbyServices);
            mNearbyServicesRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.nearby_services_map);
        if (mMapView != null) {
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

    private void addServiceMarkers() {
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            LatLng zoomMapLocation = new LatLng(mUserLocation.getLatitude(),
                    mUserLocation.getLongitude());

            // Move the camera instantly to location with zoom of 15
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomMapLocation, 13.5f));
            // zoom in, animating the camera
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13.5f), 2000, null);
        }
    }

    private class NearbyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mUserLocation = locationResult.getLastLocation();
            ServiceLab.getInstance(getActivity()).setCurrentLocation(mUserLocation);
            mNearbyServices = ServiceLab.getInstance(getActivity()).getNearbyServices(mAdapter, "query", mMap);
            addServiceMarkers();
            mFusedLocationClient.removeLocationUpdates(this);
        }
    }
}
