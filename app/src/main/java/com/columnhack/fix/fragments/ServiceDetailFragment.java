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
import androidx.viewpager.widget.ViewPager;

import com.columnhack.fix.R;
import com.columnhack.fix.utility.ServiceLab;
import com.columnhack.fix.adapters.ServiceImagesPagerAdapter;
import com.columnhack.fix.adapters.ServiceRecyclerViewAdapter;
import com.columnhack.fix.models.Service;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class ServiceDetailFragment extends Fragment {
    // vars
    private List<Service> mSimilarServices;
    private ArrayList<Fragment> imgFragments = new ArrayList<>();
    ServiceRecyclerViewAdapter mAdapter;

    // widgets
    private ViewPager servicesImageViewPager;
    private TabLayout serviceImgIndicatorTabs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: change to real similar and related services later
        mAdapter = new ServiceRecyclerViewAdapter(getActivity(), mSimilarServices);

        mAdapter = new ServiceRecyclerViewAdapter(getActivity(), mSimilarServices);
        mSimilarServices = ServiceLab.getInstance(getActivity()).getServices(mAdapter);
        mAdapter.refreshServices();


        int position = 0;
        for (Service service : mSimilarServices) {
            Fragment imgFragment = new ImgFragment();
            Bundle bundle = new Bundle();
            imgFragment.setArguments(bundle);
            imgFragments.add(imgFragment);
            position++;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_detail, container, false);

        RecyclerView similarServicesView = view.findViewById(R.id.similar_services_recycler_view);
        servicesImageViewPager = view.findViewById(R.id.service_images_view_pager);
        serviceImgIndicatorTabs = view.findViewById(R.id.service_image_indicator_tabs);
        servicesImageViewPager.setAdapter(new ServiceImagesPagerAdapter(getActivity().getSupportFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, imgFragments));
        serviceImgIndicatorTabs.setupWithViewPager(servicesImageViewPager, true);
        similarServicesView.setAdapter(mAdapter);
        similarServicesView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        return view;
    }
}
