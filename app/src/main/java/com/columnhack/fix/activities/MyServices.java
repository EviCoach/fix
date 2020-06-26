package com.columnhack.fix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.columnhack.fix.R;
import com.columnhack.fix.ServiceLab;
import com.columnhack.fix.adapters.MyServicesRecyclerViewAdapter;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.touchhelpers.MyServicesItemTouchHelperCallback;

import java.util.List;

public class MyServices extends AppCompatActivity {

    /*widgets*/
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;

    /*vars*/
    private MyServicesRecyclerViewAdapter mAdapter;
    private List<Service> mMyServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);

        mMyServices = ServiceLab.getInstance(this).getMyServices();

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_my_services);
        mRecyclerView = findViewById(R.id.my_services_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyServicesRecyclerViewAdapter(this, mMyServices);

        MyServicesItemTouchHelperCallback callback = new MyServicesItemTouchHelperCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mAdapter.setTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mAdapter.notifyDataSetChanged();

//                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}