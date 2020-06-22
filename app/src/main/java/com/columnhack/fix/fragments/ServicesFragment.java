package com.columnhack.fix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.ServicesLab;
import com.columnhack.fix.adapters.ServiceRecyclerViewAdapter;
import com.columnhack.fix.models.Service;

import java.util.List;

public class ServicesFragment extends Fragment {

    List<Service> mServices;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServices = ServicesLab.getInstance(getActivity()).getServices();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        mRecyclerView = view.findViewById(R.id.service_recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setAdapter(new ServiceRecyclerViewAdapter(getActivity(), mServices));
        return view;
    }
}
