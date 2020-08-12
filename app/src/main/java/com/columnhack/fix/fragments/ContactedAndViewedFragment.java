package com.columnhack.fix.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.adapters.NearbyServicesRecyclerViewAdapter;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.utility.ServiceLab;

import java.util.ArrayList;
import java.util.List;

public class ContactedAndViewedFragment extends Fragment {

    // vars
    public static final String CONTACTED_OR_VIEWED_SERVICES = "contacted_or_viewed_services";
    private String whichServices = "";
    private List<Service> mServices = new ArrayList<>();
    private NearbyServicesRecyclerViewAdapter mAdapter;

    // widgets
    private RecyclerView mRecyclerView;

    public static ContactedAndViewedFragment newInstance(String whichServices) {
        Bundle args = new Bundle();
        args.putString(CONTACTED_OR_VIEWED_SERVICES, whichServices);
        ContactedAndViewedFragment fragment = new ContactedAndViewedFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whichServices = getArguments().getString(CONTACTED_OR_VIEWED_SERVICES);

        mAdapter = new NearbyServicesRecyclerViewAdapter(getActivity(), mServices);

        switch (whichServices){
            case Service.CONTACTED:
                ServiceLab.getInstance(getActivity()).getContactedSerivces(mAdapter);
                break;
            case Service.VIEWED:
                ServiceLab.getInstance(getActivity()).getViewedSerivces(mAdapter);
                break;
        }



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacted_or_viewed, container, false);

        mRecyclerView = view.findViewById(R.id.contacted_and_viewed_recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}
