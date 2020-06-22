package com.columnhack.fix.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.columnhack.fix.R;
import com.columnhack.fix.ServicesLab;
import com.columnhack.fix.adapters.MyServicesRecyclerViewAdapter;
import com.columnhack.fix.adapters.NearbyServicesRecyclerViewAdapter;
import com.columnhack.fix.adapters.ServiceRecyclerViewAdapter;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.touchhelpers.MyServicesItemTouchHelperCallback;

import java.util.List;

public class MyServicesFragment extends Fragment {
    // We can only handle this when the fragment is attached to the activity
//    private onFragmentBtnSelected listener;
    /*var*/

    /*widgets*/
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    private MyServicesRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_services, container, false);
        mRecyclerView = view.findViewById(R.id.my_services_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Service> myServices = ServicesLab.getInstance(getActivity()).getNearbyServices();
        mAdapter = new MyServicesRecyclerViewAdapter(getActivity(), myServices);

        MyServicesItemTouchHelperCallback callback = new MyServicesItemTouchHelperCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mAdapter.setTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_my_services);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mAdapter.notifyDataSetChanged();

//                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter = null;
    }

    //        Button allServicesBtn = view.findViewById(R.id.all_services_btn);
//        allServicesBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onButtonSelected();
//            }
//        });
//        return view;
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (context instanceof onFragmentBtnSelected)
//            listener = (onFragmentBtnSelected) context;
//    }
//
//    public interface onFragmentBtnSelected {
//        public void onButtonSelected();
//    }
}
